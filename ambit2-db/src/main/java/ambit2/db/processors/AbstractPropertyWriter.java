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
import java.util.logging.Level;

import javax.naming.OperationNotSupportedException;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.SourceDataset;
import ambit2.db.search.property.RetrieveFieldNames;
import ambit2.db.update.dictionary.DatasetTemplateAddProperty;
import ambit2.db.update.property.CreateProperty;
import ambit2.db.update.property.ReadProperty;
import ambit2.db.update.propertyannotations.CreatePropertyAnnotation;

public abstract class AbstractPropertyWriter<Target, Result> extends AbstractRepositoryWriter<Target, Result> {
    public enum mode {
	OK, UNKNOWN, ERROR, TRUNCATED
    };

    protected CreatePropertyAnnotation annotationsWriter;
    protected CreateProperty propertyWriter;
    protected DatasetTemplateAddProperty templateDefWriter;
    protected SourceDataset dataset = null;
    protected RetrieveFieldNames selectField = new RetrieveFieldNames();
    protected ReadProperty readProperty = new ReadProperty();

    public AbstractPropertyWriter() {
	super();
	selectField.setFieldname("name");
	selectField.setPage(0);
	selectField.setPageSize(1);
	propertyWriter = new CreateProperty();
	templateDefWriter = new DatasetTemplateAddProperty();
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

    protected abstract String getComments(String name, Target target);

    protected abstract void descriptorEntry(Target target, Property property, int propertyIndex, int idtuple)
	    throws SQLException;

    protected int getTuple(SourceDataset dataset) {
	return -1;
    }

    protected abstract Result transform(Target target);

    public Result write(Target target) throws SQLException, AmbitException, OperationNotSupportedException {

	Iterable<Property> names = getPropertyNames(target);
	int idtuple = getTuple(getDataset());
	int i = 0;
	for (Property property : names) {
	    boolean found = false;
	    property.setId(-1);

	    selectField.setValue(property);

	    ResultSet rs1 = queryexec.process(selectField);
	    try {
		while (rs1.next()) {
		    property = selectField.getObject(rs1);
		    propertyEntry(property);
		    descriptorEntry(target, property, i, idtuple);
		    found = true;
		}
	    } catch (Exception x) {
		logger.log(Level.WARNING, target.toString(), x);
	    } finally {
		try {
		    queryexec.closeResults(rs1);
		} catch (Exception x) {
		}
	    }
	    // }
	    if (!found) {
		if ((property.getLabel() == null) || property.getLabel().equals(property.getName())) {
		    String comments = getComments(property.getName(), target);
		    if (comments == null)
			property.setLabel(property.getName());
		    else
			property.setLabel(comments);
		} // otherwise it is already set
		propertyEntry(property);
		descriptorEntry(target, property, i, idtuple);

	    }
	    i++;
	}

	return transform(target);
    };

    protected void propertyEntry(Property property) throws SQLException {

	write(property);

    }

    protected void write(Property property) throws SQLException {
	try {
	    if (property.getId() <= 0) {
		propertyWriter.setObject(property);
		exec.process(propertyWriter);

		if (property.getAnnotations() != null)
		    try {
			if (annotationsWriter == null)
			    annotationsWriter = new CreatePropertyAnnotation();
			annotationsWriter.setGroup(property);
			for (PropertyAnnotation a : property.getAnnotations()) {
			    if (a.getObject() instanceof Property) {
				Property linkedProperty = (Property) a.getObject();
				if (linkedProperty.getId() <= 0) {
				    propertyWriter.setObject(linkedProperty);
				    exec.process(propertyWriter);
				}
				if (linkedProperty.getId() > 0) {
				    PropertyAnnotation<String> pa = new PropertyAnnotation<String>();
				    pa.setPredicate(a.getPredicate());
				    pa.setType(a.getType());
				    pa.setIdproperty(property.getId());
				    pa.setObject(String.format("/feature/%d", linkedProperty.getId()));
				    annotationsWriter.setObject(pa);
				    exec.process(annotationsWriter);
				}
			    } else {
				annotationsWriter.setObject(a);
				exec.process(annotationsWriter);
			    }
			}

		    } catch (Exception x) {
			logger.log(Level.WARNING, x.getMessage(), x);
		    }
	    }
	    if (property.getId() > 0 && dataset != null && dataset.getID() > 0) {
		templateDefWriter.setGroup(dataset);
		templateDefWriter.setObject(property);
		exec.process(templateDefWriter);
	    }

	} catch (Exception x) {
	    throw new SQLException(x.getMessage());
	}
    }
}
