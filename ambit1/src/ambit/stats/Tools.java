package ambit.stats;

import java.util.Vector;

import ambit.stats.datastructures.Sort;



/**
 * <p>Title: Various statistical routines</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Nina Nikolova
 * @version 1.0
 */


public class Tools {
  public Tools() {
  }
  /**
   * Standard deviation
   * @param x array of double
   * @param n number of points in the array
   * @return standard deviation
   */
  public static double std(double x[], int n) {

    if (n <= 1) return 0;
    double m = mean(x, n);
    double sum = 0;
    for (int i = 0; i < n; i++)
      sum += (x[i] - m) * (x[i] - m);
    return Math.sqrt(sum / (n - 1));
  }

  /**
   * @param x vector double[]
   * @param n length of the vector
   * @return standard mean of a vector
   */
  public static double mean(double x[], int n) {
    if (n <= 0) return 0;
    double sum = 0;
    for (int i = 0; i < n; i++) sum += x[i];
    return sum / n;
  }

  public static double min(double x[], int n) {
    if (n < 0) return 0;
    double tmp = x[0];
    for (int i = 0; i < n; i++)
          if (tmp > x[i]) tmp = x[i];
    return tmp;
  };

  public static double max(double x[], int n) {
    if (n < 0) return 0;
    double tmp = x[0];
    for (int i = 0; i < n; i++)
          if (tmp < x[i]) tmp = x[i];
    return tmp;
  };

  /**
   * @param x double[] vector
   * @param n vector size
   * @return the sample interquarile range (IQR) of X
   */
  public static double siqr(double x[], int n) {
    /*
  var mlo,mhi : integer;
      zlo,zhi : double;
      order : TDoubleVector;
      i : integer;
*/
    double order[] = new double[n];
    for (int i = 0; i < n; i++)
      order[i] = x[i];

    Sort sort = new Sort();
    sort.QuickSortArray(order, n);
    sort = null;

    int mlo = (int) Math.floor(n / 4) + 1;
    int mhi = (int) Math.floor( (3 * n) / 4) + 1;
    double zlo, zhi;

  if ((mlo>=0) && (mlo<(n-1))) {
    zlo = (order[mlo]+order[mlo+1])/2;

    if ((mhi>=0) && (mhi<(n-1)))  {
      zhi = (order[mhi]+order[mhi+1])/2;
      return zhi - zlo;
    };

  };
  return 0;
  }; //siqr

  public static boolean ExtendedInterval(double data[], int number, double datarange[]) {

    datarange[0] = 0; datarange[1] = 0;
    for (int r = 0; r < number; r++) {
      if ((r==0) || (data[r] < datarange[0])) datarange[0] = data[r];
      if ((r==0) || (data[r] > datarange[1])) datarange[1] = data[r];
    }
    double d = 0;
    if (number > 0)  d = std(data,number);
    else d = (datarange[1] - datarange[0]) / 6;

    datarange[0] = datarange[0] - d;
    datarange[1] = datarange[1] + d;
    return (d > 0);
  };

  public static boolean GaussianClusters(double datamatrix[][],
                   int npoints, int nfactors,
                   double[] meanvector, int nclusters) {
/*
  var i,j,m,k : integer;
      value : double;
      u1,u2 : double;
      mean,scale : TDoubleVector;
      npoints : integer;
      NumberOfFactors : integer;
*/

  int j = (int) Math.floor(npoints / nclusters);

  double mean[] = new double[nfactors];
  double scale[] = new double[nfactors];

  for (int m = 1; m <=nclusters; m++) {
      for (int k = 0;k < nfactors; k++) {
          mean[k] = meanvector[k];
          scale[k] = 1;
      }

      for (int i = (m-1)*j; i <= ((m*j)-1) ; i++) {
             if (i >=  npoints)  continue;
             double u1, u2, value;
             for (int k = 0; k < nfactors; k= k+2) {

                 u1 = Math.random();
                 u2 = Math.random();
                 value = Math.sqrt(-2*Math.log(u1));
                 datamatrix[i][k] = value * Math.cos(2*Math.PI*u2);
                 datamatrix[i][k] = scale[k] * datamatrix[i][k] + mean[k];

                 if ((k+1) < nfactors) {
                   datamatrix[i][k+1] = value * Math.sin(2*Math.PI*u2) ;
                   datamatrix[i][k+1] = scale[k+1] * datamatrix[i][k+1] + mean[k+1];
                 }
             }
          }
  }
  mean = null; scale = null;
  return true;
  }

	/**
	 * 
	 * @param points
	 * @return double[]
	 */
	public static double[] max(double[][] points) {
		if (points == null) return null;
		int np = points.length;
		if (np == 0) return null;
		int nd = points[0].length; 
		double[] m = new double[nd];
		for (int i = 0; i < np; i++)
			for (int j = 0; j < nd; j++) {
				if ((i==0) || (m[j]<points[i][j]))
					m[j] = points[i][j];
			}
		return m;	
	}
	
	public static double[] min(double[][] points) {
		if (points == null) return null;
		int np = points.length;
		if (np == 0) return null;
		int nd = points[0].length; 
		double[] m = new double[nd];
		for (int i = 0; i < np; i++)
			for (int j = 0; j < nd; j++) {
				if ((i==0) || (m[j]>points[i][j]))
					m[j] = points[i][j];
			}
		return m;	
	}

	public static double[] min(Vector points) {
		if (points == null) return null;
		int np = points.size();
		if (np == 0) return null;
		double[] arow = (double[]) points.get(0);
		int nd = arow.length; 
		double[] m = new double[nd];
		for (int i = 0; i < np; i++) {
			arow = (double[]) points.get(i);
			for (int j = 0; j < nd; j++) {
				if ((i==0) || (m[j]> arow[j]))
					m[j] = arow[j];
			}
		}
		return m;	
	}
	public static double[] max(Vector points) {
		if (points == null) return null;
		int np = points.size();
		if (np == 0) return null;
		double[] arow = (double[]) points.get(0);
		int nd = arow.length; 
		double[] m = new double[nd];
		for (int i = 0; i < np; i++) {
			arow = (double[]) points.get(i);
			for (int j = 0; j < nd; j++) {
				if ((i==0) || (m[j]< arow[j]))
					m[j] = arow[j];
			}
		}
		return m;	
	}	
	public static double[] mean(double[][] points) {
		
		if (points == null) return null;
		int np = points.length;
		if (np == 0) return null;
		int nd = points[0].length; 
		double[] m = new double[nd];
		for (int j = 0; j < nd; j++) m[j] = 0;
		
		for (int i = 0; i < np; i++)
			for (int j = 0; j < nd; j++) {
				if ((i==0) || (m[j]>points[i][j]))
					m[j] += points[i][j];
			}
		
		for (int j = 0; j < nd; j++) m[j] = m[j]/np;		
		return m;	
	}
	
	public static double[] mean(Vector points) {
		if (points == null) return null;
		int np = points.size();
		if (np == 0) return null;
		double[] arow = (double[]) points.get(0);
		int nd = arow.length; 
		double[] m = new double[nd];
		for (int j = 0; j < nd; j++) m[j]=0;
		
		for (int i = 0; i < np; i++) {
			arow = (double[]) points.get(i);
			for (int j = 0; j < nd; j++) 
				m[j] += arow[j];
		}
		for (int j = 0; j < nd; j++) m[j] = m[j]/np;		
		return m;	
	}		
	public static long bins(int numberOfPoints) {
		return Math.round(1+3.332*Math.log(numberOfPoints));
	}
	public static double Sxx(Vector points, double[] mx) {
		int n = points.size();
		if (n == 0) return 0;
		int d = mx.length;
		double sxx = 0;
		double sx = 0;
		double[] x=null;
		for (int i = 0; i < n; i++) {
			sx = 0;
			x = (double[]) points.get(i); 
			for (int j=0; j < d; j++) {
				sx += x[j] - mx[j];
			}
			sxx += sx*sx;
		}
		return sxx/n;
	}
	public static double Syy(double[] y, double my) {
		double syy = 0;
		for (int i =0; i < y.length; i++) 
			syy += (y[i]-my)*(y[i]-my);
		return syy;
	}
	//standard error
	public static double sse(double[] predicted, double[] observed) {
		double s = 0;
		for (int i =0; i < predicted.length; i++)
			s += Math.pow(predicted[i]-observed[i],2);
		return s;

	}
	//standard deviation of residuals
	public static double sres(double sse, int n) {
		return Math.sqrt(sse/(n-2));
	}
}