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

import java.sql.SQLException;

import org.openscience.cdk.qsar.DescriptorValue;

import ambit2.base.data.Property;
import ambit2.descriptors.processors.DescriptorValue2Property;


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

	protected DescriptorValue2Property helper;

	public DbDescriptorValuesWriter() {
		helper = new DescriptorValue2Property();
	}
	@Override
    protected DescriptorValue transform(DescriptorValue target) {
    	return target;
    }
    
	@Override
	protected String getComments(String name,DescriptorValue descriptor) {
		return null;
	}
	@Override
	protected Iterable<Property> getPropertyNames(DescriptorValue descriptor) {
		return helper.getPropertyNames(descriptor);
	}


	@Override
	protected Object getValue(DescriptorValue descriptor,Property property, int propertyIndex) {
		return helper.getValue(descriptor, property, propertyIndex);

	}

	@Override
	protected boolean insertValue(double value, Property property, int idtuple,
			ambit2.db.processors.AbstractPropertyWriter.mode error)
			throws SQLException {
		return super.insertValue(value, property, idtuple, (error==mode.ERROR)?AbstractPropertyWriter.mode.ERROR:AbstractPropertyWriter.mode.OK);
	}

	@Override
	protected boolean insertValue(String value, Property property, int idtuple,
			ambit2.db.processors.AbstractPropertyWriter.mode error)
			throws SQLException {
		return super.insertValue(value, property, idtuple, (error==mode.ERROR)?AbstractPropertyWriter.mode.ERROR:AbstractPropertyWriter.mode.OK);
	}
}
