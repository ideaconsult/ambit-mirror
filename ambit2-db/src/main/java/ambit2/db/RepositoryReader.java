/*
Copyright (C) 2007-2008  

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

package ambit2.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;
import ambit2.base.data.StructureRecord;
import ambit2.base.processors.ProcessorException;

/**
 * TODO parameterize
 * @author nina
 *
 * @param <Target>
 */
public class RepositoryReader<Target > extends AbstractDBProcessor<Target, StructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7461302566335753091L;
	protected static final String read_structures = "SELECT idstructure,idchemical,format FROM structure";
	protected static final String read_structurebyid = "SELECT uncompress(structure) FROM structure where idstructure=?";
	protected PreparedStatement ps_structures;
	protected PreparedStatement ps_structurebyid;	
	protected ResultSet rs;
	public RepositoryReader() {
		super();
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);

	}
	public void open() throws DbAmbitException {
        try {
            prepareStatement(connection);        
    		ps_structures.clearParameters();
    		rs = ps_structures.executeQuery();
        } catch (SQLException x) {
            throw new DbAmbitException(null,x);
        }
	}
	public boolean hasNext() throws SQLException  {
		return rs.next();
	}
	public StructureRecord next() throws SQLException {
		return new StructureRecord(rs.getInt(2),rs.getInt(1),null,rs.getString(3));
	}	
	public String getStructure(int idstructure)  throws SQLException {
		ps_structurebyid.clearParameters();
		ps_structurebyid.setInt(1,idstructure);
		ResultSet rsstruc = ps_structurebyid.executeQuery();
		String content = null;
		while (rsstruc.next()) {
			content = rsstruc.getString(1);
		}
		rsstruc.close();
		return content;
	}
	
	protected void prepareStatement(Connection connection) throws SQLException {
		 ps_structures = connection.prepareStatement(read_structures);
		 ps_structurebyid = connection.prepareStatement(read_structurebyid);
	}	
	public void close() throws SQLException {
            if (ps_structures != null)
    		ps_structures.close();
            if (ps_structurebyid != null)
                ps_structurebyid.close();
	}
	public StructureRecord process(Target target) throws AmbitException {
		try {
			return next();
		} catch (SQLException x) {
			throw new ProcessorException(this,x);
		}
	}

}
