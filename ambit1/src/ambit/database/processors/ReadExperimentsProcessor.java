package ambit.database.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openscience.cdk.interfaces.IChemObject;

import ambit.data.IAmbitEditor;
import ambit.data.experiment.Experiment;
import ambit.data.experiment.ExperimentList;
import ambit.data.experiment.Study;
import ambit.data.experiment.StudyTemplate;
import ambit.database.query.ExperimentQuery;
import ambit.database.query.TemplateFieldQuery;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.DefaultAmbitProcessor;
import ambit.processors.DefaultProcessorEditor;
import ambit.processors.IAmbitDBProcessor;
import ambit.processors.IAmbitResult;

/**
 * Reads experimental data from database  and assigns as molecule properties
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class ReadExperimentsProcessor extends DefaultAmbitProcessor implements IAmbitDBProcessor {
	protected ExperimentQuery experiments = null;
	//protected PreparedStatement psStructure = null;
	protected PreparedStatement ps = null;
	protected static final String sqlStructure = "select id_fieldname,value,value_num,experiment.idexperiment,idstudy from study_results join experiment using(idexperiment) where idstructure=? ";
	protected static final String sqlSubstance = "select id_fieldname,value,value_num,experiment.idexperiment,idstudy from study_results join experiment using(idexperiment) join structure using(idstructure) where idsubstance=? ";
	protected static final String sqlFieldnames = " and id_fieldname in ";
	protected static final String sqlOrder = "\norder by experiment.idexperiment";
	protected static final String sqlTemplate = "select uncompress(template),template.name,study.name from template join study using(idtemplate) where study.idstudy=? limit 1";
	protected String descriptorsIDS = "";
	protected PreparedStatement psTemplate = null;
	
	public ReadExperimentsProcessor(ExperimentQuery experiments, Connection connection) {
		super();
		this.experiments = experiments;
		if (experiments != null) {
			boolean lookup = false;
			StringBuffer b = new StringBuffer();
			char d = ' ';
			if (experiments != null)
				for (int i=0; i < experiments.size();i++) {
					TemplateFieldQuery q = experiments.getFieldQuery(i);
					if (q.isEnabled()) {
						b.append(d);
						b.append(q.getId());
						if (q.getId() > -1) lookup = true;
						d=',';
					} 
				}
			descriptorsIDS = b.toString();
			if (lookup)
			try {
				ps = connection.prepareStatement(getSQL() + sqlFieldnames +  "(" + descriptorsIDS + ")" + sqlOrder);
			} catch (SQLException x) {
				ps = null;
			}
		} else
			try {
				ps = connection.prepareStatement(getSQL() +sqlOrder);
			} catch (SQLException x) {
				ps = null;
			}			
		try {
			psTemplate = connection.prepareStatement(sqlTemplate);
		} catch (SQLException x) {
			x.printStackTrace();
			psTemplate = null;
		}
	}
	public String getSQL() {
	    return sqlSubstance;
	}
	public Object process(Object object) throws AmbitException {
	    if (object == null) return null;
		Object o = ((IChemObject) object).getProperty(AmbitCONSTANTS.AMBIT_IDSUBSTANCE);
		if (o==null) return object;
		int idsubstance = -1;
		try {
			//idstructure = Integer.parseInt(o.toString());
			idsubstance = ((Integer) o).intValue();
			if (idsubstance <= 0) return object;
		} catch (Exception x) {
			return object;
		}
		if (ps != null) {
			try {
				ps.clearParameters();
				ps.setInt(1,idsubstance);
				//ps.setString(2,descriptorsIDS);
				ResultSet rs = ps.executeQuery();
				int idfieldname;
				Object value;
				Object e = ((IChemObject) object).getProperty(AmbitCONSTANTS.EXPERIMENT_LIST);	
				ExperimentList elist;
				if ((e!= null) && (e instanceof ExperimentList))
					elist = (ExperimentList) e;
				else elist = new ExperimentList();
				Experiment exp = null;
				while (rs.next()) {
					idfieldname = rs.getInt("id_fieldname");
					int idexperiment = rs.getInt("idexperiment");
					if ((exp == null) || (exp.getId() != idexperiment)) {
						Study study = null;
						if (exp !=null) {
							study = exp.getStudy();
							if (study.getId() != rs.getInt("idstudy")) 
								study = null;
						}
						exp = new Experiment();
						try {
							if (study == null) study = getStudy(rs.getInt("idstudy"));
						} catch (Exception x) {
							x.printStackTrace();
							study = null;
						}

						exp.setStudy(study);
						exp.setId(idexperiment);
						elist.addItem(exp);

					}
					if (experiments == null) {
						/* TODO
						value = rs.getString("value_num"); 
						if (value == null) value = rs.getString("value");
						exp.setResult(q.getName(), value);
						*/
					} else 
					for (int i=0; i < experiments.size();i++) {
						TemplateFieldQuery q = experiments.getFieldQuery(i);
						if (q.getId() == idfieldname) {
							if (q.isNumeric()) value = rs.getString("value_num"); else value = rs.getString("value");
							if (value != null) {
								/*
								((IChemObject) object).removeProperty(q.getName());
								((IChemObject) object).setProperty(q,value);
								break;
								*/
								exp.setResult(q.getName(), value);
							}
						}			
					}	
				}
				((IChemObject) object).setProperty(AmbitCONSTANTS.EXPERIMENT_LIST,elist);				
				rs.close();
			} catch (SQLException x) {
				x.printStackTrace();
			}
		}
		return object;
	}

	public IAmbitResult createResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public IAmbitResult getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setResult(IAmbitResult result) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
     * @see ambit.processors.IAmbitDBProcessor#close()
     */
    public void close() {
        try {
        if (ps != null) ps.close();
        } catch (SQLException x) {
            
        }

    }
    public IAmbitEditor getEditor() {

    	return new DefaultProcessorEditor(this);
    }
    @Override
    public String toString() {

    	return "Reads experimental data from database";
    }
    protected StudyTemplate getTemplateByStudy(int idstudy) throws Exception {
    	if (psTemplate == null) return null;
    	psTemplate.clearParameters();
    	psTemplate.setInt(1,idstudy);
    	ResultSet rs = psTemplate.executeQuery();
    	StudyTemplate t = null;
    	while (rs.next()) {
    		t = new StudyTemplate(rs.getString(2));
    		t.load(rs.getBinaryStream(1));
    		break;
    	}
    	rs.close();
    	return t;
    }
    protected Study getStudy(int idstudy) throws Exception {
    	if (psTemplate == null) return null;
    	psTemplate.clearParameters();
    	psTemplate.setInt(1,idstudy);
    	ResultSet rs = psTemplate.executeQuery();
    	StudyTemplate t = null;
    	Study st = null;
    	while (rs.next()) {
    		t = new StudyTemplate(rs.getString(2));
    		t.load(rs.getBinaryStream(1));
    		st = new Study(rs.getString(3),t);
    		break;
    	}
    	rs.close();
    	return st;
    }
    
}
