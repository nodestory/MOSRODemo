package tw.edu.ntu.netdb.demo;

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
	private static final LatLng START_POSITION = new LatLng(25.01673759493102, 121.53363820882066);

	private GoogleMap mMap;
	private OnPositionClickedListener mListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	public void setOnPositionClickedListener(OnPositionClickedListener listener) {
		mListener = listener;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mMap = getMap();
		mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
				.target(START_POSITION).zoom(15.5f).bearing(0).tilt(0).build()));
		mMap.setOnMarkerClickListener(this);
		mMap.addMarker(new MarkerOptions().position(START_POSITION).title("Brisbane")
				.snippet("Population: 2,074,200")
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
	}

	// Implement OnMarkerClickListener
	@Override
	public boolean onMarkerClick(Marker marker) {
		mListener.OnPositionClicked(marker.getPosition());
		return true;
	}

	public interface OnPositionClickedListener {
		public void OnPositionClicked(LatLng lntlng);
	}
}