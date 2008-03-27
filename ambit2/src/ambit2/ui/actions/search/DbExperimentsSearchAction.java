package ambit2.ui.actions.search;


import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.sql.Connection;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.database.DbConnection;
import ambit2.data.literature.ReferenceFactory;
import ambit2.data.molecule.SourceDataset;
import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.processors.ReadAliasProcessor;
import ambit2.database.processors.ReadCASProcessor;
import ambit2.database.processors.ReadDescriptorsProcessor;
import ambit2.database.processors.ReadExperimentsProcessor;
import ambit2.database.processors.ReadNameProcessor;
import ambit2.database.processors.ReadStructureProcessor;
import ambit2.database.query.ExperimentQuery;
import ambit2.database.search.DbSearchExperiments;
import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitProcessor;
import ambit2.processors.ProcessorsChain;

/**
 * Database search for experimental data. See example for {@link ambit2.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbExperimentsSearchAction extends DbSearchAction {
    protected boolean resultsQuery = true;
	public DbExperimentsSearchAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Search",null);

	}

	public DbExperimentsSearchAction(Object userData, JFrame mainFrame,
			String name) {
		this(userData, mainFrame, name,null);
	}

	public DbExperimentsSearchAction(Object userData, JFrame mainFrame,
			String name, Icon icon) {
		super(userData, mainFrame, name, icon);
		interactive = false;
		
		putValue(AbstractAction.MNEMONIC_KEY,new Integer(KeyEvent.VK_D));
		putValue(AbstractAction.SHORT_DESCRIPTION,"Search by test results");		
		
	}
	public IAmbitProcessor getProcessor() {
	    ProcessorsChain processor = new ProcessorsChain();

		if (userData instanceof AmbitDatabaseToolsData) {
			AmbitDatabaseToolsData dbaData = ((AmbitDatabaseToolsData) userData);
		    try {
			    processor.add(new ReadStructureProcessor(dbaData.getDbConnection().getConn()));
			    } catch (AmbitException x) {
			    }
			    
			//if ((!resultsQuery) || !dbaData.getExperiments().isCombineWithAND())
				processor.add(new ReadExperimentsProcessor(dbaData.getExperiments(),
						dbaData.getDbConnection().getConn()));
/*
			if (resultsQuery) 
				processor.add(new ReadStudyConditionsProcessor(dbaData.getStudyConditions(),
						dbaData.getDbConnection().getConn()));
*/
			
		    processor.add(new ReadDescriptorsProcessor(dbaData.getDescriptors(),
				dbaData.getDbConnection().getConn()
				));
	    	try {
		    	processor.add(new ReadAliasProcessor(dbaData.getDbConnection().getConn()));
		    	processor.add(new ReadNameProcessor(dbaData.getDbConnection().getConn()));
		    	processor.add(new ReadCASProcessor(dbaData.getDbConnection().getConn()));
		    	} catch (Exception x) {
		    		logger.error(x);
		    	}
		    

			//distances
		};
	
		return processor;
	}
	public IIteratingChemObjectReader getSearchReader(Connection connection, Object query,int page, int pagesize) throws AmbitException {
		if (userData instanceof AmbitDatabaseToolsData) {
			AmbitDatabaseToolsData dbaData = ((AmbitDatabaseToolsData) userData);
				try {
					DbSearchExperiments reader = new DbSearchExperiments(
							dbaData.getDbConnection().getConn(),
							getExperiments(),
							((AmbitDatabaseToolsData)dbaData).getSrcDataset(),
							page,
							pagesize
							);
						return reader;
				} catch (AmbitException x) {
					JPanel p = new JPanel(new GridLayout(4,1));
					JLabel l = new JLabel(x.getMessage());
					p.add(l);
					p.add(new JLabel( 
							"<html><b>Define conditions in experiments panel. Only checked conditions are used for the search.</b></html>")
							);
					p.add(new JLabel( 
					"<html>Click on <u>Condition</u>, <u>Atom</u> or <u>Value</u> columns to change the default settings.</html>")
					);	
					p.add(new JLabel( 
					"<html>Multiple conditions can be combined by <u>AND</u> / <u>OR</u>.</html>")
					);							
					 JOptionPane.showMessageDialog(mainFrame,p);
				}
		}
		return null;
	}
	/*
	public ChemObjectWriter getWriter() {
		if (userData instanceof AmbitDatabaseToolsData) {
				AmbitDatabaseToolsData dbaData = ((AmbitDatabaseToolsData) userData);		
				DataContainer studyList = (DataContainer) dbaData.getMolecules();
				studyList.clear();
				return new ListOfMoleculesWriter(studyList);
		} else return null;
	}
	*/
    protected ExperimentQuery getExperiments() {
        if (resultsQuery) return ((AmbitDatabaseToolsData) userData).getExperiments();
        else return ((AmbitDatabaseToolsData) userData).getStudyConditions();
    }
    public synchronized boolean isResultsQuery() {
        return resultsQuery;
    }
    public synchronized void setResultsQuery(boolean resultsQuery) {
        this.resultsQuery = resultsQuery;
    }
    /*
    @Override
    protected SourceDataset getResultsDataset(DbConnection conn) {
	return new SourceDataset("Experimental data search",ReferenceFactory.createSearchReference("Experimental data search"));
    }
    */
}
