/* ReferenceWriter.java
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ambit2.core.data.LiteratureEntry;

public class DbReferenceWriter extends AbstractRepositoryWriter<LiteratureEntry, LiteratureEntry> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4291307760401488788L;
	protected static final String insert_reference = "INSERT IGNORE INTO catalog_references (idreference, title, url) VALUES (null,?,?)";
    protected static final String select_reference_byname = "SELECT idreference, title, url FROM catalog_references WHERE title=?";
    protected PreparedStatement ps_selectreference;
    protected PreparedStatement ps_reference;

    @Override
    protected void prepareStatement(Connection connection) throws SQLException {
        ps_reference = connection.prepareStatement(insert_reference,Statement.RETURN_GENERATED_KEYS);
        ps_selectreference = connection.prepareStatement(select_reference_byname);
    }

    @Override
    public LiteratureEntry write(LiteratureEntry le) throws SQLException {
        le.setId(-1);
        ps_selectreference.clearParameters();
        ps_selectreference.setString(1,le.getTitle());
        ResultSet rs = ps_selectreference.executeQuery();
        while (rs.next()) {
            le.setId(rs.getInt(1));
            le.setURL(rs.getString(3));
        }
        rs.close();
        if (le.getId() > 0) return le;
        
        ps_reference.clearParameters();
        ps_reference.setString(1,le.getTitle());
        ps_reference.setString(2, le.getURL());
        ps_reference.executeUpdate();
        rs = ps_reference.getGeneratedKeys();

        while (rs.next()) {
            le.setId(rs.getInt(1));
        } 
        rs.close();
        return le;
    }
    
    public void close() throws SQLException {
        if (ps_reference != null)
            ps_reference.close();
        ps_reference = null;
        super.close();
    }    


}
