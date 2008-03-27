/**
 * Created November 2003
 */
package ambit2.stats.transforms.transformfilters;

import Jama.Matrix;

/**
 * Abstract class for data transformation 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class TransformFilter{
//protected
  protected double[] FMinVector;
  protected double[] FMaxVector;
  protected int FInputDescriptorNumber, FOutputDescriptorNumber;
  protected boolean FEnabled;

  protected double FDescriptorsRankThreshold = 0.99 ;

  public int[] TransformError;
  public TransformFilter() {
    FEnabled = false;
    FInputDescriptorNumber = 1;
    FOutputDescriptorNumber = 1;
    FMinVector    = null;
    FMaxVector    = null;
    TransformError = null;
    FDescriptorsRankThreshold = -1;
  };

  void Clear()  {
    TransformError = null;
    FMinVector    = null;
    FMaxVector    = null;
    FEnabled = false;
    FInputDescriptorNumber = 1;
    FOutputDescriptorNumber = 1;
  };

  public boolean FilterIsEnabled() { return FEnabled;}

  public void setEnabled(boolean value) {FEnabled = value;}

  void setInputDescriptorNumber(int n) {
          FInputDescriptorNumber = n;
          FMinVector = new double[n];
          FMaxVector = new double[n];
  }
  void setOutputDescriptorNumber(int n) {
      FOutputDescriptorNumber = n;
  }
  public double getMinValue(int index) { return FMinVector[index];}
  public double getMaxValue(int index) { return FMaxVector[index];}

  public double getDescriptorsRankThreshold() { return FDescriptorsRankThreshold;}

  public boolean InitializeFilter(Matrix points, int inputNo, int outputNo) {

  if (! FilterIsEnabled()) {
     FMinVector    = null;
     FMaxVector    = null;
     setInputDescriptorNumber(inputNo);
     setOutputDescriptorNumber(outputNo);
     boolean first = true;
     int N = points.getRowDimension();
     double a[][] = points.getArray();
     for (int i = 0; i < FInputDescriptorNumber; i++) {
         first = true;
         for (int j = 0; j < N; j++) {
             if (first || (a[j][i] < FMinVector[i]))
                 FMinVector[i] = a[j][i];
             if (first || (a[j][i] > FMaxVector[i]))
                 FMaxVector[i] = a[j][i];
             first = false;
         }
     };
     FEnabled = true;
     return true;
  } else return true;
  }

  public boolean InitializeFilter(Matrix points, int inputNo, int outputNo,
                  int[] rowsToProcess) {

  if (rowsToProcess == null) return InitializeFilter(points,inputNo,outputNo);
  else {
    int cols = points.getColumnDimension();
    Matrix m = points.getMatrix(rowsToProcess,0,cols-1);
    boolean result = InitializeFilter(m,inputNo,outputNo);
    m = null;
    return result;
  }
}
//----------------

    /**
     * Transforms points from original to transformed space
     * @param points
     * @return a new Matrix with transformed points
     */
  public Matrix TransformPoints(Matrix  points)  {
    return points.copy();
  }

  /**
   * Transforms points from original to transformed space
   * only nPoints with indices stored in pointsToProcess are evaluated
   * @param points
   * @param pointsToProcess
   * @return a new Matrix with transformed points
   */
    public Matrix TransformPoints(Matrix points,
                                    int[] pointsToProcess)  {
      if (pointsToProcess == null) return TransformPoints(points);
      else {
        int cols = points.getColumnDimension();
        Matrix m = points.getMatrix(pointsToProcess,0,cols-1);
        return TransformPoints(m);
      }
    }

  /**
   * Inverse transformation
   * @param points
   * @return Matrix
   */
  public Matrix InverseTransformPoints(Matrix points) {
       return null;
  }

/* ----------------- */
  public String toString() {
          return "Abstract transform filter";
  }

 // void toStatus(var slist : TSTringList); virtual;
  /*copies everything from another aFilter*/
  void Assign(TransformFilter aFilter) {
       Clear();
       setInputDescriptorNumber(aFilter.getInputDescriptorNumber());
       setOutputDescriptorNumber(aFilter.getOutputDescriptorNumber());
       for (int i = 0; i < FInputDescriptorNumber; i++) {
         FMinVector[i] = aFilter.getMinValue(i);
         FMaxVector[i] = aFilter.getMaxValue(i);
       }
       FEnabled = aFilter.FilterIsEnabled();
  }

  /**
   * Descriptors Rank
   * @return double array holding the rank of the descriptors in natural order
   */
  public double[] DescriptorsRank()  {
    double dL = FOutputDescriptorNumber;
    double rank[] = new double[FOutputDescriptorNumber];
    for (int i = 0; i < FOutputDescriptorNumber; i++) {
      double r = (dL - i) / dL;
      rank[i] = r;
    }
    return rank;
  }

  /**
   * MostImportantDescriptors
   * @return indices of most important descriptors
   * ( those with rank > FDescriptorsRankThreshold )
   */
  public int[] MostImportantDescriptors() {
    double rank[] = DescriptorsRank();
    int n = 0;
    for (int i = 0; i < FOutputDescriptorNumber; i++)
       if (rank[i] >= FDescriptorsRankThreshold) n++;
     int result[] = new int[n];
     n = 0;
     for (int i = 0; i < FOutputDescriptorNumber; i++)
        if (rank[i] >= FDescriptorsRankThreshold) {
          result[n] = i;
          n++;
        }
     return result;
  }

  int getInputDescriptorNumber() {return FInputDescriptorNumber;}
  int getOutputDescriptorNumber() {return FInputDescriptorNumber;}

  public Matrix substractVector(Matrix A, double B[]) {
    Matrix result = A.copy();
    double r[][] = result.getArray();
    int M = A.getRowDimension();
    int N = A.getColumnDimension();
    for (int i = 0; i < M; i++)
      for (int j = 0; j < N; j++)
         r[i][j] = A.get(i,j) - B[j];
    return result;
  }

  public boolean substractVectorEqual(Matrix A, double B[]) {
    double r[][] = A.getArray();
    int M = A.getRowDimension();
    int N = A.getColumnDimension();
    for (int i = 0; i < M; i++)
      for (int j = 0; j < N; j++)
         r[i][j] -= B[j];
    return true;
  }

  public Matrix addVector(Matrix A, double B[]) {
    Matrix result = A.copy();
    double r[][] = result.getArray();
    int M = A.getRowDimension();
    int N = A.getColumnDimension();
    for (int i = 0; i < M; i++)
      for (int j = 0; j < N; j++)
         r[i][j] = A.get(i,j) + B[j];
    return result;
  }

  public boolean addVectorEqual(Matrix A, double B[]) {
    double r[][] = A.getArray();
    int M = A.getRowDimension();
    int N = A.getColumnDimension();
    for (int i = 0; i < M; i++)
      for (int j = 0; j < N; j++)
         r[i][j] += B[j];
    return true;
  }

  public Matrix timesVector(Matrix A, double B[]) {
    Matrix result = A.copy();
    double r[][] = result.getArray();
    int M = A.getRowDimension();
    int N = A.getColumnDimension();
    for (int i = 0; i < M; i++)
      for (int j = 0; j < N; j++)
         r[i][j] = A.get(i,j) * B[j];
    return result;
  }
  public void timesVectorEqual(Matrix A, double B[]) {
    double r[][] = A.getArray();
    int M = A.getRowDimension();
    int N = A.getColumnDimension();
    for (int i = 0; i < M; i++)
      for (int j = 0; j < N; j++)
         r[i][j] = r[i][j] * B[j];
  }

/*
  property InputDescriptorNumber : int read FInputDescriptorNumber write setInputDescriptorNumber;
  property OutputDescriptorNumber : int read FOutputDescriptorNumber write setOutputDescriptorNumber;
  property MinValue[index : integer] : double read getMinValue;
  property MaxValue[index : integer] : double read getMaxValue;
  property TransformError :  int[]  read FTransformError;
*/
}

