package tw.edu.ntu.netdb.demo;

import java.util.ArrayList;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

public class StaticStreetViewFragment extends Fragment {
	private ImageView mStaitcStreetView;
	private ImageButton mTakeButton;

	private Bundle args;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		args = getArguments();

		View rootView = inflater.inflate(R.layout.fragment_static_streetview, null);
		mStaitcStreetView = (ImageView) rootView.findViewById(R.id.imageView_static_streetview);
		mTakeButton = (ImageButton) rootView.findViewById(R.id.imageButton_take);
		mTakeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).startRecognitionActivity(args);
			}
		});
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		AppResourceManager manager = (AppResourceManager) getActivity().getApplicationContext();
		Map<LatLng, ArrayList<DemoLocation>> locations = manager.getDemoLocaions();
		DemoLocation location = locations.get((LatLng) args.get("location")).get(0);
		mStaitcStreetView.setImageResource(location.getImgResId());
		args.putInt("category_index", location.getCategorIndex());
		args.putInt("img_res_id", location.getImgResId());
	}

	@Override
	public void onStop() {
		super.onStop();
//		try {
//			Drawable drawable = mStaitcStreetView.getDrawable();
//			if (drawable instanceof BitmapDrawable) {
//				BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//				Bitmap bitmap = bitmapDrawable.getBitmap();
//				bitmap.recycle();
//				bitmap = null;
//				System.gc();
//			}
//		} catch (Exception e) {
//			Log.e(getClass().getName(), e.getMessage());
//		}
	}
}