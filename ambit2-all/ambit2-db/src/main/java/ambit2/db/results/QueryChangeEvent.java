/* QueryChangeEvent.java
 * Author: nina
 * Date: Apr 5, 2009
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

import net.idea.modbcum.i.IQueryRetrieval;

public class QueryChangeEvent<T> extends PropertyChangeEvent {

	public QueryChangeEvent(Object source, String propertyName,
			IQueryRetrieval<T> oldQuery, IQueryRetrieval<T> newQuery) {
		super(source, propertyName, oldQuery,newQuery);
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8430039223408120160L;
	IQueryRetrieval<T> getNewQuery() {
		return (IQueryRetrieval<T>)getNewValue();
	}
}
