package eval;

import java.math.BigDecimal;

public class Number extends DataElement implements Comparable<DataElement> {
	BigDecimal value;

	public Number(String nr) {
		value = new BigDecimal(nr);
	}

	public Number(BigDecimal nr) {
		value = nr;
	}

	public Number(int nr) {
		value = new BigDecimal(nr);
	}

	public Number(double nr) {
		value = new BigDecimal(nr);
	}

	public int compareTo(DataElement obj) {
		if (obj.getClass() == this.getClass()) {
			return value.compareTo(((Number) obj).value);
		}
		return 0;
	}
	public boolean hasValue() {
		return value != null;
	}

    @Override
	public boolean equals(Object obj) {
		if (obj.getClass() == this.getClass()) {
			return value.equals(((Number) obj).value);
		}
		return false;
	}

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
	public String toString() {
		return value.toString();
	}
}
