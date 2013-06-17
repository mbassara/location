package pl.mbassara.location;

import java.util.ArrayList;
import java.util.List;

import pl.mbassara.location.server.Server;
import android.app.Activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SynchronizationThread extends Thread {

	private Activity context;
	private GoogleMap theMap;
	private boolean isRunning;

	private ArrayList<Marker> markers;

	public SynchronizationThread(Activity context, GoogleMap theMap) {
		this.context = context;
		this.theMap = theMap;
		this.markers = new ArrayList<Marker>();

		setName("SynchronizationThread");
	}

	@Override
	public void run() {
		isRunning = true;
		while (isRunning) {

			final List<Position> positions = Server.getPositions();
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					for (Marker m : markers)
						m.remove();

					markers.clear();

					for (Position p : positions) {
						Marker marker = theMap.addMarker(new MarkerOptions()
								.position(new LatLng(p.getLatitude(), p.getLongitude()))
								.title(p.getName())
								.icon(BitmapDescriptorFactory.fromResource(R.drawable.teardrop)));
						markers.add(marker);
					}

					// fixZoom(theMap, markers);
				}
			});

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(getName() + " has finished");
	}

	public void cancel() {
		isRunning = false;
	}

	private void fixZoom(GoogleMap theMap, List<Marker> markers) {
		LatLngBounds.Builder bc = new LatLngBounds.Builder();

		for (Marker item : markers) {
			bc.include(item.getPosition());
		}

		theMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
	}
}
