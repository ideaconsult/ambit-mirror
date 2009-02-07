/* DictionaryQueryPanel.java
 * Author: nina
 * Date: Feb 6, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
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

package ambit2.dbui.dictionary;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import ambit2.core.data.Dictionary;
import ambit2.db.readers.DictionaryRows;
import ambit2.db.search.DictionaryQuery;
import ambit2.db.search.DictionarySubjectQuery;
import ambit2.ui.editors.BeanEditor;

public class DictionaryQueryPanel extends AmbitRowsPanel<Dictionary, DictionaryQuery, DictionaryRows> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 617890468043289221L;
	protected Dictionary query = new Dictionary();
	public DictionaryQueryPanel() {
		super();

		add(new BeanEditor(query,
					new String[] {"parentTemplate","template"},
					new String[] {"Category","Name"}
					,""),BorderLayout.NORTH);
		add(new JButton(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				DictionarySubjectQuery q = new DictionarySubjectQuery();
				q.setValue("a");
				getObject().setQuery(q);
				
			}
		}),BorderLayout.EAST);

	}
	
}
