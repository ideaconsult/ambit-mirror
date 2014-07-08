/* CASKey.java
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

import org.openscience.cdk.index.CASNumber;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.CASProcessor;

/**
 * Returns CAS number given the structure and its properties
 * 
 * @author nina
 * 
 */
public class CASKey extends PropertyKey<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3848970585289051369L;
	protected Property key = null;
	protected CASProcessor transformer = new CASProcessor();

	public CASKey() {
	}

	@Override
	public boolean isKeyValid(Property key) {
		return key.isCAS();
	}
	public String process(IStructureRecord structure) throws AmbitException {
		if (structure == null)
			throw new AmbitException("Empty molecule!");

		if ((key == null) || (structure.getProperty(key) == null)) {
			// find which key corresponds to CAS
			for (Property newkey : structure.getProperties()) {
				Object cas = structure.getProperty(newkey);
				if (cas == null)
					continue;
				if (!isKeyValid(newkey)) continue;
				
				cas = transformer.process(cas.toString());
				
				if (CASNumber.isValid(cas.toString())) {
					this.key = newkey;
					return cas.toString();
				}
			}
		}
		if (key == null)
			throw new AmbitException("CAS tag not defined");
		Object o = structure.getProperty(key);
		if (o == null)
			return null;
		String cas = transformer.process(o.toString());
		if (CASProcessor.isValidFormat(cas))
		//if (CASNumber.isValid(cas))
			return cas;
		else
			return null;
	}

	@Override
	protected boolean isValid(Object key, Object value) {
		return CASProcessor.isValidFormat(value.toString());
	}

	public Class getType() {
		return String.class;
	}

}
