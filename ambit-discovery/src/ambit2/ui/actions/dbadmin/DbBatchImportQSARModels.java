package ambit2.ui.actions.dbadmin;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.listener.IReaderListener;
import org.openscience.cdk.io.setting.IOSetting;

import ambit2.ui.editors.IdentifiersProcessor;
import ambit2.data.descriptors.DescriptorsHashtable;
import ambit2.data.experiment.DefaultTemplate;
import ambit2.data.experiment.TemplateField;
import ambit2.data.literature.LiteratureEntry;
import ambit2.data.molecule.MolProperties;
import ambit2.database.data.ISharedDbData;
import ambit2.database.processors.CASSmilesLookup;
import ambit2.database.processors.FindUniqueProcessor;
import ambit2.database.writers.QSARPointWriter;
import ambit2.io.MolPropertiesIOSetting;
import ambit2.io.QSARSettingsListener;
import ambit2.processors.IAmbitProcessor;
import ambit2.processors.ProcessorsChain;
import ambit2.processors.QSARPointsProcessor;
import ambit2.processors.experiments.ExperimentParser;
import ambit2.processors.structure.SmilesGeneratorProcessor;
import ambit2.ui.UITools;

/**
 *  Action to import QSAR model from a file into database. User is prompted to enter various QSAR model information.
 *  <pre>
 	AmbitDatabaseToolsData dbadminData = new AmbitDatabaseToolsData(false);
 	DbOpenAction openAction = new DbOpenAction(dbadminData,null);
 	
 	JButton buttonOpen = new JButton(openAction);
 	
 	DbBatchImportAction importAction = new DbBatchImportAction(dbadminData,null,"Import compounds",null);
 	JButton buttonImport = new JButton(importAction);

 	DbBatchImportQSARModels importQSAR = new DbBatchImportQSARModels(dbAdminData,null,"Import experiments",null);
 	JButton buttonImportQSAR = new JButton(importQSAR);
 	</pre> 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbBatchImportQSARModels extends DbBatchImportAction {
	protected ExperimentParser experimentParser;
	protected QSARPointsProcessor qsarPointsParser;
	//protected DescriptorsHashtable descriptorLookup;
	public DbBatchImportQSARModels(Object userData, JFrame mainFrame,String caption ) {
		this(userData, mainFrame,caption,UITools.createImageIcon("ambit2/ui/images/database.png"));
	}	
	public DbBatchImportQSARModels(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Import QSAR model",UITools.createImageIcon("ambit2/ui/images/database.png"));
	}

	public DbBatchImportQSARModels(Object userData, JFrame mainFrame,
			String name, Icon icon) {
		super(userData, mainFrame, name, icon);
		descriptorLookup = new DescriptorsHashtable();
	}

	protected IReaderListener getReaderListener() {
		return new QSARSettingsListener(mainFrame,IOSetting.LOW) {
	    	public void processIOSettingQuestion(IOSetting setting) {
	    		super.processIOSettingQuestion(setting);
	    		if (setting instanceof MolPropertiesIOSetting) {
	    			MolProperties props = ((MolPropertiesIOSetting)setting).getProperties();
	    			try {
	    			if (props.getQsarModel().getReference() == null)
	    				props.getQsarModel().setReference((LiteratureEntry) (dataset.getReference().clone()));
	    			} catch (Exception x) {
	    				logger.error(x);
	    			}
	    			descriptorLookup.putAll(props.getDescriptors());
	    			experimentParser.setTemplate(props.getTemplate());
	    			experimentParser.setReference(dataset.getReference());
	    			qsarPointsParser.setProperties(props);
	    			
	    			experimentParser.setDefaultStudy(props.getQsarModel().getStudy());
	    			experimentParser.setLookup(props.getExperimental());
	    			
	    			Hashtable t = props.getExperimental();
	    			Enumeration e = t.keys();
	    			while (e.hasMoreElements()) {
	    				Object key = e.nextElement();
	    				Object value = t.get(key);
	    				if ((value != null) && (value instanceof TemplateField) && 
	    						((TemplateField) value).isResult()) {
	    					props.getQsarModel().setExperimentField((TemplateField) value);
	    					break;
	    				}
	    				/*
	    				if ((value instanceof AmbitColumnType) && 
	    					((AmbitColumnType) value).getId()==AmbitColumnType._ctYpredicted) {
	    					props.getQsarModel().setExperimentField(key.toString());
	    					break;
	    				}
	    				*/
	    			}
	    			
	    		}	
	    	}
	    };
	}
	public IAmbitProcessor getProcessor() {
		ProcessorsChain processors = new ProcessorsChain();
		if (identifiersProcessor == null) 
		    identifiersProcessor = new IdentifiersProcessor();
		processors.add(identifiersProcessor);
		
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
		
		qsarPointsParser = new QSARPointsProcessor();
		processors.add(qsarPointsParser);
		return processors;
		
	}
	public IChemObjectWriter getWriter() {
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
		    try {
		    	return new QSARPointWriter(dbaData.getDbConnection(),descriptorLookup);
		    } catch (Exception x) {
		    	return null;
		    }
		}  return  null;  				
		
	}
	
    public synchronized ExperimentParser getExperimentParser() {
        return experimentParser;
    }
    public synchronized void setExperimentParser(
            ExperimentParser experimentParser) {
        this.experimentParser = experimentParser;
    }
    public synchronized QSARPointsProcessor getQsarPointsParser() {
        return qsarPointsParser;
    }
    public synchronized void setQsarPointsParser(
            QSARPointsProcessor qsarPointsParser) {
        this.qsarPointsParser = qsarPointsParser;
    }
}
