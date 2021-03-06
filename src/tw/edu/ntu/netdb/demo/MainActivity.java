package tw.edu.ntu.netdb.demo;

import java.io.ByteArrayOutputStream;

import tw.edu.ntu.netdb.demo.MapFragment.OnPositionClickedListener;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener,
		OnPositionClickedListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	private ProgressDialog mDialog = null;
	private MapFragment mMapFragment;
	private CameraFragment mCameraFragment;

	// temporary array for valid views
	// private SparseArray<DemoPosition> mPositions = new
	// SparseArray<DemoPosition>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDialog = ProgressDialog.show(this, "Loading", "Please wait...");

		mMapFragment = new MapFragment();
		mMapFragment.setOnPositionClickedListener(this);
		mCameraFragment = new CameraFragment();

		final ActionBar actionBar = getActionBar();
		// actionBar.setDisplayShowHomeEnabled(false);
		// actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section1).setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section2).setTabListener(this));

		AppResourceManager manager = (AppResourceManager) getApplicationContext();
		manager.setDemoLocaions();

		// Intent intent = new Intent(this, ReadDataService.class);
		// startService(intent);
		new ReadResTask().execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// Map<String, SubMenu> subMenus = new HashMap<String, SubMenu>();
	//
	// InputStream inStream = getResources().openRawResource(R.raw.demo_3);
	// BufferedReader reader = new BufferedReader(new
	// InputStreamReader(inStream));
	// String line = "";
	// try {
	// int count = 1;
	// while ((line = reader.readLine()) != null) {
	// String[] elements = line.split(" ");
	// String fileName = elements[0];
	// int categoryIndex =
	// Integer.parseInt(fileName.split("_")[0].substring(1));
	// DemoPosition position = new DemoPosition(categoryIndex, getResources()
	// .getIdentifier(fileName.replace(".jpg", ""), "drawable",
	// "tw.edu.ntu.netdb.demo"), Double.parseDouble(elements[1]),
	// Double.parseDouble(elements[2]), Integer.parseInt(elements[3]),
	// Integer.parseInt(elements[4]), Integer.parseInt(elements[5]));
	// String key = fileName.split("_")[0] + "_" + fileName.split("_")[1];
	// if (!subMenus.containsKey(key)) {
	// subMenus.put(key, menu.addSubMenu(key));
	// }
	// MenuItem menuItem = subMenus.get(key).add(Menu.NONE, count, Menu.NONE,
	// fileName.split("_")[2]);
	// // MenuItem menuItem = menu.add(Menu.NONE, count, Menu.NONE,
	// // fileName);
	// mPositions.put(menuItem.getItemId(), position);
	// count++;
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// if (mPositions.indexOfKey(item.getItemId()) != -1) {
	// Bundle args = new Bundle();
	// args.putInt("category_index",
	// mPositions.get(item.getItemId()).getCategorIndex());
	// args.putInt("img_res_id",
	// mPositions.get(item.getItemId()).getImgResId());
	// args.putDouble("lat",
	// mPositions.get(item.getItemId()).getLatLng().latitude);
	// args.putDouble("lng",
	// mPositions.get(item.getItemId()).getLatLng().longitude);
	// startRecognitionActivity(args);
	// item.setVisible(false);
	// }
	// return super.onOptionsItemSelected(item);
	// }

	// public void savePicture(String filename, Bitmap bitmap, float
	// rotateDegree) {
	// try {
	// String path = Environment.getExternalStorageDirectory().toString();
	// File file = new File(path, filename);
	// Matrix matrix = new Matrix();
	// matrix.preRotate(rotateDegree);
	// FileOutputStream stream = new FileOutputStream(file);
	// bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
	// bitmap.getHeight());
	// bitmap.compress(Bitmap.CompressFormat.PNG, 20, stream);
	// stream.flush();
	// stream.close();
	//
	// Uri contentUri = Uri.fromFile(file);
	// Intent mediaScanIntent = new
	// Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	// mediaScanIntent.setData(contentUri);
	// sendBroadcast(mediaScanIntent);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	
	public void startRecognitionActivity(Bitmap streetBitmap) {
		if (!((AppResourceManager) getApplicationContext()).isLoading()) {
			Intent intent = new Intent(this, RecognitionActivity.class);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			streetBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			intent.putExtra("street_bitmap", byteArray);
			startActivity(intent);
		} else {
			Toast.makeText(this, "Please try later...", Toast.LENGTH_LONG).show();
		}
	}
	
	public void startRecognitionActivity(Bundle args) {
		if (!((AppResourceManager) getApplicationContext()).isLoading()) {
			Intent intent = new Intent(this, RecognitionActivity.class);
			intent.putExtras(args);
			startActivityForResult(intent, 0);
		} else {
			Toast.makeText(this, "Please try later...", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		switch (tab.getPosition()) {
		case 0:
			getSupportFragmentManager().beginTransaction().replace(R.id.container, mMapFragment)
					.commit();
			break;
		case 1:
			getSupportFragmentManager().beginTransaction().replace(R.id.container, mCameraFragment)
					.commit();
			break;

		default:
			break;
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void OnPositionClicked(LatLng latLng) {
		Bundle bundle = new Bundle();
		bundle.putParcelable("location", latLng);
		Intent intent = new Intent(this, StaticStreetViewActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, 0);
	}

	private class ReadResTask extends AsyncTask<String, String, String> {
		protected String doInBackground(String... args) {
			AppResourceManager manager = (AppResourceManager) getApplicationContext();
			manager.setCategories();
			return "";
		}

		protected void onPostExecute(String result) {
			mDialog.dismiss();
		}
	}
}