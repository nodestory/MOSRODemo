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
	private boolean hasData = false;
	
	private List<MOSROCategory> categories = new ArrayList<MOSROCategory>();

	public boolean hasData() {
		return hasData;
	}
	
	public void setCategories() {
		hasData = false;
		categories.add(new MOSROCategory(5, "7-Eleven", R.drawable.c5));
		categories.add(new MOSROCategory(6, "FamilyMart", R.drawable.c6));
		categories.add(new MOSROCategory(7, "Hi-Life", R.drawable.c7));
		categories.add(new MOSROCategory(8, "OK", R.drawable.c8));
		// category.add(new MOSROCategory(24, "Aurora"));
		// category.add(new MOSROCategory(38, "ChangHua"));
		// category.add(new MOSROCategory(62, "YungChing"));
		List<Integer> RawKernel = new ArrayList<Integer>();
		RawKernel.add(R.raw.kernel_5);
		RawKernel.add(R.raw.kernel_6);
		RawKernel.add(R.raw.kernel_7);
		RawKernel.add(R.raw.kernel_8);
		// RawKernel.add(R.raw.kernel_24);
		// RawKernel.add(R.raw.kernel_38);
		// RawKernel.add(R.raw.kernel_62);
		List<Integer> RawVoc = new ArrayList<Integer>();
		RawVoc.add(R.raw.voc_5);
		RawVoc.add(R.raw.voc_6);
		RawVoc.add(R.raw.voc_7);
		RawVoc.add(R.raw.voc_8);
		// RawVoc.add(R.raw.voc_24);
		// RawVoc.add(R.raw.voc_38);
		// RawVoc.add(R.raw.voc_62);
		for (int c = 0; c < categories.size(); c++) {
			int[] size = new int[2];
			double[] w = new double[0];
			List<double[]> voc = new ArrayList<double[]>();
			try {
				// Read Kernel map
				InputStream fin = getResources().openRawResource(
						RawKernel.get(c));
				BufferedReader br = new BufferedReader(new InputStreamReader(
						fin));
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
				Log.v(getClass().getName(),
						"Read Kernel#" + String.valueOf(categories.get(c).CID)
								+ " sucessfully");
				// Read Vocabulary
				fin = getResources().openRawResource(RawVoc.get(c));
				br = new BufferedReader(new InputStreamReader(fin));
				while ((s = br.readLine()) != null) {
					q = s.split(" ");
					double[] v = new double[q.length];
					for (int i = 0; i < q.length; i++) {
						v[i] = Double.parseDouble(q[i]);
					}
					voc.add(v);
				}
				Log.v(getClass().getName(),
						"Read vocabulary#"
								+ String.valueOf(categories.get(c).CID)
								+ " sucessfully");
			} catch (IOException e) {
				e.printStackTrace();
			}
			categories.get(c).w = w;
			categories.get(c).voc = voc;
		}
		hasData = true;
	}
	
	public List<MOSROCategory> getCategories() {
		return categories;
	}
}