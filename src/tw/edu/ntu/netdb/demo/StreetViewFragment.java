package tw.edu.ntu.netdb.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class StreetViewFragment extends Fragment {
	private StreetView mStreetView;
	private ImageButton mTakeButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_street_view, null);
		mStreetView = (StreetView) rootView.findViewById(R.id.imageView_street_view);
		mTakeButton = (ImageButton) rootView.findViewById(R.id.imageButton_take);
		mTakeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).startRecognitionActivity(mStreetView.getStreetBitmap());
			}
		});
		return rootView;
	}
}