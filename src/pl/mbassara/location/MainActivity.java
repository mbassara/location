package pl.mbassara.location;

import java.util.ArrayList;

import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends FragmentActivity {

	private GoogleMap theMap;
	private EditText nameEditText;
	private LocationManager locationManager;
	private LocationUpdater locationUpdater;
	private SynchronizationThread synchronizationThread;
	@SuppressWarnings("unused")
	private LatLng brzeszcze = new LatLng(49.980556, 19.151944);
	private LatLng krakow = new LatLng(50.061389, 19.938333);
	private ArrayList<OnNameChangedListener> listeners = new ArrayList<MainActivity.OnNameChangedListener>();

	public void addOnNameChangedListner(OnNameChangedListener listener) {
		listeners.add(listener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		nameEditText = (EditText) findViewById(R.id.name_editText);

		locationUpdater = new LocationUpdater();
		addOnNameChangedListner(locationUpdater);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (theMap == null) {
			theMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
					R.id.the_map)).getMap();

			if (theMap != null) {
				theMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				theMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition
						.fromLatLngZoom(krakow, 14.0f)));
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (code != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(code, this, 0);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		synchronizationThread = new SynchronizationThread(this, theMap);
		synchronizationThread.start();

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 5, 0f,
				locationUpdater);
	}

	@Override
	protected void onStop() {
		super.onStop();

		synchronizationThread.cancel();
		locationManager.removeUpdates(locationUpdater);
	}

	public void acceptPersonNameButtonOnClick(View view) {
		for (OnNameChangedListener l : listeners)
			l.nameChanged(nameEditText.getText().toString());
	}

	public interface OnNameChangedListener {
		public void nameChanged(String name);
	}

}
