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

import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import java.util.Vector;
import java.util.HashMap;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.CDKConstants;

public class SmartsHelper 
{
	int curIndex;
	HashMap<IAtom,TopLayer> firstSphere = new HashMap<IAtom,TopLayer>();	
	//Work container - list with the processed atom nodes
	HashMap<IAtom,AtomSmartsNode> nodes = new HashMap<IAtom,AtomSmartsNode>();
	int nAtom;
	int nBond;
	
	static public String getAtomsString(QueryAtomContainer query)
	{
		StringBuffer sb = new StringBuffer();	
		
		for (int i = 0; i < query.getAtomCount(); i++)
		{	
			sb.append(atomToString((SMARTSAtom)query.getAtom(i)) + " ");			
		}	
		return(sb.toString());
	}
	
		
	static public String getAtomExpressionTokens(SmartsAtomExpression expression)
	{
		StringBuffer sb = new StringBuffer();	
		
		for (int k = 0; k < expression.tokens.size(); k++)
		{
			sb.append("tok("+expression.tokens.get(k).type+","+expression.tokens.get(k).param+") ");
		}
		return(sb.toString());
	}
	
	static public String getAtomsString(IAtomContainer container)
	{
		StringBuffer sb = new StringBuffer();	
		
		for (int i = 0; i < container.getAtomCount(); i++)
			sb.append(container.getAtom(i).getSymbol() + " ");
		return(sb.toString());
	}
	
	static public String getAtomsAttributes(IAtomContainer container)
	{
		StringBuffer sb = new StringBuffer();	
		
		for (int i = 0; i < container.getAtomCount(); i++)
		{	
			IAtom at = container.getAtom(i); 
			sb.append(at.getSymbol());
			Integer explHInt = (Integer)at.getProperty("ExplicitH");
			int explHAt = 0;
			if (explHInt != null)
				explHAt = explHInt.intValue();
			sb.append(" NumH=" + (at.getHydrogenCount() + explHAt));
			if (at.getFlag(CDKConstants.ISAROMATIC)) 
				sb.append(" aromatic");
			sb.append("\n");
		}	
		return(sb.toString());
	}
	
	static public String getBondsString(IAtomContainer query)
	{
		StringBuffer sb = new StringBuffer();	
		
		for (int i = 0; i < query.getBondCount(); i++)
		{	
			sb.append(query.getBond(i).getOrder() + " ");			
		}	
		return(sb.toString());
	}

	static public String atomToString(IAtom a)
	{
		if (a instanceof SmartsAtomExpression)
			return(a.toString());		
		if (a instanceof AliphaticSymbolQueryAtom)
			return(a.getSymbol());
		
		return(a.getSymbol());		
	}
	
	static public String bondAtomNumbersToString(IAtomContainer container, IBond b)
	{
		return(" "+ container.getAtomNumber(b.getAtom(0))+ " "+container.getAtomNumber(b.getAtom(1)));				
	}

	
	
	void determineFirstSheres(QueryAtomContainer query)
	{
		firstSphere.clear();
		nAtom =  query.getAtomCount();
		nBond =  query.getBondCount();
		
		for (int i = 0; i < nAtom; i++)
		{				
			firstSphere.put(query.getAtom(i), new TopLayer());
		}	
			
		for (int i = 0; i < nBond; i++)
		{
			IBond bond = query.getBond(i);
			IAtom at0 = bond.getAtom(0);
			IAtom at1 = bond.getAtom(1);
			firstSphere.get(at0).atoms.add(at1);
			firstSphere.get(at0).bonds.add(bond);
			firstSphere.get(at1).atoms.add(at0);
			firstSphere.get(at1).bonds.add(bond);			
		}
	}
	
	public String toSmarts(QueryAtomContainer query)
	{
		determineFirstSheres(query);
		nodes.clear();
		curIndex = 1;
		AtomSmartsNode node = new AtomSmartsNode();
		node.parent = null;
		node.atom = query.getAtom(0);
		nodes.put(node.atom, node);
		return(nodeToString(node.atom));
	}
	
	String nodeToString(IAtom atom)
	{
		StringBuffer sb = new StringBuffer();
		TopLayer afs = firstSphere.get(atom);
		AtomSmartsNode curNode = nodes.get(atom);
		Vector<String> branches = new Vector<String>();
		for (int i=0; i<afs.atoms.size(); i++)
		{
			IAtom neighborAt = afs.atoms.get(i);
			if (neighborAt == curNode.parent)
				continue;
			
			AtomSmartsNode neighborNode = nodes.get(neighborAt);
			if (neighborNode == null) // This node has not been registered yet
			{
				//Registering a new Node and a new branch
				AtomSmartsNode newNode = new AtomSmartsNode();
				newNode.atom = neighborAt;
				newNode.parent = atom;
				nodes.put(newNode.atom, newNode);
				
				//Add Bond 
				//...
				String newBranch = nodeToString(neighborAt);
				branches.add(newBranch);
			}
			else
			{
				//Handle ring closure
			}
		}
		//Add atom from the current node
		sb.append(atomToString((SMARTSAtom) atom));
		
		//Add indexes
		//...
		
		//Add branches
		if (branches.size() == 0)
			return(sb.toString());
		
		for(int i = 0; i < branches.size()-1; i++)
			sb.append("("+branches.get(i).toString()+")");
		sb.append(branches.lastElement().toString());
		return(sb.toString());
	}
	
	static void printIntArray(int c[])
	{
		if (c == null)
		{	
			System.out.println("null");
			return;
		}	
		for (int i = 0; i < c.length; i++)			
			System.out.print(c[i]+" ");
		System.out.println();
	}
	
}
