/*
Copyright (C) 2005-2008  

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

package ambit2.db.processors;

import java.sql.SQLException;

import javax.naming.OperationNotSupportedException;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;

/**
 * <pre>
writes properties
</pre>
 * @author nina
 *
 */
public class PropertyValuesWriter extends ValueWriter<IStructureRecord,IStructureRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3140079486695024274L;
	protected LiteratureEntry reference = LiteratureEntry.getInstance("Structure properties");

	@Override
	public void setDataset(SourceDataset dataset) {
		super.setDataset(dataset);
		if (getDataset() != null) {
			reference = LiteratureEntry.getInstance(dataset.getTitle(),dataset.getURL());
			//tuple.setGroup(dataset);
		} 
	}
	@Override
	protected Object getValue(IStructureRecord record, Property property,
			int index) {
		Object o = record.getProperty(property);

		try {
			if (o instanceof Number) return o;  
			return Integer.parseInt(o.toString());
			
		} catch (Exception x) {
			try {
				return Double.parseDouble(o.toString());
			} catch (Exception xx) {
				return o;
			}
		}
	
	}

	@Override
	protected String getComments(String name,IStructureRecord target) {
		String n = name==null?"No name":name.toLowerCase();
		return Property.guessLabel(n);

	}

	@Override
	protected Iterable<Property> getPropertyNames(IStructureRecord record) {
		return record.getProperties();
	}

	/*
	@Override
	protected LiteratureEntry getReference(IStructureRecord target) {
		return reference;
	}
	*/

	@Override
	protected IStructureRecord transform(IStructureRecord target) {
		return target;
	}
	@Override
	public IStructureRecord write(IStructureRecord target) throws SQLException, AmbitException, OperationNotSupportedException {
		setStructure(target);
		return super.write(target);
	}

}


