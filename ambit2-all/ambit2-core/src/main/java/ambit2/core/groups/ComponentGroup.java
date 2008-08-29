/* ComponentGroup.java
 * Author: Nina Jeliazkova
 * Date: Aug 9, 2008 
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

import java.util.ArrayList;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

public class ComponentGroup extends AbstractGroup {
    protected Integer order = null;
    protected static final String mainsubscript="c";
    /**
     * 
     */
    private static final long serialVersionUID = -2031261520924881915L;
    public ComponentGroup(String label,int number) {
        super(label,number);
        bonds = new ArrayList<IBond>();
        setGroupVisible(this, false);
        setPoint2d(new Point2d(0,0));
        setSubscript(mainsubscript);
    }
    public void finalizeAtomList(IAtomContainer mol) {
        addBondsConnected2Atoms(mol,atoms,bonds);
    }
    public synchronized Integer getOrder() {
        return order;
    }
    public synchronized void setOrder(Integer order) {
        this.order = order;
        setSubscript(mainsubscript+order);
    }    
}
