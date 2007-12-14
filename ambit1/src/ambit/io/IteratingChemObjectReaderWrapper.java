package ambit.io;

import java.io.IOException;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.SetOfAtomContainers;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ISetOfAtomContainers;
import org.openscience.cdk.io.DefaultChemObjectReader;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;

/**
 * A wrapper for {@link org.openscience.cdk.io.DefaultChemObjectReader}.
 * Mostly for keeping track of file name and presenting it to the user :)
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class IteratingChemObjectReaderWrapper extends
		DefaultIteratingChemObjectReader {
	protected IChemObject chemObject = null;
	protected IResourceFormat format = null;
	protected ISetOfAtomContainers atomContainers = null;
	protected int record = -1;
	public IteratingChemObjectReaderWrapper(DefaultChemObjectReader reader) {
		super();
		try {
			format = reader.getFormat();
			chemObject = reader.read(new org.openscience.cdk.ChemFile());
			if (chemObject instanceof IChemFile) {
				int c = ((IChemFile) chemObject).getChemSequenceCount();
				if (c>0) {
					IChemSequence s = ((ChemFile) chemObject).getChemSequence(0);
					c = s.getChemModelCount();
					if (c > 0) { 
						IChemModel m = s.getChemModel(0);
						atomContainers = m.getSetOfMolecules();
					}	
				}
			} else
			if (chemObject instanceof SetOfAtomContainers) 
				atomContainers = (SetOfAtomContainers)chemObject; 
		} catch (CDKException x) {
			x.printStackTrace();
			chemObject = null;
			format = null;
		}
	}
	
	/* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#getFormat()
     */
    public IResourceFormat getFormat() {
        return format;
    }

	public void close() throws IOException {
		atomContainers = null;
	}

	public boolean hasNext() {
		if (atomContainers == null) return false;
		record++;
		return record < atomContainers.getAtomContainerCount();
	}

	public Object next() {
		if (atomContainers == null) return null;
		else
		return atomContainers.getAtomContainer(record);
	}

	public String toString() {
		return "Reads compounds from " + getFormat() + " file";
	}
}
