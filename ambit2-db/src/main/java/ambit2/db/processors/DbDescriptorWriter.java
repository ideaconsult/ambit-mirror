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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.openscience.cdk.qsar.DescriptorValue;

import ambit2.core.data.LiteratureEntry;
import ambit2.db.exceptions.DbAmbitException;



public class DbDescriptorWriter extends AbstractRepositoryWriter<DescriptorValue,DescriptorValue> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -358115974932302101L;
	protected static final String select_descriptor = "SELECT iddescriptor,idreference,name,units,error,comments,islocal,idreference,title,url from descriptors join catalog_references using(idreference) where name=? and idreference=?";
    protected static final String insert_descriptor = "INSERT IGNORE INTO descriptors (iddescriptor,idreference,name,units,error,comments,islocal) VALUES (null,?,?,?,?,?,0)";
    protected PreparedStatement ps_descriptor;
    protected PreparedStatement ps_selectdescriptor;
    protected DbReferenceWriter referenceWriter;

    public synchronized DbReferenceWriter getReferenceWriter() {
        return referenceWriter;
    }

    public synchronized void setReferenceWriter(DbReferenceWriter referenceWriter) {
        this.referenceWriter = referenceWriter;
    }

    @Override
    protected void prepareStatement(Connection connection) throws SQLException {
        ps_descriptor = connection.prepareStatement(insert_descriptor,Statement.RETURN_GENERATED_KEYS);
        ps_selectdescriptor = connection.prepareStatement(select_descriptor);
    }
    @Override
    public synchronized void setConnection(Connection connection) throws DbAmbitException {
        super.setConnection(connection);
        if (referenceWriter == null) setReferenceWriter(new DbReferenceWriter());
        referenceWriter.setConnection(connection);
       
    }
    @Override
    public void open() throws DbAmbitException {
    	super.open();
        referenceWriter.open();    	
    }
    /**
     *  INSERT IGNORE INTO descriptors (iddescriptor,idreference,name,units,error,comments,islocal) VALUES (null,?,?,?,?,?,0)";
     */
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
                ps_descriptor.setNull(4,Types.FLOAT);
                ps_descriptor.setString(5,descriptor.getSpecification().getImplementationIdentifier());
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
    
    protected void descriptorEntry(DescriptorValue descriptor,int iddescriptor,int index) throws SQLException {
        
    }
    
    public void close() throws SQLException {
        if (ps_descriptor != null)
            ps_descriptor.close();
        ps_descriptor = null;
        
        if (ps_selectdescriptor != null)
            ps_selectdescriptor.close();
        ps_selectdescriptor = null;        
        super.close();
    }    



}
