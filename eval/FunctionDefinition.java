package eval;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class FunctionDefinition {
	private String name;
	private String qName;
	private String fullClassName;
	private String methodName;
	private String className = null;

	private ArrayList<Class<? extends Object>> typeList = new ArrayList<Class<? extends Object>>();

	private Class<DataElement>[] types = null;

	public FunctionDefinition(String fullMethodName) {
		if (fullMethodName.indexOf('.') != fullMethodName.lastIndexOf('.')) {
			fullClassName = fullMethodName.substring(0, fullMethodName
					.lastIndexOf('.'));
			name = fullClassName.substring(fullClassName.lastIndexOf('.') + 1)
					+ fullMethodName.substring(fullMethodName.lastIndexOf('.'));
			methodName = fullMethodName.substring(fullMethodName
					.lastIndexOf('.') + 1);
			className = fullClassName
					.substring(fullClassName.lastIndexOf('.') + 1);
		}
	}

	public FunctionDefinition(String name, String qName) {
		this(qName + '.' + name);
	}

	public String getName() {
		return name;
	}

	public String getQualifiedName() {
		return qName;
	}

	public void addParameter(Class<? extends Object> paramType) {
		typeList.add(paramType);
	}

	public void addParameter(String paramType) {
		Class<?> type = TypeSystem.getPrimitiveType(paramType);
		if (type == null) {
			try {
				type = Class.forName(paramType);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		addParameter(type);
	}

	@SuppressWarnings("unchecked")
	private Class[] getTypes() {
		if (types == null) {
			types = new Class[typeList.size()];
			for (int i = 0; i < typeList.size(); i++) {
				types[i] = (Class) typeList.get(i);
			}
		}
		return types;
	}

	public DataElement execute(Object target, ArrayList<DataElement> params,
			VariableList varList) {
		// if (target.getClass() == NullValue.class) {
		// return (NullValue)target;
		// }
		Class<? extends Object> c;
		try {
			c = Class.forName(fullClassName);
			Object onObject = null;
			Method m;
			try {
				m = c.getMethod(methodName, getTypes());
			} catch (NoSuchMethodException nsme) {
				return NullValue.NULL;
			}
			if (!Modifier.isStatic(m.getModifiers())) {
				if (className != null && varList != null
						&& varList.getObject(className) != null) {
					onObject = varList.getObject(className);
				} else {
					onObject = target;
				}
			}
			ArrayList<Object> paramValues = new ArrayList<Object>();
			for (int i = 0; i < getTypes().length; i++) {
				paramValues.add(TypeSystem.ConvertToJavaType(params.get(i),
						getTypes()[i]));
			}
			Object returnValue = m.invoke(onObject, paramValues.toArray());
			if (m.getReturnType() == void.class) {
				return null;
			} else {
				return TypeSystem.ConvertToDataElement(returnValue);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return NullValue.NULL;
	}

}
