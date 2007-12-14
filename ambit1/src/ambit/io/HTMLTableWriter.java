package ambit.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.LoggingTool;

import ambit.exceptions.AmbitException;
import ambit.exceptions.AmbitIOException;
import ambit.processors.IAmbitResult;

/**
 * Writes HTML table.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class HTMLTableWriter extends DefaultChemObjectWriter implements IAmbitResultWriter {
	protected static LoggingTool logger = new LoggingTool(HTMLTableWriter.class);
	protected boolean writingStarted = false;
	protected BufferedWriter writer;
	protected String[]  rowDelimiter = {"<tr>","</tr>\n"};
	protected String[]  columnDelimiter = {"<td>","</td>"};
	protected String[]  headerDelimiter = {"<td>","</td>"};
	protected ArrayList header = null;
	protected int smilesIndex = -1;

	public HTMLTableWriter(Writer out) {
		super();
        try {
            writer = new BufferedWriter(out);
        } catch (Exception exc) {
        }
		
	}
    public HTMLTableWriter(OutputStream output) {
    	this(new OutputStreamWriter(output));
    }

	public void write(IChemObject object) throws CDKException {

		if (object instanceof ISetOfMolecules) {
		    writeSetOfMolecules((ISetOfMolecules)object);
		} else if (object instanceof IMolecule) {
		    writeMolecule((IMolecule)object);
		} else {
		    throw new CDKException("Only supported is writing of ChemFile and Molecule objects.");
		}
		
	}
	

	public void close() throws IOException {
        writer.flush();
        writer.close();
	}
	
	public void  writeSetOfMolecules(ISetOfMolecules som)
	{
		IMolecule[] molecules = som.getMolecules();
		writeMolecule(molecules[0]);
		for (int i = 1; i <= som.getMoleculeCount() - 1; i++) {
			try {
				writeMolecule(molecules[i]);
			} catch (Exception exc) {
			}
		}
	}
	
    public void writeMolecule(IMolecule molecule) {
        
        Object value;    	

        try {
        	//give it a chance to create a header just before the first write
        	if (!writingStarted) {
    	        if (header == null) setHeader(molecule.getProperties());
    	        writeHeader();
    	        writingStarted = true;
        	}
        	String s;
        	writer.write(rowDelimiter[0]);
        	for (int i =0; i< header.size(); i++) {
        		value = molecule.getProperty(header.get(i));
        	
        		if (value == null) value = "";
        		writer.write(columnDelimiter[0]);
	        	writer.write(value.toString().trim());
	        	writer.write(columnDelimiter[1]);	        				
        	}
            writer.write(rowDelimiter[1]);
            writer.flush();
            logger.debug("file flushed...");
        } catch(Exception x) {
            logger.error("ERROR while writing Molecule: ", x.getMessage());
            logger.debug(x);
            x.printStackTrace();
        }
    }
	
	protected void writeHeader() throws IOException {
		writer.write(rowDelimiter[0]);
		for (int i=0;i<header.size();i++) {
			String h = header.get(i).toString();
			writer.write(columnDelimiter[0]);
			writer.write(h);
			writer.write(columnDelimiter[1]);			
		}
		writer.write(rowDelimiter[1]);
		logger.debug("\tHeader written\t",header);
	}
	public synchronized void setHeader(String[] header) {
		ArrayList list = new ArrayList();
		for (int i=0; i < header.length;i++)
			list.add(header[i]);
		setHeader(list);
	}
	
	public synchronized void setHeader(ArrayList header) {
		if (writingStarted) {
			logger.error("Can't change header while writing !!!!");
			return; //cant' change header !
		}
		this.header = header;
		smilesIndex = -1;
		for (int i=0; i < header.size(); i++) 
			if (header.get(i).equals(DelimitedFileWriter.defaultSMILESHeader)) smilesIndex = i;
		if (smilesIndex == -1) { header.add(0,DelimitedFileWriter.defaultSMILESHeader); smilesIndex = 0; }
		logger.info("Header created\t",header);
	}
	/**
	 * Creates header from Hashtable keys
	 * Used for default header - created from properties of the first molecule written 
	 * @param properties
	 */
	public void setHeader(Hashtable properties) {
		if (writingStarted) {
			logger.error("Can't change header while writing !!!!");
			return; //cant' change header !
		}		
		header = new ArrayList();
		Enumeration e = properties.keys();
		smilesIndex = -1; int i = 0;
		while (e.hasMoreElements()) {
			header.add(e.nextElement());
			if (header.get(i).equals(DelimitedFileWriter.defaultSMILESHeader)) smilesIndex = i;
			i++;
		}
		if (smilesIndex == -1) { header.add(0,DelimitedFileWriter.defaultSMILESHeader); smilesIndex = 0; }
		logger.info("Header created from hashtable\t",header);
	}
	public void printHeader() {
		try {
			writer.write("<html><head></head><body><table border=1>");
		} catch (IOException x) {}
	}
	public void printFooter() {
		try {
		writer.write("</table></body></html>");
		} catch (IOException x) {}
	}

	public void writeResult(IAmbitResult result) throws AmbitException {
		try {
			writer.write("<tr><td>");
			if (result != null) result.write(writer);
			writer.write("</td></tr>");
			} catch (IOException x) { throw new AmbitIOException(x);}
	
		
	}
    public String toString() {
        return "Writing compounds to a HTML table"; 
    }
	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
			if (ISetOfMolecules.class.equals(interfaces[i])) return true;
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
