/* TemplateWriter.java
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

package ambit2.database.writers;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.formats.IResourceFormat;

import ambit2.database.AmbitDatabaseFormat;
import ambit2.exceptions.AmbitException;
import ambit2.data.AmbitUser;
import ambit2.data.experiment.StudyTemplate;
import ambit2.data.experiment.TemplateField;
import ambit2.database.exception.DbAmbitException;
import ambit2.database.processors.TemplateSearchProcessor;

/**
 * Writes {@link ambit2.data.experiment.StudyTemplate} to database. Use {@link #write(StudyTemplate)}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-30
 */
public class TemplateWriter extends DefaultDbWriter {
	public static final String insertTemplate="insert into template (idtemplate,name,template) values (null,?,compress(?))";
	public static final String insertFieldname="insert ignore into study_fieldnames (id_fieldname,name,units,fieldtype,fieldmode) values (null,?,?,?,?)";
	public static final String insertTemplateFieldname="insert ignore into template_def (idtemplate,id_fieldname) values (?,(select id_fieldname from study_fieldnames where name=?))";
	protected PreparedStatement insertStudyTemplate = null;
	protected PreparedStatement insertTemplateField = null;
	protected PreparedStatement insertTemplateDefField = null;
	protected TemplateSearchProcessor esp = null;

    /**
     * @param connection
     * @param user
     */
    public TemplateWriter(Connection connection, AmbitUser user) {
        super(connection, user);
        try {
        esp = new TemplateSearchProcessor(connection);
        } catch (Exception x) {
            logger.error(x);
            esp = null;
        }
    }

    /* (non-Javadoc)
     * @see ambit2.database.writers.DefaultDbWriter#prepareStatement()
     */
    protected void prepareStatement() throws SQLException {
        // TODO Auto-generated method stub

    }
	protected PreparedStatement prepareInsertStudyTemplate() throws SQLException {

		return connection.prepareStatement(insertTemplate,Statement.RETURN_GENERATED_KEYS);
	}
	protected PreparedStatement prepareInsertTemplateField() throws SQLException {
		return connection.prepareStatement(insertFieldname,Statement.RETURN_GENERATED_KEYS);
	}
	protected PreparedStatement prepareInsertTemplateDef() throws SQLException {
		return connection.prepareStatement(insertTemplateFieldname,Statement.RETURN_GENERATED_KEYS);
	}			
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectWriter#write(org.openscience.cdk.interfaces.ChemObject)
     */
    public void write(IChemObject arg0) throws CDKException {
        throw new CDKException("Not implemented");

    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectIO#getFormat()
     */
    public IResourceFormat getFormat() {
        return new AmbitDatabaseFormat();
    }

	
	public int write(StudyTemplate template) throws DbAmbitException {
		int idtemplate = -1;
		if (template == null) throw new DbAmbitException(template,"Undefined template!");
		try {
			idtemplate = esp.read(template);
			if (idtemplate > -1) return idtemplate;
			if (insertStudyTemplate == null) insertStudyTemplate = prepareInsertStudyTemplate();
			
			StringWriter writer = new StringWriter();
			template.save(writer);

			insertStudyTemplate.clearParameters();
			insertStudyTemplate.setString(1,template.getName());
			insertStudyTemplate.setString(2,writer.toString());
			
			insertStudyTemplate.executeUpdate();
			idtemplate = getAutoGeneratedKey(insertStudyTemplate);
			template.setId(idtemplate);
			if (idtemplate == -1) throw new DbAmbitException(template,"Template storage failed!");
			for (int i=0; i < template.size();i++)
				try {
					write(idtemplate,template.getField(i));
				} catch (SQLException x) {
				    logger.error("Error writing template field "+template.getField(i)+x.getMessage());
				    logger.debug(x);
				}
					
			return idtemplate;
		} catch (AmbitException x) {
			throw new DbAmbitException(template,x);
		
		} catch (SQLException x) {
			throw new DbAmbitException(template,x);
		}

	}	
    
	public int write(int idtemplate,TemplateField field) throws SQLException {
		if (insertTemplateField == null) insertTemplateField = prepareInsertTemplateField();
		insertTemplateField.clearParameters();
		insertTemplateField.setString(1,field.getName());
		insertTemplateField.setString(2,field.getUnits());
		insertTemplateField.setString(3,field.getType());
		insertTemplateField.setString(4,field.getMode());			
		insertTemplateField.executeUpdate();
		int id = getAutoGeneratedKey(insertTemplateField);
		field.setId(id);
		
		if (insertTemplateDefField == null) insertTemplateDefField = prepareInsertTemplateDef();
		insertTemplateDefField.clearParameters();
		insertTemplateDefField.setInt(1,idtemplate);
		insertTemplateDefField.setString(2,field.getName());
		insertTemplateDefField.executeUpdate();
		return id;
	}
	/* (non-Javadoc)
     * @see ambit2.database.writers.DefaultDbWriter#close()
     */
    public void close() throws IOException {
        try {
			if (insertStudyTemplate != null) insertStudyTemplate.close();
			if (insertTemplateField != null) insertTemplateField.close();			
			insertStudyTemplate = null;
			insertTemplateField = null;
		} catch (SQLException x) {
			new IOException(x.getMessage());
		}
        super.close();
    }
}
