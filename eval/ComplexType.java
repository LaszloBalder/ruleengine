package eval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComplexType {
	private List<String> fieldNames = new ArrayList<String>();
	private Map<String, XsdElementType> fieldList = new HashMap<String, XsdElementType>();
	private Set<String> attribList = new HashSet<String>();
	private String name;
	private String idAttribute;
	private String baseName = null;
	private List<String> references = new ArrayList<String>();
	private List<String> lists = new ArrayList<String>();
	private boolean inheritanceResolved;
	private boolean isRefType = false;
	
	public String getIdAttributeName() {
		return idAttribute;
	}
	
	public void setIdAttributeName(String idAttributeName) {
		this.idAttribute = idAttributeName;
	}

	public ComplexType(String name, String baseName) {
		this.name = name;
		this.baseName = baseName;
		this.inheritanceResolved = baseName == null || baseName.isEmpty();
	}

	public ComplexType(String name) {
		this.name = name;
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}

	public void printFields() {
		for (String name : fieldNames) {
			System.out.println(name);
		}
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
		this.inheritanceResolved = baseName == null || baseName.isEmpty();
	}

	public String getBaseName() {
		return baseName;
	}

	public boolean isInheritanceResolved() {
		return this.inheritanceResolved;
	}

	public void setInheritanceResolved(boolean inheritanceResolved) {
		this.inheritanceResolved = inheritanceResolved;
		System.out.println("check for " +this.name);
	}

	protected void insertBaseAttribues(ComplexType baseType) {
		ArrayList<String> tempList = new ArrayList<String>();
		tempList.addAll(baseType.getFields());
		tempList.addAll(this.fieldNames);
		this.fieldNames = tempList;
		for (XsdElementType attr : baseType.fieldList.values()) {
			XsdElementType clone = new XsdElementType(name, attr.getName(), attr.getType(), attr.getTypeName(), attr.getSpecialTypeName(), attr.getMaxOccurs());
			
			fieldList.put(clone.getName(), clone);
			// System.out.println(attr.getName() + " is added to: "+name);
		}
		//fieldList.putAll(baseType.fieldList);
		this.attribList.addAll(baseType.attribList);
		if (this.idAttribute == null && this.idAttribute.isEmpty()) {
			this.idAttribute = baseType.idAttribute;
		}
		this.lists.addAll(baseType.lists);
		this.references.addAll(baseType.references);
		this.inheritanceResolved = true;
	}

	public void removeListsFromRefs() {
		references.remove(lists);
	}

	protected List<String> getFields() {
		return fieldNames;
	}

	public String getName() {
		return name;
	}

	public void addAttribute(String name, Class<? extends DataElement> type,
			String xsdType, String specialXsdType, int maxOccurs) {
		fieldList.put(name, new XsdElementType(this.name, name, type,
				xsdType, specialXsdType, maxOccurs));
		fieldNames.add(name);
		if (type == null) {
			if (maxOccurs <= 1) {
				references.add(name);
			} else {
				lists.add(name);
			}
		}
	}

	public XsdElementType getXsdElementType(String attributeName) {
		return fieldList.get(attributeName);
	}

	public List<String> getReferences() {
		return references;
	}

	public List<String> getLists() {
		return lists;
	}
	
	public boolean isReferenceType() {
		return isRefType;
	}

	public String[] getSucceedingNodes(String nodeName) {
		int i = fieldNames.indexOf(nodeName);
		if (i >= 0 && i < fieldNames.size() - 1) {
			return (String[]) fieldNames.subList(i + 1, fieldNames.size())
					.toArray(new String[0]);
		}
		return new String[0];
	}
	
	public void setAttribute(String attribName) {
		attribList.add(attribName);
	}
	
	public void setRefType(boolean refType) {
		this.isRefType = refType;
	}
}
