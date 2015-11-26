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

package ambit2.core.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.smiles.SmilesGenerator;

import ambit2.core.config.AmbitCONSTANTS;

public abstract class FileWithHeaderWriter extends DefaultChemObjectWriter {
	protected static Logger logger = Logger.getLogger(DelimitedFileWriter.class.getName());
	protected Header header = null;
	protected int smilesIndex = -1;
	protected SmilesGenerator sg = new SmilesGenerator();
	public static String defaultSMILESHeader = "SMILES";
	protected boolean writingStarted = false;

	public FileWithHeaderWriter() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.io.ChemObjectWriter#write(org.openscience.cdk.ChemObject)
	 */
	public void write(IChemObject object) throws CDKException {
		fireIOSettingQuestion(null);
		if (object instanceof IAtomContainerSet) {
		    writeSetOfMolecules((IAtomContainerSet)object);
		} else if (object instanceof IAtomContainer) {
		    writeMolecule((IAtomContainer)object);
		} else {
		    throw new CDKException("Only supported is writing of ChemFile and Molecule objects." + object.getClass().getName());
		}
	}

	
	protected abstract void writeHeader() throws IOException;
	/**
	 * @return Returns the header.
	 */
	public synchronized Header getHeader() {
		return header;
	}
	/**
	 * @param header The header to set.
	 */
	public synchronized void setHeader(ArrayList header) {
		if (writingStarted) {
			logger.warning("Can't change header while writing !!!!");
			return; //cant' change header !
		}
		if (this.header == null) this.header = new Header();
		this.header.list = header;
		smilesIndex = -1;
		for (int i=0; i < header.size(); i++) 
			if (this.header.list.get(i).equals(defaultSMILESHeader)) smilesIndex = i;
		if (smilesIndex == -1) { this.header.list.add(0,defaultSMILESHeader); smilesIndex = 0; }
		logger.fine("Header created\t"+header);
	}
	/**
	 * Creates header from Hashtable keys
	 * Used for default header - created from properties of the first molecule written 
	 * @param properties
	 */
	public void setHeader(Map properties) {
		if (writingStarted) {
			logger.warning("Can't change header while writing !!!!");
			return; //cant' change header !
		}		
		header = new Header();
		Iterator e = properties.keySet().iterator();
		smilesIndex = -1; int i = 0;
		while (e.hasNext()) {
		    Object column = e.next();
		    if ((column.equals("SmallestRings")) ||
		            (column.equals("AllRings")) ||
		            (column.equals("CRAMERFLAGS")) ||
		            (column.equals(AmbitCONSTANTS.AMBIT_IDSTRUCTURE)) ||
		            (column.equals(AmbitCONSTANTS.AMBIT_IDSUBSTANCE))) continue;
		    else {

				header.list.add(column);
				if (header.list.get(i).equals(defaultSMILESHeader)) smilesIndex = i;
				i++;
		    }
		}
		if (smilesIndex == -1) { header.list.add(0,defaultSMILESHeader); smilesIndex = 0; }
		logger.fine("Header created from hashtable\t"+header);
	}
	public abstract void writeMolecule(IAtomContainer molecule) ;
	public void  writeSetOfMolecules(IAtomContainerSet som)
	{
		Iterator<IAtomContainer> molecules = som.atomContainers().iterator();
		while (molecules.hasNext()) {
			writeMolecule((IAtomContainer)molecules.next());	
		}
		/**
		writeMolecule(molecules[0]);
		for (int i = 1; i <= som.getMoleculeCount() - 1; i++) {
			try {
				writeMolecule(molecules[i]);
			} catch (Exception exc) {
			}
		}
		*/
	}


}


class Header extends IOSetting {
	protected ArrayList list;
	public Header() {
		super("Header",Importance.HIGH,"Header",null);
		list = new ArrayList();
	}
	
	@Override
	public void setSetting(String setting) throws CDKException {
		// TODO Auto-generated method stub
		super.setSetting(setting);
	}

	@Override
	public String getSetting() {
		// TODO Auto-generated method stub
		return super.getSetting();
	}

	@Override
	public String getQuestion() {
		// TODO Auto-generated method stub
		return super.getQuestion();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return super.getName();
	}

	@Override
	public Importance getLevel() {
		return super.getLevel();
	}

	@Override
	public String getDefaultSetting() {
		// TODO Auto-generated method stub
		return super.getDefaultSetting();
	}
	public ArrayList getList() {
		return list;
	}
	public void setList(ArrayList list) {
		this.list = list;
	}
	public void clear() {
		list.clear();
	}
	
	public int size() {
		return list.size();
	}


}