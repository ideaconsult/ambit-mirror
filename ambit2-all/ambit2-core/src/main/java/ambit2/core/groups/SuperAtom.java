/* SuperAtom.java
 * Author: Nina Jeliazkova
 * Date: Jul 27, 2008 
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
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import ambit2.core.data.MoleculeTools;


public class SuperAtom extends AbstractGroup  implements IExpandable {
	boolean expanded = true;
    protected List<IBond> contractedBonds;

    /**
	 * 
	 */
	private static final long serialVersionUID = -3485052440705149342L;

	public SuperAtom(String label,int number) {
        super(label,number);
        bonds = new ArrayList<IBond>();
        contractedBonds = new ArrayList<IBond>();
        setGroupVisible(this, false);
        setPoint2d(new Point2d(0,0));
    }
    @Override
    public void setSubscript(String subscript) {
        super.setSubscript(subscript);
        //setSymbol(subscript);
    }
    public void finalizeAtomList(IAtomContainer mol) {
        addBondsConnected2Atoms(mol,atoms,bonds);
    }
    @Override
    public IBond addBond(IBond bond) {
        IBond newbond = MoleculeTools.newBond(getBuilder());
        newbond.setOrder(bond.getOrder());
        Iterator<IAtom> batoms = bond.atoms().iterator();
        int position=0;
        while (batoms.hasNext()) {
            IAtom atom = batoms.next();
            if (atoms.contains(atom)) {
                newbond.setAtom(this, position++);
                this.setPoint2d(new Point2d(atom.getPoint2d().x,atom.getPoint2d().y));
            } else
                newbond.setAtom(atom, position++);
                
        }
        contractedBonds.add(newbond);
        super.addBond(bond);
        return newbond;
    }
    @Override
    public Iterable<IBond> getBonds(boolean expanded) {
        if (expanded)
            return new Iterable<IBond>() {
                public Iterator<IBond> iterator() {
                    return bonds.iterator();
                }
                
            };
        else 
            return new Iterable<IBond>() {
            public Iterator<IBond> iterator() {
                return contractedBonds.iterator();
            }
            
        };            
    }
    public Iterable<IAtom> getAtoms(boolean expanded) {
        if (expanded) {
            return new Iterable<IAtom>() {
                public Iterator<IAtom> iterator() {
                    return atoms.iterator();
                }
            };
        } else 
            return new SelfIterable<IAtom>(this);

    }
    
    @Override
    public boolean isExpanded(IBond bond) {
        return super.isExpanded(bond);
    }
	public boolean isExpanded() {
		return expanded;
	}
    public void setExpanded(boolean expanded) {
        if (this.expanded != expanded) {
            for (IAtom atom : getAtoms(this.expanded))
                setGroupVisible(atom,false);
            for (IBond bond : getBonds(this.expanded))
                setGroupVisible(bond,false);            
            this.expanded = expanded;
            for (IAtom atom : getAtoms(this.expanded))
                setGroupVisible(atom,true);
            for (IBond bond : getBonds(this.expanded))
                setGroupVisible(bond,true);                     
        }
    }   
}
