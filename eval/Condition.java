package eval;

import java.util.ArrayList;

public class Condition implements Comparable<Condition> {
	ArrayList<ConditionElement> columns = new ArrayList<ConditionElement>();

	public void addElement(ConditionElement condElem) {
		columns.add(condElem);
	}

	public int compareTo(Condition rhs) {
		for (int i = 0; i < columns.size(); i++) {
			int result = columns.get(i).compareTo(rhs.columns.get(i));
			if (result != 0) {
				return result;
			}
		}
		return 0;
	}

	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int i = 0; i < columns.size(); i++) {
			buff.append("(" + columns.get(i) + "), ");
		}
		return buff.toString();
	}
}
