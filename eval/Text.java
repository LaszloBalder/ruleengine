package eval;

public class Text extends DataElement implements Comparable<DataElement> {
	String value;

	public Text(String text) {
		value = text;
	}

	public String toString() {
		return value;
	}

	public int compareTo(DataElement obj) {
		if (obj.getClass() == this.getClass()) {
			return value.compareTo(((Text) obj).value);
		}
		return 0;
	}

	public boolean equals(Object obj) {
		if (obj.getClass() == this.getClass()) {
			return value.equals(((Text) obj).value);
		}
		return false;
	}

	public boolean hasValue() {
		return value != null;
	}

	public int getLength() {
		return value.length();
	}
}
