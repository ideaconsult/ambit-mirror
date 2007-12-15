package ambit.data.molecule;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.formats.IResourceFormat;

/**
 * A {@link org.openscience.cdk.io.DefaultChemObjectWriter} descendant 
 * to update {@link org.openscience.cdk.interfaces.IMolecule}. May seem strange to have an iterator for a single molecule,
 * but this provides a common interface for using batch processing {@link ambit.io.batch.IBatch}. <br>
 * Used in {@link ambit.ui.actions.process.MoleculeCalculationAction},
 * {@link ambit.ui.actions.search.DbSearchAction}, {@link ambit.ui.actions.search.DbExactSearchAction}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class CurrentMoleculeWriter extends DefaultChemObjectWriter {
	protected DataContainer list = null;
	public CurrentMoleculeWriter(DataContainer list) {
		super();
		this.list = list;
	}

	public void write(IChemObject arg0) throws CDKException {
	    arg0.removeProperty("EIGENVALUES");
	    arg0.removeProperty("NO. OF FILLED LEVELS");
	    Hashtable pp = arg0.getProperties();
	    Enumeration keys = pp.keys();	    
	    PropertyTranslator translator = list.getContainers().getAvailableProperties();
	    while (keys.hasMoreElements()) {
	    	Object key = keys.nextElement();
    		translator.guessAndAdd(key);
	    }	    
		list.setMolecule((IMolecule) arg0);

	}

	public IResourceFormat getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	public void close() throws IOException {
		list.setEnabled(true);

	}
    public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IAtomContainer.class.equals(interfaces[i])) return true;
		}
		return false;
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.OutputStream)
     */
    public void setWriter(OutputStream writer) throws CDKException {
        throw new CDKException(getClass().getName() + "setting a writer not allowed");

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.Writer)
     */
    public void setWriter(Writer writer) throws CDKException {
        throw new CDKException(getClass().getName() + "setting a writer not allowed");

    }
    public String toString() {
    	
    	return "Updates current molecule";
    }
}
