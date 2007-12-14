/* DBCalculateAtomDistancesAction.java
 * Author: Nina Jeliazkova
 * Date: 2006-5-16 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
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

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.database.DbConnection;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.data.ISharedDbData;
import ambit.database.processors.ReadStructureProcessor;
import ambit.database.readers.DbStructureDistanceReader;
import ambit.database.writers.AtomDistanceWriter;
import ambit.exceptions.AmbitException;
import ambit.io.batch.DefaultBatchStatistics;
import ambit.io.batch.IBatchStatistics;
import ambit.processors.IAmbitProcessor;
import ambit.processors.ProcessorsChain;
import ambit.processors.structure.AtomDistanceProcessor;
import ambit.ui.UITools;
import ambit.ui.actions.BatchAction;

/**
 * Reads structures without precalculated pairwise atom distances from the database,
 * calculates atom distances and writes them into database.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-16
 */
public class DBCalculateAtomDistancesAction extends BatchAction {

    /**
     * @param userData
     * @param mainFrame
     */
    public DBCalculateAtomDistancesAction(Object userData, JFrame mainFrame) {
        this(userData, mainFrame,"Calculate distances between 2 atoms");
    }

    /**
     * @param userData
     * @param mainFrame
     * @param name
     */
    public DBCalculateAtomDistancesAction(Object userData, JFrame mainFrame,
            String name) {
        this(userData, mainFrame, name,UITools.createImageIcon("ambit/ui/images/database.png"));
    }

    /**
     * @param userData
     * @param mainFrame
     * @param name
     * @param icon
     */
    public DBCalculateAtomDistancesAction(Object userData, JFrame mainFrame,
            String name, Icon icon) {
        super(userData, mainFrame, name, icon);
        putValue(AbstractAction.SHORT_DESCRIPTION,"Calculates distances between atoms for compounds in the database.");
    }
    /* (non-Javadoc)
     * @see ambit.ui.actions.BatchAction#getProcessor()
     */
    public IAmbitProcessor getProcessor() {
        ProcessorsChain processors = new ProcessorsChain();
        try {
        if (userData instanceof ISharedDbData)
            processors.add(new ReadStructureProcessor(((ISharedDbData) userData).getDbConnection().getConn()));
        } catch (AmbitException x) {
            logger.error(x);
            return null;
        }
        processors.add(new AtomDistanceProcessor());
        return processors;
    }
    /* (non-Javadoc)
     * @see ambit.ui.actions.BatchAction#getReader()
     */
    public IIteratingChemObjectReader getReader() {
        if (userData instanceof ISharedDbData)
            return new DbStructureDistanceReader(((ISharedDbData) userData).getDbConnection().getConn(),100000);
        else return null;
    }

    /* (non-Javadoc)
     * @see ambit.ui.actions.BatchAction#getWriter()
     */
    public IChemObjectWriter getWriter() {
        if (userData instanceof ISharedDbData) {
            DbConnection conn = ((ISharedDbData) userData).getDbConnection();
            return new AtomDistanceWriter(conn.getConn(),conn.getUser());
        } else return null;
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
