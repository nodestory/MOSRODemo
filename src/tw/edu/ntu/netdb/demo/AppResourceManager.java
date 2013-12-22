package tw.edu.ntu.netdb.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.samples.tutorial4.MOSROCategory;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class AppResourceManager extends Application {
	private static final int[] INDEXES = { 5, 15, 23, 24, 25, 36, 38, 44, 45, 58, 60, 62 };
	private int[] logos = new int[INDEXES.length];
	private int[] kernels = new int[INDEXES.length];
	private int[] vocs = new int[INDEXES.length];
	private List<MOSROCategory> categories = new ArrayList<MOSROCategory>();
	private Map<LatLng, ArrayList<DemoLocation>> locations = new HashMap<LatLng, ArrayList<DemoLocation>>();

	private boolean isLoading = true;

	public boolean isLoading() {
		return isLoading;
	}

	public void setCategories() {
		for (int i = 0; i < INDEXES.length; i++) {
			logos[i] = getResources().getIdentifier("c" + String.valueOf(INDEXES[i]), "drawable",
					getApplicationContext().getPackageName());
			kernels[i] = getResources().getIdentifier("kernel_" + String.valueOf(INDEXES[i]),
					"raw", getApplicationContext().getPackageName());
			vocs[i] = getResources().getIdentifier("voc_" + String.valueOf(INDEXES[i]), "raw",
					getApplicationContext().getPackageName());
			categories.add(new MOSROCategory(INDEXES[i], "", logos[i]));
		}

		for (int c = 0; c < categories.size(); c++) {
			int[] size = new int[2];
			double[] w = new double[0];
			List<Node> voc = new ArrayList<Node>();
			try {
				// Read Kernel map
				InputStream fin = getResources().openRawResource(kernels[c]);
				BufferedReader br = new BufferedReader(new InputStreamReader(fin));
				String s = "";
				String[] q;
				s = br.readLine();
				q = s.split(" ");
				size[0] = Integer.parseInt(q[0]);
				size[1] = Integer.parseInt(q[1]);
				s = br.readLine();
				q = s.split(" ");
				w = new double[q.length];
				for (int i = 0; i < q.length; i++) {
					w[i] = Double.parseDouble(q[i]);
				}
				Log.v(getClass().getName(), "Read Kernel#" + String.valueOf(categories.get(c).CID)
						+ " sucessfully");
				// Read Vocabulary
				fin = getResources().openRawResource(vocs[c]);
				br = new BufferedReader(new InputStreamReader(fin));
				s = br.readLine();
				int k = Integer.parseInt(s);
				while ((s = br.readLine()) != null) {
					q = s.split(" ");
					double[] v = new double[q.length];
					for (int i = 0; i < q.length; i++) {
						v[i] = Double.parseDouble(q[i]);
					}
					Node node = new Node(v);
					for (int i = 0; i < k; i++) {
						s = br.readLine();
						q = s.split(" ");
						double[] vv = new double[q.length];
						for (int ii = 0; ii < q.length; ii++) {
							vv[ii] = Double.parseDouble(q[ii]);
						}
						node.addChild(new Node(vv));
					}
					voc.add(node);
				}
				Log.v(getClass().getName(),
						"Read vocabulary#" + String.valueOf(categories.get(c).CID) + " sucessfully");
			} catch (IOException e) {
				e.printStackTrace();
			}
			categories.get(c).w = w;
			categories.get(c).voc = voc;
		}

		isLoading = false;
	}

	public List<MOSROCategory> getCategories() {
		return categories;
	}

	private double computeDist(double lat_a, double lng_a, double lat_b, double lng_b) {
		float pk = (float) (180 / 3.14169);

		double a1 = lat_a / pk;
		double a2 = lng_a / pk;
		double b1 = lat_b / pk;
		double b2 = lng_b / pk;

		double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
		double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
		double t3 = Math.sin(a1) * Math.sin(b1);
		double tt = Math.acos(t1 + t2 + t3);

		return 6366000 * tt;
	}

	public void setDemoLocaions() {
		InputStream inStream = getResources().openRawResource(R.raw.demo_locations);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
		String line = "";
		try {
			while ((line = reader.readLine()) != null) {
				String[] elements = line.split(" ");
				String fileName = elements[0];
				int categoryIndex = Integer.parseInt(fileName.split("_")[0].substring(1));
				int imgResId = getResources().getIdentifier(fileName.replace(".jpg", ""),
						"drawable", "tw.edu.ntu.netdb.demo");
				DemoLocation location = new DemoLocation(categoryIndex, imgResId,
						Double.parseDouble(elements[1]), Double.parseDouble(elements[2]),
						Integer.parseInt(elements[3]), Integer.parseInt(elements[4]),
						Integer.parseInt(elements[5]));
				LatLng latLng = new LatLng(Double.parseDouble(elements[1]),
						Double.parseDouble(elements[2]));
				for (LatLng key : locations.keySet()) {
					if (computeDist(key.latitude, key.longitude, latLng.latitude, latLng.longitude) < 15
							&& locations.get(key).get(0).getCategorIndex() == categoryIndex) {
						locations.get(key).add(location);
						latLng = null;
						break;
					}
				}
				if (latLng != null) {
					locations.put(latLng, new ArrayList<DemoLocation>());
					locations.get(latLng).add(location);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Map<LatLng, ArrayList<DemoLocation>> getDemoLocaions() {
		return locations;
	}
}