package eval;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Xsd {
	private Map<String, ComplexType> complexTypes = new HashMap<String, ComplexType>();
	private Map<String, String> elements = new HashMap<String, String>();
	private Map<String, String> simpleTypes = null;

	private XsdDateType xsdDateType = null;
	private XsdTimeType xsdTimeType = null;
	private String prefix = "";

	public Xsd() {
	}

	public XsdElementType getXsdElementType(String fullName) {
		String[] names = fullName.split("\\.");
		return getXsdElementType(names[0], names[1]);
	}

	public void printTypes() {
		for (String typeName : complexTypes.keySet()) {
			System.out.println(typeName);
			complexTypes.get(typeName).printFields();
		}
	}

	public XsdElementType getXsdElementType(String className, String attributeName) {
		ComplexType ct = complexTypes.get(className);
		return ct.getXsdElementType(attributeName);
	}

	public ComplexType getComplexType(String name) {
		if (complexTypes.containsKey(name)) {
			return complexTypes.get(name);
		}
		return null;
	}

	public String getElementTypeName(String elementName) {
		return elements.get(elementName);
	}

	public void setXsdDateType(XsdDateType xsdDateType) {
		this.xsdDateType = xsdDateType;
	}

	public void setXsdTimeType(XsdTimeType xsdTimeType) {
		this.xsdTimeType = xsdTimeType;
	}

	public XsdDateType getXsdDateType() {
		return xsdDateType;
	}

	public XsdTimeType getXsdTimeType() {
		return xsdTimeType;
	}

	public Map<String, ComplexType> getComplexTypes() {
		return complexTypes;
	}

	public void addComplexType(ComplexType ct) {
		complexTypes.put(ct.getName(), ct);
	}

	private Schema schema = null;

	public boolean isXmlValid(Document document) {
		if (schema != null) {

			Validator validator = schema.newValidator();
			try {
				validator.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
				validator.validate(new DOMSource(document));

				return true;
			} catch (SAXException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	private String xsdFileName;

	public String getXsdFileName() {
		return this.xsdFileName;
	}

	public void loadFile(String fileName) {
		this.xsdFileName = fileName;
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Source schemaFile = new StreamSource(new File(fileName));
		try {
			schema = schemaFactory.newSchema(schemaFile);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(new File(fileName));
			Element curElem = document.getDocumentElement();
			prefix = curElem.getNodeName();
			if (prefix.length() > 6 && prefix.endsWith(":schema")) {
				prefix = prefix.substring(0, prefix.indexOf(':') + 1);
			} else {
				prefix = "";
			}
			simpleTypes = collectSimpleTypes(curElem);

			collectElements(curElem);
			NodeList nlist;
			nlist = curElem.getElementsByTagName(prefix + "complexType");
			collectComplexTypes(nlist);
			if (xsdDateType != null) {
				complexTypes.remove(xsdDateType.getName());
			}
			if (xsdTimeType != null) {
				complexTypes.remove(xsdTimeType.getName());
			}
			for (ComplexType ct : this.complexTypes.values()) {
				resolveReferences(ct);
			}
			for (ComplexType ct : this.complexTypes.values()) {
				resolveInheritance(ct.getName());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void collectElements(Element curElem) {
		Node curNode = curElem.getFirstChild();
		while (curNode != null) {
			if (curNode.getNodeName().equals(prefix + "element")) {
				String elementName = ((Element) curNode).getAttribute("name");
				String elementType = ((Element) curNode).getAttribute("type");
				elements.put(elementName, elementType);
				// } else if (curNode.getNodeName().equals(prefix +
				// "attribute")) {
				// String elementName = ((Element)
				// curNode).getAttribute("name");
				// String elementType = ((Element)
				// curNode).getAttribute("type");
				// elements.put(elementName, elementType);
			}
			curNode = curNode.getNextSibling();
		}
	}

	private void collectComplexTypes(NodeList nlist) {
		for (int i = 0; i < nlist.getLength(); i++) {
			Element node = (Element) nlist.item(i);
			String className = node.getAttribute("name");
			String baseName = getBaseName(node);
			ComplexType complexType = new ComplexType(className, baseName);
			complexTypes.put(complexType.getName(), complexType);
			NodeList fields = node.getElementsByTagName(prefix + "element");
			for (int j = 0; j < fields.getLength(); j++) {
				Element field = (Element) fields.item(j);
				String attName = field.getAttribute("name");
				String fieldType = field.getAttribute("type");
				if (fieldType.indexOf(":") > 0) {
					fieldType = fieldType.substring(fieldType.indexOf(":") + 1);
				}
				if (fieldType.isEmpty()) {
					NodeList deepList = field.getElementsByTagName(prefix + "restriction");
					if (deepList.getLength() > 0) {
						fieldType = deepList.item(0).getAttributes().getNamedItem("base").getNodeValue();
					}
				}
				Class<? extends DataElement> dataType;
				if (simpleTypes.containsKey(fieldType)) {
					fieldType = simpleTypes.get(fieldType);
				}
				dataType = TypeSystem.getTypeByXsdType(fieldType, null);
				String specialType = null;
				if (dataType == null) {
					if (xsdDateType != null && fieldType.equals(xsdDateType.getName())) {
						specialType = xsdDateType.getName();
					} else if (xsdTimeType != null && fieldType.equals(xsdTimeType.getName())) {
						specialType = xsdTimeType.getName();
					} else if (fieldType.endsWith("Ref")) {

					}
				}
				String maxOccursAttr = field.getAttribute("maxOccurs");
				int maxOccurs = 1;
				if (maxOccursAttr != null && maxOccursAttr.equals("unbounded")) {
					maxOccurs = Integer.MAX_VALUE;
				} else if (maxOccursAttr.length() > 0) {
					maxOccurs = Integer.parseInt(maxOccursAttr);
				}
				complexType.addAttribute(attName, dataType, fieldType, specialType, maxOccurs);
			}
			NodeList attribs = node.getElementsByTagName(prefix + "attribute");			
			for(int k = 0; k < attribs.getLength(); k++) {
				Element field = (Element) attribs.item(k);
				String attName = field.getAttribute("name");
				String fieldType = field.getAttribute("type");
				if (fieldType.equals(prefix+"ID")) {
					complexType.setIdAttributeName(attName);					
				} else if (fieldType.equals(prefix+"IDREF")){
					complexType.setRefType(true);
				} else {
					complexType.setAttribute(attName);
				}
			}
		}
	}

	private String getBaseName(Element node) {
		NodeList contentList = node.getElementsByTagName(prefix + "extension");
		String baseName = null;
		if (contentList.getLength() == 1) {
			baseName = ((Element) contentList.item(0)).getAttribute("base");
		}
		return baseName;
	}

	private Map<String, String> collectSimpleTypes(Element curElem) {
		Map<String, String> simpleTypes = new HashMap<String, String>();
		NodeList nlist = curElem.getElementsByTagName(prefix + "simpleType");
		for (int i = 0; i < nlist.getLength(); i++) {
			Element node = (Element) nlist.item(i);
			if (node.getAttributes().getNamedItem("name") != null) {
				String typeName = node.getAttributes().getNamedItem("name").getNodeValue();
				NodeList restrictionList = node.getElementsByTagName(prefix + "restriction");
				if (restrictionList.getLength() > 0) {
					Element element = (Element) restrictionList.item(0);
					String typeBase = element.getAttributes().getNamedItem("base").getNodeValue();
					simpleTypes.put(typeName, typeBase);
				}
			}
		}
		return simpleTypes;
	}

	private void resolveInheritance(String typeName) {
		ComplexType complexType = this.complexTypes.get(typeName);
		if (complexType.isInheritanceResolved()) {
			return;
		}
		ComplexType baseType = complexTypes.get(complexType.getBaseName());
		if (!baseType.isInheritanceResolved()) {
			resolveInheritance(baseType.getName());
		} else {
			complexType.insertBaseAttribues(baseType);
		}
	}

	private void resolveReferences(ComplexType complexType) {
		java.util.List<String> removed = new ArrayList<String>();
		for (String ref : complexType.getReferences()) {
			XsdElementType attr = complexType.getXsdElementType(ref);
			ComplexType refType = complexTypes.get(attr.getTypeName());
			if (refType.getFields().size() == 1 && refType.getLists().size() == 1) {
				complexType.getLists().add(ref);
				removed.add(ref);
			}
		}
		complexType.getReferences().removeAll(removed);
	}
}
