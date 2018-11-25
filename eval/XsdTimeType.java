package eval;

public class XsdTimeType {
	private String name;
	private String hourName;
	private String minuteName;
	private String secondName;

	public XsdTimeType(String name, String hourName, String minuteName,
			String secondName) {
		this.name = name;
		this.hourName = hourName;
		this.minuteName = minuteName;
		this.secondName = secondName;
	}

	public String getName() {
		return name;
	}

	public String getHourName() {
		return hourName;
	}

	public String getMinuteName() {
		return minuteName;
	}

	public String getSecondName() {
		return secondName;
	}

}
