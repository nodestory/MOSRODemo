package org.opencv.samples.tutorial4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import tw.edu.ntu.netdb.demo.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Sample4Mixed extends Activity {
    private static final String TAG                = "Sample::Activity";

    public static final int     VIEW_MODE_RGBA     = 0;
    public static final int     VIEW_MODE_GRAY     = 1;
    public static final int     VIEW_MODE_CANNY    = 2;
    public static final int     VIEW_MODE_FEATURES = 5;

    private MenuItem            mItemPreviewRGBA;
    private MenuItem            mItemPreviewGray;
    private MenuItem            mItemPreviewCanny;
    private MenuItem            mItemPreviewFeatures;

    public static int           viewMode           = VIEW_MODE_RGBA;
    public Context context ;
    static int imageID = R.drawable.tt5;
    
    // MOSRO ��啁� parameter
    //int[] GridSize = {20, 20};

    public Sample4Mixed() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Log.i(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentView(new Sample4View(this));
        setContentView(R.layout.new_file);
        
        ImageView image = (ImageView) findViewById(R.id.imageView1);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), imageID);
        image.setImageBitmap(bmp);
        TextView text= (TextView)findViewById(R.id.textView1);
        text.setText("balabala");
        
        image.setClickable(true);
        image.setOnClickListener(new OnClickListener(){
        	//@Override
            public void onClick(View v) {
                Log.d("== My activity ===","OnClick is called");
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), imageID);
                Mat mat_rgb = new Mat(bmp.getHeight() , bmp.getWidth(), CvType.CV_8UC4);
                mat_rgb = Utils.bitmapToMat(bmp);
                // MOSRO params (See class MOSROParams.java)
                int[] GridSize = {20, 20};
                Point GPSLocation = new Point(25.079638,121.582205);
                // MOSRO Recognition
                MOSROReturn output = MOSRO_Recognize(mat_rgb, 
                		new MOSROParams(GridSize, GPSLocation));
                // compute mask and savetoFile
                //String path = Environment.getExternalStorageDirectory().toString();
    			//File file = new File(path, "demo.jpg");
                //File myDir=new File("/sdcard/MOSRO");                
                //myDir.mkdirs();                
                boolean[][] Mask;
                double max_pos = 0;
                int max_c = -1;
                int size = output.category.size();                
                for (int c=0;c<size;c++){
                	double pos = output.PosGrid(c);
                	if (pos > max_pos){
                		max_pos = pos;
                		max_c = c;
                	}
                }   
                // compute mask
            	//Mask = MOSRO_Mask(output.y[max_c], GridSize, mat_rgb.cols(), mat_rgb.rows());
                Mask = MOSRO_Mask(output.MostConnectedComponent(max_c, GridSize), GridSize, mat_rgb.cols(), mat_rgb.rows());
                // output LOGO
                //output.category.get(max_c).CLOGO;
                // output image
                for(int i=0;i<Mask.length;i++){
                	for(int j=0;j<Mask[i].length;j++){
                		if(Mask[i][j]==false){
                			Core.circle(mat_rgb, new Point(i,j), 0, new Scalar(0,0,0,255));
                		}
                	}
                }
                // show image
                Bitmap resultbmp = Bitmap.createBitmap(mat_rgb.cols(),  mat_rgb.rows(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat_rgb,resultbmp);
                ((ImageView)v).setImageBitmap(resultbmp);
                TextView text= (TextView)findViewById(R.id.textView1);
                text.setText(output.category.get(0).CName);
            }
        });
    }
    
    //////////////////////////////////////////////// MOSRO ///////////////////////////////////////////////////////
    public MOSROReturn MOSRO_Recognize(Mat mat_rgb, MOSROParams params){
        int[] GridSize = params.GridSize;
        //Point Location = params.Location;
        Mat mat_gray = new Mat(); 
        Imgproc.cvtColor(mat_rgb, mat_gray, Imgproc.COLOR_RGB2GRAY, 1);
        Mat keypoints = new Mat();
        Mat descriptors = new Mat();
                  
        FindFeatures(mat_gray.getNativeObjAddr(), mat_rgb.getNativeObjAddr(), 
        		keypoints.getNativeObjAddr(), descriptors.getNativeObjAddr());
        Log.v("EndFindFeature",mat_gray.toString());
        Log.v("EndFindFeature",mat_rgb.toString());
        Log.v("EndFindFeature",keypoints.toString());
        Log.v("EndFindFeature",descriptors.toString());

        // Read File
        ///////////////////////////////////////////////////////////////////////////////////////
        //MOSROCategory[] category = new MOSROCategory[cNum];
        List<MOSROCategory> category = new ArrayList<MOSROCategory>();
        category.add(new MOSROCategory(5, "7-Eleven", R.drawable.c5));
        category.add(new MOSROCategory(6, "FamilyMart", R.drawable.c6));
        category.add(new MOSROCategory(7, "Hi-Life", R.drawable.c7));
        category.add(new MOSROCategory(8, "OK", R.drawable.c8));
        //category.add(new MOSROCategory(24, "Aurora"));
        //category.add(new MOSROCategory(38, "ChangHua"));
        //category.add(new MOSROCategory(62, "YungChing"));
        List<Integer> RawKernel = new ArrayList<Integer>();
        RawKernel.add(R.raw.kernel_5);
        RawKernel.add(R.raw.kernel_6);
        RawKernel.add(R.raw.kernel_7);
//        RawKernel.add(R.raw.kernel_8);
        //RawKernel.add(R.raw.kernel_24);
        //RawKernel.add(R.raw.kernel_38);
        //RawKernel.add(R.raw.kernel_62);
        List<Integer> RawVoc = new ArrayList<Integer>();
        RawVoc.add(R.raw.voc_5);
        RawVoc.add(R.raw.voc_6);
        RawVoc.add(R.raw.voc_7);
//        RawVoc.add(R.raw.voc_8);
        //RawVoc.add(R.raw.voc_24);
        //RawVoc.add(R.raw.voc_38);
        //RawVoc.add(R.raw.voc_62);
        int cNum = category.size();
		///////////////////////////////////////////////////////////////////////////////////////
        for(int c=0; c<cNum; c++){
        	int[] size = new int[2];
        	double[] w = new double[0];
        	List<double[]> voc = new ArrayList<double[]>(); 
        	try {
        		// Read Kernel map
        		//InputStream fin = getResources().openRawResource(R.raw.kernel_5);	
				InputStream fin = getResources().openRawResource(RawKernel.get(c));
        		BufferedReader br = new BufferedReader(new InputStreamReader(fin));
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
        		Log.v("ReadFile","Read Kernel#" + String.valueOf(category.get(c).CID) + " sucessfully");
        		// Read Vocabulary
        		//fin = getResources().openRawResource(R.raw.voc_5);	
        		fin = getResources().openRawResource(RawVoc.get(c));
        		br = new BufferedReader(new InputStreamReader(fin));
        		while((s=br.readLine())!=null){
        			q = s.split(" ");
        			double[] v = new double[q.length];
        			for(int i=0;i<q.length;i++){
        				v[i] = Double.parseDouble(q[i]);
        			}
        			voc.add(v);
        		}
        		Log.v("ReadFile","Read vocabulary#" + String.valueOf(category.get(c).CID) + " sucessfully");
        	} catch (IOException e) {
        		Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        		e.printStackTrace();
        	}
    		category.get(c).w = w;
    		//TODO
//    		category.get(c).voc = voc;
        }
		///////////////////////////////////////////////////////////////////////////////////////
        boolean[][] output = new boolean[cNum][];
        for(int c=0; c<cNum; c++){
        	// transform to regional BoVW
        	int NGrid = GridSize[0]*GridSize[1];
        	int NVoc = category.get(c).voc.size();
        	int[][] hist = new int[NGrid][];
        	// init BoVW histogram
        	for(int i=0;i<NGrid;i++){
        		hist[i] = new int[NVoc];
        		for(int j=0;j<NVoc;j++){
        			hist[i][j]=0;
        		}
        	}
        	Log.v("MOSRO", "start transform to regional BoVW histogram for category#" + String.valueOf(category.get(c).CID));
        	// construct
        	for (int i=0;i<keypoints.rows();i++){
        		int j = (int) keypoints.row(i).total();
        		float[] krow = new float [j]; 
        		//keypoints.depth() == CvType.CV_32F
        		keypoints.row(i).get(0, 0, krow);						
        		int grid = Grid(GridSize, (int)krow[0], (int)krow[1], mat_rgb.cols(),  mat_rgb.rows());
        		int k = (int) descriptors.row(i).total();
        		float[] drow = new float[k];
        		descriptors.row(i).get(0, 0, drow);
//        		TODO
//        		int v = FindVoc(drow,category.get(c).voc);
//        		hist[grid][v]++;
        	}
        	Log.v("MOSRO", "transform to regional BoVW histogram for category#"+String.valueOf(category.get(c).CID)+" succuessfully");
		
        	Log.v("MOSRO", "start do MOSRO recognition for category#" + String.valueOf(category.get(c).CID));
        	output[c] = MOSRO(category.get(c).w, hist);
        	Log.v("MOSRO", "do MOSRO recognition for category#" + String.valueOf(category.get(c).CID) +" succuessfully");
        	//return y;
        }
        //return output;
        return new MOSROReturn(category, output);
    }
    // Do MOSRO Recognition
    private boolean[] MOSRO(double[] w, int[][] hist){
    	int grid_num = hist.length;
    	double th = 0;
    	boolean[] y = new boolean[grid_num];
    	for(int i=0;i<grid_num;i++){
    		double val = kernel_dot(w, hist[i]);
    		if(val > th){
    			y[i] = true;
    		}else{
    			y[i] = false;
    		}
    	}
    	return y;
    }
    private double kernel_dot(double[] w, int[] hist){
    	int size = w.length;
    	double val = 0;
    	for(int i=0;i<size;i++){
    		val += (w[i]*(hist[i]+0.0));
    	}
    	return val;
    }
    // output mask image
    private boolean[][] MOSRO_Mask(boolean[] y, int[] GridSize, int sizeh, int sizew){
    	boolean[][] img = new boolean[sizeh][];
    	for(int i=0;i<sizeh;i++){
    		img[i] = new boolean[sizew];
    		for(int j=0;j<sizew;j++){
    			int grid = Grid(GridSize, i, j, sizeh, sizew);
    			img[i][j] = y[grid];
    		}
    	}
    	return img;
    }
    //private int PosGridNum(boolean[][])
    //
    private RecGridPair RectangleGrid(boolean[] y, int[] GridSize){
    	int[][] o = new int[GridSize[0]][];
    	for(int i=0;i<GridSize[0];i++){
    		o[i] = new int[GridSize[1]];
    		for (int j=0;j<GridSize[1];j++){
    			boolean t = y[i+j*(GridSize[0]-1)];
    			if (t==true){
    				o[i][j] = 1;
    			}else{
    				o[i][j] = 0;
    			}
    		}
    	}
    	// Do DFS
    	RecGridPair max_rec = DFS(o, new RecGridPair(new Point(0,GridSize[0]-1),new Point(0,GridSize[0]-1),
    			new Point(0,GridSize[1]-1),new Point(0,GridSize[1]-1),Score(o,0,0,GridSize[0]-1,GridSize[1]-1)));
//    	Queue<RecGridPair> Q = new LinkedList<RecGridPair>();
//    	RecGridPair max_rec = new RecGridPair(new Point(0,GridSize[0]-1),new Point(0,GridSize[0]-1),
//    			new Point(0,GridSize[1]-1),new Point(0,GridSize[1]-1),Score(o,0,0,GridSize[0],GridSize[1]));
//    	Q.add(max_rec);
//    	Log.v("RecGridPair", max_rec.toString());
    	// BFS search
//    	while(Q.isEmpty()==false){
//    		RecGridPair rec = Q.remove();
//    		if(rec.value>max_rec.value){
//    			max_rec = rec;
//        		Log.v("RecGridPair", max_rec.toString());
//    		}
//    		int mid;
//    		double sc;
//    		// top
//    		if (Math.abs(rec.Top.x-rec.Top.y)>1){
//    			mid = (int)Math.floor((rec.Top.x+rec.Top.y)/2);
//    			Q.add(new RecGridPair(new Point(rec.Top.x, mid), rec.Bottom,rec.Left, rec.Right, rec.value));    			
//    			sc = Score(o, mid+1, (int)rec.Bottom.y, (int)rec.Left.x, (int)rec.Right.y);
//    			Q.add(new RecGridPair(new Point(mid+1, rec.Top.y), rec.Bottom, rec.Left, rec.Right, sc));
//    		}
//    		// Bottom
//    		if (Math.abs(rec.Bottom.x-rec.Bottom.y)>1){
//    			mid = (int) Math.floor((rec.Bottom.x+rec.Bottom.y)/2); 			
//    			sc = Score(o, (int)rec.Top.x, mid, (int)rec.Left.x, (int)rec.Right.y);
//    			Q.add(new RecGridPair(rec.Top,new Point(rec.Bottom.x, mid), rec.Left, rec.Right, sc));    			
//    			Q.add(new RecGridPair(rec.Top, new Point(mid+1,rec.Bottom.y), rec.Left, rec.Right, rec.value));
//    		}
//    		// left
//    		if (Math.abs(rec.Top.x-rec.Top.y)>1){
//    			mid = (int) Math.floor((rec.Left.x+rec.Left.y)/2);
//    			Q.add(new RecGridPair(rec.Top, rec.Bottom, new Point(rec.Left.x,mid), rec.Right, rec.value));    			
//    			sc = Score(o, (int)rec.Top.x, (int)rec.Bottom.y, mid+1, (int)rec.Right.y);
//    			Q.add(new RecGridPair(rec.Top, rec.Bottom, new Point(mid+1, rec.Left.y), rec.Right, sc));
//    		}
//    		// right
//    		if (Math.abs(rec.Right.x-rec.Right.y)>1){
//    			mid = (int) Math.floor((rec.Right.x+rec.Right.y)/2);
//    			sc = Score(o, (int)rec.Top.x, (int)rec.Bottom.y, (int)rec.Left.x, mid);
//    			Q.add(new RecGridPair(rec.Top, rec.Bottom, rec.Left, new Point(rec.Right.x,mid), rec.value));
//    			Q.add(new RecGridPair(rec.Top, rec.Bottom, rec.Left, new Point(mid+1,rec.Right.y), rec.value));
//    		}
//    	}
    	Log.v("RecGridPair", "MaxRec:\n"+max_rec.toString());
    	return max_rec;
    }
    //
    private RecGridPair DFS(int[][] o, RecGridPair rec){
    	int mid;
		double sc;
		RecGridPair max_rec = rec;		
		RecGridPair t;
		Log.v("RecGridPair", "Visit:" + max_rec.toString());
    	// top
		if (Math.abs(rec.Top.x-rec.Top.y)>1){
			mid = (int)Math.floor((rec.Top.x+rec.Top.y)/2);
			t = DFS(o,new RecGridPair(new Point(rec.Top.x, mid), rec.Bottom,rec.Left, rec.Right, rec.value));
			if(t.value>max_rec.value){
				max_rec = t;
				Log.v("RecGridPair", "Change:" + max_rec.toString());
			}
			sc = Score(o, mid+1, (int)rec.Bottom.y, (int)rec.Left.x, (int)rec.Right.y);
			t = DFS(o,new RecGridPair(new Point(mid+1, rec.Top.y), rec.Bottom, rec.Left, rec.Right, sc));
			if(t.value>max_rec.value){
				max_rec = t;
				Log.v("RecGridPair", "Change:" + max_rec.toString());
			}
		}
		// Bottom
		if (Math.abs(rec.Bottom.x-rec.Bottom.y)>1){
			mid = (int) Math.floor((rec.Bottom.x+rec.Bottom.y)/2); 			
			sc = Score(o, (int)rec.Top.x, mid, (int)rec.Left.x, (int)rec.Right.y);
			t = DFS(o,new RecGridPair(rec.Top,new Point(rec.Bottom.x, mid), rec.Left, rec.Right, sc));
			if(t.value>max_rec.value){
				max_rec = t;
				Log.v("RecGridPair", "Change:" + max_rec.toString());
			}
			t = DFS(o,new RecGridPair(rec.Top, new Point(mid+1,rec.Bottom.y), rec.Left, rec.Right, rec.value));
			if(t.value>max_rec.value){
				max_rec = t;
				Log.v("RecGridPair", "Change:" + max_rec.toString());
			}
		}
		// left
		if (Math.abs(rec.Top.x-rec.Top.y)>1){
			mid = (int) Math.floor((rec.Left.x+rec.Left.y)/2);
			t = DFS(o,new RecGridPair(rec.Top, rec.Bottom, new Point(rec.Left.x,mid), rec.Right, rec.value));
			if(t.value>max_rec.value){
				max_rec = t;
				Log.v("RecGridPair", "Change:" + max_rec.toString());
			}
			sc = Score(o, (int)rec.Top.x, (int)rec.Bottom.y, mid+1, (int)rec.Right.y);
			t = DFS(o,new RecGridPair(rec.Top, rec.Bottom, new Point(mid+1, rec.Left.y), rec.Right, sc));
			if(t.value>max_rec.value){
				max_rec = t;
				Log.v("RecGridPair", "Change:" + max_rec.toString());
			}
		}
		// right
		if (Math.abs(rec.Right.x-rec.Right.y)>1){
			mid = (int) Math.floor((rec.Right.x+rec.Right.y)/2);
			sc = Score(o, (int)rec.Top.x, (int)rec.Bottom.y, (int)rec.Left.x, mid);
			t = DFS(o,new RecGridPair(rec.Top, rec.Bottom, rec.Left, new Point(rec.Right.x,mid), rec.value));
			if(t.value>max_rec.value){
				max_rec = t;
				Log.v("RecGridPair", "Change:" + max_rec.toString());
			}
			t = DFS(o,new RecGridPair(rec.Top, rec.Bottom, rec.Left, new Point(mid+1,rec.Right.y), rec.value));
			if(t.value>max_rec.value){
				max_rec = t;
				Log.v("RecGridPair", "Change:" + max_rec.toString());
			}
		}
		return max_rec;
    }
    private double Score(int[][] o, int minX, int minY, int maxX, int maxY){
    	int sum_pos = 0;
    	int sum = 0;
    	for(int i=minX;i<maxX;i++){
    		for (int j=minY;j<maxY;j++){
    			sum++;
    			if(o[i][j]==1){ sum_pos++;}
    		}
    	}
    	if (sum==0){
    		return 0;
    	}else{
    		return ((double)sum_pos/Math.sqrt((double)sum));
    	}
    }
    //////////////////////////////////////////////// MOSRO ///////////////////////////////////////////////////////
    //  Grid
    private int Grid(int[] GridSize, int h, int w, int sizeh, int sizew){
    	double ratioh = (h+0.0)/sizeh;
    	double ratiow = (w+0.0)/sizeh;
    	int hh = (int)(ratioh*GridSize[0]);
    	int ww = (int)(ratiow*GridSize[1]);
    	return (hh + ww*(GridSize[0]-1));
    }
    // Find the corresponding BoVW
    private int FindVoc(float[] v, List<double[]> voc){
    	int size = voc.size();
    	double minDist = Double.MAX_VALUE;
    	int minIndex = -1;
    	for(int i=0;i<size;i++){
    		double dist = L2Dist(v, voc.get(i));
    		if(dist<minDist){
    			minDist = dist;
    			minIndex = i;
    		}
    	}
    	return minIndex;
    }
    private double L2Dist(float[] v1, double[] v2){
    	double dist = 0;
    	int size = v1.length;
    	for(int i=0;i<size;i++){
    		dist += (v1[i]-v2[i]) * (v1[i]-v2[i]);
    	}
    	return Math.sqrt(dist);    
    }    
    
    //////////////////////////////////////////////// MOSRO ///////////////////////////////////////////////////////
    public native void FindFeatures(long matAddrGr, long matAddrRgba, long keyPointsObj, long descriptorObj);
    
    //////////////////////////////////////////////// MOSRO ///////////////////////////////////////////////////////
    static {
        System.loadLibrary("mixed_sample");
    }  
    //////////////////////////////////////////////// MOSRO ///////////////////////////////////////////////////////
    
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        mItemPreviewRGBA = menu.add("Preview RGBA");
        mItemPreviewGray = menu.add("Preview GRAY");
        mItemPreviewCanny = menu.add("Canny");
        mItemPreviewFeatures = menu.add("Find features");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "Menu Item selected " + item);
        if (item == mItemPreviewRGBA)
            viewMode = VIEW_MODE_RGBA;
        else if (item == mItemPreviewGray)
            viewMode = VIEW_MODE_GRAY;
        else if (item == mItemPreviewCanny)
            viewMode = VIEW_MODE_CANNY;
        else if (item == mItemPreviewFeatures)
            viewMode = VIEW_MODE_FEATURES;
        return true;
    }
}
