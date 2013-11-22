package tw.edu.ntu.netdb.demo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class StreetView extends RelativeLayout implements OnClickListener,
		OnSeekBarChangeListener {
	private double lat = 25.017578;
	private double lng = 121.477497;
	private int heading = 60;
	private int fov = 90;
	// TODO
	// private int pitch = 0;
	private Bitmap streetBitmap;

	private TextView addressTextView;
	private VerticalSeekBar zoomBar;
	private ImageButton forwardButton;
	private ImageButton backButton;
	private ImageView streetImageView;

	private GestureDetector gestureDetector;

	private static final int MSG_HIDE_ADDRESS = 0;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_HIDE_ADDRESS:
				addressTextView.setVisibility(View.INVISIBLE);
				break;

			default:
				break;
			}
		}
	};

	public StreetView(Context context) {
		super(context);
	}

	public StreetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rootView = inflater.inflate(R.layout.layout_street_view, this,
				true);
		addressTextView = (TextView) rootView
				.findViewById(R.id.textView_address);
		streetImageView = (ImageView) rootView
				.findViewById(R.id.imageView_street);
		forwardButton = (ImageButton) rootView
				.findViewById(R.id.imageButton_move_forward);
		forwardButton.setOnClickListener(this);
		backButton = (ImageButton) rootView
				.findViewById(R.id.imageButton_move_back);
		backButton.setOnClickListener(this);
		zoomBar = (VerticalSeekBar) rootView.findViewById(R.id.seekBar);
		zoomBar.setEnabled(true);
		zoomBar.setOnSeekBarChangeListener(this);
		zoomBar.setProgress(fov - 20);
		gestureDetector = new GestureDetector(context, new GestureListener());
		new DownloadStreetView(true).execute();
	}

	public StreetView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public Bitmap getStreetBitmap() {
		return streetBitmap;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return true;
	}

	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {
		private static final int SWIPE_THRESHOLD = 100;
		private static final int SWIPE_VELOCITY_THRESHOLD = 100;

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			addressTextView.setVisibility(View.VISIBLE);
			handler.sendEmptyMessageDelayed(MSG_HIDE_ADDRESS, 5000);
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			boolean result = false;
			try {
				float diffY = e2.getY() - e1.getY();
				float diffX = e2.getX() - e1.getX();
				if (Math.abs(diffX) > Math.abs(diffY)) {
					if (Math.abs(diffX) > SWIPE_THRESHOLD
							&& Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffX > 0) {
							heading -= 10;
							heading = heading % 360;
							new DownloadStreetView(false).execute();
						} else {
							heading += 10;
							heading = heading % 360;
							new DownloadStreetView(false).execute();
						}
						result = true;
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			return result;
		}
	}

	private class DownloadStreetView extends AsyncTask<String, Void, Bitmap> {
		private boolean shouldUpdate;

		public DownloadStreetView(boolean shouldUpdate) {
			this.shouldUpdate = shouldUpdate;
		}

		protected Bitmap doInBackground(String... params) {
			Bitmap streetViewBmp = null;
			try {
				String url = String
						.format("http://maps.googleapis.com/maps/api/streetview?size=480x680&location=%1$s,%2$s&heading=%3$s&fov=%4$s&sensor=false",
								lat, lng, heading, fov);
				Log.d("Google Street View Image API", url);
				InputStream stream = new java.net.URL(url).openStream();
				streetViewBmp = BitmapFactory.decodeStream(stream);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return streetViewBmp;
		}

		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				streetBitmap = result;
				streetImageView.setImageBitmap(streetBitmap);
				if (shouldUpdate)
					new GetAddressTask().execute();
			}
		}
	}

	private class GetAddressTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... params) {
			String url = String
					.format("http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$s,%2$s&sensor=false&language=zh-TW",
							lat, lng);
			Log.d("Google Geocodind API", url);
			try {
				HttpRequestBase request = new HttpGet();
				request.setURI(new URI(url));
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(request);

				HttpEntity responseEntity = response.getEntity();
				JSONArray results = (new JSONObject(
						EntityUtils.toString(responseEntity)))
						.getJSONArray("results");
				JSONObject result = (JSONObject) results.get(0);
				String address = result.getString("formatted_address");
				return address;
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String address) {
			if (address != null) {
				addressTextView.setText(address.substring(5));
				handler.sendEmptyMessageDelayed(MSG_HIDE_ADDRESS, 3000);
			}
		}
	}

	// Implement OnClickListener
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.imageButton_move_forward:
			lat += 0.00003;
			lng = 1.598 * lat + 81.4994;
			new DownloadStreetView(true).execute();
			break;
		case R.id.imageButton_move_back:
			lat -= 0.00003;
			lng = 1.598 * lat + 81.4994;
			new DownloadStreetView(true).execute();
			break;
		}
	}

	// Implement OnSeekBarChangeListener
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		fov = -1 * seekBar.getProgress() + 120;
		new DownloadStreetView(false).execute();
	}
}