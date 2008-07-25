/*
Copyright (C) 2007-2008  

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

package ambit2.smarts;

import java.util.Vector;
import java.util.HashMap;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;


/**
 * 
 * @author Nikolay Kochev nick@uni-plovdiv.bg
 */
public class SequenceElement 
{
	//This class represents two types of objects: 
	//(1) an atom with a part of its first topological layer or
	//(2) a bond between two atoms which has already been sequenced  
	//For the second case center == null and atoms.lenght = 2 and bonds.lenght = 1
	IQueryAtom center;	
	IQueryAtom atoms[];	
	IQueryBond bonds[];	
	int atomNums[];
	int centerNum;
	
	public void setAtomNums(QueryAtomContainer container)
	{
		if (center != null)
			centerNum = container.getAtomNumber(center);
		else
			centerNum = -1;
		atomNums = new int[atoms.length];
		for (int i = 0; i < atoms.length; i++)
			atomNums[i] = container.getAtomNumber(atoms[i]);
	}
	
	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		if (center == null)
		{
			sb.append("Bond " + SmartsHelper.atomToString(atoms[0]) + " " 
					+ SmartsHelper.atomToString(atoms[1]) + 
					"   "+SmartsHelper.bondToString((IBond)bonds[0]));
		}
		else
		{
			sb.append("Center = " + SmartsHelper.atomToString(center) + "  atoms: ");
			for (int i = 0; i < atoms.length; i++)
				sb.append("("+SmartsHelper.atomToString(atoms[i])+","+ 
						SmartsHelper.bondToString((IBond)bonds[i]) + ") ");
		}
		return sb.toString();
	}
	
	public String toString(QueryAtomContainer query) 
	{
		StringBuffer sb = new StringBuffer();
		if (center == null)
		{
			sb.append("Bond " + SmartsHelper.atomToString(atoms[0]) + query.getAtomNumber(atoms[0])+" " 
					+ SmartsHelper.atomToString(atoms[1]) + query.getAtomNumber(atoms[1])+
					"   "+SmartsHelper.bondToString((IBond)bonds[0]));
		}
		else
		{
			sb.append("Center = " + SmartsHelper.atomToString(center) + 
						query.getAtomNumber(center) + "  atoms: ");
			for (int i = 0; i < atoms.length; i++)
				sb.append("("+SmartsHelper.atomToString(atoms[i])+query.getAtomNumber(atoms[i])+
						","+ SmartsHelper.bondToString((IBond)bonds[i]) + ") ");
		}
		return sb.toString();
	}
	
	//This function generates a Carbon skelleton from an atom sequence
	//It is used mainly for testing purposes
	public static IAtomContainer getCarbonSkelleton(Vector<SequenceElement> sequence)
	{
		AtomContainer mol = new AtomContainer();
		Vector<IAtom> v = new Vector<IAtom>();
		HashMap<IAtom,IAtom> m = new HashMap<IAtom,IAtom>();		
		SequenceElement el;
		
		//Processing first sequence element
		el = sequence.get(0);
		IAtom a0 = new Atom("C");		
		mol.addAtom(a0);
		m.put(el.center, a0);
		
		for (int k = 0; k < el.atoms.length; k++)
		{
			IAtom a = new Atom("C");
			mol.addAtom(a);
			//System.out.println("## --> " +  SmartsHelper.atomToString(el.atoms[k]));
			m.put(el.atoms[k],a);			
			addSkelletonBond(mol,m.get(el.center),a);
		}
		
		//Processing all other elements
		for (int i = 1; i < sequence.size(); i++)
		{
			el = sequence.get(i);
			if (el.center == null)
			{
				//It describes a bond
				addSkelletonBond(mol, m.get(el.atoms[0]), m.get(el.atoms[1]));
			}
			else
			{
				el = sequence.get(i);
				for (int k = 0; k < el.atoms.length; k++)
				{
					IAtom a = new Atom("C");
					mol.addAtom(a);
					m.put(el.atoms[k],a);
					addSkelletonBond(mol,m.get(el.center),a);
				}
			}
		}
		
		return(mol);
	}
	
	
	static void addSkelletonBond(IAtomContainer mol, IAtom at1, IAtom at2)
	{
		IAtom[] atoms = new IAtom[2];
		atoms[0] = at1;
		atoms[1] = at2;		
		Bond b = new Bond();
		b.setAtoms(atoms);
		b.setOrder(IBond.Order.SINGLE);
		mol.addBond(b);
	}
	
	
	
	
}
