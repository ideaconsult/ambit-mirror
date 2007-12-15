/*
 * Created on 2005-9-6
 *
 */
package ambit.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.DataFeatures;

/**
 * Writes delimited files (CSV, TXT) 
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-9-6
 */
public class DelimitedFileWriter extends FileWithHeaderWriter {

	protected BufferedWriter writer;
	protected DelimitedFileFormat format;
	/**
	 * 
	 */
    public DelimitedFileWriter(Writer out, DelimitedFileFormat format) {
        //logger = new LoggingTool(this);
    	this.format = format;
        try {
            writer = new BufferedWriter(out);
        } catch (Exception exc) {
        }
    }
    public DelimitedFileWriter(Writer out) {
    	this(out,new DelimitedFileFormat());
    }
    public DelimitedFileWriter(OutputStream input, DelimitedFileFormat format) {
        this(new OutputStreamWriter(input), format);
    }
    public DelimitedFileWriter(OutputStream input) {
    	this(input, new DelimitedFileFormat());
    }


    public IResourceFormat getFormat() {
        return format;
    }


	/* (non-Javadoc)
	 * @see org.openscience.cdk.io.ChemObjectIO#close()
	 */
	public void close() throws IOException {
        writer.flush();
        writer.close();

	}

	protected void writeHeader() throws IOException {
        char fieldDelimiter = format.getFieldDelimiter();
        char textDelimiter = format.getTextDelimiter();		
		for (int i=0;i<header.size();i++) {
			String h = header.list.get(i).toString();
			if (h.indexOf(fieldDelimiter) >  -1) {
				writer.write(textDelimiter);
				writer.write(h);
				writer.write(textDelimiter);
			} else writer.write(h);
			writer.write(fieldDelimiter);			
		}
		writer.newLine();
		logger.debug(format.getFormatName(),"\tHeader written\t",header);
	}
    public void writeMolecule(IMolecule molecule) {
        
        char fieldDelimiter = format.getFieldDelimiter();
        char textDelimiter = format.getTextDelimiter();
        Object value;    	

        try {
        	//give it a chance to create a header just before the first write
        	if (!writingStarted) {
    	        if (header == null) setHeader(molecule.getProperties());
    	        writeHeader();
    	        writingStarted = true;
        	}
        	String s;
        	for (int i =0; i< header.size(); i++) {
        		value = molecule.getProperty(header.list.get(i));
        		if (i == smilesIndex) {
        			
        			if (value == null) //no SMILES available
        			try {
        				value = sg.createSMILES(molecule);
        			} catch (Exception x) {
        				logger.error("Error while createSMILES\t",x.getMessage());
        				value = "";
        			}
        		} 
        	
        		if (value != null) {
        			if (value instanceof Number) {
        				writer.write(value.toString());
        			} else {
        				s = value.toString();
        				if (s.indexOf(textDelimiter) > -1) s=s.replace(textDelimiter,' ');
        				s = s.trim();
        				if (s.indexOf(fieldDelimiter) > -1) {
	        				writer.write(textDelimiter);
	        				writer.write(s);
	        				writer.write(textDelimiter);        
        				} else writer.write(s); 
        			}
        		}
        		if (i< (header.size()-1))
        				writer.write(fieldDelimiter);
        	}
            writer.newLine();
            writer.flush();
            logger.debug("file flushed...");
        } catch(Exception x) {
            logger.error("ERROR while writing Molecule: ", x.getMessage());
            logger.debug(x);
            x.printStackTrace();
        }
    }

    public String toString() {
        return "Writing compounds to " + format.toString(); 
    }
	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
			if (ISetOfMolecules.class.equals(interfaces[i])) return true;
		}
		return false;
	}
	public int getSupportedDataFeatures() {
		return DataFeatures.HAS_GRAPH_REPRESENTATION;
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.OutputStream)
     */
    public void setWriter(OutputStream writer) throws CDKException {
        this.setWriter(new OutputStreamWriter(writer));

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.Writer)
     */
    public void setWriter(Writer writer) throws CDKException {
        if (this.writer != null) {
            try {
            this.writer.close();
            } catch (IOException x) {
                logger.error(x);
            }
            this.writer = null;
        }
        this.writer = new BufferedWriter(writer);

    }
}
