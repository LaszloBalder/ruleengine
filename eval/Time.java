package eval;

public class Time extends DataElement implements Comparable<DataElement> {

	private util.Time value;

	public Time(String time) {
		value = util.Time.parseTime(time);
	}

	public Time(int hours, int minutes, int seconds) {
		this.value = new util.Time(hours, minutes, seconds);
	}

	public Time(int[] params) {
		if (params.length != 3)
			throw new IllegalArgumentException("Inavallid Time");
		this.value = new util.Time(params[0], params[1], params[2]);
	}

	public int compareTo(DataElement obj) {
		if (obj.getClass() != this.getClass()) {
			return 0;
		}
		return value.compareTo(((Time) obj).value);
	}

	public int getHours() {
		return value.getHours();
	}

	public int getMinutes() {
		return value.getMinutes();
	}
	public boolean hasValue() {
		return value != null;
	}

	public int getSeconds() {
		return value.getSeconds();
	}

	public String toString() {
		return value.toString();
	}
}
