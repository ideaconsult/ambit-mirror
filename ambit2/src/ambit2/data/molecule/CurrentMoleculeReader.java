package ambit2.data.molecule;

import java.io.IOException;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.setting.IOSetting;

/**
 * A {@link org.openscience.cdk.io.iterator.IIteratingChemObjectReader} implementation 
 * to read a {@link org.openscience.cdk.interfaces.IMolecule}. May seem strange to have an iterator for a single molecule,
 * but this provides a common interface for using batch processing {@link ambit2.io.batch.IBatch}. <br>
 * Used in {@link ambit2.ui.actions.process.MoleculeCalculationAction}. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class CurrentMoleculeReader implements IIteratingChemObjectReader {
	protected IMolecule mol ;
	protected int rows = 0;
	public CurrentMoleculeReader(IMolecule mol) {
		this.mol = mol;
		rows = 0;
	}

	public IResourceFormat getFormat() {
		// TODO Auto-generated method stub
		return null;
	}
    @Override
    public String toString() {
        return "Current molecule";
    }
	
	

	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	public IOSetting[] getIOSettings() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addChemObjectIOListener(IChemObjectIOListener arg0) {
		// TODO Auto-generated method stub

	}

	public void removeChemObjectIOListener(IChemObjectIOListener arg0) {
		// TODO Auto-generated method stub

	}

	public void remove() {
		// TODO Auto-generated method stub

	}

	public boolean hasNext() {
		return rows < 1;
	}

	public Object next() {
		rows ++;
		return mol;
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#accepts(java.lang.Class)
     */
    public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IAtomContainer.class.equals(interfaces[i])) return true;
		}
		return false;
    }

}
