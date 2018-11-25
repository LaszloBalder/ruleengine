package eval;

import java.util.ArrayList;

public abstract class DecisionRowElement {

}

class ConditionElement extends DecisionRowElement implements
		Comparable<ConditionElement> {
	private DataElement lower;

	private boolean lowerInclusive = false;

	private boolean hasLower = false;

	private DataElement upper;

	private boolean upperInclusive = false;

	private boolean hasUpper = false;

	private String compType = "";

	public ConditionElement(DataElement value) {
		lower = value;
		upper = value;
		lowerInclusive = true;
		upperInclusive = true;
		hasLower = true;
		hasUpper = true;
	}

	public ConditionElement(DataElement lower, DataElement upper) {
		this.lower = lower;
		this.upper = upper;
		lowerInclusive = true;
		upperInclusive = true;
		hasLower = true;
		hasUpper = true;
	}

	public boolean canBeQuery() {
		return (hasUpper && hasLower && (lower.compareTo(upper) == 0));
	}

	public void setCompareType(String compareType) {
		this.compType = compareType;
		if (compareType.startsWith("<")) {
			hasUpper = true;
		} else {
			hasLower = true;
		}
		if (compareType.endsWith("=")) {
			lowerInclusive = true;
			upperInclusive = true;
		}
	}

	public ConditionElement(String compareType, DataElement value) {
		this.compType = compareType;
		if (compareType.equals("<")) {
			upper = value;
			hasUpper = true;
		}
		if (compareType.equals("<=")) {
			upper = value;
			upperInclusive = true;
			hasUpper = true;
		}
		if (compareType.equals(">")) {
			lower = value;
			hasLower = true;
		}
		if (compareType.equals(">=")) {
			lower = value;
			lowerInclusive = true;
			hasLower = true;
		}
	}

	public void setValue(DataElement value) {
		upper = value;
		lower = value;
	}

	public void setLower(DataElement value) {
		lower = value;
	}

	public void setUpper(DataElement value) {
		upper = value;
	}

	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append(compType + " - ");
		buff.append(hasLower ? lower.toString() : "");
		buff.append(" - ");
		buff.append(hasUpper ? upper.toString() : "");

		return buff.toString();
	}

	public int compareTo(ConditionElement rhs) {
		if ((!hasLower && !rhs.hasLower) || (!hasUpper && !rhs.hasUpper))
			return 0;
		int result;
		if (!hasLower && !rhs.hasUpper) {
			result = upper.compareTo(rhs.lower);
			if (result == -1)
				return -1;
			if (result == 0 && (!upperInclusive || !rhs.lowerInclusive))
				return -1;
			return 0;
		}
		if (!hasUpper && !rhs.hasLower) {
			result = lower.compareTo(rhs.upper);
			if (result == 1)
				return 1;
			if (result == 0 && (!lowerInclusive || !rhs.upperInclusive))
				return 1;
			return 0;
		}
		if (hasLower && rhs.hasUpper) {
			result = lower.compareTo(rhs.upper);
			if (result > 0) {
				return 1;
			} else if (result == 0 && (!lowerInclusive || !rhs.upperInclusive)) {
				return 1;
			}
		}
		if (hasUpper && rhs.hasLower) {
			result = upper.compareTo(rhs.lower);
			if (result < 0) {
				return -1;
			} else if (result == 0 && (!upperInclusive || !rhs.lowerInclusive)) {
				return -1;
			}
		}
		return 0;
	}
}

class Result {
	ArrayList<DataElement> fields = new ArrayList<DataElement>();

	void addField(DataElement field) {
		fields.add(field);
	}
}
