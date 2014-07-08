/* RowsModel.java
 * Author: nina
 * Date: Apr 6, 2009
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

package ambit2.db.results;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;

import javax.swing.AbstractListModel;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.TypedListModel;

public class RowsModel<T> extends AbstractListModel implements TypedListModel<T>, PropertyChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5034323147503404064L;
	protected AmbitRows<T> rows;
	public RowsModel(AmbitRows<T> rows) {
		this.rows = rows;
		rows.addPropertyChangeListener(this);
	}

	public T getElementAt(int index) {
		try {
	        rows.first();
	        rows.relative(index);
			return rows.getObject();
		}catch (Exception x) {
			return null;
		}
	}

	public int getSize() {
		if (rows == null) return 0;
		return rows.size();
	}
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("status")) {
			fireContentsChanged(this,0,rows.size());

		}
		
	}

	public boolean hasNext() {
		try {
			rows.next();
			return !rows.isAfterLast();
		} catch (SQLException x) {
			return false;
		}		 
	}

	public T next() {
		try {
			return rows.getObject();
		} catch (AmbitException x) {
			return null;
		}
	}

	public void remove() {
		// TODO Auto-generated method stub
		
	}

	
}