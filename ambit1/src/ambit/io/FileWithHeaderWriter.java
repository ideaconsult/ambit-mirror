/*
Copyright (C) 2005-2006  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.LoggingTool;

import ambit.misc.AmbitCONSTANTS;

public abstract class FileWithHeaderWriter extends DefaultChemObjectWriter {
	protected static LoggingTool logger = new LoggingTool(DelimitedFileWriter.class);
	protected ArrayList header = null;
	protected int smilesIndex = -1;
	protected SmilesGenerator sg = new SmilesGenerator(DefaultChemObjectBuilder.getInstance());
	public static String defaultSMILESHeader = "SMILES";
	protected boolean writingStarted = false;

	public FileWithHeaderWriter() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.io.ChemObjectWriter#write(org.openscience.cdk.ChemObject)
	 */
	public void write(IChemObject object) throws CDKException {
		
		if (object instanceof ISetOfMolecules) {
		    writeSetOfMolecules((ISetOfMolecules)object);
		} else if (object instanceof IMolecule) {
		    writeMolecule((IMolecule)object);
		} else {
		    throw new CDKException("Only supported is writing of ChemFile and Molecule objects." + object.getClass().getName());
		}
	}

	
	protected abstract void writeHeader() throws IOException;
	/**
	 * @return Returns the header.
	 */
	public synchronized ArrayList getHeader() {
		return header;
	}
	/**
	 * @param header The header to set.
	 */
	public synchronized void setHeader(ArrayList header) {
		if (writingStarted) {
			logger.error("Can't change header while writing !!!!");
			return; //cant' change header !
		}
		this.header = header;
		smilesIndex = -1;
		for (int i=0; i < header.size(); i++) 
			if (header.get(i).equals(defaultSMILESHeader)) smilesIndex = i;
		if (smilesIndex == -1) { header.add(0,defaultSMILESHeader); smilesIndex = 0; }
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
		    Object column = e.nextElement();
		    if ((column.equals("SmallestRings")) ||
		            (column.equals("AllRings")) ||
		            (column.equals("CRAMERFLAGS")) ||
		            (column.equals(AmbitCONSTANTS.AMBIT_IDSTRUCTURE)) ||
		            (column.equals(AmbitCONSTANTS.AMBIT_IDSUBSTANCE))) continue;
		    else {

				header.add(column);
				if (header.get(i).equals(defaultSMILESHeader)) smilesIndex = i;
				i++;
		    }
		}
		if (smilesIndex == -1) { header.add(0,defaultSMILESHeader); smilesIndex = 0; }
		logger.info("Header created from hashtable\t",header);
	}
	public abstract void writeMolecule(IMolecule molecule) ;
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
	/* (non-Javadoc)
	 * @see org.openscience.cdk.io.ChemObjectWriter#highestSupportedChemObject()
	 */
	public ChemObject highestSupportedChemObject() {
        return new SetOfMolecules();
	}

}


