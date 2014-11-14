/* InchiKey.java
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

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.core.processors.structure.InchiProcessor;

/**
 * Returns inchi
 * @author nina
 *
 */
public class InchiKey extends DefaultAmbitProcessor<IAtomContainer,String> implements
		IStructureKey<IAtomContainer,String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4957862884677389524L;
	protected InchiProcessor inchi;
	protected Object key;
	public InchiKey() throws CDKException {
		inchi = new InchiProcessor();
	}
	public Object getKey() {
		return key;
	}
	public void setKey(Object key) {
		this.key = key;
	}
	public String process(IAtomContainer molecule) throws AmbitException {
		if (molecule.getAtomCount()>0)
			try { 
				return inchi.process(molecule).getInchi(); 
			} catch (Exception x) {}
		Iterator<Object> keys = molecule.getProperties().keySet().iterator();
		while (keys.hasNext()) {
			Object value = molecule.getProperties().get(keys.next());
			if (value.toString().startsWith("InChI=")) 
				return value.toString();
		}
		return null;
	}
	public Object getQueryKey() {
		return ExactStructureSearchMode.inchi.name();
	}
	public Class getType() {
		return String.class;
	}
	public boolean useExactStructureID() {
		return false;
	}	
}
