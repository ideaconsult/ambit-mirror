package ambit2.ui.actions.search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.database.DbConnection;
import ambit2.data.AmbitObject;
import ambit2.data.literature.ReferenceFactory;
import ambit2.data.molecule.CurrentMoleculeWriter;
import ambit2.data.molecule.DataContainer;
import ambit2.data.molecule.SourceDataset;
import ambit2.data.molecule.SourceDatasetList;
import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.data.DefaultSharedDbData;
import ambit2.database.data.ISharedDbData;
import ambit2.database.writers.SourceDatasetWriter;
import ambit2.exceptions.AmbitException;
import ambit2.exceptions.AmbitIOException;
import ambit2.io.FileOutputState;
import ambit2.io.ListOfMoleculesWriter;
import ambit2.io.MyIOUtilities;
import ambit2.io.batch.DefaultBatchStatistics;
import ambit2.io.batch.IBatchStatistics;
import ambit2.ui.UITools;
import ambit2.ui.actions.BatchAction;

/**
 * Abstract {@link ambit2.ui.actions.BatchAction}  class for database queries.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public abstract class DbSearchAction extends BatchAction {
    /*
	public static int page=0;
	public static int pagesize= 100;
    */
	//public static MoleculeEditAction moleculeEditAction = null;
	
	public DbSearchAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Search");
		// TODO Auto-generated constructor stub
	}

	public DbSearchAction(Object userData, JFrame mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit2/ui/images/search.png"));
		// TODO Auto-generated constructor stub
	}

	public DbSearchAction(Object userData, JFrame mainFrame, String name,
			Icon icon) {
		super(userData, mainFrame, name, icon);
		// TODO Auto-generated constructor stub
	}
	
	public abstract IIteratingChemObjectReader getSearchReader(Connection connection, Object query, int page, int pagesize) throws AmbitException;
	
	public IIteratingChemObjectReader getReader() {

		if (userData instanceof DefaultSharedDbData) {
            DefaultSharedDbData dbaData = ((DefaultSharedDbData) userData);		
			DbConnection conn = dbaData.getDbConnection();
			if ((conn ==null) || conn.isClosed()) JOptionPane.showMessageDialog(mainFrame, "Use Database/Open first");
			else
				try {
					return getSearchReader(conn.getConn(),dbaData.getQuery(),dbaData.getPage(),
                            dbaData.getPageSize());
				} catch (AmbitException x) {
					JOptionPane.showMessageDialog(mainFrame, x.toString());
				}

		}
		return null;	
	}
	public IChemObjectWriter getWriter() {
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
				switch (dbaData.getResultDestination()) {
				case ISharedDbData.MEMORY_LIST: {
					DataContainer list = (DataContainer) dbaData.getMolecules();
					list.clear();
					return new ListOfMoleculesWriter(list);
				}
				case ISharedDbData.MEMORY_CURRENT: {
					return new CurrentMoleculeWriter(dbaData.getMolecules());
				}
				case ISharedDbData.RESULTS_FILE: {
					return getFileWriter(dbaData.getDefaultDir());
				}
				case ISharedDbData.RESULTS_DATASET: {
					return getDatasetWriter();
				}				
				default: 
					return getFileWriter(dbaData.getDefaultDir());
				}
		} else return getFileWriter("");
	}

	protected SourceDataset getResultsDataset(DbConnection conn) {
		SourceDataset dataset  = null;
		if (userData instanceof AmbitDatabaseToolsData)  {
			dataset = ((AmbitDatabaseToolsData)userData).selectDataset(new SourceDatasetList() {
				@Override
				public AmbitObject createNewItem() {
					// TODO Auto-generated method stub
					return new SourceDataset("Search results",ReferenceFactory.createSearchReference(getValue(NAME).toString()));
				}
			},false,mainFrame,false);
		} 
		
		return dataset;
	}
		
	public IChemObjectWriter getDatasetWriter() {
		if (userData instanceof ISharedDbData) {
			ISharedDbData dbaData = ((ISharedDbData) userData);
			DbConnection conn = dbaData.getDbConnection();
			SourceDataset dataset = getResultsDataset(conn);
			//dataset.getReference().setEditable(false);
			
			if (dataset == null) { //create new
				dataset =  new SourceDataset("New dataset",
						ReferenceFactory.createSearchReference("New dataset"));
				try {
					if (dataset.editor(true).view(mainFrame, true, "")) {
						return new SourceDatasetWriter(dataset,conn);
					} else return null;
				} catch (Exception x) {
					logger.error(x);
					return null;
				}
			} else return new SourceDatasetWriter(dataset,conn); 	
		} else return null;
	}
	public IChemObjectWriter getFileWriter(String defaultDir) {
		File file = MyIOUtilities.selectFile(mainFrame, null, defaultDir, 
				FileOutputState.extensions,FileOutputState.extensionDescription, false);
		if (file != null) {
			try {
				return FileOutputState.getWriter(new FileOutputStream(file),file.getName());
			} catch (FileNotFoundException x) {
				logger.error(x);
			} catch (AmbitIOException x) {
				logger.error(x);
				
			}
		}
		return null;
	}
	/* (non-Javadoc)
     * @see ambit2.ui.actions.BatchAction#getBatshStatistics()
     */

    public IBatchStatistics getBatchStatistics() {
    	IBatchStatistics bs = null;
    	if (userData instanceof AmbitDatabaseToolsData) 
			bs = ((AmbitDatabaseToolsData) userData).getBatchStatistics();
		else bs = new DefaultBatchStatistics();
    	bs.setResultCaption("Found ");
    	return bs;
    }
}


