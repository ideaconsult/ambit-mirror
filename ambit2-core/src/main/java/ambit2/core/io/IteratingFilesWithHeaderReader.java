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
/**
 * 
 */
package ambit2.core.io;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.StringIOSetting;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.core.smiles.SmilesParserWrapper;

/**
 * @author Nina Jeliazkova
 *
 */
public abstract class IteratingFilesWithHeaderReader<COLUMN> extends
		DefaultIteratingChemObjectReader {
	protected static Logger logger = Logger.getLogger(DelimitedFileWriter.class.getName());
	protected SmilesParserWrapper sp = null;
	protected InChIGeneratorFactory inchiFactory = null;
	public static String defaultSMILESHeader = "SMILES";
	
	protected ArrayList<COLUMN> header;
	protected int smilesIndex = -1;
	protected int inchiIndex = -1;
	protected long timeout = 60000; //ms
	protected int numberOfHeaderLines = 1;
	/**
	 * 
	 */
	public IteratingFilesWithHeaderReader() {
		super();
		header = null;
		sp =  SmilesParserWrapper.getInstance();
	}
    
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	public int getNumberOfHeaderLines() {
		return numberOfHeaderLines;
	}
	public void setNumberOfHeaderLines(int numberOfHeaderLines) {
		this.numberOfHeaderLines = numberOfHeaderLines;
	}
	protected abstract LiteratureEntry getReference();
	protected void addHeaderColumn(String name) {
		header.add(createPropertyByColumnName(name));
		
		fireIOSettingQuestion(new StringIOSetting(name,IOSetting.MEDIUM,Property.IO_QUESTION.IO_TRANSLATE_NAME.toString(),name));
	}
	protected void setHeaderColumn(int index,String name) {
		header.ensureCapacity(index);
		while (index > header.size())
			header.add(createPropertyByColumnName(""));

		addHeaderColumn(name);
	}	
	
	protected abstract COLUMN createPropertyByColumnName(String name);
	
	protected COLUMN getHeaderColumn(int index) {
		if (header==null || header.size()<=index) return null;
		return header.get(index);
	}
	protected void updateHeaderColumn(int index,COLUMN value) {
		header.set(index,value);
	}
	protected int getNumberOfColumns() {
		return header.size();
	}
	protected boolean isHeaderEmpty() {
		boolean ok = header==null;
		if (ok) header = createHeader();
		return ok;
	}
	protected abstract ArrayList<COLUMN> createHeader();
}


