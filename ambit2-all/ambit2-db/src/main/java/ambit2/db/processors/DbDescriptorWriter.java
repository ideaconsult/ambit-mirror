/* DbDescriptorWriter.java
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
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.qsar.DescriptorValue;

import ambit2.base.data.Property;
import ambit2.descriptors.processors.DescriptorsFactory;



public class DbDescriptorWriter extends AbstractPropertyWriter<DescriptorValue,DescriptorValue> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -358115974932302101L;

	public DbDescriptorWriter() {
		super();
	}
    @Override
    protected DescriptorValue transform(DescriptorValue target) {
    	return target;
    }
    @Override
    protected void descriptorEntry(DescriptorValue target, Property property,
    		 int propertyIndex, int idtuple) throws SQLException {
    	
    }

	@Override
	protected String getComments(String name,DescriptorValue descriptor) {
		//descriptorDictionary.setParentTemplate(descriptor.getSpecification().getSpecificationReference());
		//descriptorDictionary.setTemplate(name);
		return null;//descriptorDictionary;
	}
	@Override
	protected Iterable<Property> getPropertyNames( DescriptorValue value) {
		List<Property> p = new ArrayList<Property>();
		for (String name : value.getNames()) try {
			if ("".equals(name)) continue;
			p.add(DescriptorsFactory.descriptorValue2Property(null,name,value));
		} catch (Exception x) {}
		return p;

	}


}
