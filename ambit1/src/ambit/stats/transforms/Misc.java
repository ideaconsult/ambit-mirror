/**
 * Created 2003
 */

package ambit.stats.transforms;

/**
 * Miscelaneous routines for the {@link ambit.stats.transforms.FFT}
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-6
 */
public class Misc {
  public Misc() {
  }
  /**
   * 
   * @param ax
   * @param bx
   * @param inc
   * @param xa
   * @return true if successful
   */
  public boolean linspace(double ax, double bx, int inc, double xa[]) {
    double deltax = (bx - ax) / (inc - 1);
    if (deltax == 0) {
      return false;
    }
    for (int i = 0; i < inc; i++) {
      xa[i] = ax;
      ax = ax + deltax;
    }
    return true;
  }
  /**
   * 
   * @param A
   * @param n
   */
  public void fftshift(double A[], int n) {
    if (n > 0) {
      int n2 = (int) Math.floor(n / 2);
      double tmp;
      for (int i = 0; i < n2; i++) {
        tmp = A[i];
        A[i] = A[n2 + i];
        A[n2 + i] = tmp;
      }
    }
  }

}