package ambit.ui.actions.process;

import java.awt.Container;

import javax.swing.Icon;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.data.IDataContainers;
import ambit.data.molecule.CurrentMoleculeReader;
import ambit.data.molecule.CurrentMoleculeWriter;
import ambit.data.molecule.DataContainer;
import ambit.data.molecule.MoleculesIteratorReader;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.data.ISharedDbData;
import ambit.io.batch.DefaultBatchStatistics;
import ambit.io.batch.IBatchStatistics;
import ambit.ui.actions.BatchAction;

/**
 * Common class for several actions performing calculations on the current structure.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class MoleculeCalculationAction extends BatchAction {

	public MoleculeCalculationAction(Object userData, Container mainFrame) {
		super(userData, mainFrame);
		// TODO Auto-generated constructor stub
	}

	public MoleculeCalculationAction(Object userData, Container mainFrame,
			String name) {
		super(userData, mainFrame, name);
		// TODO Auto-generated constructor stub
	}

	public MoleculeCalculationAction(Object userData, Container mainFrame,
			String name, Icon icon) {
		super(userData, mainFrame, name, icon);
		// TODO Auto-generated constructor stub
	}

	public IIteratingChemObjectReader getReader() {
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = (ISharedDbData) userData;
		    if (dbaData.getSource() == ISharedDbData.MEMORY_CURRENT)
		    	return new CurrentMoleculeReader(dbaData.getMolecule());
		    else if (dbaData.getSource() == ISharedDbData.MEMORY_LIST) {
				DataContainer c = dbaData.getMolecules();
				c.setEnabled(false);
				return new MoleculesIteratorReader(c.getContainers());
		    } else return new CurrentMoleculeReader(dbaData.getMolecule());
		} else return null;	
	}

	public IChemObjectWriter getWriter() {
		if (userData instanceof AmbitDatabaseToolsData) {
			IDataContainers dbaData = (IDataContainers) userData;		
			return new CurrentMoleculeWriter(dbaData.getMolecules());
		} else return null;	
	}
    public IBatchStatistics getBatchStatistics() {
    	IBatchStatistics bs = null;
    	if (userData instanceof AmbitDatabaseToolsData) 
			bs = ((AmbitDatabaseToolsData) userData).getBatchStatistics();
		else bs = new DefaultBatchStatistics();
    	bs.setResultCaption("Processed ");
    	return bs;
    }
    public void completed() {
    	// TODO Auto-generated method stub
    	super.completed();
    }
}
