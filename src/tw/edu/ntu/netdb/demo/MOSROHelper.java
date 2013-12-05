package tw.edu.ntu.netdb.demo;

import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.samples.tutorial4.MOSROCategory;
import org.opencv.samples.tutorial4.MOSROParams;
import org.opencv.samples.tutorial4.MOSROReturn;
import org.opencv.samples.tutorial4.RecGridPair;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class MOSROHelper {
	private Context mContext;
	public MOSROHelper(Context context) {
		mContext = context;
	}
	
	public Result identify(int categoryIndex, Bitmap bmp) {
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
        boolean[] mcc_mask = null;
        boolean[] tmp_mask;
        boolean[][] Mask;
//        double max_pos = 0;
        double max_mcc = 0;
        int max_c = -1;
        int size = output.category.size();                
        for (int c=0;c<size;c++){
//        	tmp_mask = output.MostConnectedComponent(c, GridSize);
//        	if (output.mcc[c] > max_mcc) {
        	Log.d(getClass().getName(), String.valueOf(categoryIndex));
        	Log.d(getClass().getName(), String.valueOf(output.category.get(c).CID));
        	if (categoryIndex == output.category.get(c).CID) {
        		max_c = c;
        		mcc_mask = output.MostConnectedComponent(c, GridSize);
        		break;
        	}
//        	double pos = output.PosGrid(c);
//        	if (pos > max_pos){
//        		max_pos = pos;
//        		max_c = c;
//        	}
        }
        
        // compute mask
    	//Mask = MOSRO_Mask(output.y[max_c], GridSize, mat_rgb.cols(), mat_rgb.rows());
//        Mask = MOSRO_Mask(output.MostConnectedComponent(max_c, GridSize), GridSize, mat_rgb.cols(), mat_rgb.rows());
        if (max_c == -1) {
        	return null;
        }
        Mask = MOSRO_Mask(mcc_mask, GridSize, mat_rgb.cols(), mat_rgb.rows());
        //Mask = MOSRO_Mask(output.y[max_c], GridSize, mat_rgb.cols(), mat_rgb.rows());
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
        return new Result(output.category.get(max_c), resultbmp, Mask);
    }
		
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

        // read files
        
        AppResourceManager manager = (AppResourceManager) mContext.getApplicationContext();
        List<MOSROCategory> category = manager.getCategories();
        int cNum = category.size();
        boolean[][] output = new boolean[cNum][];
        for(int c=0; c<cNum; c++){
        	// transform to regional BoVW
        	int NGrid = GridSize[0]*GridSize[1];
        	int NVoc = category.get(c).voc.size()*(category.get(c).voc.get(0).getChildrenNum());
        	Log.d(getClass().getName(), "NVoc" + String.valueOf(NVoc));
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
        		int v = FindVoc(drow,category.get(c).voc);
        		hist[grid][v]++;
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
//    	Log.v("MOSRO", LogString(y,GridSize));
//    	String s = "";
//    	for(int i=0;i<sizeh;i++){    		
//    		for(int j=0;j<sizew;j++){
//    			s += String.valueOf(img[i][j]) + " ";
//    		}
//    		s +='\n';
//    	}
//    	Log.v("MOSRO", s);
    	return img;
    }
    private String LogString(boolean[] array, int[] GridSize){
		String s="";// = String.valueOf(array[0]);
		if(array[0]==true){
			s+="1";
		}else{
			s+="0";
		}
		for (int i=1;i<array.length;i++){
			if (i%GridSize[0]==0){
				s+="\n";
			}
			if(array[i]==true){
				s+=" 1";
			}else{
				s+=" 0";
			}
		}
		return s;
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
    // TODO
    private int FindVoc(float[] v, List<Node> voc){
    	int size = voc.size();
    	double minDist = Double.MAX_VALUE;
    	int minIndex = -1;
    	for(int i=0;i<size;i++){
    		double dist = L2Dist(v, voc.get(i).getV());
//    		double dist = L2Dist(v, voc.get(i));
    		if(dist<minDist){
    			minDist = dist;
    			minIndex = i;
    		}
    	}
    	
    	double minDist2 = Double.MAX_VALUE;
    	int minIndex2 = -1;
    	size = voc.get(minIndex).getChildrenNum();
    	for(int i=0;i<size;i++){
    		double dist = L2Dist(v, voc.get(minIndex).getChild(i).getV());
//    		double dist = L2Dist(v, voc.get(i));
    		if(dist<minDist2){
    			minDist2 = dist;
    			minIndex2 = i;
    		}
    	}
    	return minIndex2 + minIndex*voc.size();
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
}
