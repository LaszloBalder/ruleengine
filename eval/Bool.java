package eval;

import java.util.logging.Logger;

public class Bool extends DataElement implements Comparable<DataElement> {
	boolean value;
	boolean isNull = false;
	public static final Bool TRUE = new Bool(true);
	public static final Bool FALSE = new Bool(false);
	private static Logger logger = Logger.getLogger(Bool.class.getName());

	public Bool(String value) {
		logger.fine(value);
		// ^(\s*)(logger\.fine.*)
		// \1//\2
		if (value == null || value.isEmpty()) {
			isNull = true;
		} else {
			this.value = Boolean.parseBoolean(value);
		}
	}

	public Bool(boolean value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return Boolean.toString(value);
	}

	public int compareTo(DataElement obj) {
		logger.fine("compareTo " + obj.getClass().getName());
		// if (obj.getClass() == this.getClass()) {
		if (value == ((Bool) obj).value)
			return 0;
		else if (value)
			return 1;
		else
			return -1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() == this.getClass()) {
			return value == ((Bool) obj).value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + (this.value ? 1 : 0);
		return hash;
	}

	public boolean hasValue() {
		return !isNull;
	}
}
