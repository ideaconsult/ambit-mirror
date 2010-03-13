/**
 * Created on 2005-3-29
 *
 */
package ambit2.model.numeric;


/**
 * A center point for the distance applicability domain methods 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public enum DatasetCenterPoint {
	_cmodeMean {
		@Override
		public String toString() {
			return "Mean  ( i.e. sum(x1..xn)/n)" ;
		}			
	},
	_cmodeCenter {
		@Override
		public String toString() {
			return "Center i.e. (max - min)/2";
		}			
	},
	
	_cmodeOrigin {
		@Override
		public String toString() {
			return "Origin of the coordinate system";
		}	
	};
	
}
