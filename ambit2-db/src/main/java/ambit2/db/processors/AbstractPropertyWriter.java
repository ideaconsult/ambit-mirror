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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import ambit2.core.data.Dictionary;
import ambit2.core.data.LiteratureEntry;
import ambit2.db.SourceDataset;
import ambit2.db.exceptions.DbAmbitException;

public abstract class AbstractPropertyWriter<Target,Result> extends
		AbstractRepositoryWriter<Target, Result> {
	protected enum mode  {OK, ERROR};
	protected static final String select_descriptor = "SELECT idproperty,idreference,name,units,comments,islocal,idreference,title,url from properties join catalog_references using(idreference) where name=? and idreference=?";
    protected static final String insert_descriptor = "INSERT IGNORE INTO properties (idproperty,idreference,name,units,comments,islocal) VALUES (null,?,?,?,?,0)";
    protected static final String insert_templatedef = "INSERT IGNORE INTO template_def SELECT idtemplate,?,? from template WHERE name=?";
    protected PreparedStatement ps_descriptor;
    protected PreparedStatement ps_selectdescriptor;
    protected PreparedStatement ps_templatedef;
    protected DbReferenceWriter referenceWriter;
    protected TemplateWriter templateWriter;
    protected SourceDataset dataset = null;
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
        ps_templatedef = connection.prepareStatement(insert_templatedef);
    }
    @Override
    public synchronized void setConnection(Connection connection) throws DbAmbitException {
        super.setConnection(connection);
        if (referenceWriter == null) setReferenceWriter(new DbReferenceWriter());
        referenceWriter.setConnection(connection);
        if (templateWriter == null) templateWriter = new TemplateWriter();
        templateWriter.setConnection(connection);        
       
    }
    @Override
    public void open() throws DbAmbitException {
    	super.open();
        referenceWriter.open();
        templateWriter.open();
    }

    public void close() throws SQLException {
        if (ps_descriptor != null)
            ps_descriptor.close();
        ps_descriptor = null;
        
        if (ps_selectdescriptor != null)
            ps_selectdescriptor.close();
        ps_selectdescriptor = null;      
        
        if (ps_templatedef != null) 
        	ps_templatedef.close();
        ps_templatedef = null;
        templateWriter.close();        
        referenceWriter.close();
        super.close();
    }    
    protected abstract LiteratureEntry getReference(Target target);
    protected abstract Iterable<String> getPropertyNames(Target target);
    protected abstract String getComments(Target target);
    protected abstract void descriptorEntry(Target target,int idproperty,String propertyName, int propertyIndex,int idtuple) throws SQLException;

    protected int getTuple(SourceDataset dataset) {
    	return -1;
    }
    protected abstract Result transform(Target target) ;
    
    public Result write(Target target) throws SQLException {

        LiteratureEntry le = getReference(target);
        le = referenceWriter.write(le);
        
        Iterable<String> names = getPropertyNames(target);
        int idtuple = getTuple(getDataset());
        int i=0;
        for (String name: names) {
        	
            ps_selectdescriptor.clearParameters();
            ps_selectdescriptor.setString(1, name);
            ps_selectdescriptor.setInt(2, le.getId());
            
            boolean found = false;
            ResultSet rs1 = ps_selectdescriptor.executeQuery();
            while (rs1.next()) {

                descriptorEntry(target,rs1.getInt(1),name,i,idtuple);
                found = true;
            }
            rs1.close();
            
            if (!found) {
            	if (ps_descriptor == null)
                    ps_descriptor = connection.prepareStatement(insert_descriptor,Statement.RETURN_GENERATED_KEYS);
                ps_descriptor.clearParameters();
                ps_descriptor.setInt(1,le.getId());
                ps_descriptor.setString(2,name);
                ps_descriptor.setNull(3,Types.VARCHAR);
                ps_descriptor.setString(4,getComments(target));
                ps_descriptor.executeUpdate();
                
                ResultSet rs = ps_descriptor.getGeneratedKeys();
                try {
	                while (rs.next()) {
	                	//
	                	int iddescriptor = rs.getInt(1);
	                	templateEntry(target,iddescriptor);	                	
	                    descriptorEntry(target,iddescriptor,name,i,idtuple);
		                    
	                } 
                } catch (Exception x) {
                	x.printStackTrace();
                	logger.error(x);
                } finally {
	                rs.close();
	                ps_descriptor.close();
	                ps_descriptor = null;
                }
            }
            i++;
        }

        return transform(target);    	
    };
    protected abstract Dictionary getTemplate(Target target)  throws SQLException ;

    	//SELECT ?,name,? from template WHERE name=?"
    
    protected  void templateEntry(Target target,int idproperty) throws SQLException {
    	
    	Dictionary dict = getTemplate(target);

    	templateWriter.write(dict);
    	ps_templatedef.clearParameters();
    	ps_templatedef.setInt(1,idproperty);
    	ps_templatedef.setInt(2,idproperty);
    	ps_templatedef.setString(3,dict.getTemplate());    	
    	ps_templatedef.execute();

    }
}
