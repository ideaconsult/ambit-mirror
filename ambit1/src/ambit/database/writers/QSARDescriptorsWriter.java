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

package ambit.database.writers;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;

import ambit.data.descriptors.Descriptor;
import ambit.data.descriptors.DescriptorDefinition;
import ambit.data.descriptors.DescriptorsHashtable;
import ambit.data.model.Model;
import ambit.database.DbConnection;
import ambit.database.exception.DbAmbitException;
import ambit.database.exception.DbModelException;
import ambit.exceptions.AmbitException;

/**
 * Same functionality as {@link PropertyDescriptorWriter} plus writing descriptors to qsardescriptors table 
 * @author Nina Jeliazkova
 *
 */
public class QSARDescriptorsWriter extends PropertyDescriptorWriter {
	protected static final String AMBIT_insertQSARdescriptor = "INSERT IGNORE INTO qsardescriptors (idqsar,iddescriptor,morder) VALUES (?,?,?)";
	protected Model model;
	protected PreparedStatement psQSARDescriptors;
	protected int order = 0;
	
	public QSARDescriptorsWriter(DbConnection conn) {
		super(conn);
		model = null;
	}

	public QSARDescriptorsWriter(DbConnection conn, Object descriptor,
			DescriptorDefinition ambitDescriptor) {
		super(conn, descriptor, ambitDescriptor);

		model = null;		
	}

	public QSARDescriptorsWriter(DbConnection conn,
			DescriptorsHashtable descriptors) {
		super(conn, descriptors);
		model = null;		
	}
	protected void initwriters(DbConnection conn) {
		super.initwriters(conn);
		try {
		psQSARDescriptors = conn.getConn().prepareStatement(AMBIT_insertQSARdescriptor);
		} catch (SQLException x) {
			logger.error(x);
			psQSARDescriptors = null;
		}
	}
	public void write(IChemObject object) throws CDKException {
		if (model == null) throw new CDKException("Model not defined");
		if (model.getId() <= 0) throw new CDKException("Model not stored into the database! Please write the model first!");
		order = 0;
		super.write(object);
	}
	protected double getDescriptorValue(IChemObject object, Object descriptor) throws AmbitException {
		order++;
		return super.getDescriptorValue(object, descriptor);
	}
	protected void writeDescriptorValue(int iddescriptor, int idstructure, double value, double error) throws DbAmbitException {
		super.writeDescriptorValue(iddescriptor, idstructure, value, error);
		addQSARDescriptor(model.getId(), iddescriptor, order);
	}
	protected void writeNullDescriptorValue(int iddescriptor, int idstructure) throws DbAmbitException {
		super.writeNullDescriptorValue(iddescriptor, idstructure);
		addQSARDescriptor(model.getId(), iddescriptor, order);
	}
	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
	/**
	 * inserts descriptor into a model
	 * @param idqsar  - {@link Model} identifier as in database
	 * @param iddescriptor  {@link Descriptor} identifier as in database
	 * @param order - order in model
	 * @return
	 * @throws DbModelException
	 */
	protected int addQSARDescriptor(int idqsar, int iddescriptor, int order) throws DbModelException {
		try {
			
			psQSARDescriptors.clearParameters();
			psQSARDescriptors.setInt(1,idqsar);
			psQSARDescriptors.setInt(2,iddescriptor);
			psQSARDescriptors.setInt(3,order);
			return psQSARDescriptors.executeUpdate();
		} catch (SQLException x) {
			throw new DbModelException(null,"addDescriptor",x);
		}		 
	}
	public void close() throws IOException {
		super.close();
		try {
			if (psQSARDescriptors != null) psQSARDescriptors.close();
			psQSARDescriptors = null;
		} catch (Exception x) {
			throw new IOException(x.getMessage());
		}
	}
}


