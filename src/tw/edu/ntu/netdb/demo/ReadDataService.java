package tw.edu.ntu.netdb.demo;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class ReadDataService extends IntentService {
	public ReadDataService() {
		super("ReadData");
		Log.d(getClass().getName(), "Construct Service!");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(getClass().getName(), "Start Service!");
		AppResourceManager manager = (AppResourceManager) getApplicationContext();
		manager.setCategories();
	}
}