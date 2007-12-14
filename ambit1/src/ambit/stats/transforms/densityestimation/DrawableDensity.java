/**
 * Created November 2003
 */
package ambit.stats.transforms.densityestimation;

/**
 * Drawable Density
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public interface DrawableDensity {
  double[] to1DGridUnfiltered(int xnumbers[], double xmin[], double xmax[],
                              double[] currentPoint, int GridResolution);

  double[] to1DGrid(int xnumbers[], double xmin[], double xmax[],
                    double[] currentPoint, int GridResolution);
  double[] to2DGridUnfiltered(int xnumbers[], double xmin[], double xmax[],
                                double[] currentPoint, int GridResolution);

  double[] to2DGrid(int xnumbers[], double xmin[], double xmax[],
                      double[] currentPoint, int GridResolution);

//  double[] to3DGrid(int xnumbers[], double xmin[], double xmax[],
//                        double[] currentPoint, int GridResolution);
}
