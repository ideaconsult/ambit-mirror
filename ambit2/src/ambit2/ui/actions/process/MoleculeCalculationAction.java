package ambit2.ui.actions.process;

import java.awt.Container;

import javax.swing.Icon;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.data.IDataContainers;
import ambit2.data.molecule.CurrentMoleculeReader;
import ambit2.data.molecule.CurrentMoleculeWriter;
import ambit2.data.molecule.DataContainer;
import ambit2.data.molecule.MoleculesIteratorReader;
import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.data.ISharedDbData;
import ambit2.io.batch.DefaultBatchStatistics;
import ambit2.io.batch.IBatchStatistics;
import ambit2.ui.actions.BatchAction;

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
