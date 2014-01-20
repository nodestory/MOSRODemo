package tw.edu.ntu.netdb.demo;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class StaticStreetViewActivity extends FragmentActivity {
	private Context mContext;

	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private ImageButton mTakeButton;

	ArrayList<DemoLocation> mLocations;
	private DemoLocation mCurrentLocation;
	private Bundle args;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_static_streetview);

		mContext = this;

		args = getIntent().getExtras();
		AppResourceManager manager = (AppResourceManager) getApplicationContext();
		mLocations = manager.getDemoLocaions().get((LatLng) args.get("location"));
		mCurrentLocation = mLocations.get(0);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new StreetImagePagerAdapter(getSupportFragmentManager());
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

		mTakeButton = (ImageButton) findViewById(R.id.imageButton_take);
		mTakeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				args.putInt("category_index", mCurrentLocation.getCategorIndex());
				args.putInt("img_res_id", mCurrentLocation.getImgResId());
				if (!((AppResourceManager) getApplicationContext()).isLoading()) {
					Intent intent = new Intent(mContext, RecognitionActivity.class);
					intent.putExtras(args);
					startActivityForResult(intent, 0);
				} else {
					Toast.makeText(mContext, "Please try later...", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 0) {
			finish();
		}
	}

	private class StreetImagePagerAdapter extends FragmentStatePagerAdapter {
		public StreetImagePagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
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