package eval;

import util.SmallDate;

public class Date extends DataElement implements Comparable<DataElement> {
	SmallDate value;

	public Date(String text) {
		if (text == null || text.equals("")) {
			value = null;
		} else {
			try {
				if (text.charAt(2) == '-') {
					value = SmallDate.parseString(text, "dd-mm-yyyy");
				} else if (text.charAt(4) == '-') {
					value = SmallDate.parseString(text, "yyyy-mm-dd");
				} else if (text.length() == 8) {
					value = SmallDate.parseString(text, "yyyymmdd");
				} else {
					value = SmallDate.parseString(text, "yymmdd");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean hasValue() {
		return value != null;
	}

	public Date(int year, int month, int day) {
		this.value = new SmallDate(year, month, day);
	}

	public Date(int[] params) {
		if (params.length != 3)
			throw new IllegalArgumentException("Inavallid Date");
		this.value = new SmallDate(params[0], params[1], params[2]);
	}

	public Date(SmallDate date) {
		value = date;
	}

	public String toString() {
		return value.toIsoString();
	}

	public int getYear() {
		return value.getYear();
	}

	public int getMonth() {
		return value.getMonth();
	}

	public Date addMonths(int months) {
		return new Date(value.addMonths(months));
	}

	public Date addYears(int years) {
		return new Date(value.addYears(years));
	}

	public Date addDays(int days) {
		return new Date(value.addDays(days));
	}

	public int getDayOfWeek() {
		return value.getDayOfWeek();
	}

	public int getDay() {
		return value.getDay();
	}

	public int compareTo(DataElement obj) {
		if (obj.getClass() == this.getClass()) {
			return value.compareTo(((Date) obj).value);
		}
		return 0;
	}

	public boolean equals(Object obj) {
		if (obj.getClass() == this.getClass()) {
			return value.equals(((Date) obj).value);
		}
		return false;
	}
}
