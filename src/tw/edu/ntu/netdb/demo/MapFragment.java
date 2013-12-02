package tw.edu.ntu.netdb.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends SupportMapFragment implements OnMarkerClickListener {
	private GoogleMap mMap;
	private LatLng mCurrentLatLng = null;
	private Map<Marker, DemoPosition> mPositions = new HashMap<Marker, DemoPosition>();
	private OnPositionClickedListener mListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
//		mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
//				.target(mCurrentLatLng).zoom(12.5f).bearing(0).tilt(0).build()));
	}

	public void setOnPositionClickedListener(OnPositionClickedListener listener) {
		mListener = listener;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mMap = getMap();
		mMap.setOnMarkerClickListener(this);

		/*
		InputStream inStream = getResources().openRawResource(R.raw.demo_2);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
		int start = 0;
		int offset = start + 10;
		int count = 0;
		String line = "";
		try {
			while ((line = reader.readLine()) != null) {
				String[] elements = line.split(" ");
				Log.i(getClass().getName(), elements[0]);
				Log.i(getClass().getName(),
						String.valueOf(getResources().getIdentifier(
								elements[0].replace(".jpg", ""), "drawable",
								"tw.edu.ntu.netdb.demo")));
				DemoPosition position = new DemoPosition(getResources().getIdentifier(
						elements[0].replace(".jpg", ""), "drawable", "tw.edu.ntu.netdb.demo"),
						Double.parseDouble(elements[1]), Double.parseDouble(elements[2]),
						Integer.parseInt(elements[3]), Integer.parseInt(elements[4]),
						Integer.parseInt(elements[5]));
				Marker marker = mMap.addMarker(new MarkerOptions()
						.title(elements[0])
						.position(position.getLatLng())
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
				mPositions.put(marker, position);
				mCurrentLatLng = position.getLatLng();
				count++;
				if (count == offset)
					break;
			}
			mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
					.target(mCurrentLatLng).zoom(12.5f).bearing(0).tilt(0).build()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}

	// Implement OnMarkerClickListener
	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		mCurrentLatLng = mPositions.get(marker).getLatLng();
		mListener.OnPositionClicked(mPositions.get(marker));
		return true;
	}

	public interface OnPositionClickedListener {
		public void OnPositionClicked(DemoPosition position);
	}
}