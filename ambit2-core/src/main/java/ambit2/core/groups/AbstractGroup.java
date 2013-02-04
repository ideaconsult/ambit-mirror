/* GenericGroup.java
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

import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;


public abstract class AbstractGroup extends PseudoAtom implements ISGroup {
    /**
     * 
     */
    private static final long serialVersionUID = -151920308613450648L;
    protected List<IAtom> atoms;
    protected List<IBond> bonds;
    
    public AbstractGroup() {
        this("Group");
    }
    public AbstractGroup(String label) {
        super(label);
        atoms = null;
        bonds = null;
    }

    public AbstractGroup(String label,int number) {
        this(label);
        setNumber(number);
    }
    
    protected int number;    
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
    public int compareTo(ISGroup o) {
    	return getNumber()- o.getNumber();
    }
    @Override
    public boolean equals(Object obj) {
    	if (obj instanceof ISGroup)
    		return compareTo((ISGroup)obj)==0;
    	else try {
    		return getNumber() == Integer.parseInt(obj.toString());
    	} catch (Exception x) {
    		return false;
    	}
    }
    public IAtom addAtom(IAtom atom) {
    	if (atoms == null) atoms = new ArrayList<IAtom>();
    	atoms.add(atom);
        return atom;
    	
    }
    public IBond addBond(IBond bond) {
    	if (bonds == null) bonds = new ArrayList<IBond>();
    	bonds.add(bond);
        return bond;
    	
    }
    /**
     * does nothing
     */
    public void finalizeAtomList(IAtomContainer mol) {
        
    }
    public void setSubscript(String subscript) {
    	setLabel(subscript);
    }
    public String getSubscript() {
    	return getLabel();
    }
    public void addBrackets(double x1, double y1, double x2, double y2) {
    	
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
    protected class SelfIterable<T> implements Iterable<T> {
            
            protected T atom;
            public SelfIterable(T atom) {
                this.atom = atom;
            }
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    private int index = 0;
                    public boolean hasNext() {
                        return (index++ == 0);
                    }
                    public T next() {
                        return atom;
                    }
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }

                
    }
    public Iterable<IBond> getBonds(boolean expanded) {
        if (expanded) {
            return new Iterable<IBond>() {
                public Iterator<IBond> iterator() {
                    return new EmptyIterator<IBond>();
                }
            };              
                
        } else 
            return new Iterable<IBond>() {
                public Iterator<IBond> iterator() {
                    return bonds.iterator();
                }
            };        
        
    }
    protected class EmptyIterator<T> implements Iterator<T> {
        public boolean hasNext() {
            return false;
        }
        public T next() {
            return null;
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    public boolean isExpanded(IBond bond) {
        return bonds.contains(bond);
    }
    public static void setGroupVisible(IChemObject c, boolean visible) {
    	c.setProperty(ISGroup.SGROUP_VISIBLE,new Boolean(visible));

    }
    public static boolean isGroupVisible(IChemObject c) {
    	Object p = c.getProperty(ISGroup.SGROUP_VISIBLE);
    	if ((p==null) || !(p instanceof Boolean)) return false;
    	else return ((Boolean)p).booleanValue();
    }
    public void addBondsConnected2Atoms(IAtomContainer mol,List<IAtom> atoms, List<IBond> bonds) {
        for (int i=0; i < atoms.size(); i++) {
            IAtom atom = atoms.get(i);
            List<IBond> b = mol.getConnectedBondsList(atom);
            for (int j=0; j < b.size(); j++) {
                IBond bond = b.get(j);
                IAtom otherAtom = bond.getConnectedAtom(atom);
                if (atoms.contains(otherAtom) && !bonds.contains(bond)) {
                    bonds.add(bond);
                }
            }
        }
    }    
}
