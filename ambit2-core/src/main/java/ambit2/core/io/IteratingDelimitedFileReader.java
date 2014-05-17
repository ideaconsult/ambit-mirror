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

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

/**
 * Iterating reader for delimited files.
 * 
 * @author Nina Jeliazkova <b>Modified</b> 2005-9-5
 */
public class IteratingDelimitedFileReader extends IteratingDelimitedFileReaderComplexHeader<String> implements IIteratingChemObjectReader {


	public IteratingDelimitedFileReader(Reader in) throws CDKException {
		this(in, new DelimitedFileFormat()); // default format
	}

	/**
	 * 
	 */
	public IteratingDelimitedFileReader(Reader in, DelimitedFileFormat format) throws CDKException {
		super(in,format);
	}

	public IteratingDelimitedFileReader(InputStream in) throws UnsupportedEncodingException, CDKException {
		super(in);
	}

	public IteratingDelimitedFileReader(InputStream in,DelimitedFileFormat format) throws UnsupportedEncodingException, CDKException {
		super(in, format);
	}

	
	@Override
	protected String getSmilesHeader(int index) {
		return getHeaderColumn(index).toUpperCase();
	}
	@Override
    protected String createPropertyByColumnName(String name) {
    	return name;
    }
}
