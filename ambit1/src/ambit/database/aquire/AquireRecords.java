/* AquireRecords.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-14 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
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

package ambit.database.aquire;

import ambit.data.AmbitObject;
import ambit.data.IAmbitEditor;
import ambit.data.experiment.ExperimentList;
import ambit.ui.data.AmbitListEditor;

/**
 * Stores info from AQUIRE database.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2007-3-09
 */
public class AquireRecords extends ExperimentList {

    /**
     * 
     */
    public AquireRecords() {
        super();
    }
    @Override
    public AquireExperiment getItem(int index) {
    	return (AquireExperiment) super.getItem(index);
    }
    @Override
    public int addItem(AmbitObject entry) {
    	if (entry instanceof AquireExperiment)
    		return super.addItem(entry);
    	else return -1;
    }

    /* (non-Javadoc)
     * @see java.util.AbstractCollection#toString()
     */
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return toString(true); //return "AQUIRE entries (" + size() + ")";
    }
    
    public String toString(boolean verbose) {
    	if (!verbose) return "AQUIRE entries (" + size() + ")";
        StringBuffer b = new StringBuffer();
        for (int i=0; i < size();i++) {
            b.append(getItem(i).toString());
            b.append("\n");
        }
        return b.toString();
            
    }

    public String toHTML() {
        StringBuffer b = new StringBuffer();
//        b.append("<html>");
        for (int i=0; i < size();i++) {
            b.append(getItem(i).toString());
        }
  //      b.append("</html>");
        return b.toString();
            
    }

}
