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

import java.util.Iterator;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;

public abstract class PropertyKey<Result> extends DefaultAmbitProcessor<IStructureRecord,Result>
		implements IStructureKey<IStructureRecord,Result> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6393097111823664858L;
	protected String key=null;
	public PropertyKey() {
		this(null);
	}
	public PropertyKey(String key) {
		setKey(key);
	}
		
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}	
	public Result process(IStructureRecord structure) throws AmbitException {
		if (structure==null)
			throw new AmbitException("Empty molecule!");
		
		if ((key == null) || (structure.getProperty(key)==null)) {
			//find which key corresponds to CAS
			Iterator keys = structure.getProperties().keySet().iterator();
			while (keys.hasNext()) {
				Object newkey = keys.next();
				if (isValid(newkey, structure.getProperties().get(newkey).toString())) {
					this.key = newkey.toString();
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
		return (Result)structure.getProperty(key);
	}
	@Override
	public String toString() {
		return "Property tag";
	}
	protected boolean isValid(Object key, Object value) {
		return key != null;
	}
	public String getQueryKey() {
		return null;
	}
	public abstract Class getType();
}
