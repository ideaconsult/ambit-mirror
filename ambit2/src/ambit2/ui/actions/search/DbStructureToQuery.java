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

package ambit2.ui.actions.search;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.ui.UITools;
import ambit2.ui.actions.AmbitAction;

/**
 * Loads current structure from {@link ambit2.database.data.AmbitDatabaseToolsData#getMolecules()} 
 * to {@link ambit2.database.data.AmbitDatabaseToolsData#getQueries()}, thus making available the current structure 
 * for query. See example for {@link ambit2.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbStructureToQuery extends AmbitAction {
	public DbStructureToQuery(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Load");
		
	}

	public DbStructureToQuery(Object userData, JFrame mainFrame, String arg0) {
		this(userData, mainFrame, arg0,null);

	}

	public DbStructureToQuery(Object userData, JFrame mainFrame, String arg0,
			Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
		putValue(AbstractAction.SHORT_DESCRIPTION, "Loads Query with the current compound from Molecule Browser");
	}
	public void run(ActionEvent arg0) {
		if (userData instanceof AmbitDatabaseToolsData)
		    try {
			((AmbitDatabaseToolsData) userData).setCurrentMoleculeAsQuery();
		    } catch (Exception x) {
		        JOptionPane.showMessageDialog(mainFrame,x.getCause().getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		    }
	}
}


