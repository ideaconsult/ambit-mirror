package ambit.data.molecule;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.formats.IResourceFormat;
/**
 * @deprecated
 * A {@link org.openscience.cdk.io.DefaultChemObjectWriter} descendant to add compounds to {@link ambit.data.molecule.CompoundsList}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class CompoundsListWriter extends DefaultChemObjectWriter {
	protected CompoundsList list = null;
	protected IResourceFormat format = null;
	public CompoundsListWriter(CompoundsList list) {
		super();
		this.list = list;
	}

	public void write(IChemObject arg0) throws CDKException {
		if (list == null) return;
		try {
			list.addItem(new Compound((IMolecule) arg0));
		} catch (Exception x) {
			throw new CDKException(x.getMessage());
		}
	}

	/* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#accepts(java.lang.Class)
     */
    public boolean accepts(Class classObject) {
        return true;
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#getFormat()
     */
    public IResourceFormat getFormat() {
        return format;
    }
	public void close() throws IOException {
		list.setModified(true);
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.OutputStream)
     */
    public void setWriter(OutputStream writer) throws CDKException {
        throw new CDKException(getClass().getName() + " setting a writer not allowed");

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.Writer)
     */
    public void setWriter(Writer writer) throws CDKException {
        throw new CDKException(getClass().getName() + " setting a writer not allowed");

    }
}
