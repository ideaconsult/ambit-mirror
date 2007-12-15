package ambit.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.IDescriptor;

import ambit.data.molecule.DataContainer;
import ambit.data.molecule.PropertyTranslator;
import ambit.log.AmbitLogger;
import ambit.misc.AmbitCONSTANTS;

/**
 * {@link org.openscience.cdk.io.DefaultChemObjectWriter} descendant to add compounds into {@link ambit.data.molecule.DataContainer}.
 * Used as a writer in batch processing {@link ambit.io.batch.DefaultBatchProcessing}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class ListOfMoleculesWriter extends DefaultChemObjectWriter {
	protected PropertyTranslator translator; 
	protected DataContainer list = null;
	protected static AmbitLogger logger = new AmbitLogger(ListOfMoleculesWriter.class);
	public ListOfMoleculesWriter(DataContainer list) {
		super();
		this.list = list;

	}

	public void write(IChemObject arg0) throws CDKException {
		if (list == null) return;
		try {
		    logger.debug("Write "+ arg0.toString());
		    if (translator == null) {
			    Hashtable pp = arg0.getProperties();
			    Enumeration keys = pp.keys();
			    translator = list.getContainers().getAvailableProperties();
			    while (keys.hasMoreElements()) {
			    	Object key = keys.nextElement();
		    		translator.guessAndAdd(key);
			    }
		    }
		    
			if (list.getMoleculesCount() == 0)
				list.addMolecule((IMolecule) arg0);
			else
				list.addMoleculeNoNotify((IMolecule) arg0);
		} catch (Exception x) {
			throw new CDKException(x.getMessage());
		}
	}



	public void close() throws IOException {
		list.first();
	}
	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
			if (ISetOfMolecules.class.equals(interfaces[i])) return true;
			if (IMolecule.class.equals(interfaces[i])) return true;
		}
		return false;
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#getFormat()
     */
    public IResourceFormat getFormat() {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.OutputStream)
     */
    public void setWriter(OutputStream writer) throws CDKException {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.Writer)
     */
    public void setWriter(Writer writer) throws CDKException {
        // TODO Auto-generated method stub

    }
}
