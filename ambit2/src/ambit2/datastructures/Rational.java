/*
 * Created on 2004-12-1
 *
 * This class is used only as a test for Heap data structure
 */
package ambit2.datastructures;


/**
 * this is a test 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class Rational implements Comparable {
	  private long num, denom;
		
	/**
	 * 
	 */
	public Rational(int num, int denom) {
		this.num = num;
		this.denom = denom;
	}
	public String toString() {
		return num + "/" + denom;
	}
   public int compareTo(Object o) {
		       Rational a = this;
		       Rational b = (Rational) o;
		       if      (a.num * b.denom < a.denom * b.num) return -1;
		       else if (a.num * b.denom > a.denom * b.num) return  1;
		       else                                    return  0;
		   }
	


}
