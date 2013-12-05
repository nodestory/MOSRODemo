package tw.edu.ntu.netdb.demo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

public class StaticStreetViewFragment extends Fragment {
	private ImageView mStaitcStreetView;
	private ImageButton mTakeButton;

	private Bundle args;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		args = getArguments();

		View rootView = inflater.inflate(R.layout.fragment_static_streetview, null);
		mStaitcStreetView = (ImageView) rootView.findViewById(R.id.imageView_static_streetview);
		mStaitcStreetView.setImageResource(args.getInt("img_res_id"));
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
	public void onStop() {
		super.onStop();
		try {
			Drawable drawable = mStaitcStreetView.getDrawable();
			if (drawable instanceof BitmapDrawable) {
				BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
				Bitmap bitmap = bitmapDrawable.getBitmap();
				bitmap.recycle();
				System.gc();
			}
		} catch (Exception e) {
			Log.e(getClass().getName(), e.getMessage());
		}
	}
}