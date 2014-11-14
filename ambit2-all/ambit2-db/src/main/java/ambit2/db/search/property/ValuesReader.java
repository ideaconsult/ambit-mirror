/* ValuesReader.java
 * Author: nina
 * Date: Apr 26, 2009
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

package ambit2.db.search.property;

import java.sql.Connection;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.ValuesByTemplateReader;

public class ValuesReader extends ValuesByTemplateReader<IStructureRecord> {
	protected  ProcessorStructureRetrieval reader;
	protected Profile<Property> profile = null;
	
	public Profile<Property> getProfile() {
		return profile;
	}
	public void setProfile(Profile<Property> profile) {
		this.profile = profile;
		propertyQuery = new RetrieveProfile();
		((RetrieveProfile)propertyQuery).setValue(profile);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3678652087132468828L;

	public ValuesReader() {
		this(new ProcessorStructureRetrieval());
	}
	
	public ValuesReader(ProcessorStructureRetrieval reader) {
		 this.reader = reader;
	}	
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		if (reader != null) reader.setConnection(connection);
		
	}
	@Override
	public IStructureRecord process(IStructureRecord target) throws AmbitException {
		IStructureRecord record = super.process(reader==null?target:reader.process(target));
		return record != null?record:target;
	}
	@Override
	public void close() throws Exception {

		super.close();
		if (reader != null) 	reader.close();
	}
	
	@Override
	protected IStructureRecord createResult(IStructureRecord target) throws AmbitException {
		return target;
	}

	@Override
	protected void set(IStructureRecord result, Property fieldname, Object value)
			throws AmbitException {
		result.setProperty(fieldname, value);
		
	}

}
