package tw.edu.ntu.netdb.demo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		// actionBar.setDisplayShowHomeEnabled(false);
		// actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section1)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section2)
				.setTabListener(this));

		Intent intent = new Intent(this, ReadDataService.class);
		startService(intent);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void savePicture(String filename, Bitmap bitmap, float rotateDegree) {
		try {
			String path = Environment.getExternalStorageDirectory().toString();
			File file = new File(path, filename);
			Matrix matrix = new Matrix();
			matrix.preRotate(rotateDegree);
			FileOutputStream stream = new FileOutputStream(file);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			bitmap.compress(Bitmap.CompressFormat.PNG, 20, stream);
			stream.flush();
			stream.close();

			Uri contentUri = Uri.fromFile(file);
			Intent mediaScanIntent = new Intent(
					Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			mediaScanIntent.setData(contentUri);
			sendBroadcast(mediaScanIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startRecognitionActivity(Bitmap streetBitmap) {
		if (((AppResourceManager) getApplicationContext()).hasData()) {
			Intent intent = new Intent(this, RecognitionActivity.class);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			streetBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			intent.putExtra("street_bitmap", byteArray);
			startActivity(intent);
		} else {
			Toast.makeText(this, "Please try later...", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		switch (tab.getPosition()) {
		case 0:
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, new StreetViewFragment()).commit();
			break;
		case 1:
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, new CameraFragment()).commit();
			break;

		default:
			break;
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
}