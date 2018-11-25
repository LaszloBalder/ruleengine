package eval;

public class NullValue extends DataElement {

	public static final NullValue NULL = new NullValue();
	
	private NullValue() {
	}

	@Override
	public boolean hasValue() {
		return false;
	}

	@Override
	public int compareTo(DataElement o) {
		return 0;
	}
	@Override
	public String toString() {
		return "null";
	}
    @Override
	public boolean equals(Object obj) {
		if (obj.getClass() == this.getClass()) {
			return true;
		}
		return false;
	}

}
