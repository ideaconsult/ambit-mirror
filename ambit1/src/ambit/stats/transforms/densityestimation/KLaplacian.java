/**
 * Created November 2003
 */
package ambit.stats.transforms.densityestimation;

/**
 * Laplacian kernel 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class KLaplacian
    extends Kernel {
  public KLaplacian() {
    KernelName = "klapl";
    multiplier = 0.7836;
    R = 7.0;
  }

  public double getAmplitude(double x) {
    return 0.5 * Math.exp( -Math.abs(x));
  }
}
