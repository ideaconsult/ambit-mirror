/* InchiPropertyKey.java
 * Author: nina
 * Date: Apr 7, 2009
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

package ambit2.core.processors.structure.key;

import ambit2.base.data.Property;
import ambit2.core.processors.structure.InchiProcessor;

public class InchiPropertyKey extends PropertyKey<String> {
	protected static String inchitag="InChI=";
	protected InchiProcessor inchi;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1718062022783509128L;
	public InchiPropertyKey() {
		super(Property.getInChIInstance());
	}
	@Override
	protected boolean isValid(Object key, Object value) {
		return (value!=null) && (value.toString().indexOf(inchitag)==0);
	}
	@Override
	public Object getKey() {
		return null;
	}
	@Override
	public Object getQueryKey() {
		return key;
	}
	@Override
	public Class getType() {
		return String.class;
	}

}
