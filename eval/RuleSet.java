package eval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class RuleSet {
	private String name;
	private ArrayList<Rule> rules = new ArrayList<Rule>();
	private Map<String, Rule> namedRule = new HashMap<String, Rule>();

	private Set<String> neededVars = new TreeSet<String>();

	public RuleSet(String name) {
		this.name = name;
	}

	public void addRule(Rule rule) {
		neededVars.addAll(rule.getParameters());
		rules.add(rule);
	}

	public void addRule(Rule rule, String name) {
		if (name == null || name.isEmpty()) {
			addRule(rule);
		} else {
			namedRule.put(name, rule);
			neededVars.addAll(rule.getParameters());
		}
	}

	public Rule getRule(String ruleName) {
		return namedRule.get(ruleName);
	}

	public void execute(VariableList vars) {
		// System.out.println("Executing ruleset " + name);
		for (int i = 0; i < rules.size(); i++) {
			Rule rule = (Rule) rules.get(i);
			rule.execute(vars);
		}
	}

	public String getName() {
		return name;
	}

	public Set<String> getParameters() {
		return neededVars;
	}
}

class Rule {
	private ExpressionDefintion condition;

	private Set<String> neededVars = new TreeSet<String>();

	private ArrayList<Action> actions = new ArrayList<Action>();
	private ArrayList<Action> elseActions = new ArrayList<Action>();

	public Rule(String conditionStr) {
		if (conditionStr == null || conditionStr.isEmpty()) {
			conditionStr = "true";
		}
		this.condition = new ExpressionDefintion(conditionStr);
		neededVars.addAll(condition.getParameterNames());
	}

	public Rule() {
		this.condition = new ExpressionDefintion("true");
	}

	public void addAction(Action action) {
		neededVars.addAll(action.getParameters());
		actions.add(action);
	}

	public void addElseAction(Action action) {
		neededVars.addAll(action.getParameters());
		elseActions.add(action);
	}

	public void execute(VariableList vars) {
		Expression cond = new Expression(condition);
		ArrayList<Identifier> condParams = cond.getParameters();
		for (int i = 0; i < condParams.size(); i++) {
			String varName = condParams.get(i).getName();
			Variable var = vars.getVariable(varName);
			if (var != null) {
				cond.setParameter(varName, var.getValue());
			}
		}
		Bool condAnswer = Bool.FALSE;
		try {
			condAnswer = (Bool) cond.evaluate(vars);
			// System.out.println(cond.toString());
		} catch (Exception ex) {
			System.out.println(cond.toString());
		}
		if (condAnswer.value) {
			for (int i = 0; i < actions.size(); i++) {
				Action action = actions.get(i);
				action.execute(vars);
			}
		} else {
			for (int i = 0; i < elseActions.size(); i++) {
				Action action = elseActions.get(i);
				action.execute(vars);
			}
		}
	}

	public Set<String> getParameters() {
		return neededVars;
	}

	public String toString() {
		return condition.toString();
	}
}

abstract class Action {
	protected Set<String> neededVars = new TreeSet<String>();

	protected abstract Set<String> getParameters();

	public abstract void execute(VariableList vars);
}

class AssignAction extends Action {
	ExpressionDefintion ed;

	String lhsName;

	public AssignAction(String lhsName, String expression) {
		this.lhsName = lhsName;
		ed = new ExpressionDefintion(expression);
		neededVars.addAll(ed.getParameterNames());
	}

	public void execute(VariableList vars) {
		Expression exp = new Expression(ed);
		ArrayList<Identifier> params = exp.getParameters();
		for (int i = 0; i < params.size(); i++) {
			String varName = params.get(i).getName();
			DataElement value = vars.getValue(varName);
			exp.setParameter(varName, value);
		}
		DataElement retVal = (DataElement) exp.evaluate(vars);
		vars.addVariable(lhsName, retVal);
	}

	protected Set<String> getParameters() {
		return ed.getParameterNames();
	}
}

class ExecuteAction extends Action {
	ExpressionDefintion ed;

	public ExecuteAction(String expression) {
		ed = new ExpressionDefintion(expression);
		neededVars.addAll(ed.getParameterNames());
	}

	public void execute(VariableList vars) {
		Expression exp = new Expression(ed);
		ArrayList<Identifier> params = exp.getParameters();
		for (int i = 0; i < params.size(); i++) {
			String varName = params.get(i).getName();
			DataElement value = vars.getValue(varName);
			exp.setParameter(varName, value);
		}
		exp.evaluate(vars);
	}

	protected Set<String> getParameters() {
		return ed.getParameterNames();
	}
}

class EvaluateAction extends Action {
	String decissionTableName;

	DecisionTable decissionTable;

	public EvaluateAction(DecisionTable decissionTable) {
		this.decissionTable = decissionTable;
	}

	public void execute(VariableList vars) {
		ArrayList<DataElement> arr = new ArrayList<DataElement>();
		for (int i = 0; i < decissionTable.getConditionColumns().size(); i++) {
			String name = decissionTable.getColumnName(i);
			arr.add(vars.getValue(name));
		}
		Result result = decissionTable.getResult(arr);
		if (result != null) {
			for (int i = 0; i < result.fields.size(); i++) {
				vars.setValue(decissionTable.resultColumns.get(i).toString(), result.fields.get(i));
			}
		}
	}

	protected Set<String> getParameters() {
		Set<String> params = new TreeSet<String>(decissionTable.conditionColumns);
		return params;
	}
}

class AddToBagAction extends Action {
	String name;

	DataElement value;

	public AddToBagAction(String name, String value) {
		this.name = name;
		this.value = new Text(value);
	}

	public void execute(VariableList vars) {
		if (value.getClass() == Text.class && vars.getVariable(value.toString()) != null) {
			vars.addToPropertyBag(name, vars.getVariable(value.toString()).getValue());
		} else {
			vars.addToPropertyBag(name, value);
		}
	}

	protected Set<String> getParameters() {
		return new TreeSet<String>();
	}
}

class RuleAction extends Action {
	Rule rule = null;
	String name;

	public RuleAction(Rule rule) {
		this.rule = rule;
	}

	// public RuleAction(String ruleName) {
	// this.name = ruleName;
	// }

	public void execute(VariableList vars) {
		rule.execute(vars);
	}

	protected Set<String> getParameters() {
		return new TreeSet<String>();
	}
}

class RuleSetAction extends Action {
	RuleSet ruleSet;

	public RuleSetAction(RuleSet ruleSet) {
		this.ruleSet = ruleSet;
	}

	public void execute(VariableList vars) {
		ruleSet.execute(vars);
	}

	protected Set<String> getParameters() {
		return new TreeSet<String>();
	}
}

class Variable {
	private String name;

	private Class<? extends DataElement> type;

	private DataElement value;

	private DataElement defaultValue = null;

	public Variable(String name) {
		this.name = name;
		this.value = NullValue.NULL;
	}

	public Variable(String name, Class<? extends DataElement> type, String defaultValue) {
		this.name = name;
		this.type = type;
		if (defaultValue != null && defaultValue.length() > 0) {
			try {
				this.defaultValue = type.getConstructor(String.class).newInstance(defaultValue);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public Variable(String name, Class<? extends DataElement> type) {
		this.name = name;
		this.type = type;
	}

	public void setValue(DataElement value) {
		if (type == null) {
			this.value = value;
			this.type = value.getClass();
		} else if (value.getClass().equals(type)) {
			this.value = value;
		}
	}

	public DataElement getValue() {
		return value == null ? NullValue.NULL : value;
	}

	public String getName() {
		return name;
	}

	public DataElement getDefaultValue() {
		return defaultValue;
	}

	public void setToDefault() {
		value = defaultValue;
	}

	public void setDefaultValue(DataElement defaultValue) {
		if (defaultValue.getClass().equals(type)) {
			this.defaultValue = defaultValue;
		}
	}

	public Class<? extends DataElement> getType() {
		return type;
	}

	@Override
	public String toString() {
		return name + " " + (value != null ? value.toString() : "") + " (" + type + ")";
	}
}
