/* PubchemCID.java
 * Author: nina
 * Date: Mar 24, 2009
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

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;

/**
 * returns pubchem CID given the structure properties
 * @author nina
 *
 */
public class PubchemCID extends PropertyKey<Number> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5172763951258433726L;
	public PubchemCID() {
		super(Property.getInstance("PUBCHEM_CID","PUBCHEM", "http://pubchem.ncbi.nlm.nih.gov"));
	}

	@Override
	protected boolean isValid(Object newkey, Object value) {
		if (newkey != null) {
			newkey = newkey.toString().toUpperCase();
			if (key.getName().equals(newkey)) try {
				return Integer.parseInt(value.toString()) > 0;
			} catch(Exception x) {
				return false;
			}
		} 
		return false;
	}
	@Override
	public Number process(IStructureRecord structure) throws AmbitException {
		Object o = super.process(structure);
		if (o == null) return null;
		try {
			return new Integer(o.toString());
		} catch (NumberFormatException x) {
			throw new AmbitException(x);
		}
	}
	@Override
	public Object getQueryKey() {
		return getKey();
	}
	public Class getType() {
		return Integer.class;
	}


}
