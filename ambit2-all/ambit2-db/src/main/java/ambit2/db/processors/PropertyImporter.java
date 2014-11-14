/* PropertyImporter.java
 * Author: nina
 * Date: Apr 3, 2009
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.SourceDataset;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.key.CASKey;
import ambit2.core.processors.structure.key.ExactStructureSearchMode;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.core.processors.structure.key.InchiKey;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.SmilesKey;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.search.structure.QueryField;
import ambit2.db.search.structure.QueryFieldNumeric;
import ambit2.db.search.structure.QueryStructure;
import ambit2.db.update.dataset.DatasetAddStructure;

/**
 * Imports only properties, doesn't write structures - TODO merge with
 * repository writer
 * 
 * @author nina
 * 
 */
public class PropertyImporter extends
		AbstractRepositoryWriter<IAtomContainer, List<IStructureRecord>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -723253191799638564L;
	protected PropertyValuesWriter propertyWriter;
	protected IStructureKey queryKey;
	protected AbstractStructureQuery query_property;
	protected DatasetAddStructure datasetAddStruc = new DatasetAddStructure();

	public PropertyImporter() {
		propertyWriter = new PropertyValuesWriter();
		setPropertyKey(new CASKey());
	}

	public IStructureKey getPropertyKey() {
		return queryKey;
	}

	public void setPropertyKey(IStructureKey propertyKey) {
		this.queryKey = propertyKey;
		if ((propertyKey instanceof SmilesKey)
				|| (propertyKey instanceof InchiKey)) {
			query_property = new QueryStructure();
		} else if ((propertyKey.getType() == Number.class)
				|| (propertyKey.getType() == Integer.class)
				|| (propertyKey.getType() == Double.class))
			query_property = new QueryFieldNumeric();
		else
			query_property = new QueryField();

		query_property.setId(-1);

	}

	@Override
	public synchronized void setConnection(Connection connection)
			throws DbAmbitException {
		super.setConnection(connection);
		propertyWriter.setConnection(connection);
	}

	@Override
	public void open() throws DbAmbitException {
		super.open();
		propertyWriter.open();
	}

	public void close() throws Exception {
		try {

			if (propertyWriter != null)
				propertyWriter.close();

		} catch (SQLException x) {
			logger.log(java.util.logging.Level.SEVERE, x.getMessage(), x);
		}
		super.close();
	}

	public void setDataset(SourceDataset dataset) {
		propertyWriter.setDataset(dataset);
	}

	public SourceDataset getDataset() {
		return propertyWriter.getDataset();
	}

	protected Object getValue(IAtomContainer molecule) throws Exception {
		if (getPropertyKey() instanceof PropertyKey) {
			IStructureRecord structure = new StructureRecord();
			structure.setReference(getDataset().getReference());
			structure.addProperties(molecule.getProperties());
			return queryKey.process(structure);
		} else
			return queryKey.process(molecule);
	}

	@Override
	public List<IStructureRecord> write(IAtomContainer molecule)
			throws SQLException, OperationNotSupportedException, AmbitException {
		List<IStructureRecord> sr = new ArrayList<IStructureRecord>();

		ResultSet rs = null;
		try {
			Object value = getValue(molecule);
			if (value == null)
				throw new AmbitException("No value to match "
						+ getPropertyKey());

			query_property.setValue(value);

			if (queryKey.getQueryKey() != null) {
				if (query_property instanceof QueryStructure)
					query_property.setFieldname(ExactStructureSearchMode
							.valueOf(queryKey.getQueryKey().toString()));
				else
					query_property.setFieldname(queryKey.getQueryKey());
			} else
				query_property.setFieldname(null);
			rs = queryexec.process(query_property);

			IStructureRecord old_structure = null;
			while (rs.next()) {
				IStructureRecord structure = query_property.getObject(rs);
				if ((old_structure != null)
						&& (structure.getIdchemical() == old_structure
								.getIdchemical())
						&& (structure.getIdstructure() == old_structure
								.getIdstructure()))
					continue;
				structure.setReference(getDataset().getReference());
				structure.clearProperties();
				structure.addProperties(molecule.getProperties());
				writeDataset(structure);
				propertyWriter.process(structure);
				sr.add(structure);
				old_structure = structure;
			}
			if (sr.size() == 0)
				throw new AmbitException("No matching entry! "
						+ getPropertyKey() + "=" + value);
		} catch (Exception x) {
			throw new AmbitException(x);			
		} finally {
			if (rs != null)
				queryexec.closeResults(rs);
		}

		return sr;
	}

	protected void writeDataset(IStructureRecord structure)
			throws SQLException, AmbitException, OperationNotSupportedException {
		if (getDataset() == null)
			setDataset(new SourceDataset("Default"));

		datasetAddStruc.setObject(structure);
		datasetAddStruc.setGroup(getDataset());
		exec.process(datasetAddStruc);
	}
}
