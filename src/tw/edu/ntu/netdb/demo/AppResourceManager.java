package tw.edu.ntu.netdb.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.opencv.samples.tutorial4.MOSROCategory;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class AppResourceManager extends Application {
	private static final String PREF_NAME = "MOSRODEMO";

	private static final String[] CATEGORY_NAMES = { "7-Eleven", "FamilyMart", "Hi-Life" };
	private static final int[] CATEGORY_LOGOS = { R.drawable.c5, R.drawable.c6, R.drawable.c7 };
	private static final int[] CATEGORY_KERNELS = { R.raw.kernel_5, R.raw.kernel_6, R.raw.kernel_7 };
	private static final int[] CATEGORY_VOCS = { R.raw.voc_5, R.raw.voc_6, R.raw.voc_7 };

	private boolean isLoading = true;
	private List<MOSROCategory> categories = new ArrayList<MOSROCategory>();

	public boolean isLoading() {
		return isLoading;
	}

	public void setCategories() {
		for (int i = 0; i < CATEGORY_NAMES.length; i++) {
			categories.add(new MOSROCategory(i + 5, CATEGORY_NAMES[i], CATEGORY_LOGOS[i]));
		}

		SharedPreferences prefs = getSharedPreferences(PREF_NAME, 0);
		for (int i = 0; i < CATEGORY_NAMES.length; i++) {
			// read kernel
			if (!prefs.contains(CATEGORY_NAMES[i] + ".kernel")) {
				Log.i(getClass().getName(), "start reading kernel for the first time - "
						+ CATEGORY_NAMES[i]);
				Editor prefEditor = prefs.edit();
				InputStream inStream = getResources().openRawResource(CATEGORY_KERNELS[i]);
				BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
				String line = "";
				String fileString = "";
				try {
					while ((line = reader.readLine()) != null) {
						fileString += line + "\n";
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				prefEditor.putString(CATEGORY_NAMES[i] + ".kernel", fileString);
				prefEditor.commit();
				Log.i(getClass().getName(), "finish reading kernel for the first time - "
						+ CATEGORY_NAMES[i]);
			}

			// read voc
			if (!prefs.contains(CATEGORY_NAMES[i] + ".voc")) {
				Log.i(getClass().getName(), "start reading voc for the first time - "
						+ CATEGORY_NAMES[i]);
				Editor prefEditor = prefs.edit();
				InputStream inStream = getResources().openRawResource(CATEGORY_VOCS[i]);
				BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
				String line = "";
				String fileString = "";
				try {
					while ((line = reader.readLine()) != null) {

						fileString += line + "\n";
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				prefEditor.putString(CATEGORY_NAMES[i] + ".voc", fileString);
				prefEditor.commit();
				Log.i(getClass().getName(), "finish reading voc for the first time - "
						+ CATEGORY_NAMES[i]);
			}
		}

		for (int i = 0; i < CATEGORY_NAMES.length; i++) {
			// read kernel
			Log.i(getClass().getName(), "start reading kernel - " + CATEGORY_NAMES[i]);
			String fileString = prefs.getString(CATEGORY_NAMES[i] + ".kernel", "");
			String[] lines = fileString.split("\n");

			int[] size = new int[2];
			int index = 0;
			for (String str : lines[0].split(" ")) {
				size[index] = Integer.parseInt(str);
				index++;
			}

			String[] values = lines[1].split(" ");
			double[] w = new double[values.length];
			index = 0;
			for (int j = 0; j < values.length; j++) {
				w[index] = Double.parseDouble(values[j]);
				index++;
			}
			Log.i(getClass().getName(), "finish reading kernel - " + CATEGORY_NAMES[i]);

			// read voc
			Log.i(getClass().getName(), "start reading voc - " + CATEGORY_NAMES[i]);
			List<Node> voc = new ArrayList<Node>();
			fileString = prefs.getString(CATEGORY_NAMES[i] + ".voc", "");
			lines = fileString.split("\n");

			int k = Integer.parseInt(lines[0]);
			int lineNum = 0;
			while (lineNum < lines.length - 1) {
				lineNum++;
				String[] q = lines[lineNum].split(" ");
				double[] v = new double[q.length];
				for (int j = 0; j < q.length; j++) {
					v[j] = Double.parseDouble(q[j]);
				}

				Node node = new Node(v);
				for (int j = 0; j < k; j++) {
					lineNum++;
					if (lineNum == lines.length) {
						break;
					}
					q = lines[lineNum].split(" ");
					double[] vv = new double[q.length];
					for (int ii = 0; ii < q.length; ii++) {
						vv[ii] = Double.parseDouble(q[ii]);
					}
					node.addChild(new Node(vv));
				}
				voc.add(node);
			}
			Log.i(getClass().getName(), "finish reading voc - " + CATEGORY_NAMES[i]);

			categories.get(i).w = w;
			categories.get(i).voc = voc;
		}
		isLoading = false;
	}

	public List<MOSROCategory> getCategories() {
		return categories;
	}
}