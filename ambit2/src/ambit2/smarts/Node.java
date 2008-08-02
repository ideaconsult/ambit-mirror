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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * 
 * @author Nikolay Kochev nick@uni-plovdiv.bg
 */
public class Node 
{
	//The Node represent a partial information in the
	//isomorphism searching tree
	//array element atoms[k] contains the target atom which is mapped to the k-th query atom
	
	//Node parent = null;
	//IAtom center;
	int sequenceElNum;	
	IAtom atoms[]; 
		
	public Node()
	{	
	}
		
	public void nullifyAtoms(int n)
	{
		atoms = new IAtom[n];
		for (int i=0; i < n; i++)
			atoms[i] = null;
	}
	
	public void copyAtoms(Node node)
	{
		atoms = new IAtom[node.atoms.length];
		for (int i=0; i < atoms.length; i++)
			atoms[i] = node.atoms[i];
	}
	
	public Node cloneNode()
	{
		Node node = new Node();
		node.copyAtoms(this);
		return(node);
	}
	
	public String toString(IAtomContainer container)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Node: el_num=" + sequenceElNum + " at:");
		for (int i = 0; i<atoms.length; i++)
			if (atoms[i] != null)			
				sb.append(" "+i+"->"+container.getAtomNumber(atoms[i]));
				
		return(sb.toString());
	}
}



//This is the older description of the class Node
//
//The node describes a correspondence between
//a sequence element and a target atom
//Correspondence is of the type:
//	sequnceEl.center    <-->  center
//  sequnceEl.atoms[k]  <-->  atoms[k]
//The isomorphism between the query and target can be obtained
//by a sequence of nodes.