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

	private int mCategory;

	private static final int SCALE = 3;
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
			sOriginalWidth = mProcessedBitmap.getWidth();
			sOriginalHeight = mProcessedBitmap.getHeight();
			Log.d(getClass().getName(), String.valueOf(sOriginalWidth));
			Log.d(getClass().getName(), String.valueOf(sOriginalHeight));
			// resize the bitmap
			mProcessedBitmap = Bitmap.createScaledBitmap(mProcessedBitmap, sOriginalWidth / SCALE,
					sOriginalHeight / SCALE, false);
			mLogoImageView = (ImageView) findViewById(R.id.imageView_logos);
		} else {
			byte[] byteArray = getIntent().getByteArrayExtra("street_bitmap");
			Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
			// get the center part of the bitmap
			if (bitmap.getHeight() > bitmap.getWidth()) {
				mProcessedBitmap = Bitmap.createBitmap(bitmap, 0,
						bitmap.getHeight() / 2 - bitmap.getWidth() / 2, bitmap.getWidth(),
						bitmap.getWidth());
			}
			mStreetImageView = (ImageView) findViewById(R.id.imageView_street);
			mLogoImageView = (ImageView) findViewById(R.id.imageView_logos);
		}

		new IdentifyTask().execute();
	}

	private void displayLogo(int centerX, int centerY, int category) {
		// recycle the unused in free of out of memory
		mProcessedBitmap.recycle();
		// create a bitmap overlaying the original street view bitmap for
		// displaying results
		Bitmap bgBitmap = Bitmap.createBitmap(sOriginalWidth, sOriginalHeight,
				Bitmap.Config.ARGB_8888);
		// read the logo from res
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 10;
		Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), category, options);
		int scale = 200 / Math.max(logoBitmap.getWidth(), logoBitmap.getHeight());
		logoBitmap = Bitmap.createScaledBitmap(logoBitmap, logoBitmap.getWidth() * scale,
				logoBitmap.getHeight() * scale, false);

		// draw the logo onto the bitmap
		Canvas canvas = new Canvas(bgBitmap);
		canvas.drawColor(Color.TRANSPARENT);
		Paint paint = new Paint();
		canvas.drawBitmap(logoBitmap, centerX - logoBitmap.getWidth() / 2,
				(centerY - logoBitmap.getHeight() / 2) + (sOriginalHeight - sOriginalWidth), paint);

		mLogoImageView.setImageBitmap(bgBitmap);

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
				Log.d(getClass().getName(), String.valueOf(result.getCenterX()));
				Log.d(getClass().getName(), String.valueOf(result.getCenterY()));
				Log.d(getClass().getName(), String.valueOf(result.getCategory().CLOGO));
				displayLogo(result.getCenterX() * SCALE, result.getCenterY() * SCALE,
						result.getCategory().CLOGO);
			}
		}
	}
}