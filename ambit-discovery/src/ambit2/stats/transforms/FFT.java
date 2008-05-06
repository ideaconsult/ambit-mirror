/**
 * FFT.java
 *
 * Created on November 4, 2003, 4:28 PM
 */

package ambit2.stats.transforms;

/**
 * Fast Fourie Transform class 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-2-2
 */
public class FFT {

  /** Creates a new instance of FFT class */
  public FFT() {
  }
 /**
  * FFT transform
  * @param NumSamples
  * @param RealIn
  * @param ImagIn
  * @param RealOut
  * @param ImagOut
  * @return true if successful
  */
  public boolean FFT1(int NumSamples,
                      /* must be a positive integer power of 2 */
                      double RealIn[], double ImagIn[],
                      double RealOut[], double ImagOut[]) {
    return FourierTransform(2 * Math.PI, NumSamples, RealIn, ImagIn, RealOut,
                            ImagOut);
  }
  /**
   * inverse FFT transform
   * @param NumSamples
   * @param RealIn
   * @param ImagIn
   * @param RealOut
   * @param ImagOut
   * @return true if successful
   */
  public boolean iFFT1(int NumSamples,
                       /* must be a positive integer power of 2 */
                       double RealIn[], double ImagIn[],
                       double RealOut[], double ImagOut[]) {

    boolean result = FourierTransform( -2 * Math.PI, NumSamples, RealIn, ImagIn,
                                      RealOut, ImagOut);

    /* Normalize the resulting time samples... */
    for (int i = 0; i < NumSamples; i++) {
      RealOut[i] = RealOut[i] / NumSamples;
      ImagOut[i] = ImagOut[i] / NumSamples;
    }
    return result;
  }

  private boolean IsPowerOfTwo(int x) {
    int y = 2;
    for (int i = 1; i <= 15; i++) {
      if (x == y) {
        return true;
      }
      ;
      y = y << 1;
    }
    ;
    return false;
  }

  private int NumberOfBitsNeeded(int PowerOfTwo) {

    for (int i = 0; i <= 16; i++) {
      if ( (PowerOfTwo & (1 << i)) != 0) {
        return i;
      }
    }
    return 16;
  }

  private int ReverseBits(int index, int NumBits) {
    int rev = 0;
    for (int i = 0; i < NumBits; i++) {
      rev = (rev << 1) | (index & 1);
      index = index >> 1;
    }
    ;
    return rev;
  }

  private boolean FourierTransform(
      double AngleNumerator,
      int NumSamples,
      double RealIn[], double ImagIn[],
      double RealOut[], double ImagOut[]) {

    int NumBits, k, n, BlockSize, BlockEnd;
    double delta_angle, delta_ar;
    double alpha, beta;
    double tr, ti, ar, ai;

    if (!IsPowerOfTwo(NumSamples) || (NumSamples < 2)) {
      //       write ( 'Error in procedure Fourier:  NumSamples=', NumSamples );
      //       writeln ( ' is not a positive integer power of 2.' );
      return false;
    }
    ;

    NumBits = NumberOfBitsNeeded(NumSamples);

    int i, j;
    for (i = 0; i < NumSamples; i++) {
      j = ReverseBits(i, NumBits);
      RealOut[j] = RealIn[i];
      ImagOut[j] = ImagIn[i];
    }
    ;

    BlockEnd = 1;
    BlockSize = 2;
    while (BlockSize <= NumSamples) {
      delta_angle = AngleNumerator / BlockSize;
      alpha = Math.sin(0.5 * delta_angle);
      alpha = 2.0 * alpha * alpha;
      beta = Math.sin(delta_angle);

      i = 0;
      while (i < NumSamples) {
        ar = 1.0; /* cos(0) */
        ai = 0.0; /* sin(0) */

        j = i;
        for (n = 0; n < BlockEnd; n++) {
          k = j + BlockEnd;
          tr = ar * RealOut[k] - ai * ImagOut[k];
          ti = ar * ImagOut[k] + ai * RealOut[k];
          RealOut[k] = RealOut[j] - tr;
          ImagOut[k] = ImagOut[j] - ti;
          RealOut[j] = RealOut[j] + tr;
          ImagOut[j] = ImagOut[j] + ti;
          delta_ar = alpha * ar + beta * ai;
          ai = ai - (alpha * ai - beta * ar);
          ar = ar - delta_ar;
          j++;
        }
        ;

        i = i + BlockSize;
      }

      BlockEnd = BlockSize;
      BlockSize = BlockSize << 1;
    }
    return true;
  }
  /**
   * Convolution
   * @param data
   * @param n
   * @param kernel
   * @param nk
   * @return true if successful
   */
  public boolean convolution(double data[], int n, double kernel[], int nk) {
    /*
        var rDataIn, iDataIn, rDataOut, iDataOut : PBuffer;
            rKernelOut, iKernelOut : PBuffer;
            i : integer;
            rPart, iPart : double;
            FreqIndex : integer;
     */
    boolean result = true;
    double rDataOut[];
    double iDataIn[];
    double iDataOut[];

    iDataIn = new double[n];
    rDataOut = new double[n];
    iDataOut = new double[n];
    for (int i = 0; i < n; i++) {
      iDataIn[i] = 0;
    }
    result = FFT1(n, data, iDataIn, rDataOut, iDataOut);
    double iKernelIn[] = new double[nk];
    double rKernelOut[] = new double[nk];
    double iKernelOut[] = new double[nk];

    for (int i = 0; i < nk; i++) {
      iKernelIn[i] = 0;
    }
    result = result && FFT1(nk, kernel, iKernelIn, rKernelOut, iKernelOut);
    iKernelIn = null;

    int FreqIndex = n; // Math.Floor(n / 4);
    int n2 = (int) Math.floor(n / 2);
    double rPart, iPart;
    for (int i = 0; i < n; i++) {
      if ( ( (i > FreqIndex) && (i < (n2))) ||
          ( (i >= (n2)) && (i < n - FreqIndex))) {
        rDataOut[i] = 0;
        iDataOut[i] = 0;
      }
      else {
        rPart = rDataOut[i] * rKernelOut[i] - iDataOut[i] * iKernelOut[i];
        iPart = rDataOut[i] * iKernelOut[i] + rKernelOut[i] * iDataOut[i];
        rDataOut[i] = rPart;
        iDataOut[i] = iPart;
      }
      ;
    }

    result = result && iFFT1(n, rDataOut, iDataOut, data, iDataIn);

    iDataIn = null;
    rKernelOut = null;
    iKernelOut = null;
    iDataOut = null;
    rDataOut = null;
    return result;
  };

} 
