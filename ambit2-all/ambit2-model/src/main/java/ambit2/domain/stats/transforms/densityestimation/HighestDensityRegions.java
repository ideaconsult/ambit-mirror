/**
 * Created November 2003
 */

package ambit2.domain.stats.transforms.densityestimation;


import Jama.Matrix;

/**
 * Highest Density Regions calculations
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public interface HighestDensityRegions {
  void AssignHPDLevels(double m[][]);
  void AssignHPDLevels(int numberOfLevels);
  double getHPDLevelsError(int index);
  int[] getHPDCounters();
  boolean HPDBySorting(Matrix points);
}