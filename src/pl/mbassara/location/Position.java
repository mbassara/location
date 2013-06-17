package pl.mbassara.location;

public class Position {
	private String name;
	private double latitude;
	private double longitude;

	public Position(String name, double latitude, double longitude) {
		super();
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Position)
			return name.equals(((Position) o).getName());
		else
			return false;
	}
}
