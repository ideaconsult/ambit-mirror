/**
 * Created on 2005-3-29
 *
 */
package ambit2.domain;

import ambit2.data.AmbitObjectFixedValues;

/**
 * A center point for the distance applicability domain methods 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DatasetCenterPoint extends AmbitObjectFixedValues {

	public static final int _cmodeCenter = 1;
	public static final int _cmodeMean = 0;
	public static final int _cmodeOrigin = 2;	
	public static final String[] centerMode = {"Mean  ( i.e. sum(x1..xn)/n)","Center i.e. (max - min)/2","Origin of the coordinate system"};
	/**
	 * 
	 */
	public DatasetCenterPoint() {
		super();
		setName(centerMode[0]);
	}

	/**
	 * @param name
	 */
	public DatasetCenterPoint(String name) {
		super();
		setName(name);
	}

	/**
	 * @param name
	 * @param id
	 */
	public DatasetCenterPoint(String name, int id) {
		super(name, id);
	}
	public String[] predefinedvalues() {
		return centerMode;
	}		
}
