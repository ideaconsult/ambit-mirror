	/* AquireSearchAction.java
 * Author: Nina Jeliazkova
 * Date: Mar 20, 2007 
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

import java.sql.Connection;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.data.literature.ReferenceFactory;
import ambit.data.molecule.SourceDataset;
import ambit.database.DbConnection;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.data.ISharedDbData;
import ambit.database.processors.CASSmilesLookup;
import ambit.database.processors.ReadAliasProcessor;
import ambit.database.processors.ReadCASProcessor;
import ambit.database.processors.ReadNameProcessor;
import ambit.exceptions.AmbitException;
import ambit.processors.IAmbitProcessor;
import ambit.processors.ProcessorsChain;
import ambit.ui.UITools;
import ambit.ui.actions.search.DbSearchAction;

public class AquireSearchAction extends DbSearchAction {
    public AquireSearchAction(Object userData, JFrame mainFrame) {
        this(userData, mainFrame,"Search AQUIRE");
    }

    public AquireSearchAction(Object userData, JFrame mainFrame, String name) {
        this(userData, mainFrame, name,UITools.createImageIcon("ambit/ui/images/search.png"));
    }

    public AquireSearchAction(Object userData, JFrame mainFrame, String name,
            Icon icon) {
        super(userData, mainFrame, name, icon);
        putValue(SHORT_DESCRIPTION, "Search AQUIRE by endpoint and species");
        setInteractive(false);
    }

    @Override
    public IIteratingChemObjectReader getSearchReader(Connection connection,
            Object query, int page, int pagesize) throws AmbitException {
        try {
            
            AmbitDatabaseToolsData dbaData = ((AmbitDatabaseToolsData) userData);
            AquireOptions options = new AquireOptions(null,	dbaData);
            if (JOptionPane.showConfirmDialog(mainFrame,options,"Search options",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                dbaData.setAquire_endpoint(options.getEndpoint());
                dbaData.setAquire_endpoint_description(options.getEndpointDescription());
                dbaData.setAquire_species(options.getSpecies());     
                dbaData.setAquire_simpletemplate(options.useSimpletemplate);
                return new AquireCompoundsReader(dbaData.getAquire_endpoint(),
                        dbaData.getAquire_species(),page,pagesize);
            } else return null;
        } catch (Exception x) {
            logger.error(x);
            return null;
            
        }
    }
	public IAmbitProcessor getProcessor() {
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);		
			DbConnection conn = dbaData.getDbConnection();
			ProcessorsChain processors = new ProcessorsChain();
			try {
				Connection c = dbaData.getDbConnection().getConn();
				processors.add(new CASSmilesLookup(c,true));
				//processors.add(new FindUniqueProcessor(c));
				//processors.add(new ReadStructureProcessor(c));
				//processors.add(new ReadSMILESProcessor(dbaData.getDbConnection().getConn()));
				processors.add(new ReadCASProcessor(c));
				processors.add(new ReadNameProcessor(c));
				processors.add(new ReadAliasProcessor(c));
				
			    if (userData instanceof AmbitDatabaseToolsData) {
			    	AmbitDatabaseToolsData dba = ((AmbitDatabaseToolsData) userData);
				    IAmbitProcessor p = new DbAquireProcessor(c,
							dba.getHost(),	
							dba.getPort(),
							dba.getUser().getName(),
							dba.getUser().getPassword(),
				    		dba.getAquire_endpoint(),
				    		dba.getAquire_species(),
				    		dba.isAquire_simpletemplate());
				    p.setEnabled(false);			    
				    processors.addProcessor(p);			    
			    }
			} catch (Exception x) {
				logger.error(x);
			}
			return processors;
		} else return super.getProcessor();
	}
@Override
	protected SourceDataset getResultsDataset(DbConnection conn) {
	return new SourceDataset("AQUIRE search",ReferenceFactory.createSearchReference("AQUIRE search"));
	}
}

