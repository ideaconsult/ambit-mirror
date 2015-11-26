/* DescriptorValue2Property.java
 * Author: nina
 * Date: Apr 18, 2009
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.BooleanResult;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerResult;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.AbstractDescriptorResultType;
import ambit2.core.data.ArrayResult;
import ambit2.descriptors.VerboseDescriptorResult;

public class DescriptorValue2Property extends DefaultAmbitProcessor<DescriptorValue, IStructureRecord> {
	/**
	 * 
	 */
	protected enum answer {YES,NO};
	private static final long serialVersionUID = -5299954753073067252L;
	protected IStructureRecord structure;
	public IStructureRecord getStructure() {
		return structure;
	}
	public void setStructure(IStructureRecord structure) {
		this.structure = structure;
	}
	public IStructureRecord process(DescriptorValue descriptor) throws AmbitException {
		IStructureRecord record = getStructure();
		if (record == null) record = new StructureRecord();
		int i=0;		
		for (Property property:getPropertyNames(descriptor)) {
			record.setRecordProperty(property,getValue(descriptor, property, i));
			i++;
		}
		return record;
	}
	
	public Iterable<Property> getPropertyNames(DescriptorValue value) {
		List<Property> p = new ArrayList<Property>();

		for (String name: value.getNames()) try {
			Property property = DescriptorsFactory.descriptorValue2Property(null, name, value);
			p.add(property);				

		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		return p;
	}	
	
	public Object getValue(DescriptorValue descriptor,Property property, int propertyIndex) {
		if (descriptor.getException()!=null) {
			Throwable x = descriptor.getException();
			while (x.getCause()!=null)	x = x.getCause();
			return x;
		}	
        IDescriptorResult result = descriptor.getValue();
        Object value = Double.NaN;
        if (result instanceof VerboseDescriptorResult)
        	result = ((VerboseDescriptorResult)result).getResult();
        if (result instanceof DoubleResult) value = ((DoubleResult) result).doubleValue();
        else if (result instanceof IntegerResult) value = ((IntegerResult) result).intValue();
        else if (result instanceof BooleanResult) {
            value = (((BooleanResult) result).booleanValue())?answer.YES.toString():answer.NO.toString();
        }
        else if (result instanceof ArrayResult) value = ((ArrayResult) result).get(propertyIndex);
        else if (result instanceof IntegerArrayResult) value = ((IntegerArrayResult) result).get(propertyIndex);
        else if (result instanceof DoubleArrayResult) value = ((DoubleArrayResult) result).get(propertyIndex);
        else if (result instanceof AbstractDescriptorResultType) 
            return ((AbstractDescriptorResultType)result).getValue();
        return value;    
	}	

}
