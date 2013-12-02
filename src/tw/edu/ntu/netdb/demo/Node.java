package tw.edu.ntu.netdb.demo;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private double[] v = new double[384];
	
	private List<Node> children = new ArrayList<Node>();
	
	public Node(double[] v) {
		setV(v);
	}
	
	public double[] getV() {
		return v;
	}
	
	public void setV(double[] v) {
		this.v = v;
	}
	
	public void addChild(Node node) {
		children.add(node);
	}
	
	public Node getChild(int index) {
		return children.get(index);
	}
	
	public int getChildrenNum() {
		return children.size();
	}
}