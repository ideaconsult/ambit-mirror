/* AquirePrepareAction.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-15 
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

import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.database.DbConnection;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.data.ISharedDbData;
import ambit.database.processors.CASSmilesLookup;
import ambit.database.writers.SourceDatasetWriter;
import ambit.io.batch.DefaultBatchStatistics;
import ambit.io.batch.IBatchStatistics;
import ambit.processors.IAmbitProcessor;
import ambit.ui.actions.BatchAction;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-15
 */
public class AquirePrepareAction extends BatchAction {

    /**
     * @param userData
     * @param mainFrame
     */
    public AquirePrepareAction(Object userData, JFrame mainFrame) {
        this(userData, mainFrame,"Prepare AQUIRE links",null);
    }

    /**
     * @param userData
     * @param mainFrame
     * @param name
     */
    public AquirePrepareAction(Object userData, JFrame mainFrame, String name) {
        this(userData, mainFrame, name,null);
    }

    /**
     * @param userData
     * @param mainFrame
     * @param name
     * @param icon
     */
    public AquirePrepareAction(Object userData, JFrame mainFrame, String name,
            Icon icon) {
        super(userData, mainFrame, name, icon);
        interactive = false;
    }
    
    /* (non-Javadoc)
     * @see ambit.ui.actions.BatchAction#getProcessor()
     */
    public IAmbitProcessor getProcessor() {
		if (userData instanceof ISharedDbData) {
			ISharedDbData dbaData = ((ISharedDbData) userData);
			DbConnection conn = dbaData.getDbConnection();
			return new CASSmilesLookup(conn.getConn(),false);
		} return null;
    }

    /* (non-Javadoc)
     * @see ambit.ui.actions.BatchAction#getReader()
     */
    public IIteratingChemObjectReader getReader() {
        //return AquireCompoundsReader
        try {
   
            return new AquireCompoundsReader(null,null,0,Integer.MAX_VALUE);
        } catch (Exception x) {
            logger.error(x);
            return null;
            
        }
    }

    /* (non-Javadoc)
     * @see ambit.ui.actions.BatchAction#getWriter()
     */
    public IChemObjectWriter getWriter() {
        if (userData instanceof ISharedDbData) {
        	ISharedDbData dbaData = ((ISharedDbData) userData);
			DbConnection conn = dbaData.getDbConnection();
			return new SourceDatasetWriter(conn);
		} return null;
		
        
    }
	/* (non-Javadoc)
     * @see ambit.ui.actions.BatchAction#getBatshStatistics()
     */
    public IBatchStatistics getBatchStatistics() {
    	IBatchStatistics bs = null;
    	if (userData instanceof AmbitDatabaseToolsData) 
			bs = ((AmbitDatabaseToolsData) userData).getBatchStatistics();
		else bs = new DefaultBatchStatistics();
    	bs.setResultCaption("Processed ");
    	return bs;
    }
}
