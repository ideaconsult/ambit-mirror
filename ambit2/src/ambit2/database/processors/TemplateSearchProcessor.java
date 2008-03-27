/* TemplateSearchProcessor.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-30 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
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

package ambit2.database.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ambit2.exceptions.AmbitException;
import ambit2.data.experiment.StudyTemplate;
import ambit2.database.exception.DbAmbitException;

/**
 * Expects {@link ambit2.data.experiment.StudyTemplate} object in {@link #process(Object)} method.
 * Searches the database for the template name. If found, assigns {@link ambit2.data.experiment.StudyTemplate}.setId()
 * with the template identifier from the database. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-30
 */
public class TemplateSearchProcessor extends DefaultDbProcessor {
    protected Connection connection ;
    public static final String selectTemplate="select idtemplate,name from template where template.name=?";
	public static final String selectTemplateFields=
		"select t.idtemplate,t.name,f.id_fieldname,f.name,f.units,f.fieldtype from template as t\n"+
		"join template_def using(idtemplate)\n"+
		"join study_fieldnames as f using(id_fieldname)\n"+
		"where t.name=?";
	protected PreparedStatement selectStudyTemplate = null;
    /**
     * @param connection
     * @throws AmbitException
     */
    public TemplateSearchProcessor(Connection connection) throws AmbitException {
        super(connection);
        
    }
    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#process(java.lang.Object)
     */
    public Object process(Object object) throws AmbitException {
        if (object instanceof StudyTemplate) {
            read((StudyTemplate)object);
        }         
        return object;
    }

    /* (non-Javadoc)
     * @see ambit2.database.processors.DefaultDbProcessor#prepare(java.sql.Connection)
     */
    public void prepare(Connection connection) throws AmbitException {
        this.connection = connection;

    }
	protected PreparedStatement prepareSelectStudyTemplate() throws SQLException {
		return connection.prepareStatement(selectTemplate);
	}
	public int read(StudyTemplate template) throws DbAmbitException {
		int idtemplate = -1;
		if (template == null) throw new DbAmbitException(template,"Undefined template!");
		try {
			if (selectStudyTemplate == null) selectStudyTemplate = prepareSelectStudyTemplate();
			selectStudyTemplate.clearParameters();
			selectStudyTemplate.setString(1,template.getName());
			ResultSet rs = selectStudyTemplate.executeQuery();
			while (rs.next()) {
				idtemplate = rs.getInt("idtemplate");
			}
			rs.close();
			template.setId(idtemplate);
			return idtemplate;
		} catch (SQLException x) {
			throw new DbAmbitException(template,x);
		}
	}	

}
