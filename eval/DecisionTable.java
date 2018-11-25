package eval;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class DecisionTable {

	private String name;

	Map<Condition, Result> rows = new TreeMap<Condition, Result>();

	ArrayList<String> conditionColumns;
	int conditionCnt = 0;

	ArrayList<String> resultColumns;

	public DecisionTable(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setCondition(ArrayList<String> list) {
		// todo: if rows.size() > 0 exception
		conditionColumns = list;
		conditionCnt = conditionColumns.size();
	}

	public void setResult(ArrayList<String> list) {
		// todo: if rows.size() > 0 exception
		resultColumns = list;
	}

	public ArrayList<String> getConditionColumns() {
		return conditionColumns;
	}

	public void addRow(Condition condition, Result action) {
		rows.put(condition, action);
	}
	
	public void addRow(ArrayList<Object> row) {
		Condition cdt = new Condition();
		for (int i = 0; i < conditionCnt; i++) {
			cdt.addElement((ConditionElement)row.get(i));
		}
		Result rslt = new Result();
		for (int i = conditionCnt; i < row.size(); i++) {
			rslt.addField((DataElement)row.get(i));
		}
		rows.put(cdt, rslt);
	}

	public Result getResult(Condition condition) {
		return (Result) rows.get(condition);
	}

	public Result getResult(ArrayList<DataElement> list) {
		Condition c = new Condition();
		for (int i = 0; i < list.size(); i++) {
			ConditionElement ce2 = new ConditionElement(list
					.get(i));
			c.addElement(ce2);
		}
		return getResult(c);
	}
	
	public String getColumnName(int columnNr) {
		
		if (columnNr < conditionCnt) 
			return conditionColumns.get(columnNr);
		if (columnNr < (conditionCnt + resultColumns.size()))
			return resultColumns.get(columnNr - conditionCnt).toString();
		return "";
	}

	public void print() {
		Object[] arr = rows.keySet().toArray();
		for (int i = 0; i < arr.length; i++) {
			System.out.println(arr[i].toString());
		}
	}
}

