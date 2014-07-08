/* EINECSKey.java
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
import ambit2.core.data.EINECS;

/**
 * returns einecs rn, given structure and its properties
 * @author nina
 *
 */
public class EINECSKey extends PropertyKey<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9098011521397491242L;

	@Override
	protected boolean isValid(Object key, Object value) {
		return EINECS.isValidFormat(value.toString());
	}
	@Override
	public boolean isKeyValid(Property key) {
		return key.isEINECS();
	}
	public Class getType() {
		return String.class;
	}
	
	public String process(IStructureRecord structure) throws AmbitException {
		if (structure == null)
			throw new AmbitException("Empty molecule!");

		if ((key == null) || (structure.getProperty(key) == null)) {
			// find which key corresponds to EINECS
			for (Property newkey : structure.getProperties()) {
				if (newkey.getName().contains("RELATED")) continue;
				Object einecs = structure.getProperty(newkey);
				if (einecs == null)
					continue;
				if (!isKeyValid(newkey)) continue;
				
				if (EINECS.isValid(einecs.toString())) {
					this.key = newkey;
					return einecs.toString();
				}
			}
		}
		if (key == null)
			throw new AmbitException("EINECS tag not defined");
		Object o = structure.getProperty(key);
		if (o == null)
			return null;
		if (EINECS.isValidFormat(o.toString()))
			return o.toString();
		else
			return null;
	}


	

}
