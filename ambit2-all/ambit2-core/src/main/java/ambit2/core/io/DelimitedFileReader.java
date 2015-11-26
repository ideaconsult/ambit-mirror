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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.DefaultChemObjectReader;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.silent.AtomContainerSet;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.LoggingTool;

import ambit2.core.data.MoleculeTools;

/*
 Reads delimited files (CSV, TXT) 
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-9-6
 */
public class DelimitedFileReader extends DefaultChemObjectReader implements
		IChemObjectReader {
	protected DelimitedFileFormat format;
	private BufferedReader input = null;
	private static LoggingTool logger = new LoggingTool(
			DelimitedFileReader.class);

	/**
	 * Default delimited file format - CSV
	 */
	// TODO make use of IOSettings to recognise fields in the header
	public DelimitedFileReader(Reader input, DelimitedFileFormat format) {
		logger = new LoggingTool(this);
		this.input = new BufferedReader(input);
		this.format = format;
	}

	public DelimitedFileReader(Reader input) {
		this(input, new DelimitedFileFormat());
	}

	public DelimitedFileReader(InputStream input, DelimitedFileFormat format) {
		this(new InputStreamReader(input));
		this.format = format;
	}

	public DelimitedFileReader(InputStream input) {
		this(new InputStreamReader(input), new DelimitedFileFormat());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openscience.cdk.io.ChemObjectReader#read(org.openscience.cdk.ChemObject
	 * )
	 */
	public IChemObject read(IChemObject object) throws CDKException {
		if (object instanceof IAtomContainerSet) {
			return (IChemObject) readSetOfMolecules();
		} else if (object instanceof IChemFile) {
			IChemFile file = MoleculeTools.newChemFile(SilentChemObjectBuilder
					.getInstance());
			IChemSequence sequence = MoleculeTools
					.newChemSequence(SilentChemObjectBuilder.getInstance());
			IChemModel chemModel = MoleculeTools
					.newChemModel(SilentChemObjectBuilder.getInstance());
			chemModel.setMoleculeSet(readSetOfMolecules());
			sequence.addChemModel(chemModel);
			file.addChemSequence(sequence);
			return (IChemObject) file;
		} else {
			throw new CDKException(
					"Only supported is reading of SetOfMolecules objects.");
		}

	}

	private IAtomContainerSet readSetOfMolecules() {
		try {
			IAtomContainerSet som = new AtomContainerSet();
			IteratingDelimitedFileReader reader = new IteratingDelimitedFileReader(
					input, format);
			while (reader.hasNext()) {
				Object object = reader.next();
				if (object instanceof IAtomContainer)
					som.addAtomContainer((IAtomContainer) object);
			}

			reader.close();
			return som;
		} catch (Exception x) {
			logger.error(x);
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openscience.cdk.io.ChemObjectReader#setReader(java.io.Reader)
	 */
	public void setReader(Reader reader) throws CDKException {
		if (reader instanceof BufferedReader) {
			this.input = (BufferedReader) reader;
		} else {
			this.input = new BufferedReader(reader);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openscience.cdk.io.ChemObjectReader#setReader(java.io.InputStream)
	 */
	public void setReader(InputStream reader) throws CDKException {
		setReader(new InputStreamReader(reader));
	}

	public IResourceFormat getFormat() {
		return format;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openscience.cdk.io.ChemObjectIO#close()
	 */
	public void close() throws IOException {
		input.close();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openscience.cdk.io.ChemObjectIO#getIOSettings()
	 */
	@Override
	public IOSetting[] getIOSettings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return "Reading compounds from " + format.toString();
	}

	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i]))
				return true;
			if (IAtomContainerSet.class.equals(interfaces[i]))
				return true;
		}
		return false;
	}

}
