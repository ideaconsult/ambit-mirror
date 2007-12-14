/* DbCombinedSearchAction.java
 * Author: Nina Jeliazkova
 * Date: Sep 1, 2006 
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

package ambit.ui.actions.search;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;

import ambit.applications.dbadmin.AmbitDatabase;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.ui.UITools;
import ambit.ui.actions.AmbitAction;
import ambit.ui.query.DbQueryOptionsPanel;

/**
 * Provides common interface for {@link ambit.ui.actions.search.DbExactSearchAction},
 * {@link ambit.ui.actions.search.DbSimilaritySearchAction},
 * {@link ambit.ui.actions.search.DbSubstructureSearchAction},
 * {@link ambit.ui.actions.search.DbDescriptorsSearchAction},
 * {@link ambit.ui.actions.search.DbExperimentsSearchAction}. 
 * Displays {@link ambit.ui.query.DbQueryOptionsPanel} where the user can specify query details.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbCombinedSearchAction extends AmbitAction {
	public DbCombinedSearchAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Structure search");
	}

	public DbCombinedSearchAction(Object userData, JFrame mainFrame,
			String arg0) {
		this(userData, mainFrame, arg0,UITools.createImageIcon("ambit/ui/images/search.png"));
	}
	public DbCombinedSearchAction(Object userData, JFrame mainFrame,
			String arg0, Icon icon) {
		super(userData, mainFrame, arg0,icon);
	}
    /* (non-Javadoc)
     * @see ambit.ui.actions.AmbitAction#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
		putValue(AbstractAction.SHORT_DESCRIPTION, "Search by structure (exact, substructure,similarity), by descriptors and by experimental data");
		JFrame q = DbQueryOptionsPanel.getDbQueryOptions((AmbitDatabaseToolsData)userData);
		AmbitDatabase.centerScreen(q);
		q.setVisible(true);
    }
}
