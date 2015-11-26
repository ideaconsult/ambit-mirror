/* PropertyKey.java
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
import net.idea.modbcum.p.DefaultAmbitProcessor;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;

public abstract class PropertyKey<Result> extends DefaultAmbitProcessor<IStructureRecord,Result>
		implements IStructureKey<IStructureRecord,Result> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6393097111823664858L;
	protected Property key=null;
	public PropertyKey() {
		this(null);
	}
	public PropertyKey(Property key) {
		setKey(key);
	}
		
	public Object getKey() {
		if (key == null) return null;
		return key;
	}
	public void setKey(Property key) {
		this.key = key;
	}	
	public boolean isKeyValid(Property key) {
		return true;
	}
	public Result process(IStructureRecord structure) throws AmbitException {
		if (structure==null)
			throw new AmbitException("Empty molecule!");
		
		if ((key == null) || (structure.getRecordProperty(key)==null)) {
			//find which key corresponds to CAS
			for (Property newkey: structure.getRecordProperties()) {
				
				if (isKeyValid(newkey) && isValid(newkey, structure.getRecordProperty(newkey).toString())) {
					this.key = newkey;
					return getProperty(structure);
				}
			}
		}
		if (key == null) throw new AmbitException(toString() + " not defined");
		Result o = getProperty(structure);
		if ((o != null) && isValid(key,o.toString())) return o;
		else return null;
	}
	protected Result getProperty(IStructureRecord structure) throws AmbitException {
		return (Result)structure.getRecordProperty(key);
	}
	@Override
	public String toString() {
		return "Custom key to match structures";
	}
	protected boolean isValid(Object key, Object value) {
		return key != null;
	}
	public Object getQueryKey() {
		return null;
	}
	public abstract Class getType();
	@Override
	public boolean useExactStructureID() {
		return false;
	}
}
