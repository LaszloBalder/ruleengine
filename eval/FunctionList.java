package eval;

import java.util.Map;
import java.util.TreeMap;

public class FunctionList {
	static FunctionList instance = null;
	private Map<String, FunctionDefinition> map = new TreeMap<String, FunctionDefinition>();

	private FunctionList() {

	}

	public static FunctionList getInstance() {
		if (instance == null) {
			instance = new FunctionList();
		}
		return instance;
	}

	public void addFunction(FunctionDefinition exf) {
		map.put(exf.getName(), exf);

	}

	public FunctionDefinition getFunction(String name) {
		return map.get(name);
	}

}
