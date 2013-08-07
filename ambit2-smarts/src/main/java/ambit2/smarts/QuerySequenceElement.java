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

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;


/**
 * 
 * @author Nikolay Kochev nick@uni-plovdiv.bg
 */
public class QuerySequenceElement 
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
	
	public void setAtomNums(IQueryAtomContainer container)
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
	
	public String toString(IQueryAtomContainer query) 
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
	
	
}
