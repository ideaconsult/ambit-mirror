package ambit.ui.actions.search;

import java.sql.Connection;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.data.ISharedData;
import ambit.data.molecule.SourceDataset;
import ambit.database.DbConnection;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.data.ISharedDbData;
import ambit.database.processors.ReadAliasProcessor;
import ambit.database.processors.ReadCASProcessor;
import ambit.database.processors.ReadNameProcessor;
import ambit.database.processors.ReadSMILESProcessor;
import ambit.database.processors.ReadStructureProcessor;
import ambit.database.search.DbDatasetReader;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.IAmbitProcessor;
import ambit.processors.ProcessorsChain;
import ambit.ui.UITools;

/**
 * Retrieves all compounds from specified dataset. The dataset is selected by {@link ambit.ui.actions.search.SelectDatasetAction}. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DatasetSearchAction extends DbSearchAction {
	public static String caption = "Retrieve dataset";
	public DatasetSearchAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,caption);
	}
	public DatasetSearchAction(Object userData, JFrame mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit/ui/images/dataset.png"));

	}

	public DatasetSearchAction(Object userData, JFrame mainFrame, String name,
			Icon icon) {
		super(userData, mainFrame, name, icon);
		interactive = false;
		putValue(AbstractAction.SHORT_DESCRIPTION,"Retrieves a set of compounds from selected AMBIT dataset");
		
	}	
	public IIteratingChemObjectReader getSearchReader(Connection connection,
			Object query, int page, int pagesize) throws AmbitException {
		SourceDataset d = null;
		if (userData instanceof AmbitDatabaseToolsData) {
			d = ((AmbitDatabaseToolsData)userData).getSrcDataset();
			if (d==null) { 
				JOptionPane.showMessageDialog(mainFrame, "<html>Select dataset to retrieve by <u>Search options/Dataset</u> menu</html>");
				return null;
			}
		} else {	
			d = new SourceDataset();
			d.setName(AmbitCONSTANTS.AQUIRE);
		} 
		return new DbDatasetReader(connection,d,page,pagesize);
	}
	/* (non-Javadoc)
     * @see ambit.ui.actions.BatchAction#getProcessor()
     */
    public IAmbitProcessor getProcessor() {
        try {
        if (userData instanceof ISharedData) {
            ISharedDbData dbaData = ((ISharedDbData) userData);
            ProcessorsChain processors = new ProcessorsChain();
			processors.add(new ReadStructureProcessor(dbaData.getDbConnection().getConn()));
			processors.add(new ReadAliasProcessor(dbaData.getDbConnection().getConn()));
           	processors.add(new ReadNameProcessor(dbaData.getDbConnection().getConn()));
           	processors.add(new ReadCASProcessor(dbaData.getDbConnection().getConn()));
           	processors.add(new ReadSMILESProcessor(dbaData.getDbConnection().getConn()));
           	//processors.add(new ReadExperimentsProcessor(null,dbaData.getDbConnection().getConn()));        
           	//processors.add(new ReadDescriptorsProcessor(null,dbaData.getDbConnection().getConn()));
           	return processors;	
		}
        } catch (Exception x) {
            logger.error(x);
        }
        return super.getProcessor();
    }
    @Override
    public IChemObjectWriter getWriter() {
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
		    if (ISharedDbData.RESULTS_DATASET == dbaData.getResultDestination())
		    	dbaData.setResultDestination(ISharedDbData.MEMORY_LIST);
		}
    	return super.getWriter();
    }
    @Override
    protected SourceDataset getResultsDataset(DbConnection conn) {
    	// TODO Auto-generated method stub
    	return null;
    }
}
