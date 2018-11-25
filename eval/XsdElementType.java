package eval;


public class XsdElementType {
	private String name;
	private String specialTypeName;
	private String className;
	private String typeName;
	private Class<? extends DataElement> type;
	private int maxOccurs = 1;

	public XsdElementType(String className, String name, Class<? extends DataElement> type, String typeName, String specialTypeName, int maxOccurs) {
		this.className = className;
		this.name = name;
		this.typeName = typeName;
		this.type = type;
		this.specialTypeName = specialTypeName;
		this.maxOccurs = maxOccurs;
	}

	public String getName() {
		return name;
	}
	
	public String getSpecialTypeName() {
		return specialTypeName;
	}

	public String getClassName() {
		return className;
	}

	public int getMaxOccurs() {
		return maxOccurs;
	}

	public String getTypeName() {
		return typeName;
	}
	
	public Class<? extends DataElement> getType() {
		return type;
	}
}
