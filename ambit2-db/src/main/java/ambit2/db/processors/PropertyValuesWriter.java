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

import org.openscience.cdk.CDKConstants;

import ambit2.core.data.Dictionary;
import ambit2.core.data.IStructureRecord;
import ambit2.core.data.LiteratureEntry;
import ambit2.db.SourceDataset;

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
	protected Dictionary dictionary = new Dictionary();

	@Override
	public void setDataset(SourceDataset dataset) {
		super.setDataset(dataset);
		if (getDataset() != null)
			dictionary = new Dictionary(getDataset().getName(),"Dataset");
		else
			dictionary = new Dictionary("Dataset","All");
	}
	@Override
	protected Object getValue(IStructureRecord record, String propertyName,
			int index) {
		Object o = record.getProperties().get(propertyName);

		try {
			return Double.parseDouble(o.toString());
		} catch (Exception x) {
			logger.warn(x);
			return o;
		}
	
	}

	@Override
	protected String getComments(String name,IStructureRecord target) {
		String n = name.toLowerCase();
		if (n.startsWith("cas")) return CDKConstants.CASRN;
		if (n.contains("name")) return CDKConstants.NAMES;
		if (n.contains("iupac")) return "IUPAC Name";
		if (n.contains("inchi")) return CDKConstants.INCHI;
		if (n.contains("smiles")) return CDKConstants.SMILES;
		return name;
	}

	@Override
	protected Iterable<String> getPropertyNames(IStructureRecord record) {
		return record.getProperties().keySet();
	}

	@Override
	protected LiteratureEntry getReference(IStructureRecord target) {
		return new LiteratureEntry("Structure properties");
	}

	@Override
	protected IStructureRecord transform(IStructureRecord target) {
		return target;
	}
	@Override
	public IStructureRecord write(IStructureRecord target) throws SQLException {
		setStructure(target);
		return super.write(target);
	}
	@Override
	protected Dictionary getTemplate(IStructureRecord target)
			throws SQLException {
		return dictionary;
	}
}


