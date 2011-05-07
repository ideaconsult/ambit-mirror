/* PropertyWriter.java
 * Author: nina
 * Date: Jan 9, 2009
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

package ambit2.db.processors;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.OperationNotSupportedException;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.property.RetrieveFieldNames;
import ambit2.db.update.property.CreateProperty;
import ambit2.db.update.property.ReadProperty;

public abstract class AbstractPropertyWriter<Target,Result> extends
		AbstractRepositoryWriter<Target, Result> {
	public enum mode  {OK, UNKNOWN,ERROR,TRUNCATED};
	protected CreateProperty propertyWriter;
    protected SourceDataset dataset = null;
    protected RetrieveFieldNames selectField = new RetrieveFieldNames();
    protected ReadProperty readProperty = new ReadProperty();
    
    public AbstractPropertyWriter() {
		super();
		selectField.setFieldname("name");
        propertyWriter = new CreateProperty();
	}

	public SourceDataset getDataset() {
		return dataset;
	}

	public void setDataset(SourceDataset dataset) {
		this.dataset = dataset;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3962356158979832113L;

 
   // protected abstract LiteratureEntry getReference(Target target);
    protected abstract Iterable<Property> getPropertyNames(Target target);
    protected abstract String getComments(String name,Target target);
    protected abstract void descriptorEntry(Target target,Property property, int propertyIndex,int idtuple) throws SQLException;

    protected int getTuple(SourceDataset dataset) {
    	return -1;
    }
    protected abstract Result transform(Target target) ;
    
    public Result write(Target target) throws SQLException, AmbitException, OperationNotSupportedException {

        Iterable<Property> names = getPropertyNames(target);
        int idtuple = getTuple(getDataset());
        int i=0;
        for (Property property: names) {
        	boolean found = false;
        	property.setId(-1);

	            selectField.setValue(property);
	            ResultSet rs1 = queryexec.process(selectField);
	            while (rs1.next()) {
	            	property = selectField.getObject(rs1);
	            	propertyEntry(property);	  
	                descriptorEntry(target, property,i,idtuple);
	                found = true;
	            }
	            queryexec.closeResults(rs1);
        	//}
            if (!found) {
            	
                String comments = getComments(property.getName(),target);
                if (comments == null)
                	property.setLabel(property.getName());
                else
                	property.setLabel(comments);

            	propertyEntry(property);	                	
                descriptorEntry(target,property,i,idtuple);
                
            }
            i++;
        }

        return transform(target);    	
    };
    
    protected  void propertyEntry(Property property) throws SQLException {
    	
    	write(property);

    }
    protected  void write(Property property) throws SQLException {
    	try {
    		if (property.getId()<=0) {
		    	propertyWriter.setObject(property);
		    	exec.process(propertyWriter);
    		}

    	} catch (Exception x) {
    		throw new SQLException(x.getMessage());
    	}
    }    
}
