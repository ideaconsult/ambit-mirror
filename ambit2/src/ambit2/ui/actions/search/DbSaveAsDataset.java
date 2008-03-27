package ambit2.ui.actions.search;

import java.sql.Connection;

import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.database.DbConnection;
import ambit2.data.IDataContainers;
import ambit2.data.ISharedData;
import ambit2.data.literature.ReferenceFactory;
import ambit2.data.molecule.CurrentMoleculeReader;
import ambit2.data.molecule.DataContainer;
import ambit2.data.molecule.MoleculesIteratorReader;
import ambit2.data.molecule.SourceDataset;
import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.data.ISharedDbData;
import ambit2.exceptions.AmbitException;
import ambit2.ui.UITools;

public class DbSaveAsDataset extends DbSearchAction {
	public DbSaveAsDataset(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Save as dataset");
	}

	public DbSaveAsDataset(Object userData, JFrame mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit2/ui/images/dataset.png"));
	}

	public DbSaveAsDataset(Object userData, JFrame mainFrame, String name,
			Icon icon) {
		super(userData, mainFrame, name, icon);
		setInteractive(false);
	}
	@Override
	public IIteratingChemObjectReader getSearchReader(Connection connection, Object query, int page, int pagesize) throws AmbitException {
		if (userData instanceof ISharedDbData) {
			
		    ISharedDbData dbaData = (ISharedDbData) userData;
		    /*
		    if (dbaData.getSource() == ISharedDbData.MEMORY_CURRENT)
		    	return new CurrentMoleculeReader(dbaData.getMolecule());
		    else if (dbaData.getSource() == ISharedDbData.MEMORY_LIST) {
				DataContainer c = dbaData.getMolecules();
				c.setEnabled(false);
				return new MoleculesIteratorReader(c.getContainers());
		    } else return new CurrentMoleculeReader(dbaData.getMolecule());
		    */
			DataContainer c = dbaData.getMolecules();
			c.setEnabled(false);
		    return new MoleculesIteratorReader(c.getContainers());
		} else return null;	
	}	
	public void completed() {
		if (userData instanceof ISharedData)  
			((IDataContainers) userData).getMolecules().setEnabled(true);
		super.completed();
	}
	@Override
	public IChemObjectWriter getWriter() {
		return getDatasetWriter();
	}


}
