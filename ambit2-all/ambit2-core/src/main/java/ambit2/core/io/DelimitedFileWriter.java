/*
Copyright Ideaconsult Ltd. (C) 2005-2007 

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

*/
package ambit2.core.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.DataFeatures;

/**
 * Writes delimited files
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-9-6
 */
public class DelimitedFileWriter extends FilesWithHeaderWriter {

	protected BufferedWriter writer;
	protected DelimitedFileFormat format;

	protected SmilesGenerator sg = new SmilesGenerator(true);


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
	/* (non-Javadoc)
	 * @see org.openscience.cdk.io.ChemObjectWriter#write(org.openscience.cdk.ChemObject)
	 */
	public void write(IChemObject object) throws CDKException {
		
		if (object instanceof IAtomContainerSet) {
		    writeSetOfMolecules((IAtomContainerSet)object);
		} else if (object instanceof IAtomContainer) {
		    writeMolecule((IAtomContainer)object);
		} else {
		    throw new CDKException("Only supported is writing of ChemFile and Molecule objects.");
		}

	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.io.ChemObjectWriter#highestSupportedChemObject()
	 */
	public ChemObject highestSupportedChemObject() {
        return new MoleculeSet();
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
	public void  writeSetOfMolecules(IAtomContainerSet som)
	{
		for (int i = 0; i < som.getAtomContainerCount(); i++) {
			try {
				writeMolecule(som.getAtomContainer(i));
			} catch (Exception exc) {
			}
		}
	}
	protected void writeHeader() throws IOException {
        String fieldDelimiter =  format.getFieldDelimiter().substring(0,1);
        char textDelimiter = format.getTextDelimiter();		
		for (int i=0;i<header.size();i++) {
			String h = header.get(i).toString();
			if (h.indexOf(fieldDelimiter) >  -1) {
				writer.write(textDelimiter);
				writer.write(h);
				writer.write(textDelimiter);
			} else writer.write(h);
			writer.write(fieldDelimiter);			
		}
		writer.newLine();
		logger.fine(format.getFormatName()+"\tHeader written\t"+header);
	}
    public void writeMolecule(IAtomContainer molecule) {
        
    	String fieldDelimiter =  format.getFieldDelimiter().substring(0,1);
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
        		value = molecule.getProperty(header.get(i));
        		if (i == smilesIndex) {
        			
        			if (value == null) //no SMILES available
        			try {
        				value = sg.createSMILES(molecule);
        			} catch (Exception x) {
        				logger.log(Level.WARNING,"Error while createSMILES\t",x);
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
            logger.finer("file flushed...");
        } catch(Exception x) {
            logger.log(Level.SEVERE,"ERROR while writing Molecule: ", x);
        }
    }




    @Override
	public String toString() {
        return "Writing compounds to " + format.toString(); 
    }
	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
			if (IMoleculeSet.class.equals(interfaces[i])) return true;
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
            	logger.log(Level.SEVERE,x.getMessage(),x);
            }
            this.writer = null;
        }
        this.writer = new BufferedWriter(writer);

    }
}
