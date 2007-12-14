/*
 * Created on 2005-9-5
 *
 */
package ambit.io;

import org.openscience.cdk.io.formats.IChemFormatMatcher;
import org.openscience.cdk.tools.DataFeatures;

/**
 * {@link org.openscience.cdk.io.formats.IChemFormatMatcher} implementation for delimited text files.
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
	/* (non-Javadoc)
	 * @see org.openscience.cdk.io.extensions.ChemFormat#getReaderClassName()
	 */
	public String getReaderClassName() {
		return "ambit.io.DelimitedFileReader";
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.io.extensions.ChemFormat#getWriterClassName()
	 */
	public String getWriterClassName() {
		return "ambit.io.DelimitedFileWriter";
		
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
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	StringBuffer b = new StringBuffer();
    	if (fieldDelimiter == '\t') b.append("tab delimited file");
    	else  if (fieldDelimiter == ',') b.append("comma delimited file");
    	else {
    		b.append("'");
    		b.append(fieldDelimiter);
    		b.append("' delimited file");
    	}
    	return b.toString();
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
}
