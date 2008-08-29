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

import org.openscience.cdk.io.formats.IChemFormatMatcher;

/**
 * Microsoft Excel XLS file format.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class XLSFileFormat implements IChemFormatMatcher {

	public XLSFileFormat() {
		super();
	}
	//TODO
	public boolean matches(int arg0, String arg1) {
		return false;
	}

	public String getFormatName() {
		return "XLS";
	}

	public String getReaderClassName() {
		return "ambit2.io.ExcelFileReader";
	}

	public String getWriterClassName() {
		return "ambit2.io.ExcelFileWriter";
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.io.formats.IResourceFormat#getMIMEType()
     */
    public String getMIMEType() {
        return "application/vnd.ms-excel";
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.formats.IResourceFormat#getNameExtensions()
     */
    public String[] getNameExtensions() {
        // TODO Auto-generated method stub
        return new String[] {"xls"};
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.formats.IResourceFormat#getPreferredNameExtension()
     */
    public String getPreferredNameExtension() {
        return "xls";
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.formats.IChemFormat#getSupportedDataFeatures()
     */
    public int getSupportedDataFeatures() {
        // TODO Auto-generated method stub
        return 0;
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.formats.IResourceFormat#isXMLBased()
     */
    public boolean isXMLBased() {

        return false;
    }
	public int getRequiredDataFeatures() {
		// TODO Auto-generated method stub
		return 0;
	}
    
}


