package tw.edu.ntu.netdb.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.opencv.samples.tutorial4.MOSROCategory;

import android.app.Application;
import android.util.Log;

public class AppResourceManager extends Application {
	private static final int[] INDEXES = { 5, 15, 23, 24, 25, 36, 38, 44, 45, 58, 60, 62 };
	private int[] logos = new int[INDEXES.length];
	private int[] kernels = new int[INDEXES.length];
	private int[] vocs = new int[INDEXES.length];
	private List<MOSROCategory> categories = new ArrayList<MOSROCategory>();

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
}