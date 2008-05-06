package ambit2.stats.transforms.densityestimation;

/*
 * <p>Title: MVDE.java  Multivariate density estimation abstract class  </p>
 * <p>Description: Provides common functonality for diverse methods for multivariate density estimation.
 * <br>
 Provides Highest Probability Density Regions estimations by a fast algorithm @link(HPDBySorting)) *
 <br>
 Provides Highest Probability Density Regions estimations by a fast algorithm @link(HPDBySorting))
 <br>
 Provides basic functionality for drawing (see @link(to1DGrid), @link(to2DGrid), @link(to3DGrid) methods).
 <br>
  * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Nina Nikolova
 * @version 1.0
 */

import Jama.Matrix;
import ambit2.stats.transforms.transformfilters.TransformFilter;
import ambit2.stats.datastructures.Sort;
/**
 * Multivariate kernel density estimation
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public abstract class KDensityMD implements HighestDensityRegions, DrawableDensity {

  private double covarianceMatrix[][];
  boolean FCovarianceSingular;
  private double meanVector[];

  private TransformFilter FFilter = null;

  private int FPointsProcessed;
  private double FLevel;
//        FOnStatus : TOnStatusEvent;

  private int HPDCounters[];
  private double MultivariateHPDLevelsError[];
  private double MultivariateHPDLevels[][];
  private int NumHPDLevels;

  protected int FNumberOfFactors;
  protected int FNumberOfImportantFactors;
  protected int FImportantFactors[];
  protected double FDescriptorsImportance[];
  protected boolean FIsEnabled;

  /**
   * MVDE class constructor
   * @param number the number of descriptors (dimensionality of the descriptor space)
   */
public KDensityMD(int number) {
  FNumberOfFactors = number;
  MultivariateHPDLevels = null;
  MultivariateHPDLevelsError = null;
  FDescriptorsImportance = null;
  FLevel = 0;
  meanVector = new double[number];
  covarianceMatrix = null;
  FFilter = null;
  FCovarianceSingular = false;
  HPDCounters = null;
  setIndices(number);
  FIsEnabled = false;
}

  /**
   * MaxAmplitude
   * @return maximum probability value over all defined range
   */
  public abstract double maxAmplitude();

  /* if FFilter == null then the same as getdensityvalue */
  /**
   * getAmplitude
   * @param point multivariate point
   * @return probability value at point[]
   */
  abstract double getAmplitude(double point[]);
  /**
   *
   * @param points
   * @return double[]
   */
  public double[] getAmplitude(Matrix points) {
    int M = points.getRowDimension();
    int N = points.getColumnDimension();
    double a[][] = points.getArray();
    double result[] = new double[M];
    for (int i = 0; i < M; i++)
       result[i] = getAmplitude(a[i]);
    return result;
  }

  /**
   * setTransformFilter
   * sets the private variable FFilter to parameter F
   * The filter will be used to transform the points from original to another space
   * (see different filters in the package
   * src.lri.transforms.TransformFilter
   * @param F filter to be set
   */
  void setTransformFilter(TransformFilter F) {   FFilter = F; }
  /**
   * getTransformFilter
   * @return current filter , previously set by setTransformFilter
   */
  TransformFilter getTransformFilter() {   return FFilter; }
  /**
   * setDescriptorImportance
   * sets private variable FDescriptorImportance to rank
   * This will be used if ordered descriptors are needed
   * (example -  principal components ranked by eigenvalues)
   * @param rank
   */
  void setDescriptorImportance(double rank[]) {
    FDescriptorsImportance = null;
    if (rank.length > 0) {
      FDescriptorsImportance = (double[]) rank.clone();
    }
  }
  /**
   * setIndices
   * stores in the private variable int FImportantFactors[] the indices of the descriptors
   * to be used in density estimation (a subset of descriptors can be used)
   * This implementation will simply get the first n descriptors.
   * Override the function to have another behaviour.
   * @param n number of descriptors to be used in density estimation
   */
  void setIndices(int n) {
    FNumberOfImportantFactors = n;
    FImportantFactors = null;
    FImportantFactors = new int[n];
    for (int i = 0; i < n; i++) {
      FImportantFactors[i] = i;
    }
  }

  /**
   * Clear()
   * clears some private arrays
   * (meanVector, covarianceMatrix, MultivariateHPDLevels, HPDCounters, FImportantFactors)
   */
  void clear() {
    if (meanVector != null) meanVector = null;
    if (covarianceMatrix != null) covarianceMatrix = null;
    covarianceMatrix = null;
    if (MultivariateHPDLevels != null)  MultivariateHPDLevels = null;
    MultivariateHPDLevels = null;
    MultivariateHPDLevelsError = null;
    FDescriptorsImportance = null;
    HPDCounters = null;
    FImportantFactors = null;
  }
  /**
   * actual data processing
   * called from EstimateDensityFromData
   * @param points
   * @return true if success
   */
  abstract boolean ProcessData(Matrix points);

  /**
   * EstimateDensityFromData
   * Estimates density from set of points
   * If filter is not null, then density in transformed space is estimated
   * @param points
   * @param filter
   * @return true on success
   */
  public boolean EstimateDensityFromData(Matrix points,
                                  TransformFilter filter) {

    if (points == null) {
      return false;
    }
    int L = points.getRowDimension();
    if (L <= 1) return false;

    int N = points.getColumnDimension();
    if (FNumberOfFactors != N) {
      return false;
    }

    if ( (filter != null) && filter.FilterIsEnabled()) {
      setTransformFilter(filter);
    }
    else {
      setTransformFilter(null);
    }
    TransformFilter F = getTransformFilter();

    if ( (F != null) && F.FilterIsEnabled()) {
      Matrix ftmp = F.TransformPoints(points);
      FIsEnabled = ProcessData(ftmp); //, F.TransformError);
      ftmp = null;
    }
    else {
      FIsEnabled = ProcessData(points); //, null);
    }
    return FIsEnabled;
  }

  /**
   * InitializeCountes()
   * Initializes HPDCounters
   * @return true if successful
   */
  boolean InitializeCounters() {
    FPointsProcessed = 0;
    if (MultivariateHPDLevels == null) return false;
    else if (HPDCounters == null) HPDCounters = new int[NumHPDLevels];

    for (int i = 0; i < NumHPDLevels; i++) HPDCounters[i] = 0;
    return true;
  }
  /**
   * setCovariance(double covariance[][])
   * sets covariance matrix
   * @param covariance
   */
  void setCovariance(double covariance[][]) {
    covarianceMatrix = null;
    if (covariance != null) covarianceMatrix = (double[][]) covariance.clone();
    FCovarianceSingular = false;
  };
  /**
   * setMean(double mean[])
   * sets standard mean
   * @param mean
   */
  void setMean(double mean[]) {
    meanVector = null;
    if (mean != null) meanVector = (double[]) mean.clone();
  }

  /**
   * points for which density values are to be calculated
   * points for which density values are to be calculated
   * @param points
   * @return density values
   */
  public double[] getDensityValues(Matrix points) {

    if (points == null) {
      return null;
    }
    InitializeCounters();
    TransformFilter F = getTransformFilter();
    if ( (F == null) || (! F.FilterIsEnabled()))
      return getAmplitude(points);
    else {
      Matrix ftmp = F.TransformPoints(points);
      double result[] = getAmplitude(ftmp);
      ftmp = null;
      return result;
    }
  }

  /**
   * getDensityValue of a point
   * if FFilter == null then the same as getamplitude, otherwise it first
   * transforms the point and then calculates density value in the transformed space
   * @param point
   * @return density value of a point
   */
  double getDensityValue(double point[]) {
    TransformFilter F = getTransformFilter();
    if ( ( F == null) || (!F.FilterIsEnabled())) {
      return getAmplitude(point);
    }
    else {
      int L = point.length;
      Matrix tmp = new Matrix(1,L);
      for (int j = 0; j < L; j++) tmp.set(0,j,point[j]);
      Matrix ftmp = F.TransformPoints(tmp);
      double d = 0;
      if (ftmp != null) d = getAmplitude(ftmp.getArray()[0]);
      else d = 0;
      tmp = null; ftmp = null;
      return d;
    }
  }


  /**
   * getCoverageValues
   * calculates Highest Density Region of a set of points
   * @param points
   * @return calculated coverage values for each point
   */
  public double[] getCoverageValues(Matrix points) {
    InitializeCounters();
    if (MultivariateHPDLevels == null) return null;
    else  {
      double coverageValues[] = getDensityValues(points);
      int L = coverageValues.length;
      for (int i = 0; i < L; i++)
        coverageValues[i] = Density2Coverage(coverageValues[i]);
      return coverageValues;
    }
  }

  /**
   * getCoverageValues
   * calculates Highest Density Region of a point
   * @param point
   * @return HPD value
   */
  double getCoverageValue(double point[]) {
    if (MultivariateHPDLevels != null) {
      return Density2Coverage(getDensityValue(point));
    }
    else {
      return 0;
    }
  }

  /**
   * getmostDenseVector
   * @param p return parameter, contains point for which density value is maximum
   */
  public abstract void getMostDenseVector(double p[]);

  double Density2Coverage(double density) {
    int i;
    double d = 0;
    for (int k = 0; k < NumHPDLevels; k++) {
      if (density >= MultivariateHPDLevels[1][k]) {
        d = MultivariateHPDLevels[0][k];
        i = k;
        HPDCounters[i] = HPDCounters[i] + 1;
      }
    }
    FPointsProcessed++;
    return d;
  }


  abstract void getMinVector(double minv[]);

  abstract void getMaxVector(double maxv[]);

  public String toString() {    return "Abstract class for multivariate density estimation";
  };

  abstract double getMinValue(int index);

  abstract double getMaxValue(int index);

  double[][] getMultivariateHPDLevels() {    return MultivariateHPDLevels; }
  /**
   * getPointsProcessed
   * @return the number of points processed
   * private variable FPointsProcessed is incremented only in @Density2Coverage
   */
  int getPointsProcessed() {  return FPointsProcessed;  }
  /**
   * isEnabled
   * @return true if density was estimated successfully
   */
  boolean isEnabled() {
    return FIsEnabled && (covarianceMatrix != null) && (meanVector != null);
  }

  /**
   * Grid2ContourGrid
   * uses precalculated MultivariateHPDLevels to store HPD values for each grid cell
   * @param grid contains density values on input, HPD values on output
   * @return true if successful
   */
  public boolean Grid2ContourGrid(double grid[]) {
    if (MultivariateHPDLevels == null) {
      return false;
    }
    int N = grid.length;
    if (N <= 0) {
      return false;
    }
    double a;
    for (int i = 0; i < N; i++) {
        a = 0;
        for (int k = (NumHPDLevels-1); k >= 0; k--)
          if (grid[i] >= MultivariateHPDLevels[1][k]) {
            a = MultivariateHPDLevels[0][k];
            break;
          }
        grid[i] = a;
    }
    return true;
  }

  /**
   * AssignHPDLevels
   * HighestDensityRegions interface
   */

  public void AssignHPDLevels(double m[][]) {
    if (MultivariateHPDLevels != null) {
      MultivariateHPDLevels = null;
//      FinalizeMatrix(MultivariateHPDLevels);
    }
    MultivariateHPDLevels = (double[][]) m.clone();
    if (HPDCounters != null) {
      HPDCounters = null;
    }
    NumHPDLevels = MultivariateHPDLevels[0].length;
    HPDCounters = new int[NumHPDLevels];
    MultivariateHPDLevelsError = null;
  }
  /**
   * AssignHPDLevels
   * HighestDensityRegions interface
   * @param numberOfLevels
   */
  public void AssignHPDLevels(int numberOfLevels) {

    if (MultivariateHPDLevels != null) {
      MultivariateHPDLevels = null;
//      FinalizeMatrix(MultivariateHPDLevels);
    }

//    InitializeMatrix(MultivariateHPDLevels, 2, numberOfLevels);
    MultivariateHPDLevels = new double[2][numberOfLevels];
    for (int i = 0; i < numberOfLevels; i++) {
      MultivariateHPDLevels[0][i] = i ;
      MultivariateHPDLevels[0][i] = MultivariateHPDLevels[0][i] / numberOfLevels;
      MultivariateHPDLevels[1][i] = 0;
    }
    NumHPDLevels = numberOfLevels;
  }
  /**
   * getHPDCounters()
   * HighestDensityRegions interface
   * @return int[]
   */
  public int[] getHPDCounters() { return HPDCounters;}
  /**
   * getHPDLevelsError()
   * HighestDensityRegions interface
   * @param index
   * @return double 
   */
  public double getHPDLevelsError(int index) {
  if ( (MultivariateHPDLevelsError == null) ||
      (index >= NumHPDLevels)) return 1.0;
  else return MultivariateHPDLevelsError[index];
  }


  /**
   * HighestDensityRegions interface
   * @param points
   * @return  true if successful
   */
  public boolean HPDBySorting(Matrix points) {

    MultivariateHPDLevelsError = null;
    int i = points.getRowDimension();
    if (i == 0) return false;

    MultivariateHPDLevelsError = new double[NumHPDLevels];
    for (int j = 0; j < NumHPDLevels; j++)
      MultivariateHPDLevelsError[j] =
          Math.sqrt(MultivariateHPDLevels[0][j] *
                    (1 - MultivariateHPDLevels[0][j]) / i);

    double d[] = getDensityValues(points);
    if (d != null) {
      Sort sr = new Sort();
      sr.QuickSortArray(d, i);
      sr = null;
      double h = d.length;
      for (int L = 0; L < NumHPDLevels; L++) MultivariateHPDLevels[1][L] = 0;

      double s = 0;
      for (int j = (i - 1); j >= 0; j--) {
        s++;
        for (int L = 0; L < NumHPDLevels; L++)
          if ( (s / h) <= (1.0 - MultivariateHPDLevels[0][L]))
            MultivariateHPDLevels[1][L] = d[j];
      }
      //fixup
      int hh = 0;
      for (int L = 0; L < NumHPDLevels; L++)
        if (MultivariateHPDLevels[1][L] > 0) hh = L;
        else MultivariateHPDLevels[1][L] = MultivariateHPDLevels[1][hh];
    }
    return true;
  }

  /**
   * to1dGrid
   * DrawableDensity interface
   * this is general solution, write more efficient one in the subclass
   * @param xnumbers
   * @param xmin
   * @param xmax
   * @param currentPoint
   * @param GridResolution
   * @return array of doubles, representing 1D density within range (xmin,xmax)
   */
   public double[] to1DGrid(int xnumbers[], double xmin[], double xmax[],
                     double currentPoint[], int GridResolution) {

          double result[] = null;
          double astep = (xmax[xnumbers[0]] - xmin[xnumbers[0]]) / GridResolution;
          if (astep == 0) return null;
          int L = currentPoint.length;
          double[] x = (double[]) currentPoint.clone();
          x[xnumbers[0]] = xmin[xnumbers[0]];
          TransformFilter F = getTransformFilter();

          Matrix tmpM = new Matrix(GridResolution, L);
          double tmp[][] = tmpM.getArray();
          for (int i = 0; i < GridResolution; i++) {
              for (int j = 0; j < L; j++) tmp[i][j] = x[j];
              x[xnumbers[0]] = x[xnumbers[0]] + astep;
          }

          if ((F != null) && (F.FilterIsEnabled())) {
            Matrix ftmpM = F.TransformPoints(tmpM);
            result = getAmplitude(ftmpM);
            ftmpM = null;
          } else
            result = getAmplitude(tmpM);

          tmpM = null;
          x = null;
          return result;
   }
   /**
    * @deprecated
    * to1DGridUnfiltered
    * Density Drawable interface
    * @param xnumbers
    * @param xmin
    * @param xmax
    * @param currentPoint
    * @param GridResolution
    * @return double[]
    */
   public double[] to1DGridUnfiltered(int xnumbers[], double xmin[], double xmax[],
                               double[] currentPoint, int GridResolution)
   {
          boolean e = false;
          TransformFilter F = getTransformFilter();
          if (F != null) {
             e = F.FilterIsEnabled();
             F.setEnabled(false);
          };
          double result[] = to1DGrid(xnumbers,
                          xmin,xmax, currentPoint, GridResolution);
          if (F != null) F.setEnabled(e);
          return result;
   }
   /**
    * get2DGridPoints
    * @param xnumbers
    * @param xmin
    * @param xmax
    * @param currentPoint
    * @param GridResolution
    * @return {@link Matrix}
    */
   public Matrix get2DgridPoints(int xnumbers[], double xmin[], double xmax[],
                       double[] currentPoint, int GridResolution) {
    double astep[] = new double[2];
    for (int i = 0; i < 2; i++)
      astep[i] = (xmax[xnumbers[i]] - xmin[xnumbers[i]]) / GridResolution;
    double p[] = (double []) currentPoint.clone();
    int L = currentPoint.length;

    //grid points initialization
    Matrix gridPoints = new Matrix(GridResolution*GridResolution,L);
    double gpArray[][] = gridPoints.getArray();
    int gpNo = 0;
    p[xnumbers[0]] = xmin[xnumbers[0]];
    for (int i = 0; i < GridResolution; i++) {
         p[xnumbers[1]] = xmin[xnumbers[1]];
         for (int j = 0; j < GridResolution; j++) {
             for (int k = 0; k < L; k++)
                gpArray[gpNo][k] = p[k];
             gpNo++;
             p[xnumbers[1]] = p[xnumbers[1]] + astep[1];
         }
         p[xnumbers[0]] = p[xnumbers[0]] + astep[0];
    }
    p = null; astep = null;
    return gridPoints;
  }
   /**
    * to2DGrid
    * Density Drawable interface
    * @param xnumbers
    * @param xmin
    * @param xmax
    * @param currentPoint
    * @param GridResolution
    * @return 2d array packed by nPoints
    */
   public double[] to2DGrid(int xnumbers[], double xmin[], double xmax[],
                       double[] currentPoint, int GridResolution) {

       if (xnumbers[0] == xnumbers[1])
          return to1DGrid(xnumbers,xmin,xmax,currentPoint,GridResolution);
       if ((xnumbers[0] >=0 ) && (xnumbers[1] >= 0) &&
           (xnumbers[0] < FNumberOfFactors) && (xnumbers[1] < FNumberOfFactors)) {
              double result[] = null;

              Matrix gridPoints =
                  get2DgridPoints(xnumbers,xmin,xmax,currentPoint,GridResolution);
              //amplitude calculation
              TransformFilter F = getTransformFilter();
              if ((F != null) && (F.FilterIsEnabled()))  {
                 Matrix ftmp = F.TransformPoints(gridPoints);
                 result = getAmplitude(ftmp);
                 ftmp = null;
              } else //copied for the better performance
                 result = getAmplitude(gridPoints);
              return result;
       } else return null;

}

  /**
   * to2DGridUnfiltered
   * Density Drawable interface
   * @param xnumbers
   * @param xmin
   * @param xmax
   * @param currentPoint
   * @param GridResolution
   * @return double[]
   */
  public double[] to2DGridUnfiltered(int xnumbers[], double xmin[], double xmax[],
                           double[] currentPoint, int GridResolution) {

    boolean e = false;
    TransformFilter F = getTransformFilter();
    if (F != null) {
      e = F.FilterIsEnabled();
      F.setEnabled(false);
    }
    double result[] = to2DGrid(xnumbers,
                     xmin, xmax, currentPoint, GridResolution);
    if (F != null)  F.setEnabled(e);
    return result;
}

  /**
   * to3DGrid
   * Density Drawable interface
   * @param xnumbers
   * @param xmin
   * @param xmax
   * @param currentPoint
   * @param GridResolution
   * @return
   */
  /*
  public double[] to3DGrid(int xnumbers[], double xmin[], double xmax[],
                           double[] currentPoint, int GridResolution) {

   boolean ok = true;
   for (int i = 0; i< 3; i++)
     ok = ok && (xnumbers[i] >= 0) && (xnumbers[i] < FNumberOfFactors);

   if (ok) {
          double result[][][] = new double[GridResolution][GridResolution][GridResolution];
          double astep[] = new double[3];
          for (int i = 0; i< 3; i++) astep[i] = (xmax[xnumbers[i]] - xmin[xnumbers[i]]) / GridResolution;
          double p[] = (double[]) currentPoint.clone();
          int L = p.length;
          TransformFilter F = getTransformFilter();
          if ((F != null) && (F.FilterIsEnabled())) {
             double tmp[] = new double[L];
             p[xnumbers[0]] = xmin[xnumbers[0]];
             for (int i = 0; i< GridResolution;i++) {
                  p[xnumbers[1]] = xmin[xnumbers[1]];
                  for (int j = 0; j< GridResolution;j++) {
                      p[xnumbers[2]] = xmin[xnumbers[2]];
                      for (int k = 0; k< GridResolution;k++) {
                          if (F.TransformPoint(p,tmp))
                            result[i][j][k] = getAmplitude(tmp);
                          else result[i][j][k] = 0;
                          p[xnumbers[2]] = p[xnumbers[2]] + astep[2];
                      }
                      p[xnumbers[1]] = p[xnumbers[1]] + astep[1];
                  }
                  p[xnumbers[0]] = p[xnumbers[0]] + astep[0];
             }
             tmp = null;
          } else {
            p[xnumbers[0]] = xmin[xnumbers[0]];
            for (int i = 0; i < GridResolution; i++) {
              p[xnumbers[1]] = xmin[xnumbers[1]];
              for (int j = 0; j< GridResolution;j++) {
                p[xnumbers[2]] = xmin[xnumbers[2]];
                for (int k = 0; k< GridResolution;k++) {
                  result[i][j][k] = getAmplitude(p);
                  p[xnumbers[2]] = p[xnumbers[2]] + astep[2];
                }
                p[xnumbers[1]] = p[xnumbers[1]] + astep[1];
              }
              p[xnumbers[0]] = p[xnumbers[0]] + astep[0];
            }
          }
          astep = null;
          p = null;
          return result;
   } else return null;
   }
   */
/*
   void TMVDE.setOnStatus(e : TOnStatusEvent);
   {
          FOnStatus = e;
   };
   void TMVDE.PrintStatus;
   var slist : TStringList;
      i : integer;
   {
          slist = TStringList.Create;
             slist.add('[HPDLEVELS]');
             slist.add(toString);
             slist.add('');
             {
             for i = low(counters) to high(counters) do {
                 slist.add(Format('HPD %5.3f%s%d',
                 [MultivariateHPDLevels[0,i],chr(9)+chr(9),counters[i]]));
             };
              }
          for i = 0 to slist.count-1 do FOnStatus(slist[i],-1);
          slist.free;
   };
      function toStatus(var slist : TStringList) : string;virtual;
     begin
          slist.add(toString);
       if covariancematrix == null then slist.Add('Covariance matrix NOT assigned');
     end;

   */
//        double[] Kernel2Points(k : string; index : integer; n : integer) : TDoubleVector; virtual; abstract;

//        void SaveToText(var slist : TStringList); virtual;abstract;
//        void LoadFromText(slist : TStringList); virtual;abstract;
//        void SaveToStream(Stream: TStream); virtual; abstract;
//        {Loads from stream}
//        void LoadFromStream(Stream: TStream);  virtual; abstract;

}
