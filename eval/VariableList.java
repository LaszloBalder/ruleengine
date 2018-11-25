package eval;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class VariableList {

	private Map<String, Variable> varList = new HashMap<String, Variable>();

	private ArrayList<Variable> propertyBag = new ArrayList<Variable>();

	private Map<String, Object> objectList = new HashMap<String, Object>();

	private Map<String, Element> nodeList = new HashMap<String, Element>();

	private Map<String, Attribute> attribList = new HashMap<String, Attribute>();
	private Xsd xsd;

	public void setXsd(Xsd xsd) {
		this.xsd = xsd;
		// xsd.printTypes();
	}

	public void addVariable(String name, DataElement value) {
		if (name.indexOf('.') > 0) {
			setAttributeValue(name, value);
			return;
		}
		Variable var = new Variable(name, value.getClass());
		var.setValue(value);
		varList.put(name, var);
	}

	public Object getObject(String className) {
		return objectList.get(className);
	}

	public void addVariable(String name, Class<? extends DataElement> type,
			String defaultValue) {
		Variable var = new Variable(name, type, defaultValue);
		varList.put(name, var);
	}

	public Variable getVariable(String name) {
		return (Variable) varList.get(name);
	}

	public void setValue(String name, DataElement value) {
		Variable var = varList.get(name);
		if (var != null) {
			var.setValue(value);
		} else if (name.indexOf('.') < 0) {
			this.addVariable(name, value);
		} else {
			setAttributeValue(name, value);
		}
	}

	public DataElement getValue(String name) {
		Variable var = varList.get(name);
		if (var != null) {
			return var.getValue();
		} else {
			return getAttributeValue(name);
		}
	}

	public Constructor<? extends DataElement> getVariableConstructor(String name) {
		Variable var = varList.get(name);
		if (var != null) {
			try {
				return var.getType().getConstructor(String.class);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	public void addObject(Object object) {
		if (Element.class.isInstance(object)) {
			addXmlElement((Element) object);
		} else {
			String[] nameParts = object.getClass().getName().split("\\.");
			objectList.put(nameParts[nameParts.length - 1], object);
		}
	}

	public void addXmlElement(Element element, String name) {
		nodeList.put(name, element);
	}

	public void addXmlElement(Element element) {
		addXmlElement(element, element.getNodeName());
	}

	public void removeObject(Object object) {
		if (Element.class.isInstance(object)) {
			removeNode((Element) object);
		} else {
			String[] nameParts = object.getClass().getName().split("\\.");
			objectList.remove(nameParts[nameParts.length - 1]);
		}
	}

	private void removeNode(Element element) {
		nodeList.remove(element.getNodeName());
	}

	public void removeXmlElement(String name) {
		nodeList.remove(name);
	}

	private void registerAttribute(Attribute attribute) {
		attribList.put(attribute.getName(), attribute);
		try {
			Object object = objectList.get(attribute.getClassName());
			attribute.setGetter(object.getClass().getMethod(
					"get" + attribute.getAttributeName()));
			Class<?> retType = attribute.getGetter().getReturnType();
			attribute.setSetter(object.getClass().getMethod(
					"set" + attribute.getAttributeName(), retType));
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Element insertElement(Element base, XsdElementType node) {
		Element newElement = base.getOwnerDocument().createElement(
				node.getName());
		String[] succNodes = xsd.getComplexType(node.getClassName())
				.getSucceedingNodes(node.getName());
		for (int i = 0; i < succNodes.length; i++) {
			NodeList succeeder = base.getElementsByTagName(succNodes[i]);
			if (succeeder.getLength() > 0) {
				base.insertBefore(newElement, succeeder.item(i));
				return newElement;
			}
		}
		base.appendChild(newElement);
		return newElement;
	}

	private void setXmlElement(String name, DataElement value) {
		XsdElementType elementType = xsd.getXsdElementType(name);
		Element element = nodeList.get(elementType.getClassName());
		NodeList tagList = element.getElementsByTagName(elementType.getName());
		Element newElement;
		if (tagList.getLength() == 0) {
			newElement = insertElement(element, elementType);
		} else {
			newElement = (Element) tagList.item(0);
			newElement.removeAttributeNS(
					"http://www.w3.org/2001/XMLSchema-instance", "nil");
		}
		if (elementType.getSpecialTypeName() == null) {
			newElement.setTextContent(value.toString());
		} else {
			setSpecialXmlElement(newElement, value);
		}
	}

	private void setSpecialXmlElement(Element element, DataElement value) {
		Document owner = element.getOwnerDocument();
		if (value.getClass() == Date.class) {
			Date date = (Date) value;
			while (element.getChildNodes().getLength() > 0) {
				element.removeChild(element.getChildNodes().item(0));
			}
			Element sub = owner.createElement(xsd.getXsdDateType()
					.getYearName());
			sub.setTextContent(Integer.toString(date.getYear()));
			element.appendChild(sub);
			sub = owner.createElement(xsd.getXsdDateType().getMonthName());
			sub.setTextContent(Integer.toString(date.getMonth()));
			element.appendChild(sub);
			sub = owner.createElement(xsd.getXsdDateType().getDayName());
			sub.setTextContent(Integer.toString(date.getDay()));
			element.appendChild(sub);
		} else if (value.getClass() == Time.class) {
			Time time = (Time) value;
			while (element.getChildNodes().getLength() > 0) {
				element.removeChild(element.getChildNodes().item(0));
			}
			Element sub = owner.createElement(xsd.getXsdTimeType()
					.getHourName());
			sub.setTextContent(Integer.toString(time.getHours()));
			element.appendChild(sub);
			sub = owner.createElement(xsd.getXsdTimeType().getMinuteName());
			sub.setTextContent(Integer.toString(time.getMinutes()));
			element.appendChild(sub);
			sub = owner.createElement(xsd.getXsdTimeType().getSecondName());
			sub.setTextContent(Integer.toString(time.getSeconds()));
			element.appendChild(sub);
		}
	}

	private DataElement getXmlElementValue(String name) {
		XsdElementType elementType = xsd.getXsdElementType(name);

		Element element = (Element) this.nodeList.get(elementType
				.getClassName());
		NodeList nodeList = element.getElementsByTagName(elementType.getName());
		if (nodeList.getLength() > 0) {
			Class<? extends DataElement> type = elementType.getType();
			Constructor<? extends DataElement> constr = null;
			try {
				if (elementType.getSpecialTypeName() == null) {
					constr = type.getConstructor(String.class);
					return constr
							.newInstance(nodeList.item(0).getTextContent());
				} else {
					Element specialType = (Element) nodeList.item(0);

					if (elementType.getSpecialTypeName().equals(
							xsd.getXsdDateType().getName())) {
						constr = type.getConstructor(int[].class);
						int[] params = new int[3];
						params[0] = Integer.parseInt(specialType
								.getElementsByTagName(
										xsd.getXsdDateType().getYearName())
								.item(0).getTextContent());
						params[1] = Integer.parseInt(specialType
								.getElementsByTagName(
										xsd.getXsdDateType().getMonthName())
								.item(0).getTextContent());
						params[2] = Integer.parseInt(specialType
								.getElementsByTagName(
										xsd.getXsdDateType().getDayName())
								.item(0).getTextContent());
						return constr.newInstance(params);
					} else if (elementType.getSpecialTypeName().equals(
							xsd.getXsdTimeType().getName())) {
						constr = type.getConstructor(int[].class);
						int[] params = new int[3];
						params[0] = Integer.parseInt(specialType
								.getElementsByTagName(
										xsd.getXsdTimeType().getHourName())
								.item(0).getTextContent());
						params[1] = Integer.parseInt(specialType
								.getElementsByTagName(
										xsd.getXsdTimeType().getMinuteName())
								.item(0).getTextContent());
						params[2] = Integer.parseInt(specialType
								.getElementsByTagName(
										xsd.getXsdTimeType().getSecondName())
								.item(0).getTextContent());
						return constr.newInstance(params);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return NullValue.NULL;
	}

	private void setAttributeValue(String name, DataElement value) {
		Attribute attribute = attribList.get(name);
		if (attribute == null) {
			attribute = new Attribute(name);
			if (objectList.containsKey(attribute.getClassName())) {
				registerAttribute(attribute);
			} else {
				setXmlElement(name, value);
				return;
			}
		}
		Object object = this.objectList.get(attribute.getClassName());
		try {
			attribute.getSetter().invoke(
					object,
					TypeSystem.ConvertToJavaType(value, attribute.getGetter()
							.getReturnType()));
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private DataElement getAttributeValue(String name) {
		Attribute attribute = attribList.get(name);
		if (attribute == null) {
			attribute = new Attribute(name);
			if (objectList.containsKey(attribute.getClassName())) {
				registerAttribute(attribute);
			} else {
				return getXmlElementValue(name);
			}
		}
		Object object = objectList.get(attribute.getClassName());
		try {
			Method method = attribute.getGetter();
			return TypeSystem.ConvertToDataElement(method.invoke(object));
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void addToPropertyBag(String name, DataElement value) {
		Variable var = new Variable(name, value.getClass());
		var.setValue(value);
		propertyBag.add(var);
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		Object[] vars = varList.values().toArray();
		for (int i = 0; i < vars.length; i++) {
			buff.append(vars[i].toString());
			buff.append('\n');
		}
		for (int i = 0; i < propertyBag.size(); i++) {
			Variable var = (Variable) propertyBag.get(i);
			buff.append(var.getName());
			buff.append(" ");
			buff.append(var.getValue());
			buff.append('\n');
		}
		return buff.toString();
	}
}
