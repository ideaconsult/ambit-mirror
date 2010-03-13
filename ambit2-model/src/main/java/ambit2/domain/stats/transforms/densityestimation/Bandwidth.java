/**
 * Created November 2003
 */
package ambit2.domain.stats.transforms.densityestimation;

import ambit2.domain.stats.Tools;


/**
 * Bandwidth calculation for Kernel density estimation {@link ambit2.stats.transforms.densityestimation.KDensity1D}
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-6
 */
public class Bandwidth {
  public Bandwidth() {
  }

  public static double hns(double a[], int n, Kernel k) {
    double s1 = Tools.siqr(a, n);
    double s;
    if (s1 > 0) {
      s = Tools.std(a, n);
      s1 = s1 / 1.34;
      if (s1 < s) {
        s = s1;
      }
    }
    else {
      s = Tools.std(a, n);

    }
    if (s == 0) {
      return 0;
    }
    else {
      s1 = 1.0 / 5.0;
      s1 = Math.pow(n, s1);
      return k.multiplier * s / s1;
    }
  }

}