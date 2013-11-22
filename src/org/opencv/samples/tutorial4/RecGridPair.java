package org.opencv.samples.tutorial4;

import org.opencv.core.Point;

public class RecGridPair {
	public Point Left;
	public Point Right;
	public Point Top;
	public Point Bottom;
	public double value;
	
	public RecGridPair(Point Top,Point Bottom,Point Left,Point Right,double value){
		this.Left=Left;
		this.Right=Right;
		this.Top=Top;
		this.Bottom=Bottom;
		this.value=value;
	}
	
	public String toString(){
		String s = "("+ String.valueOf(Top.x) + "," + String.valueOf(Top.y) + ")";
		s += " ("+ String.valueOf(Bottom.x) + "," + String.valueOf(Bottom.y) + ")";
		s += " ("+ String.valueOf(Left.x) + "," + String.valueOf(Left.y) + ")";
		s += " ("+ String.valueOf(Right.x) + "," + String.valueOf(Right.y) + ")";
		s += ":"+ String.valueOf(value) +"\n";
		return s;
	}
}
