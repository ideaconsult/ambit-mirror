/*
Copyright (C) 2005-2007  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.query.smarts;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Generic interface for a SMARTS pattern.
 * @author Nina Jeliazkova nina@acad.bg
 *
 * @param <T>
 */
public interface ISmartsPattern<T> {
	String getSmarts();
	void setSmarts(String smarts) throws SMARTSException;
	boolean isNegate();
	void setNegate(boolean negate);
	int hasSMARTSPattern(T object) throws SMARTSException;
	T getObjectToVerify(IAtomContainer mol);
	int match(IAtomContainer mol) throws SMARTSException;
	List<List<Integer>> getUniqueMatchingAtoms() throws SMARTSException;
	String getName();
	void setName(String name);
	String getHint();
	void setHint(String hint);	
}


