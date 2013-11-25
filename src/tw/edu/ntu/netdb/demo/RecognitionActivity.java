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

public class RecognitionActivity extends Activity {
	private Context mContext;
	private ImageView mStreetImageView;
	private ImageView mLogoImageView;
	private Bitmap mDisplayedBitmap;
	private Bitmap mProcessedBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_recognition);
		setProgressBarIndeterminateVisibility(true);

		byte[] byteArray = getIntent().getByteArrayExtra("street_bitmap");
		Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		mDisplayedBitmap = bitmap;
		// get the center part of the bitmap
		if (bitmap.getHeight() > bitmap.getWidth()) {
			mProcessedBitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
					bitmap.getWidth(), bitmap.getWidth());
		}

		// resize the bitmap
		// mProcessedBitmap = Bitmap.createScaledBitmap(mProcessedBitmap,
		// mProcessedBitmap.getWidth() / 2,
		// mProcessedBitmap.getHeight() / 2, false);
		// test a default bitmap
		// mProcessedBitmap = BitmapFactory.decodeResource(getResources(),
		// R.drawable.tt5);
		mStreetImageView = (ImageView) findViewById(R.id.imageView_street);
		mStreetImageView.setImageBitmap(mDisplayedBitmap);
		mLogoImageView = (ImageView) findViewById(R.id.imageView_logos);
		new IdentifyTask().execute();

		setProgressBarIndeterminate(true);
	}

	private void displayLogo(int centerX, int centerY, int category) {
		// recycle the unused in free of out of memory
		mProcessedBitmap.recycle();
		// create a bitmap overlaying the original street view bitmap for
		// displaying results
		Bitmap bgBitmap = Bitmap.createBitmap(mDisplayedBitmap.getWidth(), mDisplayedBitmap.getHeight(),
				Bitmap.Config.ARGB_8888);
		// read the logo from res
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 20;
		Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), category, options);

		// draw the logo onto the bitmap
		Canvas canvas = new Canvas(bgBitmap);
		canvas.drawColor(Color.TRANSPARENT);
		Paint paint = new Paint();
		// paint.setColor(Color.YELLOW);
		// paint.setStrokeWidth(2);
		// paint.setStyle(Paint.Style.STROKE);
		canvas.drawBitmap(logoBitmap, centerX - logoBitmap.getWidth() / 2, (centerY - logoBitmap.getHeight() / 2)
				+ (mDisplayedBitmap.getHeight() / 2 - mDisplayedBitmap.getWidth() / 2), paint);

		mLogoImageView.setImageBitmap(bgBitmap);

		// set the animation
		final Animation scaleAnimation = new ScaleAnimation(2f, 1f, 2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
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
			return helper.identify(mProcessedBitmap);
		}

		protected void onPostExecute(Result result) {
			// DEBUG
			// display the identified region
			// Bitmap bitmap = result.getMaskBitmap();
			// mStreetImageView.setImageBitmap(bitmap);
			setProgressBarIndeterminateVisibility(false);
			// displayLogo(result.getCenterX() * 2, result.getCenterY() * 2,
			// result.getCategory().CLOGO);
			// TODO
			displayLogo(result.getCenterX(), result.getCenterY(), result.getCategory().CLOGO);
			Log.d(getClass().getName(), String.valueOf(result.getCenterX()) + String.valueOf(result.getCenterY()));
		}
	}
}