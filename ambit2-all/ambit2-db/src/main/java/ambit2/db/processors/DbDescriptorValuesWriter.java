/* DbDescriptorValuesWriter.java
 * Author: Nina Jeliazkova
 * Date: May 5, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
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

package ambit2.db.processors;

import java.util.Arrays;

import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.BooleanResult;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerResult;

import ambit2.core.data.IntArrayResult;
import ambit2.core.data.LiteratureEntry;
import ambit2.core.data.StringDescriptorResultType;


/**
 * Writes descriptor values into properties/property_values tables
 * @author nina
 *
 */
public class DbDescriptorValuesWriter extends ValueWriter<DescriptorValue,DescriptorValue> {


    /**
	 * 
	 */
	private static final long serialVersionUID = 3453741433797677188L;

	@Override
    protected DescriptorValue transform(DescriptorValue target) {
    	return target;
    }
    
	@Override
	protected String getComments(DescriptorValue descriptor) {
		return descriptor.getSpecification().getImplementationIdentifier();
	}
	@Override
	protected Iterable<String> getPropertyNames(DescriptorValue descriptor) {
		return Arrays.asList(descriptor.getNames());
	}
	@Override
	protected LiteratureEntry getReference(DescriptorValue descriptor) {
		return new LiteratureEntry(descriptor.getSpecification().getImplementationTitle(),descriptor.getSpecification().getSpecificationReference());
	}

	@Override
	protected Object getValue(DescriptorValue descriptor,String propertyName, int propertyIndex) {
        IDescriptorResult result = descriptor.getValue();
        double value = Double.NaN;
        if (result instanceof DoubleResult) value = ((DoubleResult) result).doubleValue();
        else if (result instanceof IntegerResult) value = ((IntegerResult) result).intValue();
        else if (result instanceof BooleanResult) {
            if (((BooleanResult) result).booleanValue()) value = 1;
            else value = 0;
        }
        else if (result instanceof IntegerArrayResult) value = ((IntegerArrayResult) result).get(propertyIndex);
        else if (result instanceof IntArrayResult) value = ((IntArrayResult) result).get(propertyIndex);
        else if (result instanceof DoubleArrayResult) value = ((DoubleArrayResult) result).get(propertyIndex);
        else if (result instanceof StringDescriptorResultType) 
            return descriptor.getValue().toString();
        return new Double(value);    
	}

}
