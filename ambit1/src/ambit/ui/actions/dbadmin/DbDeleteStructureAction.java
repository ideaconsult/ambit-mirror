/* DbDeleteStructureAction.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-16 
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

package ambit.ui.actions.dbadmin;

import java.sql.Connection;

import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.data.molecule.SourceDataset;
import ambit.database.DbConnection;
import ambit.database.readers.DbStructureReader;
import ambit.database.writers.DbDeleteStructure;
import ambit.exceptions.AmbitException;
import ambit.ui.actions.search.DbSearchAction;

/**
 * Deletes structure.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-16
 */
public class DbDeleteStructureAction extends DbSearchAction {
    Connection connection = null;
    /**
     * @param userData
     * @param mainFrame
     */
    public DbDeleteStructureAction(Object userData, JFrame mainFrame) {
        super(userData, mainFrame);
        
    }

    /**
     * @param userData
     * @param mainFrame
     * @param name
     */
    public DbDeleteStructureAction(Object userData, JFrame mainFrame,
            String name) {
        super(userData, mainFrame, name);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param userData
     * @param mainFrame
     * @param name
     * @param icon
     */
    public DbDeleteStructureAction(Object userData, JFrame mainFrame,
            String name, Icon icon) {
        super(userData, mainFrame, name, icon);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * @see ambit.ui.actions.search.DbSearchAction#getSearchReader(java.sql.Connection, java.lang.Object, int, int)
     */
    public IIteratingChemObjectReader getSearchReader(Connection connection,
            Object query, int page, int pagesize) throws AmbitException {
        this.connection = connection;
        //String sql = "SELECT distinct(dvalues.idstructure),uncompress(structure) as ustructure from dvalues join structure using (idstructure) where status=\"ERROR\" limit 100";
        String sql = "SELECT distinct(dvalues.idstructure) from dvalues where status=\"ERROR\" limit 5000";
        return new DbStructureReader(connection,sql);
    }
    /* (non-Javadoc)
     * @see ambit.ui.actions.DbSearchAction#getWriter()
     */
    public IChemObjectWriter getWriter() {
        // TODO Auto-generated method stub
        try {
        return new DbDeleteStructure(connection);
        } catch (AmbitException x) {
            return  null;
        }
    }
    @Override
    protected SourceDataset getResultsDataset(DbConnection conn) {
    	// TODO Auto-generated method stub
    	return null;
    }
}
