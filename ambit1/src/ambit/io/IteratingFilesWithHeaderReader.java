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

/**
 * 
 */
package ambit.io;

import java.util.ArrayList;

import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;
import org.openscience.cdk.io.setting.IOSetting;

import ambit.data.molecule.SmilesParserWithTimeout;
import ambit.log.AmbitLogger;

/**
 * Abstract class for reading files with header (like CSV).
 * @author Nina Jeliazkova
 *
 */
public abstract class IteratingFilesWithHeaderReader extends
		DefaultIteratingChemObjectReader {
	protected SmilesParserWithTimeout sp = null;
	protected static AmbitLogger logger = new AmbitLogger(IteratingFilesWithHeaderReader.class);	
	public static String defaultSMILESHeader = "SMILES";
	protected ArrayList header;
	protected int smilesIndex = -1;
	protected long timeout = 60000; //ms
	protected IOSetting[] headerOptions = null;	
	protected int numberOfHeaderLines = 1;
	/**
	 * 
	 */
	public IteratingFilesWithHeaderReader() {
		super();
		header = new ArrayList();
		sp = new SmilesParserWithTimeout();
	}
    protected IOSetting[] setHeaderOptions(ArrayList header) {
        IOSetting[] ios = new IOSetting[1];
        ios[0] = new MolPropertiesIOSetting(header,IOSetting.HIGH,"Select column types");
        return ios;
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
}


