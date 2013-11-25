package org.opencv.samples.tutorial4;

import java.util.List;

import tw.edu.ntu.netdb.demo.Node;

public class MOSROCategory {
	public int CID;
	public String CName;
	public double[] w;
	// public List<double[]> voc;
	public List<Node> voc;
	public String Info;
	public int CLOGO;

	public MOSROCategory(int CID, String CName, int CLOGO) {
		this.CID = CID;
		this.CName = CName;
		this.CLOGO = CLOGO;
	}
}