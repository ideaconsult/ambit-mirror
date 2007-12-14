package ambit.database.writers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Enumeration;
import java.util.Hashtable;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.formats.IResourceFormat;

import ambit.data.AmbitUser;
import ambit.data.experiment.Experiment;
import ambit.data.experiment.Study;
import ambit.data.experiment.TemplateField;
import ambit.data.literature.LiteratureEntry;
import ambit.database.AmbitDatabaseFormat;
import ambit.database.DbConnection;
import ambit.database.core.DbReference;
import ambit.database.exception.DbAmbitException;
import ambit.database.processors.ExperimentSearchProcessor;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;

import com.mysql.jdbc.MysqlDataTruncation;

/**
 * Writes {@link ambit.data.experiment.Experiment} to database. The experimental data is expected to come as molecule property 
 * object.getProperty({@link AmbitCONSTANTS#EXPERIMENT}) of type {@link ambit.data.experiment.Experiment} <br>
 * Used by {@link ambit.database.writers.QSARPointWriter}, {@link ambit.ui.actions.dbadmin.DbBatchImportExperiments}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class ExperimentWriter extends DefaultDbWriter {
	protected DbConnection dbconn = null;
	protected ExperimentSearchProcessor esp;
	protected TemplateWriter templateWriter;
	
	public static final String insertResultSQL="insert into study_results (idexperiment,id_fieldname,value,value_num) values (?,(select id_fieldname from study_fieldnames where name=?),?,?) ON DUPLICATE KEY UPDATE value=?,value_num=?";
	public static final String insertExperimentSQL="insert into experiment (idexperiment,idstudy,idref,idstructure,structure_type,idstructure_parent) values (null,?,?,?,?,?)";

	public static final String insertStudySQL="insert into study (idstudy,idtemplate,name) values (null,?,?)";
	
	
	public static final String insertStudyConditionsSQL="insert into study_conditions (idstudy,id_fieldname,value) values (?,(select id_fieldname from study_fieldnames where name=?),?) ON DUPLICATE KEY UPDATE value=?";
	protected boolean addCompound = false;
	
	protected PreparedStatement insertResult = null;

	protected PreparedStatement insertExperiment = null;
	
	protected PreparedStatement insertStudy = null;

	protected PreparedStatement insertStudyConditions = null;
	
	protected DbReference insertReference = null;
	
	
	public ExperimentWriter(DbConnection dbconn) {
		this(dbconn.getConn(),dbconn.getUser());
		this.dbconn = dbconn;
		
	}
	
	public ExperimentWriter(Connection conn,AmbitUser user) {
		super(conn,user);
		try {
		    esp = new ExperimentSearchProcessor(conn);
		} catch (Exception x) {
		    esp = null;
		}
		templateWriter = new TemplateWriter(conn,user);
	}

	public void write(IChemObject object) throws CDKException {
		try {
			int idstructure = getIdStructure(object);
			
			Object e = object.getProperty(AmbitCONSTANTS.EXPERIMENT);
			if (e != null) {
			    if (idstructure <= 0) throw new CDKException("Molecule not defined for experiment "+e);
			    write(idstructure,(Experiment) e);
			}
		} catch (AmbitException x) {
			x.printStackTrace();
			throw new CDKException(x.getMessage());
		}
	}

    public IResourceFormat getFormat() {
        return new AmbitDatabaseFormat();
    }

	public void close() throws IOException {
		try {
			

			if (insertStudy != null) insertStudy.close();

			if (insertExperiment != null) insertExperiment.close();
			if (insertResult != null) insertResult.close();
			if (insertReference != null) insertReference.close();

			
			insertStudy = null;
			insertExperiment = null;
			insertResult = null;
			insertReference = null;
		} catch (SQLException x) {
			new IOException(x.getMessage());
		}
	}
	protected PreparedStatement prepareInsertExperiment() throws SQLException {
		return connection.prepareStatement(insertExperimentSQL,Statement.RETURN_GENERATED_KEYS);
	}
	public int write(Experiment e) throws DbAmbitException {
	    int idstructure = getIdStructure(e.getMolecule());
	    if (idstructure <= 0) throw new DbAmbitException(e,"Molecule not defined for experiment "+e);
	    return write(idstructure,e);
	}
	public int write(int idstructure,Experiment e) throws DbAmbitException {
		int idstudy = -1;
		idstudy = write(e.getStudy());

		int idref = write(e.getReference());
		int idexperiment = esp.read(idstructure,idstudy,idref,e);
		try {
			if (idexperiment == -1) {
				if (insertExperiment == null) insertExperiment = prepareInsertExperiment();
				insertExperiment.clearParameters();
				insertExperiment.setInt(1,idstudy);
				insertExperiment.setInt(2,idref);
				insertExperiment.setInt(3,idstructure);
				insertExperiment.setString(4,"tested");
				insertExperiment.setInt(5,0);
				insertExperiment.executeUpdate();
				idexperiment = getAutoGeneratedKey(insertExperiment);
			}
			e.setId(idexperiment);
			if (idexperiment > -1) {
				Hashtable results = e.getResults();
				if (results != null) {
					Enumeration i = results.keys();
					while (i.hasMoreElements()) {
						Object f = i.nextElement();
						Object v = results.get(f);
						try {
						if (f instanceof TemplateField) 
							write(idexperiment,((TemplateField)f).getName(),v);
						else	
							write(idexperiment,f.toString(),v);
						} catch (MysqlDataTruncation x) {
							logger.warn(x);
						} catch (SQLException x) {
							throw new DbAmbitException(null,f.toString(),x);
						}
					}
				}
			}
			return idexperiment;
		} catch (SQLException x) {
			throw new DbAmbitException(e,x);
		}
	}
	public int read(LiteratureEntry reference) throws DbAmbitException {
			if (insertReference == null) {
				insertReference = new DbReference(dbconn);
				insertReference.initialize();
				insertReference.initializeInsertEntry();
			}
			return insertReference.getReference(reference);
		
	}	
	public int write(LiteratureEntry reference) throws DbAmbitException {
			int idref = read(reference);
			if (idref == -1) {
				idref = insertReference.addReference(reference);
				if (idref == -1) throw new DbAmbitException(null,"Undefined reference");
			}
			return idref; 

	}

	protected PreparedStatement prepareInsertResult() throws SQLException {
		return connection.prepareStatement(insertResultSQL,Statement.RETURN_GENERATED_KEYS);
	}		
	/*
	 * CREATE TABLE study_results (
  idexperiment INTEGER UNSIGNED NOT NULL,
  id_fieldname INTEGER UNSIGNED NOT NULL,
  value VARCHAR(64) NULL,
  value_num NUMERIC NULL,
  
  insert into study_results set (?,(select id_fieldname from study_fieldnames where name=?),?,?)
	 */
	public int write(int idexperiment,String field,Object result) throws SQLException {
		if (insertResult == null) insertResult = prepareInsertResult();
		insertResult.clearParameters();
		insertResult.setInt(1,idexperiment);
		insertResult.setString(2,field);
		if (result instanceof Number) {
			insertResult.setNull(3,Types.VARCHAR);
			insertResult.setDouble(4,((Number) result).doubleValue());
			insertResult.setNull(5,Types.VARCHAR);
			insertResult.setDouble(6,((Number) result).doubleValue());		
			
		} else {
			String r = result.toString();
			if (r.length()>64)
				r = r.substring(0,64);
			insertResult.setString(3,r);
			insertResult.setNull(4,Types.DOUBLE);
			insertResult.setString(5,r);
			insertResult.setNull(6,Types.DOUBLE);			
		}	
		insertResult.executeUpdate();
		int id = getAutoGeneratedKey(insertResult);

		return id;
	}	
	public int write(Study study) throws DbAmbitException {
		if (study == null) throw new DbAmbitException(study,"Undefined study!");
		int idstudy=esp.read(study);
		if (idstudy > -1) return idstudy;
		int idtemplate = templateWriter.write(study.getTemplate());
		if (idtemplate == -1) throw new DbAmbitException(study.getTemplate(),"Undefined template!");
		try {
			if (insertStudy == null) insertStudy = prepareInsertStudy();
			if (study.getName().equals("")) study.setName(study.getTemplate().getName());
			insertStudy.clearParameters();
			insertStudy.setInt(1,idtemplate);
			insertStudy.setString(2,study.getName());			
			insertStudy.executeUpdate();;
			idstudy = getAutoGeneratedKey(insertStudy);
			study.setId(idstudy);
			
			Hashtable c = study.getStudyConditions();
			if ( c != null) {
			Enumeration e = c.keys();
				while (e.hasMoreElements()) {
					Object key = e.nextElement();
					Object value = c.get(key);
					if (value == null) continue;
					if (key instanceof TemplateField)
						write(idstudy,((TemplateField)key).getName(),c.get(key).toString());
					else 
						write(idstudy,key.toString(),c.get(key).toString());
				}
			}
			
			return idstudy;
		} catch (SQLException x) {
			throw new DbAmbitException(study,x);
		}
	}
	protected PreparedStatement prepareInsertStudy() throws SQLException {
		return connection.prepareStatement(insertStudySQL,Statement.RETURN_GENERATED_KEYS);
	}		
	protected PreparedStatement prepareInsertStudyCondition() throws SQLException {
		return connection.prepareStatement(insertStudyConditionsSQL,Statement.RETURN_GENERATED_KEYS);
	}		
	/**
	 * Writes study conditions into database
	 * @param idstudy  An identifier of a {@link Study}, already stored into study table 
	 * @param field
	 * @param value
	 * @throws SQLException
	 */
	public void write(int idstudy,String field, String value) throws SQLException {
		if (insertStudyConditions == null) insertStudyConditions = prepareInsertStudyCondition();
		insertStudyConditions.clearParameters();
		insertStudyConditions.setInt(1,idstudy);
		insertStudyConditions.setString(2,field);
		insertStudyConditions.setString(3,value);		
		insertStudyConditions.setString(4,value);
		insertStudyConditions.executeUpdate();

	}

	public boolean isAddCompound() {
		return addCompound;
	}

	public void setAddCompound(boolean addCompound) {
		this.addCompound = addCompound;
	}
	
	protected void prepareStatement() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	public String toString() {
		return "Write experiment results to database";
	}
}
