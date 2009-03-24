/* TemplateWriter.java
 * Author: nina
 * Date: Feb 7, 2009
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
import java.sql.SQLException;

import ambit2.base.data.Dictionary;

/**
 * to write into template tables
 * @author nina
 *
 * @param <Target>
 * @param <Result>
 */
public class TemplateWriter extends AbstractRepositoryWriter<Dictionary, Dictionary> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5537236303618368311L;
	protected static final String insert_template = "INSERT IGNORE INTO template (idtemplate,name) VALUES (null,?)";
	protected static final String insert_dictionary = "INSERT IGNORE INTO dictionary SELECT t1.idtemplate,?,t2.idtemplate FROM template t1 join template t2 where t1.name=? and t2.name=?";	
    protected PreparedStatement ps_template;
    protected PreparedStatement ps_dictionary;
	
    public TemplateWriter() {
		super();
	}
    @Override
    protected void prepareStatement(Connection connection) throws SQLException {
    	ps_template = connection.prepareStatement(insert_template);
    	ps_dictionary = connection.prepareStatement(insert_dictionary);
    	
    }
    public Dictionary write(Dictionary entry) throws SQLException {
    	
    	boolean commit = getConnection().getAutoCommit();
    	try {
    		getConnection().setAutoCommit(false);
    		
    		if (entry.getTemplate()!=null ) {
		    	ps_template.setString(1,entry.getTemplate());
		    	ps_template.execute();
    		}
	    	
	    	if (entry.getParentTemplate()!=null ) {
		    	ps_template.setString(1,entry.getParentTemplate());
		    	ps_template.execute(); 
	    	}
	    	
	    	if ((entry.getTemplate()!=null ) && (entry.getParentTemplate()!=null ) && (!entry.getTemplate().equals(entry.getParentTemplate()))) {
		    	ps_dictionary.clearParameters();
		    	ps_dictionary.setString(1,entry.getRelationship());
		    	ps_dictionary.setString(2,entry.getTemplate());
		    	ps_dictionary.setString(3,entry.getParentTemplate());
		    	ps_dictionary.execute();
	    	}
	    	getConnection().commit();
	    	return entry;
    	} catch (Exception x) {
    		x.printStackTrace();
    		getConnection().rollback();
    		return null;
    	} finally {
    		getConnection().setAutoCommit(commit);
    	}
    };
    @Override
    public void close() throws SQLException {
    	if (ps_template!=null) ps_template.close();
    	if (ps_dictionary!=null) ps_dictionary.close();    	
    	super.close();
    }
}
