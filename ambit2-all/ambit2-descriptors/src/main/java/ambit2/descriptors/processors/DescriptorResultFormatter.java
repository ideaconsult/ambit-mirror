/* DescriptorREsultFormatter.java
 * Author: nina
 * Date: Feb 5, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.descriptors.processors;

import java.text.NumberFormat;
import java.util.Locale;

import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerResult;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;

/**
 * Prints {@link DescriptorValue} as a string
 * @author nina
 *
 */
public class DescriptorResultFormatter extends
		DefaultAmbitProcessor<DescriptorValue, String> {
	protected NumberFormat format = NumberFormat.getNumberInstance(); //new DecimalFormat("#####.####");
	//protected Locale locale = new Locale("us", "US");
	protected int index = -1;
	public DescriptorResultFormatter() {
	}
	public DescriptorResultFormatter(Locale locale) {
		this();
		setLocale(locale);
	}
	
	public void setLocale(Locale locale) {
		format = NumberFormat.getNumberInstance(locale);
	}
	/**
	 * Index of the array, if the Descrptor value is of type DoubleArrayResult or IntegerArrayResult
	 * @return
	 */
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8879440610070527255L;

	public String process(DescriptorValue target) throws AmbitException {
		
		IDescriptorResult result = target.getValue();
		if (result instanceof DoubleResult)
			return format.format(
				((DoubleResult)(target.getValue())).doubleValue());
		else if (result instanceof IntegerResult)
			return Integer.toString(((IntegerResult)(target.getValue())).intValue());
		else if (result instanceof DoubleArrayResult) {
			DoubleArrayResult array = (DoubleArrayResult) result;
			if ((index >= 0) && (index<array.length()))
				return format.format(array.get(index));
		} else if (result instanceof IntegerArrayResult) {
			IntegerArrayResult array = (IntegerArrayResult) result;
			if ((index >= 0) && (index<array.length()))
				return format.format(array.get(index));
		}		
		//catch all
		return result.toString();
	}

}
