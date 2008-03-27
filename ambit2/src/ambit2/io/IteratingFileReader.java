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

package ambit2.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.setting.IOSetting;

import ambit2.exceptions.AmbitIOException;

/**
 * A wrapper for all file readers. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class IteratingFileReader implements IIteratingChemObjectReader {
	protected IIteratingChemObjectReader reader;
	protected String filename = "";
	
	
	public IteratingFileReader(File file) throws AmbitIOException {	
	}
	public IteratingFileReader(File file,IChemFormat format) throws AmbitIOException {
		super();
		try {
			reader = FileInputState.getReader(new FileInputStream(file), file.getName(),format);
			filename = file.getName();
		} catch (Exception x) {
			throw new AmbitIOException("Error",x,file.getName()); 
		}
	}

	public boolean accepts(Class arg0) {
		return reader.accepts(arg0);
	}

	public void close() throws IOException {
		reader.close();
	}

	public IOSetting[] getIOSettings() {
		return reader.getIOSettings();
	}

	public void addChemObjectIOListener(IChemObjectIOListener arg0) {
		reader.addChemObjectIOListener(arg0);
	}

	public void removeChemObjectIOListener(IChemObjectIOListener arg0) {
		reader.removeChemObjectIOListener(arg0);
	}

	public void remove() {
		reader.remove();

	}

	public boolean hasNext() {
		return reader.hasNext();
	}

	public Object next() {
		return reader.next();
	}
	public String toString() {
		return "Reading compounds from " + filename;
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#getFormat()
     */
    public IResourceFormat getFormat() {
        return reader.getFormat();
    }
}


