package tw.edu.ntu.netdb.demo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

public class RecognitionActivity extends Activity {
	private Context mContext;
	private ImageView mStreetImageView;
	private ImageView mLogoImageView;
	private Bitmap mProcessedBitmap;
	private Bitmap mDisplayedBitmap;
	private Bitmap mLogoBitmap;

	private int mCategory;

	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;
	private static int sOriginalWidth;
	private static int sOriginalHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_recognition);
		setProgressBarIndeterminateVisibility(true);

		Bundle args = getIntent().getExtras();
		if (!args.containsKey("street_bitmap")) {
			int imgResId = args.getInt("img_res_id");
			mCategory = args.getInt("category_index");
			mStreetImageView = (ImageView) findViewById(R.id.imageView_street);
			mStreetImageView.setImageResource(imgResId);
			mProcessedBitmap = BitmapFactory.decodeResource(getResources(), imgResId);
			// pseudo image for testing
			// BitmapFactory.decodeResource(getResources(), R.drawable.tt5);

		} else {
			byte[] byteArray = getIntent().getByteArrayExtra("street_bitmap");
			Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
			mStreetImageView = (ImageView) findViewById(R.id.imageView_street);
			mStreetImageView.setImageBitmap(bitmap);
			// get the center part of the bitmap
			if (bitmap.getHeight() > bitmap.getWidth()) {
				mProcessedBitmap = Bitmap.createBitmap(bitmap, 0,
						bitmap.getHeight() / 2 - bitmap.getWidth() / 2, bitmap.getWidth(),
						bitmap.getWidth());
			}
		}
		sOriginalWidth = mProcessedBitmap.getWidth();
		sOriginalHeight = mProcessedBitmap.getHeight();
		// resize the bitmap
		mProcessedBitmap = Bitmap.createScaledBitmap(mProcessedBitmap, WIDTH, HEIGHT, false);

		mLogoImageView = (ImageView) findViewById(R.id.imageView_logos);

		new IdentifyTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_recognition, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_retry:
			setResult(1);
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStop() {
		super.onStop();
		try {
			if (!mProcessedBitmap.isRecycled())
				mProcessedBitmap.recycle();
			mProcessedBitmap = null;
			if (!mDisplayedBitmap.isRecycled())
				mDisplayedBitmap.recycle();
			mDisplayedBitmap = null;
			if (!mLogoBitmap.isRecycled())
				mLogoBitmap.recycle();
			System.gc();
		} catch (Exception e) {
		}
	}
	
	@Override
	public void onBackPressed() {
		setResult(0);
		finish();
	}

	private void displayLogo(int centerX, int centerY, int category) {
		// create a bitmap overlaying the original street view bitmap for
		// displaying results
		mDisplayedBitmap = Bitmap.createBitmap(sOriginalWidth, sOriginalHeight,
				Bitmap.Config.ARGB_8888);
		// read the logo from res and resize it
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inInputShareable = true;
		// options.inSampleSize = 10;
		mLogoBitmap = BitmapFactory.decodeResource(getResources(), category, options);
		double average = (mLogoBitmap.getWidth() + mLogoBitmap.getHeight()) / 2.0;
		double threshold = 150.0;
		double ratio = average / threshold;
		mLogoBitmap = Bitmap.createScaledBitmap(mLogoBitmap,
				(int) (mLogoBitmap.getWidth() / ratio), (int) (mLogoBitmap.getHeight() / ratio),
				false);

		// draw the logo onto the bitmap
		Canvas canvas = new Canvas(mDisplayedBitmap);
		canvas.drawColor(Color.TRANSPARENT);
		Paint paint = new Paint();
		canvas.drawBitmap(mLogoBitmap, centerX - mLogoBitmap.getWidth() / 2,
				(centerY - mLogoBitmap.getHeight() / 2) + (sOriginalHeight - sOriginalWidth), paint);

		mLogoImageView.setImageBitmap(mDisplayedBitmap);

		// set the animation
		final Animation scaleAnimation = new ScaleAnimation(2f, 1f, 2f, 1f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(1000);
		mLogoImageView.setAnimation(scaleAnimation);
		mLogoImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mLogoImageView.startAnimation(scaleAnimation);
			}
		});
	}

	private class IdentifyTask extends AsyncTask<String, Void, Result> {
		protected Result doInBackground(String... params) {
			MOSROHelper helper = new MOSROHelper(mContext);
			return helper.identify(mCategory, mProcessedBitmap);
		}

		protected void onPostExecute(Result result) {
			setProgressBarIndeterminateVisibility(false);
			if (result == null) {
				Toast.makeText(getApplicationContext(), "No results!", Toast.LENGTH_SHORT).show();
			} else {
				// display the identified region for testing
				// mStreetImageView.setImageBitmap(result.getMaskBitmap());
				Log.v(getClass().getName(), "centerX: " + String.valueOf(result.getCenterX()));
				Log.v(getClass().getName(), "centerY: " + String.valueOf(result.getCenterY()));
				displayLogo(result.getCenterX() * (sOriginalWidth / WIDTH), result.getCenterY()
						* (sOriginalHeight / HEIGHT), result.getCategory().CLOGO);
			}
		}
	}
}