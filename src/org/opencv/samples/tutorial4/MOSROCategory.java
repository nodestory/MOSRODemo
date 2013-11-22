package org.opencv.samples.tutorial4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class MOSROCategory {
	public int CID;
	public String CName;
	public double[] w;
	public List<double[]> voc;
	public String Info;
	public int CLOGO;
	
	public MOSROCategory(int CID, String CName, int CLOGO){
		this.CID = CID;
		this.CName = CName;
		this.CLOGO = CLOGO;
	}
	public MOSROCategory(int CID, InputStream kernelRaw, InputStream vocRaw){
		this.CID = CID;
		this.ReadFile(CID, kernelRaw, vocRaw);
	}
	public void ReadW(int CID, InputStream kernelRaw){
		int[] size = new int[2];
		try {
			// Read Kernel map
			//InputStream fin = getResources().openRawResource(R.raw.kernel_5);	
			BufferedReader br = new BufferedReader(new InputStreamReader(kernelRaw));
			String s="";
			String[] q;
			s = br.readLine();
			q = s.split(" ");
			size[0] = Integer.parseInt(q[0]);
			size[1] = Integer.parseInt(q[1]);
			s = br.readLine();
			q = s.split(" ");
			w = new double[q.length];
			for(int i=0;i<q.length;i++){
				w[i] = Double.parseDouble(q[i]);
			}
			Log.v("ReadFile","Kernel#" +String.valueOf(5) +" :\n"+ w.toString());
			Log.v("ReadFile","Read Kernel#" + String.valueOf(CID) + " sucessfully");
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void ReadVoc(int CID, InputStream vocRaw){
		voc = new ArrayList<double[]>();  
		try{
			// Read Vocabulary
			BufferedReader br = new BufferedReader(new InputStreamReader(vocRaw));
			String s="";
			String[] q;
			while((s=br.readLine())!=null){
				q = s.split(" ");
				double[] v = new double[q.length];
				for(int i=0;i<q.length;i++){
					v[i] = Double.parseDouble(q[i]);
				}
				voc.add(v);
				Log.v("ReadFile","vocabulary#" +String.valueOf(5) +" :(i)\n"+ v.toString());
			}
			Log.v("ReadFile","vocabulary#" +String.valueOf(5) +" :\n"+ voc.toString());
			Log.v("ReadFile","Read vocabulary#" + String.valueOf(CID) + " sucessfully");
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void ReadFile(int CID, InputStream kernelRaw, InputStream vocRaw){
		int[] size = new int[2];
		try {
			// Read Kernel map
			//InputStream fin = getResources().openRawResource(R.raw.kernel_5);	
			BufferedReader br = new BufferedReader(new InputStreamReader(kernelRaw));
			String s="";
			String[] q;
			s = br.readLine();
			q = s.split(" ");
			size[0] = Integer.parseInt(q[0]);
			size[1] = Integer.parseInt(q[1]);
			s = br.readLine();
			q = s.split(" ");
			w = new double[q.length];
			for(int i=0;i<q.length;i++){
				w[i] = Double.parseDouble(q[i]);
			}
			Log.v("ReadFile","Read Kernel#" + String.valueOf(CID) + " sucessfully");
			// Read Vocabulary
			br = new BufferedReader(new InputStreamReader(vocRaw));
			while((s=br.readLine())!=null){
				q = s.split(" ");
				double[] v = new double[q.length];
				for(int i=0;i<q.length;i++){
					v[i] = Double.parseDouble(q[i]);
				}
				voc.add(v);
			}
			Log.v("ReadFile","Read vocabulary#" + String.valueOf(CID) + " sucessfully");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
