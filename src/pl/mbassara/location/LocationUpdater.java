package pl.mbassara.location;

import pl.mbassara.location.MainActivity.OnNameChangedListener;
import pl.mbassara.location.server.Server;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LocationUpdater implements LocationListener, OnNameChangedListener {

	private String ownName = null;

	@Override
	public void onLocationChanged(Location location) {
		System.out.println(ownName + ": " + location.getLatitude() + " X "
				+ location.getLongitude());
		if (ownName != null)
			Server.sendPosition(new Position(ownName, location.getLatitude(), location
					.getLongitude()));
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void nameChanged(String name) {
		ownName = name;
	}

}
