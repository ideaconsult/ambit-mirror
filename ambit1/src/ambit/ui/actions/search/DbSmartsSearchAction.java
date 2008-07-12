/*
Copyright (C) 2005-2008  

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

package ambit.ui.actions.search;

import java.sql.Connection;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.data.molecule.SourceDataset;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.data.ISharedDbData;
import ambit.database.processors.ReadStructureProcessor;
import ambit.database.search.DbDatasetReader;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.IAmbitProcessor;
import ambit.processors.ProcessorsChain;
import ambit.processors.structure.SMARTSSearchProcessor;
import ambit.ui.UITools;

public class DbSmartsSearchAction extends DbSearchAction {
	public DbSmartsSearchAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Search");
	}

	public DbSmartsSearchAction(Object userData, JFrame mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit/ui/images/search.png"));
	}

	public DbSmartsSearchAction(Object userData, JFrame mainFrame, String name,
			Icon icon) {
		super(userData, mainFrame, name, icon);
		putValue(AbstractAction.SHORT_DESCRIPTION,"SMARTS");

	}
	
	@Override
	public IIteratingChemObjectReader getSearchReader(Connection connection,
			Object query, int page, int pagesize) throws AmbitException {
		SourceDataset d = null;
		if (userData instanceof AmbitDatabaseToolsData) {
			d = ((AmbitDatabaseToolsData)userData).getSrcDataset();
			if (d==null) { 
				if (JOptionPane.showConfirmDialog(
						mainFrame, 
						"<html>SMARTS search over the entire database can be very time consuming! Continue?</html>",
						"Please confirm",
						JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION)
					return null;
			}
		} else {	
			d = new SourceDataset();
			d.setName(AmbitCONSTANTS.AQUIRE);
		} 
		return new DbDatasetReader(connection,d,page,pagesize);
	}
	
	public IAmbitProcessor getProcessor() {
		ProcessorsChain chain = new ProcessorsChain();
		try {
			chain.addProcessor(new ReadStructureProcessor(((ISharedDbData) userData).getDbConnection().getConn()));
			chain.addProcessor(new SMARTSSearchProcessor(((AmbitDatabaseToolsData)userData).getFragments()));
			return chain;
		} catch (AmbitException x) {
			logger.error(x);
			return null;
		}
	}	

}
