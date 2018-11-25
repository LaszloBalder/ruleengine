package eval;

public class DateTime extends DataElement implements Comparable<DataElement> {

	Date dateValue = null;
	Time timeValue = null;

	public DateTime(String value) {
		if (value.contains("T")) {
			dateValue = new Date(value.substring(0, value.indexOf('T')));
			timeValue = new Time(value.substring(value.indexOf('T') + 1));
		} else {
			dateValue = new Date(value);
			timeValue = new Time("00:00:00");
		}
	}

	@Override
	public boolean hasValue() {
		return dateValue != null || timeValue != null;
	}

	@Override
	public int compareTo(DataElement obj) {
		if (obj.getClass() == this.getClass()) {
			DateTime rhs = (DateTime) obj;
			int dateCompare = this.dateValue.compareTo(rhs.dateValue);
			if (dateCompare == 0) {
				return this.timeValue.compareTo(rhs.timeValue);
			} else {
				return dateCompare;
			}
		}
		return 0;
	}

}
