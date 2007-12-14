package ambit.ui.actions.search;

import java.sql.Connection;

import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.data.IDataContainers;
import ambit.data.ISharedData;
import ambit.data.literature.ReferenceFactory;
import ambit.data.molecule.CurrentMoleculeReader;
import ambit.data.molecule.DataContainer;
import ambit.data.molecule.MoleculesIteratorReader;
import ambit.data.molecule.SourceDataset;
import ambit.database.DbConnection;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.data.ISharedDbData;
import ambit.exceptions.AmbitException;
import ambit.ui.UITools;

public class DbSaveAsDataset extends DbSearchAction {
	public DbSaveAsDataset(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Save as dataset");
	}

	public DbSaveAsDataset(Object userData, JFrame mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit/ui/images/dataset.png"));
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
