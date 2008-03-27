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

package ambit2.database.aquire;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.config.AmbitCONSTANTS;
import ambit2.database.DbConnection;
import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.data.ISharedDbData;
import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitProcessor;
import ambit2.ui.UITools;
import ambit2.ui.actions.process.MoleculeCalculationAction;
import ambit2.data.molecule.CurrentMoleculeReader;
import ambit2.data.molecule.DataContainer;
import ambit2.data.molecule.MoleculesIteratorReader;

/**
 * Reads data from AQUIRE database for the current compound.
 * @author Nina Jeliazkova
 *
 */
public class AquireRetrieveAction extends MoleculeCalculationAction {

	public AquireRetrieveAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,AmbitCONSTANTS.AQUIRE);

	}

	public AquireRetrieveAction(Object userData, JFrame mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit2/ui/images/experiment_16.png"));
	}

	public AquireRetrieveAction(Object userData, JFrame mainFrame, String name,
			Icon icon) {
		super(userData, mainFrame, name, icon);
		setInteractive(false);
		putValue(AbstractAction.SHORT_DESCRIPTION,"Looks for the current compound in AQUIRE database");
		putValue(AbstractAction.LONG_DESCRIPTION,"Looks for the current compound in AQUIRE database. Lookup is by CAS number.");
	}
	public IIteratingChemObjectReader getReader() {
		setInteractive(false);
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = (ISharedDbData) userData;
		    if (dbaData.getSource() == ISharedDbData.MEMORY_CURRENT)
		    	return new CurrentMoleculeReader(dbaData.getMolecule());
		    else if (dbaData.getSource() == ISharedDbData.MEMORY_LIST) {
				DataContainer c = dbaData.getMolecules();
				c.setEnabled(false);
				
				setInteractive(true);
				return new MoleculesIteratorReader(c.getContainers());
		    } else return new CurrentMoleculeReader(dbaData.getMolecule());
		} else return null;	
	}	
	@Override
	public IAmbitProcessor getProcessor() {
		AmbitDatabaseToolsData dba = ((AmbitDatabaseToolsData) userData);
		
		DbConnection conn = dba.getDbConnection();
		
		
		if ((conn ==null) || conn.isClosed()) JOptionPane.showMessageDialog(mainFrame, "Use Database/Open first");

		try {
			return new DbAquireProcessor(conn.getConn(),
					dba.getHost(),	
					dba.getPort(),
					dba.getUser().getName(),
					dba.getUser().getPassword(),
					dba.getAquire_endpoint(),
					dba.getAquire_species(),dba.isAquire_simpletemplate());
		} catch (AmbitException x) {
			logger.error(x);
			return null;
		}
	}

}


