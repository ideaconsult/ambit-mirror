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
package ambit2.io;

import org.openscience.cdk.io.formats.IChemFormatMatcher;
import org.openscience.cdk.tools.DataFeatures;

/**
 * File format for delimited files
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-9-5
 */
public class DelimitedFileFormat implements IChemFormatMatcher {
	protected char fieldDelimiter = ',';
	protected char textDelimiter = '"';
	/**
	 * Default field delimiter ','
	 * Default text delimiter '"'
	 */
	public DelimitedFileFormat() {
		super();
	}
	/**
	 * 
	 * @param fieldDelimiter  , default ','
	 * @param textDelimiter , default '"'
	 */
	public DelimitedFileFormat(char fieldDelimiter, char textDelimiter) {
		super();
		this.fieldDelimiter = fieldDelimiter;
		this.textDelimiter = textDelimiter;
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.io.extensions.ChemFormatMatcher#matches(int, java.lang.String)
	 */
	public boolean matches(int lineNumber, String line) {

		return false;
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.io.extensions.ChemFormat#getFormatName()
	 */
	public String getFormatName() {
		if (fieldDelimiter == ',') 	return "CSV";
		else if (fieldDelimiter == ' ') 	return "TXT";
		else if (fieldDelimiter == '\t') 	return "TXT";
		else return "Unknown";
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.io.extensions.ChemFormat#getReaderClassName()
	 */
	public String getReaderClassName() {
		return "toxTree.io.DelimitedFileReader";
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.io.extensions.ChemFormat#getWriterClassName()
	 */
	public String getWriterClassName() {
		return "toxTree.io.DelimitedFileWriter";
		
	}

	/**
	 * @return Returns the fieldDelimiter.
	 */
	public synchronized char getFieldDelimiter() {
		return fieldDelimiter;
	}
	/**
	 * @return Returns the textDelimiter.
	 */
	public synchronized char getTextDelimiter() {
		return textDelimiter;
	}
	   /* (non-Javadoc)
     * @see org.openscience.cdk.io.formats.IResourceFormat#getPreferredNameExtension()
     */
    public String getPreferredNameExtension() {
        return getNameExtensions()[0];
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.formats.IChemFormat#getSupportedDataFeatures()
     */
	public int getSupportedDataFeatures() {
		return DataFeatures.HAS_GRAPH_REPRESENTATION;
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.io.formats.IResourceFormat#isXMLBased()
     */
    public boolean isXMLBased() {
        return false;
    }	
	/* (non-Javadoc)
     * @see org.openscience.cdk.io.formats.IResourceFormat#getMIMEType()
     */
    public String getMIMEType() {
        return "text/plain";
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.formats.IResourceFormat#getNameExtensions()
     */
    public String[] getNameExtensions() {
        return new String[]{"csv", "txt"};
    }
    public int getRequiredDataFeatures() {
    	return DataFeatures.NONE;
    }
}
