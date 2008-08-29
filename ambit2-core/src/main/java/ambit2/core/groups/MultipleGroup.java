/* MultipleGroup.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;



public class MultipleGroup extends AbstractGroup implements IExpandable {
    protected int multiplier;
    protected int number;
    protected List<IAtom> parentAtoms;
    protected List<IBond> parentBonds;
    boolean expanded = true;    
    /**
     * 
     */
    private static final long serialVersionUID = -9110648470808236343L;

	public MultipleGroup(String label,int number) {
        super(label,number);
        parentAtoms = null;
        parentBonds = null;
        setGroupVisible(this,false);
    }

    public synchronized int getMultiplier() {
        return multiplier;
    }

    public synchronized void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }
    public void addParentAtom(IAtom atom) {
    	if (parentAtoms == null) parentAtoms = new ArrayList<IAtom>();
    	parentAtoms.add(atom);
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
    public Iterable<IAtom> getAtoms(boolean expanded) {
        if (expanded) {
            return new Iterable<IAtom>() {
                public Iterator<IAtom> iterator() {
                    return atoms.iterator();
                }
            };
        } else 
            return new Iterable<IAtom>() {
            public Iterator<IAtom> iterator() {
                return parentAtoms.iterator();
            }
        };            
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
                return parentBonds.iterator();
            }
            
        };            
    }    
    public void finalizeAtomList(IAtomContainer mol) {
        if (bonds == null) bonds = new ArrayList<IBond>();
        addBondsConnected2Atoms(mol,atoms,bonds);
    }
    public void finalizeParentAtomList(IAtomContainer mol) {
        if (parentBonds == null) parentBonds = new ArrayList<IBond>();
        if (parentAtoms != null) 
            addBondsConnected2Atoms(mol,parentAtoms,parentBonds);
    }
    
    public boolean isExpanded() {
        return expanded;
    }    
}
