/*
 * Created on 2005-9-6
 *
 */
package ambit.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.io.DefaultChemObjectReader;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Reads delimited files (CSV, TXT) 
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-9-6
 */
public class DelimitedFileReader extends DefaultChemObjectReader implements IChemObjectReader {
	protected DelimitedFileFormat format;
    private BufferedReader input = null;
    //private SmilesParser sp = null;
    private static LoggingTool logger = new LoggingTool(DelimitedFileReader.class);
	
	/**
	 * Default delimited file format - CSV
	 */
    //TODO make use of IOSettings to recognise fields in the header
    public DelimitedFileReader(Reader input,DelimitedFileFormat format) {
        logger = new LoggingTool(this);
        this.input = new BufferedReader(input);
      //  sp = new SmilesParser();
        this.format = format;
    }	
    public DelimitedFileReader(Reader input) {
    	this(input,new DelimitedFileFormat());
    }

    public DelimitedFileReader(InputStream input,DelimitedFileFormat format) {
        this(new InputStreamReader(input));
        this.format = format;
    }
    public DelimitedFileReader(InputStream input) {
    	this(new InputStreamReader(input),new DelimitedFileFormat());
    }    
	/* (non-Javadoc)
	 * @see org.openscience.cdk.io.ChemObjectReader#read(org.openscience.cdk.ChemObject)
	 */
	public IChemObject read(IChemObject object) throws CDKException {
        if (object instanceof ISetOfMolecules) {
            return (IChemObject)readSetOfMolecules();
        } else if (object instanceof IChemFile) {
            IChemFile file = DefaultChemObjectBuilder.getInstance().newChemFile();
            IChemSequence sequence = DefaultChemObjectBuilder.getInstance().newChemSequence();
            IChemModel chemModel = DefaultChemObjectBuilder.getInstance().newChemModel();
            chemModel.setSetOfMolecules(readSetOfMolecules());
            sequence.addChemModel(chemModel);
            file.addChemSequence(sequence);
            return (IChemObject) file;
        } else {
            throw new CDKException("Only supported is reading of SetOfMolecules objects.");
        }

	}
    private SetOfMolecules readSetOfMolecules() {
    	try {
    		SetOfMolecules som = new SetOfMolecules();
    		IteratingDelimitedFileReader reader = new IteratingDelimitedFileReader(input,format);
        while (reader.hasNext()) {
	        Object object = reader.next();
	        if (object instanceof AtomContainer) 
	        	som.addAtomContainer((AtomContainer)object);
        }
        
        	reader.close();
        	return som;
        } catch(Exception x) {
        	logger.error(x);
        	return null;
        }
        
    }
	/* (non-Javadoc)
	 * @see org.openscience.cdk.io.ChemObjectReader#setReader(java.io.Reader)
	 */
	public void setReader(Reader reader) throws CDKException {
        if (reader instanceof BufferedReader) {
            this.input = (BufferedReader)reader;
        } else {
            this.input = new BufferedReader(reader);
        }		
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.io.ChemObjectReader#setReader(java.io.InputStream)
	 */
	public void setReader(InputStream reader) throws CDKException {
		setReader(new InputStreamReader(reader));
	}

    public IResourceFormat getFormat() {
        return format;
    }



	/* (non-Javadoc)
	 * @see org.openscience.cdk.io.ChemObjectIO#close()
	 */
	public void close() throws IOException {
		input.close();

	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.io.ChemObjectIO#getIOSettings()
	 */
	public IOSetting[] getIOSettings() {
		// TODO Auto-generated method stub
		return null;
	}
    public String toString() {
        return "Reading compounds from " + format.toString(); 
    }
	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
			if (ISetOfMolecules.class.equals(interfaces[i])) return true;
		}
		return false;
	}

}
