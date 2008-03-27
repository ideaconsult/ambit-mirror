/* ExperimentSearchProcessor.java
 * Author: Nina Jeliazkova
 * Date: 2006-5-3 
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
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;

import ambit2.database.query.ExperimentQuery;
import ambit2.database.query.TemplateFieldQuery;
import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitResult;
import ambit2.data.AmbitList;
import ambit2.data.experiment.Experiment;
import ambit2.data.experiment.Study;
import ambit2.data.experiment.StudyTemplate;
import ambit2.data.experiment.TemplateField;
import ambit2.database.exception.DbAmbitException;
import ambit2.database.exception.DbStudyConditionsNotDefined;

/**
 * Expects {@link ambit2.data.experiment.Study} or  {@link ambit2.data.experiment.Experiment} object in {@link #process(Object)} method.
 * However, most methods are {@link #read(int, int, int, Experiment)} and {@link #read(Study)} <br>
 * Used by {@link ambit2.database.writers.ExperimentWriter} and {@link ambit2.ui.actions.search.ExperimentsByStudyAction}
 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-3
 */
public class ExperimentSearchProcessor extends TemplateSearchProcessor {

	public static final String selectFieldname = 
		"select id_fieldname,name,units,fieldtype,fieldmode from study_fieldnames where name=?";
	
	public static final String selectFieldnames = 
		"select name,units,fieldtype,f.id_fieldname,min(value_num) as minv,avg(value_num) as avgv,max(value_num) as maxv from study_results"+
		"\njoin study_fieldnames as f using(id_fieldname) group by id_fieldname"; 
	public static final String selectConditionsFieldnames = 
		"select name,units,fieldtype,f.id_fieldname,value from study_conditions"+
		"\njoin study_fieldnames as f using(id_fieldname) group by id_fieldname"; 

	
	public static final String selectFieldnamesByStudy = 
		"select e.idexperiment,idref,idstudy,"+
		"\nname,units,fieldtype,f.id_fieldname,min(value_num) as minv,avg(value_num) as avgv,max(value_num) as maxv from experiment as e"+
		"\njoin study_results using(idexperiment)"+
		"\njoin study_fieldnames as f using(id_fieldname)"+
		"\nwhere idstudy=?"+
		"\ngroup by id_fieldname";

	public static final String selectConditionFieldnamesByStudy = 
		"select e.idexperiment,idref,study_conditions.idstudy,"+
		"\nname,units,fieldtype,f.id_fieldname, value from experiment as e"+
		"\njoin study_conditions using(idstudy)"+
		"\njoin study_fieldnames as f using(id_fieldname)"+
		"\nwhere study_conditions.idstudy=?"+
		"\ngroup by id_fieldname";
	
	public static final String selectAllStudiesSQL="select idstudy,study.idtemplate,study.name as studyname, template.name as templatename from study join template using(idtemplate)";
	public static final String selectStudySQL="select idstudy,idtemplate,name from study where name=?";
	
	public static final String selectExperimentSQL="select idexperiment from experiment where idstudy=? and idref=? and idstructure=?";
	
	
	protected PreparedStatement selectStudy = null;
	
	protected PreparedStatement selectExperiment = null;
	
	protected PreparedStatement psSelectFieldname = null;
    /**
     * 
     */
    public ExperimentSearchProcessor(Connection connection) throws AmbitException {
        super(connection);
    }
    /* (non-Javadoc)
     * @see ambit2.database.processors.DefaultDbProcessor#prepare(java.sql.Connection)
     */
    public void prepare(Connection connection) throws AmbitException {
        this.connection = connection;

    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#process(java.lang.Object)
     */
    public Object process(Object object) throws AmbitException {
        if (object instanceof Study)
            return search((Study)object);
        if (object instanceof Experiment)
            return search((Experiment)object);        
        else return super.process(object);
    }
    public AmbitList search(Study object) throws AmbitException {
        AmbitList list = new AmbitList();
        int r = read(object);
        if (r>-1) list.addItem(object);
        return list;
    }
    
    /**
     * 
     * @param field  field.name should be available
     * @return  true if fouund
     * @throws DbAmbitException
     */
    public boolean readFieldname(TemplateField field)  throws DbAmbitException {
    	try {
    		if (psSelectFieldname == null) connection.prepareStatement(selectFieldname);
    		psSelectFieldname.clearParameters();
    		psSelectFieldname.setString(1,field.getName());
    		ResultSet rs = psSelectFieldname.executeQuery();
    		boolean found = false;
    		while (rs.next()) {
    			field.setId(rs.getInt("id_fieldname"));
    			field.setNumeric("numeric".equals(rs.getBoolean("fieldtype")));
    			field.setResult("result".equals(rs.getBoolean("fieldmode")));
    			found = true;
    		}
    		rs.close();
    		rs= null;
    		return found;
    	} catch (Exception x) {
    		throw new DbAmbitException(field,x);
    	}
    }
    //TODO load template as well
    /**
     * returns all studies available from the database in the list
     */
	public void readStudy(AmbitList list) throws DbAmbitException {
		int idstudy = -1;
		Hashtable templates = new Hashtable();
		try {
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery(selectAllStudiesSQL);
			while (rs.next()) {
			    String templateName = rs.getString("templatename");
			    Object template = templates.get(templateName);
			    if (template == null) {
			        template = new StudyTemplate(templateName);
			        int idtemplate = read((StudyTemplate)template);
			    }
				list.addItem(new Study(rs.getString("studyname"),rs.getInt("idstudy"),(StudyTemplate)template));
			}
			rs.close();
			st.close();
			st = null; rs = null;
		} catch (SQLException x) {
			throw new DbAmbitException(null,x.getMessage());
		}

	}
    /**
     * does nothing! TODO
     * @param object
     * @return list
     * @throws AmbitException
     */
    public AmbitList search(Experiment object) throws AmbitException {
        AmbitList list = new AmbitList();
        //int r = read(object);
        //if (r>-1) studyList.addItem(object);
        return list;
    }    

    public void loadQuery(Study study,ExperimentQuery list,boolean results) throws AmbitException {
        if (results) loadQuery(study,list);
        else loadConditionQuery(study,list);
    }
	public void loadQuery(Study study,ExperimentQuery list) throws AmbitException {	    
	    String sql;
	    sql = selectFieldnamesByStudy;
	    logger.debug(sql);
		int id = -1;
		try {
			Statement s; ResultSet rs;
			if ((study == null) || (study.getId() < 0)) {
				s = connection.createStatement();
				rs = s.executeQuery(selectFieldnames);
			} else {
				s = connection.prepareStatement(sql);
				((PreparedStatement) s).setInt(1,study.getId());
				rs = ((PreparedStatement) s).executeQuery();
			}
			
			while (rs.next()) {
				TemplateFieldQuery query = new TemplateFieldQuery(
						rs.getString("name"),
						rs.getString("units"),
						rs.getString("fieldType").equals("numeric"),
						true
						);
				if (query.isNumeric()) {
					try {
						query.setMinValue(rs.getDouble("minv"));
						query.setMaxValue(rs.getDouble("maxv"));
						query.setValue(new Double(rs.getDouble("avgv")));
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				query.setId(rs.getInt("id_fieldname"));
				if (query.getId()> 0) {
					if (list == null) list = new ExperimentQuery();
					list.addItem(query);
				}	
				else break;
				
			}
			rs.close();
			s.close();
			s = null;

		} catch (SQLException x) {

			throw new AmbitException(x);
		}

	}	

	public void loadConditionQuery(Study study,ExperimentQuery list) throws AmbitException {
	    String sql = selectConditionFieldnamesByStudy;
	    
		int id = -1;
		try {
			Statement s; ResultSet rs;
			if ((study == null) || (study.getId() < 0)) {
				s = connection.createStatement();
				rs = s.executeQuery(selectConditionsFieldnames);
			} else {
				s = connection.prepareStatement(sql);
				((PreparedStatement) s).setInt(1,study.getId());
				rs = ((PreparedStatement) s).executeQuery();
			}
			
			while (rs.next()) {
				TemplateFieldQuery query = new TemplateFieldQuery(
						rs.getString("name"),
						"",
						false,false
						);
				query.setCondition("=");
				query.setValue(rs.getString("value"));
				query.setId(rs.getInt("id_fieldname"));
				if (query.getId()> 0) {
					if (list == null) list = new ExperimentQuery();
					list.addItem(query);
				}	
				else break;
				
			}
			rs.close();
			s.close();
			s = null;

		} catch (SQLException x) {
			x.printStackTrace();
			throw new AmbitException(x);
		}

	}	
	
    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#createResult()
     */
    public IAmbitResult createResult() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#getResult()
     */
    public IAmbitResult getResult() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#setResult(ambit2.processors.IAmbitResult)
     */
    public void setResult(IAmbitResult result) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#close()
     */
    public void close() {
        try {
			if (selectStudy != null) selectStudy.close();
			selectStudy = null;
			if (selectStudyTemplate != null) selectStudyTemplate.close();
			selectStudyTemplate = null;
			if (selectExperiment != null) selectExperiment.close();
			selectExperiment = null;

        } catch (Exception x) {
            
        }

    }
    
	protected PreparedStatement prepareSelectStudy() throws SQLException {
		return connection.prepareStatement(selectStudySQL);
	}	
	public int read(Study study) throws DbAmbitException {
		int idstudy = -1;
		if (study == null) throw new DbAmbitException(study,"Undefined study!");
//		try {
			/*
			if (selectStudy == null) selectStudy = prepareSelectStudy();
			selectStudy.clearParameters();
			selectStudy.setString(1,study.getName());
			ResultSet rs = selectStudy.executeQuery();
			while (rs.next()) {
				idstudy = rs.getInt("idstudy");
			}
			rs.close();
			study.setId(idstudy);
			if (idstudy == -1)
			*/ 
			idstudy = searchStudyByConditions(study);
			return idstudy;
		//} catch (SQLException x) {
//			throw new DbAmbitException(study,x.getMessage());
	//	}

	}

	
	protected PreparedStatement prepareSelectExperiment() throws SQLException {
		return connection.prepareStatement(selectExperimentSQL,Statement.RETURN_GENERATED_KEYS);
	}				
	/**
	 * 
	 * @param idstructure
	 * @param idstudy
	 * @param idref
	 * @param e
	 * @return experiment identifier if found in the database, 
	 * @throws DbAmbitException
	 */
	public int read(int idstructure,int idstudy,int idref, Experiment e) throws DbAmbitException {
		try {
			if (selectExperiment == null) selectExperiment = prepareSelectExperiment();
			selectExperiment.clearParameters();
			selectExperiment.setInt(1,idstudy);
			selectExperiment.setInt(2,idref);
			selectExperiment.setInt(3,idstructure);
			ResultSet rs = selectExperiment.executeQuery();
			int idexp = -1;
			while (rs.next()) {
				idexp = rs.getInt("idexperiment");
			}
			rs.close();
			return idexp;
		} catch (SQLException x) {
			throw new DbAmbitException(e,x);
		}
	}
	public int searchStudyByConditions(Study study) throws DbAmbitException {
		String sql = studyConditionsToSQL(study);
		try {
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if (rs.next()) {
				study.setId(rs.getInt("idstudy"));
				study.setName(rs.getString("name"));
			}
			rs.close();
			st.close();
		} catch (SQLException x) {
			throw new DbAmbitException(study,x);
		}
		return study.getId();

	}
	/*
	 * select study.idstudy, study.name from study
join study_conditions as s1 using (idstudy)
join study_conditions as s2 using (idstudy)
where s2.id_fieldname=(select id_fieldname from study_fieldnames where name="Endpoint") and s2.value = "TA98"
and s1.id_fieldname=(select id_fieldname from study_fieldnames where name="Species") and s1.value="Salmonella typhimurium";
	 */
	public String studyConditionsToSQL(Study study) throws DbAmbitException {
		Hashtable table = study.getStudyConditions();
		if ((table == null) || (table.size() == 0)) throw new DbStudyConditionsNotDefined(study,"Study conditions not defined!");
		StringBuffer b = new StringBuffer();
		b.append("select study.idstudy, study.name from study");		
		for (int i=0; i < table.size(); i++) {
			b.append("\njoin study_conditions as s");
			b.append((i+1));
			b.append(" using (idstudy)");
		}	
		String c = "where ";
		Enumeration e = table.keys();
		Object key, value;
		String alias = "s";
		int row = 1;
		while (e.hasMoreElements()) {
			alias = "s" + Integer.toString(row);
			key = e.nextElement();
			value = table.get(key);
			b.append('\n');
			b.append(c);
			b.append(alias);
			b.append(".id_fieldname=(select id_fieldname from study_fieldnames where name=\"");
			b.append(key);
			b.append("\") and ");
			b.append(alias);
			b.append(".value=\"");
			b.append(value);
			b.append("\"");
			c = "and ";
			row++;
		}
		return b.toString();
	}

}
