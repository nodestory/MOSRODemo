package tw.edu.ntu.netdb.demo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import android.os.Bundle;

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
	private OnPositionClickedListener mListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		// TODO
		if (mCurrentLatLng != null) {
			mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
					.target(mCurrentLatLng).zoom(12.5f).bearing(0).tilt(0).build()));
		}
	}

	public void setOnPositionClickedListener(OnPositionClickedListener listener) {
		mListener = listener;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mMap = getMap();
		mMap.setOnMarkerClickListener(this);

		AppResourceManager manager = (AppResourceManager) getActivity().getApplicationContext();
		Map<LatLng, ArrayList<DemoLocation>> locations = manager.getDemoLocaions();
		for (LatLng latLng : locations.keySet()) {
			mMap.addMarker(new MarkerOptions()
					.position(latLng)
					.icon(BitmapDescriptorFactory
							.defaultMarker(locations.get(latLng).size() > 1 ? BitmapDescriptorFactory.HUE_BLUE
									: BitmapDescriptorFactory.HUE_RED)));
			mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
					.target(latLng).zoom(12.5f).bearing(0).tilt(0).build()));
		}
	}

	// Implement OnMarkerClickListener
	@Override
	public boolean onMarkerClick(Marker marker) {
		LatLng position = marker.getPosition();
		LatLng latLng = new LatLng(new BigDecimal(position.latitude).setScale(6,
				BigDecimal.ROUND_HALF_UP).doubleValue(), new BigDecimal(position.longitude)
				.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue());
		mCurrentLatLng = latLng;
		mListener.OnPositionClicked(latLng);
		return true;
	}

	public interface OnPositionClickedListener {
		public void OnPositionClicked(LatLng latLng);
	}
}