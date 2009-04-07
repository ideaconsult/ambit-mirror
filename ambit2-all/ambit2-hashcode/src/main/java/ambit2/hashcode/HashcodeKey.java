/* HashcodeKey.java
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

package ambit2.hashcode;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.processors.structure.key.IStructureKey;

/**
 * Returns hash code
 * @author nina
 *
 */
public class HashcodeKey extends DefaultAmbitProcessor<IAtomContainer,Long>
		implements IStructureKey<IAtomContainer,Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8380664924329486420L;
	protected MoleculeAndAtomsHashing hashing;
	protected Object key="hashcode";
	public HashcodeKey() {
		hashing = new MoleculeAndAtomsHashing();
	}
	public Object getKey() {
		return key;
	}
	public void setKey(Object key) {
		this.key = key;
	}
	public Long process(IAtomContainer molecule) throws AmbitException {
		if ((molecule==null)||(molecule.getAtomCount()==0)) return 0L;
		return hashing.getMoleculeHash(molecule);
	}
	public Object getQueryKey() {
		return null;
	}
	public Class getType() {
		return Long.class;
	}

	
}
