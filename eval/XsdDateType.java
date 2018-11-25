package eval;

public class XsdDateType {
	private String name;
	private String yearName;
	private String monthName;
	private String dayName;

	public XsdDateType(String name, String yearName, String monthName,
			String dayName) {
		this.name = name;
		this.yearName = yearName;
		this.monthName = monthName;
		this.dayName = dayName;
	}

	public String getName() {
		return name;
	}

	public String getYearName() {
		return yearName;
	}

	public String getMonthName() {
		return monthName;
	}

	public String getDayName() {
		return dayName;
	}

}
