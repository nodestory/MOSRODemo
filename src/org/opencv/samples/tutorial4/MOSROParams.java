package org.opencv.samples.tutorial4;

import org.opencv.core.Point;

public class MOSROParams {
	public int[] GridSize;
	public Point Location;
	public String Address;
	public float heading;
	public float fov;
	public float pitch;
	
	public MOSROParams(int[] GridSize, Point loc){
		this.GridSize = GridSize;
		this.Location = loc;
	}
	public MOSROParams(int[] GridSize, Point loc, String addr, float heading, float fov, float pitch){
		this.GridSize = GridSize;
		this.Location = loc;
		this.Address = addr;
		this.heading = heading;
		this.fov = fov;
		this.pitch = pitch;
	}
}
