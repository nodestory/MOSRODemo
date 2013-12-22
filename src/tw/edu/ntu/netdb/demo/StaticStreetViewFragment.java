package tw.edu.ntu.netdb.demo;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class StaticStreetViewFragment extends Fragment {
	// private ImageView mStaitcStreetView;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private ImageButton mTakeButton;

	ArrayList<DemoLocation> mLocations;
	private DemoLocation mCurrentLocation;
	private Bundle args;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		args = getArguments();

		View rootView = inflater.inflate(R.layout.fragment_static_streetview, null);
		// mStaitcStreetView = (ImageView)
		// rootView.findViewById(R.id.imageView_static_streetview);
		mPager = (ViewPager) rootView.findViewById(R.id.pager);
		mTakeButton = (ImageButton) rootView.findViewById(R.id.imageButton_take);
		mTakeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				args.putInt("category_index", mCurrentLocation.getCategorIndex());
				args.putInt("img_res_id", mCurrentLocation.getImgResId());
				((MainActivity) getActivity()).startRecognitionActivity(args);
			}
		});
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		AppResourceManager manager = (AppResourceManager) getActivity().getApplicationContext();
		mLocations = manager.getDemoLocaions().get((LatLng) args.get("location"));
		mCurrentLocation = mLocations.get(0);
		mPagerAdapter = new StreetViewPagerAdapter(getFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mCurrentLocation = mLocations.get(position);
				Log.v(getClass().getName(), String.valueOf(mCurrentLocation.getImgResId()));
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

	@Override
	public void onStop() {
		super.onStop();
		// try {
		// Drawable drawable = mStaitcStreetView.getDrawable();
		// if (drawable instanceof BitmapDrawable) {
		// BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
		// Bitmap bitmap = bitmapDrawable.getBitmap();
		// bitmap.recycle();
		// bitmap = null;
		// System.gc();
		// }
		// } catch (Exception e) {
		// Log.e(getClass().getName(), e.getMessage());
		// }
	}

	private class StreetViewPagerAdapter extends FragmentStatePagerAdapter {
		public StreetViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return new ImageFragment(mLocations.get(position).getImgResId(),
					String.valueOf(position + 1) + " / " + String.valueOf(mLocations.size()));
		}

		@Override
		public int getCount() {
			return mLocations.size();
		}
	}

	@SuppressLint("ValidFragment")
	public class ImageFragment extends Fragment {
		private int imgResIndex;
		private String posIndicator;

		public ImageFragment() {
		}

		public ImageFragment(int imgResIndex, String posIndicator) {
			this.imgResIndex = imgResIndex;
			this.posIndicator = posIndicator;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_image, container,
					false);
			ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
			imageView.setImageResource(imgResIndex);
			TextView textView = (TextView) rootView.findViewById(R.id.textView);
			textView.setText(posIndicator);
			return rootView;
		}
	}
}