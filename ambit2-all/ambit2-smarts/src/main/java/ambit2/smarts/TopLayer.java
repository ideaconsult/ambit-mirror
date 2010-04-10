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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * 
 * @author Nikolay Kochev nick@uni-plovdiv.bg
 */
public class TopLayer 
{
	public static final String  TLProp = "TL";
	public Vector<IAtom> atoms = new Vector<IAtom>();
	public Vector<IBond> bonds = new Vector<IBond>();
			
	static public void setAtomTopLayers(IAtomContainer container, Object propObj)
	{	
		int nAtom =  container.getAtomCount();
		int nBond =  container.getBondCount();		
		for (int i = 0; i < nAtom; i++)						
			container.getAtom(i).setProperty(propObj, new TopLayer());
			
		for (int i = 0; i < nBond; i++)
		{	
			IBond bond = container.getBond(i);
			IAtom at0 = bond.getAtom(0);
			IAtom at1 = bond.getAtom(1);
			TopLayer tl0 = (TopLayer)at0.getProperty(propObj);
			TopLayer tl1 = (TopLayer)at1.getProperty(propObj);
			tl0.atoms.add(at1);
			tl0.bonds.add(bond);
			tl1.atoms.add(at0);
			tl1.bonds.add(bond);
		}
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();		
		for(int i = 0; i < atoms.size(); i++)
			sb.append("("+SmartsHelper.atomToString(atoms.get(i)) + 
					", " + SmartsHelper.bondToString(bonds.get(i))+") ");
		return(sb.toString());
	}
}
