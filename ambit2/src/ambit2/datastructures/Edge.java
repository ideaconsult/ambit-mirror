/**
 * Created on 2004-12-1
 *
 */
package ambit2.datastructures;

/**
 * this is a test 
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class Edge implements Comparable {
	private int v1,v2;
	private int weight;
	/**
	 * 
	 */
	public Edge(int v1,int v2, int weight) {
		this.v1 = v1;
		this.v2 = v2;		
		this.weight = weight;
	}

	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("(");		
		b.append(v1);
		b.append(",");
		b.append(v2);
		b.append(")-[");		
		b.append(weight);
		b.append("]");		
		return b.toString();
	}
	/* (non-Javadoc)
	 * @see ambit2.datastructures.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		Edge a = this;
		Edge b = (Edge) o;
	       if      (a.weight > b.weight) return -1;
	       else if (a.weight < b.weight) return  1;
	       else                                    return  0;		

	}

	/**
	 * @return Returns the v1.
	 */
	public int getV1() {
		return v1;
	}
	/**
	 * @return Returns the v2.
	 */
	public int getV2() {
		return v2;
	}
	/**
	 * @return Returns the weight.
	 */
	public int getWeight() {
		return weight;
	}
}
