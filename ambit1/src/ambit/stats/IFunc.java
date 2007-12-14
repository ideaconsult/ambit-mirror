/**
 * Created on 2005-2-11
 *
 */
package ambit.stats;


/**
 * IFunc
 * @author Nina Jeliazkova
 * Modified: 2005-4-6
 *
 * Copyright (C) 2005  AMBIT project http://luna.acad.bg/nina/projects
 *
 * Contact: nina@acad.bg
 * 
 */
public interface IFunc {
	public double funcs(double x, double [] a,double [] derivatives, int na);
}
