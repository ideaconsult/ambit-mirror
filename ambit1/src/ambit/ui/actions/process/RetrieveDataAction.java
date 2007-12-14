/* RetrieveDataAction.java
 * Author: Nina Jeliazkova
 * Date: Mar 21, 2007 
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

package ambit.ui.actions.process;

import javax.swing.Icon;
import javax.swing.JFrame;

import ambit.database.data.AmbitDatabaseToolsData;
import ambit.processors.IAmbitProcessor;
import ambit.ui.UITools;

public class RetrieveDataAction extends MoleculeCalculationAction {

    public RetrieveDataAction(Object userData, JFrame mainFrame) {
        this(userData, mainFrame,"Retrieve");
    }

    public RetrieveDataAction(Object userData, JFrame mainFrame, String name) {
        this(userData, mainFrame, name,UITools.createImageIcon("ambit/ui/images/dataset.png"));

    }

    public RetrieveDataAction(Object userData, JFrame mainFrame, String name,
            Icon icon) {
        super(userData, mainFrame, name, icon);
        putValue(SHORT_DESCRIPTION,"Retrieves information from database for the current structure(s)");
        setInteractive(true);
    }
    public IAmbitProcessor getProcessor() {
        try {
        	return ((AmbitDatabaseToolsData)userData).createProcessorChain();
        	/*
        if (userData instanceof ISharedData) {
            ISharedDbData dbaData = ((ISharedDbData) userData);
            ProcessorsChain processors = new ProcessorsChain();
            Connection connection = dbaData.getDbConnection().getConn();
            processors.add(new FindUniqueProcessor(connection));
            processors.add(new ReadStructureProcessor(connection));
            processors.add(new ReadAliasProcessor(connection));
            processors.add(new ReadNameProcessor(connection));
            processors.add(new ReadCASProcessor(connection));
            processors.add(new ReadSMILESProcessor(connection));
            
            
            if (userData instanceof AmbitDatabaseToolsData) {
            	
	            processors.add(new ReadExperimentsProcessor(
	            		((AmbitDatabaseToolsData)userData).getExperiments(),connection));        
	            processors.add(new ReadDescriptorsProcessor(
	            		((AmbitDatabaseToolsData)userData).getDescriptors(),connection));
            }
            return processors;  
        }
        */
        } catch (Exception x) {
            logger.error(x);
            return super.getProcessor();
        }
        
    }
}
