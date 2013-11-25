package org.opencv.samples.tutorial4;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.util.Log;

public class MOSROReturn {
	public List<MOSROCategory> category;
	public boolean[][] y;
	private int[] GridSize;
	
	public MOSROReturn(List<MOSROCategory> cat, boolean[][] output){
		this.category = cat;
		this.y = output;
	}
	
	public double PosGrid(int c){
		int pos = 0;
		for (int i=0;i<y[c].length;i++){
			if(y[c][i]==true){
				pos++;
			}
		}
		return ((pos+0.0)/(y[c].length));
	}
	
	public boolean[] MostConnectedComponent(int c, int[] GridSize){
		this.GridSize = GridSize;
//		Log.v("MOSRO", LogString(y[c]));
		int len = y[c].length;
		int label = 1;
		int[] cc = new int[len];
		for(int i=0;i<len;i++){
			if(y[c][i]==true){
				cc[i] = 0;
			}else{
				cc[i] = -1;
			}
		}
		int max_count = 0;
		int max_label = -1;
//		Log.v("MOSRO", LogString(cc));
		for(int i=0;i<len;i++){
			if(cc[i]==0){
				// Do BFS Search
				int count = BFS(cc, i, label);
//				Log.v("MOSRO", "Label#"+String.valueOf(label)+ " : "+String.valueOf(count));
				if (count>max_count){
					max_count = count;
					max_label = label;
				}
				label++;
			}
		}
//		Log.v("MOSRO", "Connected Component map:\n");
//		Log.v("MOSRO", LogString(cc));
//		Log.v("MOSRO", "Label#"+String.valueOf(max_label)+ " : "+String.valueOf(max_count));
		boolean[] Maskcc = new boolean[len];
		for(int i=0;i<len;i++){
			if(cc[i]==max_label){
				Maskcc[i] = true;
			}else{
				Maskcc[i] = false;
			}
		}
		Log.v("MOSRO", LogString(Maskcc));
		return Maskcc;
	}
	
	private int BFS(int[] map, int start, int label){
		Queue<Integer> Q = new LinkedList<Integer>();
		Q.add(start);
		map[start] = label;
		int count = 0;
		
		while(Q.isEmpty()==false){
			int curr = (Integer) Q.remove();
			//map[curr] = label;
			count++;
//			Log.v("MOSRO", "curr="+String.valueOf(curr)+", label#"+String.valueOf(label));
//			Log.v("MOSRO", LogString(map));
//			Log.v("MOSRO", ""+String.valueOf(curr%GridSize[0]+1)+","+String.valueOf(map[curr+1]));
//			Log.v("MOSRO", ""+String.valueOf(curr+GridSize[0])+","+String.valueOf(map[curr+GridSize[0]]));
//			Log.v("MOSRO", ""+String.valueOf(map[curr+1+GridSize[0]]));
			boolean isTop=false, isBottom=false, isLeft=false, isRight=false;
			if (curr%GridSize[0]>0){
				isTop=true;
			}
			if ((curr%GridSize[0])+1<GridSize[0]){
				isBottom=true;
			}
			if (curr-GridSize[0]>0){
				isLeft = true;
			}
			if (curr+GridSize[0]<(GridSize[0]*GridSize[1])){
				isRight=true;
			}
			// top
			if (isTop && map[curr-1]==0){
				Q.add(curr-1);
				map[curr-1]=label;
			}
			// bottom
			if (isBottom && map[curr+1]==0){
				Q.add(curr+1);
				map[curr+1]=label;
			}
			// left
			if (isLeft && map[curr-GridSize[0]]==0){
				Q.add(curr-GridSize[0]);
				map[curr-GridSize[0]]=label;
			}
			// right
			if (isRight && map[curr+GridSize[0]]==0){
				Q.add(curr+GridSize[0]);
				map[curr+GridSize[0]]=label;
			}
			// top-left
//			if (isTop && isLeft && map[curr-1-GridSize[0]]==0){
//				Q.add(curr-1-GridSize[0]);
//				map[curr-1-GridSize[0]]=label;
//			}
			// top-right
//			if(isTop && isRight && map[curr-1+GridSize[0]]==0){
//				Q.add(curr-1+GridSize[0]);
//				map[curr-1+GridSize[0]]=label;
//			}
			// bottom-left
//			if(isBottom && isLeft && map[curr+1-GridSize[0]]==0){
//				Q.add(curr+1-GridSize[0]);
//				map[curr+1-GridSize[0]]=label;
//			}
			// bottom-right
			//if ((curr%GridSize[0])+1<GridSize[0] && (curr+GridSize[0]<(GridSize[0]*GridSize[1])) && map[curr+1+GridSize[0]]==0){
//			if (isBottom && isRight && map[curr+1+GridSize[0]]==0){
//				Q.add(curr+1+GridSize[0]);
//				map[curr+1+GridSize[0]]=label;
//			}
		}	
		return count;
	}
	
	private String LogString(int[] array){
		String s = String.valueOf(array[0]);		
		for (int i=1;i<array.length;i++){
			if (i%GridSize[0]==0){
				s+="\n";
			}
			s += " " + array[i];
		}
		return s;
	}
	private String LogString(boolean[] array){
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
	
}
