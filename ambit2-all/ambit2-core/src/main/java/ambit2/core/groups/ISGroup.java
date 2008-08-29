/* ISGroup.java
 * Author: Nina Jeliazkova
 * Date: Jul 26, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
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

package ambit2.core.groups;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import ambit2.core.io.SGroupMDL2000Helper.SGROUP_LINE;


public interface ISGroup extends Comparable<ISGroup>, IAtom {
	static final String SGROUP_VISIBLE = "ambit2.groups.ISGroup.visible";
    static final String SGROUP_CONNECTIVITY = "ambit2.groups.ISGroup.SGROUP_CONNECTIVITY";
    
    int getNumber();
    void setNumber(int number);
    IAtom addAtom(IAtom atom);
    IBond addBond(IBond bond);
    /**
     * text of Sgroup subscript. See {@link SGROUP_LINE#M__SMT}.
     * @param subscript
     */
    void setSubscript(String subscript);
    String getSubscript();
    void addBrackets(double x1,double y1,double x2,double y2);
    Iterable<IAtom> getAtoms(boolean expanded);
    Iterable<IBond> getBonds(boolean expanded);
    
    void finalizeAtomList(IAtomContainer mol);
    
}
