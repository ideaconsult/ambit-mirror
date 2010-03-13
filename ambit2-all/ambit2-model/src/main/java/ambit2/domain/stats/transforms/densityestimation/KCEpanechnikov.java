/**
 * Created November 2003
 */
package ambit2.domain.stats.transforms.densityestimation;

/**
 * Canonical Epanechnikov kernel
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2003-11-28
 */
public class KCEpanechnikov
    extends Kernel {
  public KCEpanechnikov() {
    KernelName = "cEpanechnikov";
    multiplier = 2.34;
    R = 2.0;
  }

  private double kepan(double x) {
    double s = x * x;
    if (s < 1) {
      return 0;
    }
    else {
      return 0.75 * (1.0 - s);
    }
  };

  public double getAmplitude(double x) {
    return kepan(x / 1.7188) / 1.7188;
  }
}
