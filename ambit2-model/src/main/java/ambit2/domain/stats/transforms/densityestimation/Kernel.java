package ambit2.domain.stats.transforms.densityestimation;

import java.io.Serializable;



/**
 * Abstract kernel 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
abstract public class Kernel implements Serializable {
  /**
	 * 
	 */
	private static final long serialVersionUID = 4014631502430481861L;
   public String KernelName = "";
  public double multiplier = 1.0;
  //to be used in KDE1.estimatedensity
  public double R = 4.0;
  /*
       if (k='knorm')  or  (k='cnorm')  or  (k='clapl')  or  (k='clogi') then r :=4
             else if (k='klogi')  or  (k='klapl') then r :=7
                  else if (k='cbiwe')  or  (k='ctriw') then r :=3
       else if (k='cepan')  or  (k='ctria')  or  (k='crect') then r:=2
                       else r:=1;
   */
  public Kernel() {
  }

  public double getAmplitude(double x) {
    return 1;
  }

}
