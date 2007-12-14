/**
 * Created November 2003
 */
package ambit.stats.transforms.densityestimation;


/**
 * NorGaussian (normal) mal kernel 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class KNormal
    extends Kernel {
  static double PI2 = (1 / Math.sqrt(2 * Math.PI));
  public KNormal() {
    KernelName = "kNormal";
    multiplier = 1.0592;
    R = 4.0;
  }

  public double getAmplitude(double x) {
    return PI2 * Math.exp( -0.5 * x * x);
  }
}
