package eval;

import java.lang.reflect.Method;
import java.util.ArrayList;

public abstract class ExpressionElement {
}

class Block extends ExpressionElement {
	char ch;
	public static final Block START = new Block('(');
	public static final Block SEPERATOR = new Block(',');
	public static final Block END = new Block(')');

	public Block(char ch) {
		this.ch = ch;
	}

	public String toString() {
		return new Character(ch).toString();
	}

	public boolean equals(Object obj) {
		if (obj.getClass() == this.getClass()) {
			return ch == ((Block) obj).ch;
		}
		return false;
	}
}

class Identifier extends ExpressionElement {
	String name;

	public Identifier(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean equals(Object obj) {
		return name.equals(obj);
	}
}

class Attribute extends Identifier {
	private String className;
	private String attName;
	private Method getter = null;
	private Method setter = null;

	public Attribute(String name) {
		super(name);
		String[] arr = name.split("\\.", 2);
		if (arr.length > 0)
			className = arr[0];
		if (arr.length > 1)
			attName = arr[1];
	}

	public void setGetter(Method getter) {
		this.getter = getter;
	}

	public Method getGetter() {
		return getter;
	}

	public void setSetter(Method setter) {
		this.setter = setter;
	}

	public Method getSetter() {
		return setter;
	}

	public String getClassName() {
		return className;
	}

	public String getAttributeName() {
		return attName;
	}
}

class Function extends ExpressionElement {
	String name;

	public Function(String name) {
		this.name = name;
	}

	public String toString() {
		return "[" + name + "]";
	}

	public String getName() {
		return name;
	}
}

class List extends DataElement implements Comparable<DataElement> {
	ArrayList<DataElement> value;

	public List(ArrayList<DataElement> list) {
		value = list;
	}

	public int compareTo(DataElement obj) {
		return 0;
	}
	public boolean hasValue() {
		return value != null;
	}

	public String toString() {
		StringBuilder bld = new StringBuilder();
		for (int i = 0; i < value.size(); i++) {
			bld.append(i == 0 ? "(" : ",");
			bld.append(value.get(i).toString());
		}
		bld.append(")");
		return bld.toString();
	}
}

class XmlElement extends Identifier {
	private String className;
	private String attName;
	private String xsdType = null;
	private Class<? extends DataElement> type = null;

	public XmlElement(String name, Class<? extends DataElement> type,
			String xsdType) {
		super(name);
		this.type = type;
		this.xsdType = xsdType;
		String[] arr = name.split("\\.", 2);
		if (arr.length > 0)
			className = arr[0];
		if (arr.length > 1)
			attName = arr[1];
	}

	public XmlElement(String name, Class<? extends DataElement> type) {
		this(name, type, null);
	}

	public String getXsdType() {
		return xsdType;
	}

	public Class<? extends DataElement> getType() {
		return type;
	}

	public String getClassName() {
		return className;
	}

	public String getAttributeName() {
		return attName;
	}
}