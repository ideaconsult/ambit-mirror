package ambit2.stats.transforms.densityestimation;

import ambit2.stats.transforms.FFT;
import ambit2.stats.transforms.Misc;


/**
 * Kernel Density Estimation Package 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class KDensity1D {
  private double Fmin, Fmax, FStep;
  private double Resolution = 0;
  private double kernelH = 0;
  private double FGrid[] = null;
  protected static double eps = 1.0E-300;
  protected boolean FInterpolate = true;
  public KDensity1D() {
  }

  public double getGrid(int index) {
    return FGrid[index];
  }

  public boolean estimateDensity(double a[], int n, Kernel k, int ngrid,
                                 double h, double ax, double bx, double weight[]) {
    /*
        var P, r : integer;
            xa : TDoubleVector;        // size of ngrid
            c  : PBuffer;        // size of ngrid
            deltax : double;
            binx : TIntegerVector;  // size of n
            L : integer;
            kw,temp : PBuffer;    //size of 2*ngrid
            i : integer;
            tmpGrid : PBuffer;
            err : ext}ed;
            minErr : ext}ed;
     */
    FStep = 0;
    if (ax == bx) {
      return false;
    }
    Fmin = ax;
    Fmax = bx;
    Resolution = ngrid;
    FStep = (Fmax - Fmin) / ngrid;

    int ngrid2 = 2 * ngrid;
    if (h == 0) {
      h = Bandwidth.hns(a, n, k);
      if (h == 0) {
        return false;
      }
    }
    kernelH = h;
    double xa[] = new double[ngrid];
    Misc m = new Misc();
    m.linspace(ax, bx, ngrid, xa);

    FGrid = new double[ngrid];
    for (int i = 0; i < ngrid; i++) {
      FGrid[i] = 0.0;

    }
    double deltax = (bx - ax) / (ngrid - 1);
    int binx[] = new int[n];

    for (int i = 0; i < n; i++) {
      binx[i] = (int) Math.floor( (a[i] - ax) / deltax);
//              if (binx[i] >= ngrid) binx[i] = ngrid-1;

    }
    ;
    /*
            if weight <> nil then
              for i :=0 to n-1 do { // Loop over data points in x direction.
         c[binx[i]-1] := c[binx[i]-1] + weight[i]*(xa[binx[i]]-AD[i])/deltax;
         c[binx[i]] := c[binx[i]]+ weight[i]*(AD[i]-xa[binx[i]-1])/deltax;
              }
            else
     */
    for (int i = 0; i < n; i++) { // Loop over data points in x direction.
      if ( (binx[i] + 1) < ngrid) {
        FGrid[binx[i]] = FGrid[binx[i]] + (xa[binx[i] + 1] - a[i]) / deltax;
        FGrid[binx[i] + 1] = FGrid[binx[i] + 1] + (a[i] - xa[binx[i]]) / deltax;
      }
    }
    ;

    binx = null;

    int L = Math.min( (int) Math.floor(k.R * h * (ngrid - 1) / (bx - ax)),
                     ngrid);

    double kw[] = new double[ngrid2];
    for (int i = 0; i < ngrid2; i++) {
      kw[i] = 0;

    }
    double temp[] = new double[L];
    for (int i = 0; i < L; i++) {
      temp[i] = k.getAmplitude( (bx - ax) * i / ( (ngrid - 1) * h)) / (n * h);

    }
    for (int i = ngrid; i < ngrid + L; i++) {
      kw[i] = temp[i - ngrid];
    }
    for (int i = ngrid - L; i < ngrid; i++) {
      kw[i] = temp[ngrid - i - 1];
    }
    temp = null;
    m.fftshift(kw, ngrid2);
    m = null;

    double tmpGrid[] = new double[ngrid2];
    for (int i = 0; i < ngrid; i++) {
      tmpGrid[i] = FGrid[i];
    }
    for (int i = ngrid; i < ngrid2; i++) {
      tmpGrid[i] = 0;

    }
    FFT fft = new FFT();
    fft.convolution(tmpGrid, ngrid2, kw, ngrid2);
    fft = null;

    for (int i = 0; i < ngrid; i++) {
      if (tmpGrid[i] < 0) {
        FGrid[i] = 0;
      }
      else {
        FGrid[i] = tmpGrid[i];
        /*
             for (int i = 0; i < ngrid2; i++) if (FGrid[i] < 0) FGrid[i] = 0;
                 double err = 0;
                 for (int i = 0; i < ngrid; i++)
                     err += Math.sqrt(tmpGrid[i]) + Math.sqrt(FGrid[i]);
                 err *= FStep;
                 double Ferr = 0;
                 for (int i = 0; i < ngrid; i++)
                     Ferr += (tmpGrid[i] - FGrid[i])*(tmpGrid[i] - FGrid[i]);
                 Ferr /= ngrid;
                 Ferr += err;
         */
      }
    }
    tmpGrid = null;
    xa = null;
    kw = null;

    normalize();
    return true;

  }
  /**
   * clear()
   */
  public void clear() {   FGrid = null;  }

  /**
   * normalize()
   * @return double
   */
  public double normalize() {

    if (FGrid == null) {
      return 0;
    }
    double sum = 0;
    for (int i = 0; i < Resolution; i++) {
      sum += FGrid[i];

    }
    sum *= (Fmax - Fmin) / Resolution;

    if (sum > 0) {
      for (int i = 0; i < Resolution; i++) {
        FGrid[i] = FGrid[i] / sum;

      }
    }
    return sum;
  }

  /**
   *
   * @return kernel bandwidth
   */
  public double getKernelH() {    return kernelH;  }

  /**
   *
   * @return step
   */
  public double getStep() {    return FStep;  }

  public double getMinValue() {  return Fmin;  }
  public double getMaxValue() {  return Fmax; }
  public double maxAmplitude() {
          double result = FGrid[0];
          for (int i = 1 ; i < Resolution; i++)
           if (result < FGrid[i]) result = FGrid[i];
          return result;
  }

  double getMostDenseVector() {
    double resultM = FGrid[0];
    double resultX = Fmin;
    for (int i = 1 ; i < Resolution; i++)
     if (resultM < FGrid[i]) {
       resultM = FGrid[i];
       resultX = Fmin + (i*FStep);
     }
    return resultX;
  }

  public double getGridPoint(double x) {

     if (FStep < eps) return eps;
     if (FGrid == null) return eps;
     if ((x < Fmin) || (x > Fmax))  return eps;
     double result;
     double tmp = (x - Fmin) / FStep;
     int left; int right;
     if (tmp > eps) left = (int) Math.floor(tmp);
     else left = 0;

     if (FInterpolate) {
           right = left + 1;
           if (right >= Resolution) result = FGrid[left];
              else {
                 double y1, y2, x1, x2;
                 y1 = FGrid[left]; y2 = FGrid[right];
                 x1 = Fmin + FStep * left;
                 x2 = x1 + FStep;

                 tmp = (x2 - x1);
                 if (tmp < eps) result = FGrid[left];
                 else result = (y2-y1)*(x-x1)/tmp + y1;

                 if (result < eps) result = eps;
              }
     } else result = FGrid[left];
      if (result < eps) result = eps;
     return result;
  }



}