package ambit2.database.writers;

import java.io.IOException;
import java.sql.SQLException;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.formats.IResourceFormat;

import ambit2.config.AmbitCONSTANTS;
import ambit2.database.AmbitDatabaseFormat;
import ambit2.database.DbConnection;
import ambit2.exceptions.AmbitException;
import ambit2.data.descriptors.DescriptorsHashtable;
import ambit2.data.experiment.Experiment;
import ambit2.data.model.Model;
import ambit2.data.model.PredictedValue;
import ambit2.database.core.DbModel;
import ambit2.database.exception.DbModelException;

/**
 * Writes points from a QSAR model to the database. <br>
 * Used by {@link ambit2.ui.actions.dbadmin.DbBatchImportQSARModels}<br>
 * Expects object.getProperty(AmbitCONSTANTS.PREDICTED) to be {@link ambit2.data.model.PredictedValue}<br>
 * object.getProperty(AmbitCONSTANTS.EXPERIMENT) to be {@link ambit2.data.experiment.Experiment}
 * object.getProperty(AmbitCONSTANTS.QSARPOINT) to be one of "Training","Validation","Unspecified"
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class QSARPointWriter extends DefaultDbWriter {
	protected static final String AMBIT_insertQSARpoint = "INSERT INTO qsardata (idqsar,idexperiment,idpoint,ypredicted,yexperimental,pointType) VALUES (?,?,?,?,?,?);";
	protected DbConnection dbconn = null;
	protected DbModel dbModel = null;
	protected ExperimentWriter expWriter;
	protected QSARDescriptorsWriter dWriter;
	
	protected int idpoint = 0;
	
	public QSARPointWriter(DbConnection dbconn,DescriptorsHashtable descriptorLookup) {
		super(dbconn.getConn(),dbconn.getUser());
		this.dbconn = dbconn;
		dbModel = new DbModel(dbconn);
		try {
			dbModel.initialize();
			dbModel.initializeInsertModel();
		} catch (Exception x) {
			logger.error(x);
		}
		expWriter = new ExperimentWriter(dbconn);
		dWriter = new QSARDescriptorsWriter(dbconn,descriptorLookup);
		ps = null;
	}

	protected void prepareStatement() throws SQLException {
		ps = connection.prepareStatement(AMBIT_insertQSARpoint);

	}

	public void write(IChemObject object) throws CDKException {
		idpoint++;
		Object prediction = "" ;
		try {
			Object p = object.getProperty(AmbitCONSTANTS.PREDICTED);
			if (p==null) throw new CDKException("Predicted value not defined!");
			
			if (((PredictedValue)p).getModel() == null) throw new CDKException("QSAR Model not defined!");
			Model model = ((PredictedValue)p).getModel();
			dWriter.setModel(model);
			int idstructure = getIdStructure(object);
			
			Object e = object.getProperty(AmbitCONSTANTS.EXPERIMENT);
			if (e != null) {
			    if (idstructure <= 0) throw new CDKException("Molecule not defined! "+e);
			    prediction = object.getProperty(AmbitCONSTANTS.PREDICTED);
			    if (prediction == null) throw new CDKException("Missing predicted value! ");
				
			    Object isTraining = object.getProperty(AmbitCONSTANTS.QSARPOINT);
			    if (isTraining == null) isTraining="Unspecified";
				int idqsar = model.getId();
				if (idqsar <= 0) {
					idqsar = dbModel.addModel(model); 
					if (idqsar <= 0) throw new CDKException("Can't insert model! "+model.toString());
				}
				dWriter.write(idstructure, object);
				
				Object result = ((Experiment) e).getResult(model.getExperimentField());
				if (result == null)  throw new CDKException("Undefined observed data! "+model.getExperimentField());
				
				int idexperiment = expWriter.write(idstructure, (Experiment)e);
				
		    	write(idqsar, Double.parseDouble(prediction.toString()), 
		    			Double.parseDouble(result.toString()),
		    			idpoint,
		    			isTraining.toString(),
		    			idexperiment
		    			);
			}	else throw  new CDKException("Experiment not defined! "+e);
		} catch (NumberFormatException x) {
		    	throw new CDKException("Value not numeric!\t" );
		} catch (AmbitException x) {
			//TODO rollback!
			x.printStackTrace();
			throw new CDKException(x.getMessage()+x.getCause().getMessage());
		}
	}


	/**
	 * Adds a QSAR point into qsardata table
	 * @param idqsar  {@link Model} identifier as in database
	 * @param ypredicted  Predicted value
	 * @param yexperimental Experimental value
	 * @param idpoint Sequential number of point
	 * @param training true if belongs to the training set
	 * @param idexperiment identifier of the {@link Experiment} already stored into experiment table
	 * @return number of points inserted (1 is success)
	 * @throws DbModelException
	 */
	
	public int write(int idqsar, double ypredicted, double yexperimental,
			int idpoint, String training, int idexperiment) throws DbModelException {
		//initializeInsertCompounds() to be called before
		try {
		    if (ps == null) prepareStatement();
			ps.clearParameters();
			//idqsar,idexperiment,idpoint,ypredicted,yexperimental,pointType
			ps.setInt(1,idqsar);
			ps.setInt(2,idexperiment);
			ps.setInt(3,idpoint);
			ps.setDouble(4,ypredicted);
			ps.setDouble(5,yexperimental);
			ps.setString(6,training);
			
			;
			return ps.executeUpdate();
		} catch (SQLException x) {
			throw new DbModelException(null,"addQSARPoint  idqsar="+idqsar+" idexperiment ="+idexperiment+" ypredicted ="+ypredicted,x);
		}
	}
    public IResourceFormat getFormat() {
        return new AmbitDatabaseFormat();
    }

	public void close() throws IOException {
		try {
			dWriter.close();
			expWriter.close();
			dbModel.close();
		} catch (Exception x) {
			logger.error(x);
		}
		super.close();
	}

}
