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
import java.util.Arrays;

import org.openscience.cdk.qsar.DescriptorValue;

import ambit2.core.data.Dictionary;
import ambit2.core.data.LiteratureEntry;



public class DbDescriptorWriter extends AbstractPropertyWriter<DescriptorValue,DescriptorValue> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -358115974932302101L;

	/*
    @Override
    public DescriptorValue write(DescriptorValue descriptor) throws SQLException {

        LiteratureEntry le = new LiteratureEntry(descriptor.getSpecification().getImplementationTitle(),descriptor.getSpecification().getSpecificationReference());
        le = referenceWriter.write(le);
        
        String[] names = descriptor.getNames();
        for (int i=0; i < names.length; i++) {
            ps_selectdescriptor.clearParameters();
            ps_selectdescriptor.setString(1, names[i]);
            ps_selectdescriptor.setInt(2, le.getId());
            
            boolean found = false;
            ResultSet rs1 = ps_selectdescriptor.executeQuery();
            while (rs1.next()) {
                descriptorEntry(descriptor,rs1.getInt(1),i);
                found = true;
            }
            rs1.close();
            
            if (!found) {
                ps_descriptor.clearParameters();
                ps_descriptor.setInt(1,le.getId());
                ps_descriptor.setString(2,names[i]);
                ps_descriptor.setNull(3,Types.VARCHAR);
                ps_descriptor.setString(4,descriptor.getSpecification().getImplementationIdentifier());
                ps_descriptor.executeUpdate();
                ResultSet rs = ps_descriptor.getGeneratedKeys();
    
                while (rs.next()) {
                    descriptorEntry(descriptor,rs.getInt(1),i);
                } 
                rs.close();
            }
        }

        return descriptor;
    }
    */
    @Override
    protected DescriptorValue transform(DescriptorValue target) {
    	return target;
    }
    @Override
    protected void descriptorEntry(DescriptorValue target, int idproperty,
    		String propertyName, int propertyIndex, int idtuple) throws SQLException {
    	
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
	protected Dictionary getTemplate(DescriptorValue target)
			throws SQLException {
		return new Dictionary("Descriptors","All");
	}

}
