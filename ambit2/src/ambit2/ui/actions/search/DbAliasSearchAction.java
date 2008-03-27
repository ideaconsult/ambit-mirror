/* DbAliasSearchAction.java
 * Author: Nina Jeliazkova
 * Date: Mar 22, 2007 
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

package ambit2.ui.actions.search;

import java.sql.Connection;

import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.database.DbConnection;
import ambit2.database.data.ISharedDbData;
import ambit2.database.processors.ReadAliasProcessor;
import ambit2.database.processors.ReadCASProcessor;
import ambit2.database.processors.ReadNameProcessor;
import ambit2.database.processors.ReadSMILESProcessor;
import ambit2.database.processors.ReadStructureProcessor;
import ambit2.database.readers.DbStructureReader;
import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitProcessor;
import ambit2.processors.ProcessorsChain;
import ambit2.ui.UITools;

public class DbAliasSearchAction extends DbNameSearchAction {
    protected String sql = "select structure.idstructure,idsubstance,uncompress(structure) as ustructure,alias from structure join alias using(idstructure) where alias ";

    public DbAliasSearchAction(Object userData, JFrame mainFrame) {
        this(userData, mainFrame,"Identifier");
    }

    public DbAliasSearchAction(Object userData, JFrame mainFrame, String arg0) {
        this(userData, mainFrame, arg0,UITools.createImageIcon("ambit2/ui/images/search.png"));
    }

    public DbAliasSearchAction(Object userData, JFrame mainFrame, String arg0,
            Icon arg1) {
        super(userData, mainFrame, arg0, arg1);
    }
    public IIteratingChemObjectReader getSearchReader(Connection connection, Object query,int page, int pagesize) throws AmbitException {
        String name = "";
        if (query instanceof Molecule ) {
            Object c = ((Molecule) query).getProperty(CDKConstants.NAMES);
            if (c != null)
                name = c.toString();
        }   
        name = getSearchCriteria("Search database by other identifiers (e.g. INChI, aliases, etc.)", 
                "Enter identifier\n",name);
            //If a string was returned, say so.
        if ((name != null) && (name.length() > 0)) {
            return new DbStructureReader(connection,sql + comparisons[0][comparison] + '"' + name + "\" limit " + + page + "," + pagesize );
        }
        return null;
    }    
    public IAmbitProcessor getProcessor() {
        if (userData instanceof ISharedDbData) {
            ISharedDbData dbaData = ((ISharedDbData) userData);     
            DbConnection conn = dbaData.getDbConnection();
            ProcessorsChain processors = new ProcessorsChain();
            try {
                processors.add(new ReadStructureProcessor(dbaData.getDbConnection().getConn()));
                processors.add(new ReadSMILESProcessor(dbaData.getDbConnection().getConn()));
                processors.add(new ReadCASProcessor(dbaData.getDbConnection().getConn()));
                processors.add(new ReadNameProcessor(dbaData.getDbConnection().getConn()));
            } catch (Exception x) {
                logger.error(x);
            }
            return processors;
        } else return super.getProcessor();
    }
}
