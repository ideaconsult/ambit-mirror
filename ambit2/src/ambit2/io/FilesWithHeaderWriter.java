/* FilesWithHeaderWriter.java
 * Author: Nina Jeliazkova
 * Date: Feb 2, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.tools.LoggingTool;

public abstract class FilesWithHeaderWriter extends DefaultChemObjectWriter {
    protected static LoggingTool logger = new LoggingTool(FilesWithHeaderWriter.class);    
    public static String defaultSMILESHeader = "SMILES";    
    protected ArrayList header = null;
    protected boolean writingStarted = false;    
    protected int smilesIndex = -1;    

    /**
     * @return Returns the header.
     */
    public synchronized ArrayList getHeader() {
        return header;
    }
    /**
     * Creates header from Hashtable keys
     * Used for default header - created from properties of the first molecule written 
     * @param properties
     */
    public void setHeader(Map properties) {
        if (writingStarted) {
            logger.error("Can't change header while writing !!!!");
            return; //cant' change header !
        }       
        header = new ArrayList();
        Iterator e = properties.keySet().iterator();
        smilesIndex = -1; int i = 0;
        while (e.hasNext()) {
            header.add(e.next());
            if (header.get(i).equals(defaultSMILESHeader)) smilesIndex = i;
            i++;
        }
        if (smilesIndex == -1) { header.add(0,defaultSMILESHeader); smilesIndex = 0; }
        logger.info("Header created from hashtable\t",header);
    }    
    abstract protected void writeHeader() throws IOException;
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
}
