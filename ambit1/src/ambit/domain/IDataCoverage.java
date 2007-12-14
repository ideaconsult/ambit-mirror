/*
 Copyright (C) 2005-2006  

 Contact: nina@acad.bg

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation; either version 2.1
 of the License, or (at your option) any later version.
 All we ask is that proper credit is given for our work, which includes
 - but is not limited to - adding the above copyright notice to the beginning
 of your source code files, and to any copyright notice that you may distribute
 with programs based on this work.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
 */

package ambit.domain;


public interface IDataCoverage {

	public abstract boolean isEmpty();

	/**
	 * @param mode - as in ADomainMethodType
	 * _modeRANGE = 0, _modeLEVERAGE = 1; modeEUCLIDEAN = 2, 
	 * _modeCITYBLOCK = 3, _modeMAHALANOBIS = 4,  _modeDENSITY = 5;
	 * 
	 */
	public abstract void setMode(int mode);

	public abstract double getPThreshold();

	/**
	 * The percent of points in-domain from the training set [0 - 1]<br>
	 * percent - 1 means 100% , all points in
	 * @param athreshold
	 */
	public abstract void setPThreshold(double athreshold);
	/*
	public abstract boolean build(Object data);
	public abstract double[] predict(Object data);
	
	
	
	public abstract boolean buildStart();
	
	public abstract boolean buildCompleted();
	

	
*/
	public boolean build(AllData data);
	public double[] predict(AllData data) ;
	public abstract int getDomain(double coverage);

	public abstract int[] getDomain(double[] coverage);

	public abstract ADomainMethodType getAppDomainMethodType();

	public abstract void setAppDomainMethodType(
			ADomainMethodType appDomainMethodType);


}
