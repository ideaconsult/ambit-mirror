package ambit2.test.stats;

import junit.framework.TestCase;
import ambit2.stats.transforms.FFT;


/**
 * JUnit test for {@link FFT} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class TestFFT
    extends TestCase {
  private FFT fFT = null;
  private static double zero = 1e-14;

  public TestFFT(String name) {
    super(name);
  }

  public void testFFT1() {
    fFT = new FFT();
    int NumSamples = 4096;
    double RealIn[] = new double[NumSamples];
    double ImagIn[] = new double[NumSamples];
    double RealOut[] = new double[NumSamples];
    double ImagOut[] = new double[NumSamples];
    double iRealOut[] = new double[NumSamples];
    double iImagOut[] = new double[NumSamples];

    for (int i = 0; i < NumSamples; i++) {
      RealIn[i] = Math.sin(16 * Math.PI * i / NumSamples) + Math.random();
      ImagIn[i] = 0;
    }
    assertEquals(true, fFT.FFT1(NumSamples, RealIn, ImagIn, RealOut, ImagOut));

    assertEquals(true,
                 fFT.iFFT1(NumSamples, RealOut, ImagOut, iRealOut, iImagOut));
    boolean r = true;
    for (int i = 0; i < NumSamples; i++) {
      r = r && (Math.abs(RealIn[i] - iRealOut[i]) <= zero) &&
          (Math.abs(ImagIn[i] - iImagOut[i]) <= zero);

    }
    assertEquals(true, r);
    fFT = null;

  }

  protected void setUp() throws Exception {
    super.setUp();
    /**@todo verify the constructors*/
    fFT = new FFT();
    assertNotNull(fFT);
  }

  protected void tearDown() throws Exception {
    fFT = null;
    super.tearDown();
  }

}
