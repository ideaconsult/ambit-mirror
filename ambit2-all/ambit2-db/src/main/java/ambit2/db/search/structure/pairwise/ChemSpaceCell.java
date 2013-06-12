package ambit2.db.search.structure.pairwise;

import java.io.Serializable;


public class ChemSpaceCell implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7933062037070427670L;
	protected int[] index = new int[2];
	protected double[] left;
	public double[] getLeft() {
		return left;
	}

	public void setLeft(double... left) {
		this.left = left;
	}

	protected double[] right;
	

	public double[] getRight() {
		return right;
	}

	public void setRight(double... right) {
		this.right = right;
	}

	public int[] getIndex() {
		return index;
	}

	public void setIndex(int... index) {
		this.index = index;
	}

	protected double distance = Float.NaN;
	
	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public void clear() {
		index = null;
		this.distance = Double.NaN;
	}
}
