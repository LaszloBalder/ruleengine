package eval;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import util.SmallDate;

public abstract class TypeSystem {
	private static Map<String, Class<?>> nameToPrimitiveClass = new HashMap<String, Class<?>>();
	private static Set<String> numbers = new TreeSet<String>();
	private static NumberFormat nf;
	private static SimpleDateFormat sdf;
	static {
		nf = NumberFormat.getNumberInstance(Locale.US);
		nf.setGroupingUsed(false);
		sdf = new SimpleDateFormat("dd-MM-yyyy");
		nameToPrimitiveClass.put("boolean", Boolean.TYPE);
		nameToPrimitiveClass.put("byte", Byte.TYPE);
		nameToPrimitiveClass.put("char", Character.TYPE);
		nameToPrimitiveClass.put("short", Short.TYPE);
		nameToPrimitiveClass.put("int", Integer.TYPE);
		nameToPrimitiveClass.put("long", Long.TYPE);
		nameToPrimitiveClass.put("float", Float.TYPE);
		nameToPrimitiveClass.put("double", Double.TYPE);
		numbers.add("int");
		numbers.add("integer");
		numbers.add("decimal");
		numbers.add("gYear");
		numbers.add("nonNegativeInteger");
		numbers.add("positiveInteger");
	}

	// private static Map<String, Xsd> xsdList = new HashMap<String, Xsd>();

	private static Map<String, Type> xsdSpecialTypes = new TreeMap<String, Type>();

	public static void setXsdSpecialType(String name, Type type) {
		xsdSpecialTypes.put(name, type);
	}

	public static Type getXsdSpecialType(String name) {
		return xsdSpecialTypes.get(name);
	}

	public static Class<?> getPrimitiveType(String primitiveName) {
		return nameToPrimitiveClass.get(primitiveName);
	}

	public static Object ConvertToJavaType(DataElement value, Class<?> type) {
		if (value.getClass() == Number.class) {
			if (type.getSuperclass() == java.lang.Number.class) {
				Constructor<?> constr;
				try {
					constr = type.getConstructor(String.class);
					return constr.newInstance(value.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (type == long.class) {
					return Long.parseLong(value.toString());
				}
				if (type == int.class) {
					return Integer.parseInt(value.toString());
				}
				if (type == short.class) {
					return Short.parseShort(value.toString());
				}
				if (type == byte.class) {
					return Byte.parseByte(value.toString());
				}
				if (type == double.class) {
					return Double.parseDouble(value.toString());
				}
				if (type == float.class) {
					return Float.parseFloat(value.toString());
				}
				// throw new Exception("Conversion not supported");
			}
		}
		if (value.getClass() == Text.class) {
			if (type == char.class) {
				return value.toString().charAt(0);
			}
			return value.toString();
		}
		if (value.getClass() == Date.class) {
			if (type == java.util.Date.class) {
				return ((Date) value).value.toDate();
			}
			return ((Date) value).value;
		}
		if (value.getClass() == Bool.class
				&& (type == boolean.class || type == Boolean.class)) {
			return ((Bool) value).value;
		}
		return null;
	}

	public static Class<? extends DataElement> getTypeByXsdType(String xsdType,
			String prefix) {
		String baseType = xsdType;
		if (!xsdType.contains(":")) {
			prefix="";
		}
		if (baseType.compareToIgnoreCase(prefix + "string") == 0) {
			return eval.Text.class;
		}
		if (numbers.contains(baseType)) {
			return eval.Number.class;
		}
		if (baseType.compareToIgnoreCase(prefix + "bool") == 0) {
			return eval.Bool.class;
		}
		if (baseType.compareToIgnoreCase(prefix + "boolean") == 0) {
			return eval.Bool.class;
		}
		if (baseType.compareToIgnoreCase(prefix + "date") == 0) {
			return eval.Date.class;
		}
		if (baseType.compareToIgnoreCase(prefix + "time") == 0) {
			return eval.Time.class;
		}
		if (baseType.compareToIgnoreCase(prefix + "dateTime") == 0) {
			return eval.DateTime.class;
		}
		return null;
	}

	public static DataElement ConvertToDataElement(Object value) {
		if (value == null) {
			return NullValue.NULL;
		}
		if (value.getClass().getSuperclass() == eval.DataElement.class) {
			return (DataElement) value;
		}
		String valueString = "";
		Class<? extends DataElement> type = null;
		if (value.getClass().getSuperclass() == java.lang.Number.class) {
			valueString = nf.format(value);
			type = Number.class;
		}
		if (value.getClass() == java.util.Date.class) {
			valueString = sdf.format(value);
			type = Date.class;
		}
		if (value.getClass() == util.SmallDate.class) {
			return new Date((SmallDate) value);
		}
		if (value.getClass() == java.lang.String.class) {
			valueString = value.toString();
			type = Text.class;
		}
		if (value.getClass() == java.lang.Boolean.class) {
			valueString = value.toString();
			type = Bool.class;
		}
		Constructor<?> constr;
		try {
			constr = type.getConstructor(String.class);
			return (DataElement) constr.newInstance(valueString);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
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
}




