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

import ambit2.config.AmbitCONSTANTS;
import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.data.DefaultSharedDbData;
import ambit2.database.processors.ReadAliasProcessor;
import ambit2.database.processors.ReadDescriptorsProcessor;
import ambit2.database.processors.ReadExperimentsProcessor;
import ambit2.database.processors.ReadNameProcessor;
import ambit2.database.processors.ReadSMILESProcessor;
import ambit2.database.processors.ReadStructureProcessor;
import ambit2.database.query.DistanceQuery;
import ambit2.database.search.DbSearchDescriptors;
import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitProcessor;
import ambit2.processors.ProcessorsChain;
import ambit2.processors.SetLimitsProcessor;
import ambit2.processors.structure.AtomDistancesFilter;
import ambit2.processors.structure.UniqueIDProcessor;

/**
 * Database search by descriptors. See example for {@link ambit2.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbDescriptorsSearchAction extends DbSearchAction {

	public DbDescriptorsSearchAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Search",null);

	}

	public DbDescriptorsSearchAction(Object userData, JFrame mainFrame,
			String name) {
		this(userData, mainFrame, name,null);
	}

	public DbDescriptorsSearchAction(Object userData, JFrame mainFrame,
			String name, Icon icon) {
		super(userData, mainFrame, name, icon);
		interactive = false;
		
		putValue(AbstractAction.MNEMONIC_KEY,new Integer(KeyEvent.VK_D));
		putValue(AbstractAction.SHORT_DESCRIPTION,"Search by descriptors and distance between atoms");		
	}
	public IAmbitProcessor getProcessor() {
	    ProcessorsChain processor = new ProcessorsChain();
	    processor.add(new UniqueIDProcessor(AmbitCONSTANTS.AMBIT_IDSTRUCTURE));
	    try {
            if (userData instanceof DefaultSharedDbData) {
                DefaultSharedDbData dbaData = ((DefaultSharedDbData) userData);
                processor.add(new SetLimitsProcessor(dbaData.getPage(),dbaData.getPageSize()));
            }    
	    } catch (NumberFormatException x) {
	        
	    }
		if (userData instanceof AmbitDatabaseToolsData) {
			AmbitDatabaseToolsData dbaData = ((AmbitDatabaseToolsData) userData);
			try {
			    processor.add(new ReadStructureProcessor(dbaData.getDbConnection().getConn()));
			    } catch (AmbitException x) {
			        
			    }
			if (!dbaData.getDescriptors().isCombineWithAND()) {
			    processor.add(new ReadDescriptorsProcessor(dbaData.getDescriptors(),
					dbaData.getDbConnection().getConn()
					));
			}    
			
			processor.add(new ReadExperimentsProcessor(dbaData.getExperiments(),
						dbaData.getDbConnection().getConn()));
								
			
			//distances
	    	boolean distance = false;
	    	for (int i=dbaData.getDescriptors().size()-1;i>=0;i--) {
	    		Object o = dbaData.getDescriptors().getItem(i);
	    		if ((o instanceof DistanceQuery) &&
	    			((DistanceQuery)o).isEnabled()) {
	    			distance = true;
	    			processor.add(new AtomDistancesFilter((DistanceQuery)o));
	    			break;
	    		}
	    	}	
	    	try {
	    	processor.add(new ReadAliasProcessor(dbaData.getDbConnection().getConn()));
	    	processor.add(new ReadNameProcessor(dbaData.getDbConnection().getConn()));
	    	//processor.add(new ReadCASProcessor(dbaData.getDbConnection().getConn()));
	    	processor.add(new ReadSMILESProcessor(dbaData.getDbConnection().getConn()));
	    	} catch (Exception x) {
	    		logger.error(x);
	    	}
			
		};
	
		return processor;
	}
	public IIteratingChemObjectReader getSearchReader(Connection connection, Object query, int page, int pagesize) throws AmbitException {
		if (userData instanceof AmbitDatabaseToolsData) {
			AmbitDatabaseToolsData dbaData = ((AmbitDatabaseToolsData) userData);
				try {
					DbSearchDescriptors reader = new DbSearchDescriptors(
							dbaData.getDbConnection(),
							dbaData.getDescriptors(),
							((AmbitDatabaseToolsData)dbaData).getSrcDataset(),
							page,pagesize
							);
						return reader;
				} catch (AmbitException x) {
					JPanel p = new JPanel(new GridLayout(4,1));
					JLabel l = new JLabel(x.getMessage());
					p.add(l);
					p.add(new JLabel( 
							"<html><b>Define conditions in descriptors panel. Only checked conditions are used for the search.</b></html>")
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
	@Override
	protected SourceDataset getResultsDataset(DbConnection conn) {
		return new SourceDataset("Descriptors search",ReferenceFactory.createSearchReference("Descriptors search"));
	}
	*/
}
