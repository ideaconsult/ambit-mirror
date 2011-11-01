package ambit2.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMoleculeSet;
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
	protected IMoleculeSet atomContainers = null;
	protected int record = -1;
	public IteratingChemObjectReaderWrapper(DefaultChemObjectReader reader) {
		super();
		try {
			format = reader.getFormat();
			chemObject = reader.read(new org.openscience.cdk.ChemFile());
			if (chemObject instanceof IChemFile) {
				int c = ((IChemFile) chemObject).getChemSequenceCount();
				if (c>0) {
					IChemSequence s = ((IChemFile) chemObject).getChemSequence(0);
					c = s.getChemModelCount();
					if (c > 0) { 
						IChemModel m = s.getChemModel(0);
						atomContainers = m.getMoleculeSet();
					}	
				}
			} else
			if (chemObject instanceof IMoleculeSet) 
				atomContainers = (IMoleculeSet)chemObject; 
		} catch (CDKException x) {
			x.printStackTrace();
			chemObject = null;
			format = null;
		}
	}
	public void setReader(InputStream reader) throws CDKException {
		throw new CDKException("Not implemented");
		
	}
	public void setReader(Reader reader) throws CDKException {
		throw new CDKException("Not implemented");
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
