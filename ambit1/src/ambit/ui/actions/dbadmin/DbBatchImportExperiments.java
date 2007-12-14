package ambit.ui.actions.dbadmin;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.setting.IOSetting;

import ambit.data.experiment.DefaultTemplate;
import ambit.data.molecule.MolProperties;
import ambit.database.DbConnection;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.data.ISharedDbData;
import ambit.database.processors.CASSmilesLookup;
import ambit.database.processors.FindUniqueProcessor;
import ambit.database.writers.ExperimentWriter;
import ambit.io.AmbitSettingsListener;
import ambit.io.MolPropertiesIOSetting;
import ambit.processors.IAmbitProcessor;
import ambit.processors.IdentifiersProcessor;
import ambit.processors.ProcessorsChain;
import ambit.processors.experiments.ExperimentParser;
import ambit.processors.structure.SmilesGeneratorProcessor;
import ambit.ui.UITools;

/**
 * Imports experimental data from an user selected file into database. Doesn't import compounds!. To be able to import experimental data, the compounds concerned should already exist in the database. <br>
 * Uses {@link ambit.database.processors.CASSmilesLookup}, {@link ambit.processors.structure.SmilesGeneratorProcessor} and {@link ambit.database.processors.FindUniqueProcessor} 
 * to find the compound from the database. Then uses {@link ambit.processors.experiments.ExperimentParser} to extract experimental data from compound properties and 
 * prepares it in a format suiteble for database. <br> Finally, uses {@link ambit.database.writers.ExperimentWriter} to write experimental data into database. <br>
 * Example: creates three buttons, 
 * first uses {@link ambit.ui.actions.dbadmin.DbOpenAction} to open a connection to database on click,
 * the second uses {@link ambit.ui.actions.dbadmin.DbBatchImportAction} to import user selected file into database
 * and the third uses {@link DbBatchImportExperiments} to import experimental data.
 <pre>
 	
 	AmbitDatabaseToolsData dbadminData = new AmbitDatabaseToolsData(false);
 	DbOpenAction openAction = new DbOpenAction(dbadminData,null);
 	
 	JButton buttonOpen = new JButton(openAction);
 	
 	DbBatchImportAction importAction = new DbBatchImportAction(dbadminData,null,"Import compounds",null);
 	JButton buttonImport = new JButton(importAction);

 	DbBatchImportExperiments importExperimentsAction = new DbBatchImportExperimentsAction(dbAdminData,null,"Import experiments",null);
 	JButton buttonImportExperiments = new JButton(importExperimentsAction); 	
 </pre>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbBatchImportExperiments extends DbBatchImportAction {
	protected ExperimentParser experimentParser;
	public DbBatchImportExperiments(Object userData, JFrame mainFrame) {
		super(userData,mainFrame,"Import test data",UITools.createImageIcon("ambit/ui/images/experiment_16.png"));
		putValue(AbstractAction.SHORT_DESCRIPTION,"Import experimental data from a file with chemical compounds. Also asks for test data template.");
		
	}
	public IIteratingChemObjectReader getReader() {
		IIteratingChemObjectReader reader = super.getReader();
		if (reader !=null)
		    reader.addChemObjectIOListener(new AmbitSettingsListener(null,IOSetting.LOW) {
	    	public void processIOSettingQuestion(IOSetting setting) {
	    		super.processIOSettingQuestion(setting);
	    		if (setting instanceof MolPropertiesIOSetting) {
	    			MolProperties props = ((MolPropertiesIOSetting)setting).getProperties();
	    			experimentParser.setTemplate(props.getTemplate());
	    			experimentParser.setReference(dataset.getReference());
	    			
	    			descriptorLookup.putAll(props.getDescriptors());

	    			experimentParser.setDefaultStudy(props.getStudy());
	    			experimentParser.setLookup(props.getExperimental());	    			
	    		}	
	    	}
	    });
		return reader;
	}
	public IAmbitProcessor getProcessor() {
		ProcessorsChain processors = new ProcessorsChain();
		processors.add(new IdentifiersProcessor());
		
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
		    try {
		        IAmbitProcessor p = new CASSmilesLookup(dbaData.getDbConnection().getConn(),true);
		        p.setEnabled(false);
		        processors.add(p);
		        SmilesGeneratorProcessor smigen = new SmilesGeneratorProcessor(5*60*1000);
				processors.add(smigen);
		        processors.add(new FindUniqueProcessor(dbaData.getDbConnection().getConn()));

		    } catch (Exception x) {
		    	
		    }
		}    		
		experimentParser = new ExperimentParser(
				new DefaultTemplate("Default"),dataset.getReference(),
				((ISharedDbData)userData).getTemplateDir()
				);
		processors.add(experimentParser);
		return processors;
		
	}
	public IChemObjectWriter getWriter() {
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
		    try {
		    	return new ExperimentWriter(dbaData.getDbConnection());
		    } catch (Exception x) {
		    	return null;
		    }
		}  return  null;  				
		
	}
	/* (non-Javadoc)
     * @see ambit.ui.actions.AmbitAction#done()
     */
    public void done() {
        super.done();
        DbConnection dbc = ((ISharedDbData) userData).getDbConnection();
        if (dbc != null)
        ((AmbitDatabaseToolsData) userData).initExperiments(dbc.getConn());
    }
}
