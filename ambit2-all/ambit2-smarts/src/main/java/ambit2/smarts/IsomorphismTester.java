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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;


/**
 * 
 * @author Nikolay Kochev nick@uni-plovdiv.bg
 */
public class IsomorphismTester 
{
	IQueryAtomContainer query;
	IAtomContainer target;
	boolean isomorphismFound;
	boolean FlagStoreIsomorphismNode = false;
	List<Node> isomorphismNodes = new ArrayList<Node>(); 
	Stack<Node> stack = new Stack<Node>();
	List<IAtom> targetAt = new ArrayList<IAtom>(); //a work container
	List<QuerySequenceElement> sequence = new ArrayList<QuerySequenceElement>();
	List<IQueryAtom> sequencedAtoms = new ArrayList<IQueryAtom>();
	List<IQueryAtom> sequencedBondAt1 = new ArrayList<IQueryAtom>();
	List<IQueryAtom> sequencedBondAt2 = new ArrayList<IQueryAtom>();
	
	
	public void setQuery(IQueryAtomContainer container)
	{
		query = container;
		TopLayer.setAtomTopLayers(query, TopLayer.TLProp);
		setQueryAtomSequence(null);
	}
	
	public List<QuerySequenceElement> getSequence()
	{
		return(sequence);
	}
	
	public void setSequence(IQueryAtomContainer queryContainer, List<QuerySequenceElement> externalSequence) 
	{
		query = queryContainer;
		sequence = externalSequence;
	}
	
	public List<QuerySequenceElement> transferSequenceToOwner()
	{
		List<QuerySequenceElement> ownerSeq = sequence;
		sequence = new ArrayList<QuerySequenceElement>();
		return(ownerSeq);
	}
	
	void setQueryAtomSequence(IQueryAtom firstAt)
	{	
		IQueryAtom firstAtom;
		QuerySequenceElement seqEl;
		TopLayer topLayer;
		//List<IQueryAtom> curAddedAtoms = new List<IQueryAtom>();  
		int n;
		
		if (firstAt == null)
			firstAtom = (IQueryAtom)query.getFirstAtom();
		else
			firstAtom = firstAt;
		sequence.clear();
		sequencedAtoms.clear();
		sequencedBondAt1.clear();
		sequencedBondAt2.clear();
		
		//Set first sequence atom
		sequencedAtoms.add(firstAtom);		
		seqEl = new QuerySequenceElement();
		seqEl.center = firstAtom;
		topLayer = (TopLayer)firstAtom.getProperty(TopLayer.TLProp);
		n = topLayer.atoms.size();
		seqEl.atoms = new IQueryAtom[n];
		seqEl.bonds = new IQueryBond[n];
		for (int i = 0; i < n; i++)
		{
			sequencedAtoms.add((IQueryAtom)topLayer.atoms.get(i));
			seqEl.atoms[i] = (IQueryAtom)topLayer.atoms.get(i);
			seqEl.bonds[i] = (IQueryBond)topLayer.bonds.get(i);
			addSeqBond(seqEl.center,seqEl.atoms[i]);
		}
		sequence.add(seqEl);
		
		//Sequencing the entire query structure
		Stack<QuerySequenceElement> stack = new Stack<QuerySequenceElement>();
		stack.push(seqEl);
		while (!stack.empty())
		{
			//curAddedAtoms.clear();
			QuerySequenceElement curSeqAt = stack.pop();
			for (int i = 0; i < curSeqAt.atoms.length; i++)
			{
				topLayer = (TopLayer)curSeqAt.atoms[i].getProperty(TopLayer.TLProp);
				if (topLayer.atoms.size() == 1)
					continue; // it is terminal atom and no further sequencing should be done
				int a[] = getSeqAtomsInLayer(topLayer);
				
				n = 0;
				for (int k = 0; k<a.length; k++)
					if (a[k] == 0)
						n++;
				
				if (n > 0)
				{	
					seqEl = new QuerySequenceElement();
					seqEl.center = curSeqAt.atoms[i];
					seqEl.atoms = new IQueryAtom[n];
					seqEl.bonds = new IQueryBond[n];
					sequence.add(seqEl);
					stack.add(seqEl);
				}	
				
				int j = 0;				
				for (int k = 0; k < a.length; k++)
				{
					if (a[k] == 0)
					{	
						seqEl.atoms[j] = (IQueryAtom)topLayer.atoms.get(k);
						seqEl.bonds[j] = (IQueryBond)topLayer.bonds.get(k);
						addSeqBond(seqEl.center,seqEl.atoms[j]);
						sequencedAtoms.add(seqEl.atoms[j]);
						//curAddedAtoms.add(seqEl.atoms[j]);
						j++;
					}
					else
					{	
						if (curSeqAt.center == topLayer.atoms.get(k))
							continue;
						//Check whether  bond(curSeqAt.atoms[i]-topLayer.atoms.get(k))
						//is already sequenced
						if (getSeqBond(curSeqAt.atoms[i],topLayer.atoms.get(k)) != -1)
							continue;						
						//topLayer.atoms.get(k) atom is already sequenced.
						//Therefore sequnce element of 'bond' type is registered.						
						//newSeqEl is not added in the stack (this is not needed for this bond)
						QuerySequenceElement newSeqEl = new QuerySequenceElement();						
						newSeqEl.center = null;
						newSeqEl.atoms = new IQueryAtom[2];
						newSeqEl.bonds = new IQueryBond[1];
						newSeqEl.atoms[0] = curSeqAt.atoms[i];
						newSeqEl.atoms[1] = (IQueryAtom)topLayer.atoms.get(k);
						addSeqBond(newSeqEl.atoms[0],newSeqEl.atoms[1]);
						newSeqEl.bonds[0] = (IQueryBond)topLayer.bonds.get(k);
						sequence.add(newSeqEl);						
					}
				}
			}			
		}
		
		for(int i = 0; i < sequence.size(); i++)
			sequence.get(i).setAtomNums(query);
	}
		
	boolean containsAtom(List<IQueryAtom> v, IQueryAtom atom)
	{
		for(int i = 0; i < v.size(); i++)
			if (v.get(i) == atom)
				return(true);
		return(false);
	}
	
	boolean containsAtom(IAtom[] a, IAtom atom)
	{
		for(int i = 0; i < a.length; i++)
			if (a[i] == atom)
				return(true);
		return(false);
	}
	
	int[] getSeqAtomsInLayer(TopLayer topLayer)
	{
		int a[] = new int[topLayer.atoms.size()];
		for (int i = 0; i <topLayer.atoms.size(); i++)
		{	
			if (containsAtom(sequencedAtoms,(IQueryAtom)topLayer.atoms.get(i)))
			{	
				a[i] = 1;
			}	
			else
				a[i] = 0;
		}	
		return(a);
	}
	
	void addSeqBond(IQueryAtom at1, IQueryAtom at2)
	{
		sequencedBondAt1.add(at1);
		sequencedBondAt2.add(at2);
	}
	
	int getSeqBond(IAtom at1, IAtom at2)
	{
		for (int i = 0; i < sequencedBondAt1.size(); i++)
		{
			if (sequencedBondAt1.get(i)==at1)
			{
				if (sequencedBondAt2.get(i)==at2)
					return(i);
			}
			else
				if (sequencedBondAt1.get(i)==at2)
				{
					if (sequencedBondAt2.get(i)==at1)
						return(i);
				}
		}
		return(-1);		
	}
	
	public boolean hasIsomorphism(IAtomContainer container)
	{	
		target = container;	
		FlagStoreIsomorphismNode = false;
		isomorphismNodes.clear();
		
		if (query.getAtomCount() == 1)
			return(singleAtomIsomorphism());
		
		TopLayer.setAtomTopLayers(target, TopLayer.TLProp);
		executeSequence(true);
		return(isomorphismFound);
	}
	
	public List<Integer> getIsomorphismPositions(IAtomContainer container)
	{	
		target = container;	
		FlagStoreIsomorphismNode = false;
		isomorphismNodes.clear();
		
		List<Integer> v = new ArrayList<Integer>(); 
		if (query.getAtomCount() == 1)
		{	
			SMARTSAtom qa = (SMARTSAtom)query.getAtom(0);			
			for (int i = 0; i < target.getAtomCount(); i++)
			{	
				if (qa.matches(target.getAtom(i)))
					v.add(new Integer(i));
			}	
			return(v);
		}	
				
		TopLayer.setAtomTopLayers(target, TopLayer.TLProp);
		for (int i = 0; i < target.getAtomCount(); i++)
		{	
			executeSequenceAtPos(i);
			if (isomorphismFound)
				v.add(new Integer(i));
		}
		return(v);
	}
	
	public boolean checkIsomorphismAtPosition(IAtomContainer container, int atomNum)
	{
		if ((atomNum < 0) || (atomNum >= container.getAtomCount()))
			return false;
		
		target = container;	
		FlagStoreIsomorphismNode = false;
		isomorphismNodes.clear();
		
		if (query.getAtomCount() == 1)
		{
			SMARTSAtom qa = (SMARTSAtom)query.getAtom(0);
			return qa.matches(target.getAtom(atomNum));
		}
			
			
		TopLayer.setAtomTopLayers(target, TopLayer.TLProp);
		
		executeSequenceAtPos(atomNum);
			if (isomorphismFound)
				return true;
			
		return false;
	}
	
	/**
	 * This function returns null if no isomorphism is found
	 * @param container
	 * @return
	 */
	public List<IAtom> getIsomorphismMapping(IAtomContainer container)
	{
		if (query == null) return null;
		target = container;	
		FlagStoreIsomorphismNode = true;
		isomorphismNodes.clear();
		
		if (query.getAtomCount() == 1)
		{	
			SMARTSAtom qa = (SMARTSAtom)query.getAtom(0);			
			for (int i = 0; i < target.getAtomCount(); i++)
			{	
				if (qa.matches(target.getAtom(i)))
				{	
					List<IAtom> v = new ArrayList<IAtom>();
					v.add(target.getAtom(i));
					return(v);
				}	
			}
			return null;
		}	
				
		
		TopLayer.setAtomTopLayers(target, TopLayer.TLProp);
		executeSequence(true);
		
		if (isomorphismFound)
		{	
			//Getting the data from the Node
			Node node = isomorphismNodes.get(0);
			List<IAtom> v = new ArrayList<IAtom>();
			for (int i = 0; i < node.atoms.length; i++)
				v.add(node.atoms[i]);
			
			return (v);
		}
		else
			return (null);
	}
	
	public List<IBond> generateBondMapping(IAtomContainer container, List<IAtom> atomMapping)
	{
		if (query == null) 
			return null;
		
		List<IBond> v  = new ArrayList<IBond>();
		for (int i = 0; i < query.getBondCount(); i++)
		{
			IAtom qa0 = query.getBond(i).getAtom(0);
			IAtom qa1 = query.getBond(i).getAtom(1);
			IAtom a0 = atomMapping.get(query.getAtomNumber(qa0));
			IAtom a1 = atomMapping.get(query.getAtomNumber(qa1));
			
			v.add(container.getBond(a0,a1));
		}
		
		return (v);
	}
	
	
	/**
	 * If no isomorphism is found the result is empty vector
	 * @param container
	 * @return
	 */
	public List<List<IAtom>> getAllIsomorphismMappings(IAtomContainer container)
	{
		if (query == null) return null;
		target = container;	
		FlagStoreIsomorphismNode = true;
		isomorphismNodes.clear();
		List<List<IAtom>> result = new ArrayList<List<IAtom>>(); 
		
		
		if (query.getAtomCount() == 1)
		{	
			SMARTSAtom qa = (SMARTSAtom)query.getAtom(0);			
			for (int i = 0; i < target.getAtomCount(); i++)
			{	
				if (qa.matches(target.getAtom(i)))
				{	
					List<IAtom> v = new ArrayList<IAtom>();
					v.add(target.getAtom(i));
					result.add(v);
				}	
			}
			return result;
		}	
				
		
		TopLayer.setAtomTopLayers(target, TopLayer.TLProp);
		executeSequence(false);
		
		if (isomorphismFound)
		{	
			//Getting the data from the all stored Nodes
			for (int k = 0; k < isomorphismNodes.size(); k++)
			{	
				Node node = isomorphismNodes.get(k);
				List<IAtom> v = new ArrayList<IAtom>();
				for (int i = 0; i < node.atoms.length; i++)
					v.add(node.atoms[i]);
				result.add(v);
			}
		}
		return result;		
	}
	
	public List<List<IAtom>> getNonIdenticalMappings(IAtomContainer container)
	{
		List<List<IAtom>> result = new ArrayList<List<IAtom>>();
		List<List<IAtom>> allMaps = getAllIsomorphismMappings(container);
		if (allMaps.isEmpty())
			return(result);
		
		int nMaps = allMaps.size();
		int nAtoms = allMaps.get(0).size();
		List<IAtom> map;
		List<IAtom> map1;
		boolean FlagOK;
		boolean FlagOneFifferent;
		
		for (int i = 0; i < nMaps; i++)
		{
			map = allMaps.get(i);
			FlagOK = true;
			for (int j = 0; j < result.size(); j++)
			{
				//comparing map against j-th element of result 
				map1 = result.get(j);
				FlagOneFifferent = false;
				for (int k = 0; k < nAtoms; k++)
				{
					//map1 MUST NOT conatin at least one atom from map
					if (!map1.contains(map.get(k)))
					{
						FlagOneFifferent = true;
						break;
					}
				}
				
				if (!FlagOneFifferent)
				{	
					FlagOK = false;
					break;
				}
			}
			
			if (FlagOK)
				result.add(map);
		}
		
		return(result);
	}
	
	
	public List<List<IAtom>> getNonOverlappingMappings(IAtomContainer container)
	{
		List<List<IAtom>> result = new ArrayList<List<IAtom>>();
		List<List<IAtom>> allMaps = getAllIsomorphismMappings(container);
		if (allMaps.isEmpty())
			return(result);
		
		int nMaps = allMaps.size();
		int nAtoms = allMaps.get(0).size();
		List<IAtom> map;
		List<IAtom> map1;
		boolean FlagOK;
		
		for (int i = 0; i < nMaps; i++)
		{
			map = allMaps.get(i);
			FlagOK = true;
			for (int j = 0; j < result.size(); j++)
			{
				//comparing map against j-th element of result 
				map1 = result.get(j);				
				for (int k = 0; k < nAtoms; k++)
				{
					//map1 must not have any intersection
					if (map1.contains(map.get(k)))
					{
						FlagOK = false;
						break;
					}
				}
				
				if (!FlagOK)
					break;
			}
			
			if (FlagOK)
				result.add(map);
		}
		
		return(result);
	}

	
	/*
	 * returns a set of groups (arrays with indexes) of overlapped mappings
	 */
	public List<List<Integer>> getOverlappedMappingClusters(List<List<IAtom>> maps)
	{
		List<List<Integer>>  v = new ArrayList<List<Integer>>();
		if (maps.size() == 0)
			return (v);
		
		//The first cluster is created
		List<Integer> vInt;
		vInt = new ArrayList<Integer>();
		vInt.add(new Integer(0));
		v.add(vInt);
		
		
		if (maps.size() == 1)	
			return(v);
			
		//The case with more than 1  mapping
		for (int i = 1; i < maps.size(); i++)
		{
			List<IAtom> map = maps.get(i);
			
			boolean FlagOverlap = false;
			for (int k = 0; k < v.size(); k++)
			{
				if (overlapsWithCluster(map,v.get(k), maps))
				{
					v.get(k).add(new Integer(i));
					FlagOverlap = true;
					break;
				}
			}
			
			if (!FlagOverlap)
			{
				//New cluster is created
				vInt = new ArrayList<Integer>();
				vInt.add(new Integer(i));
				v.add(vInt);
			}
		}
		
		return v;
	}
	
	boolean overlapsWithCluster(List<IAtom> map, List<Integer> cluster, List<List<IAtom>> maps)
	{
		for (int i = 0; i < cluster.size(); i++)
		{
			int mapNum = cluster.get(i).intValue();
			List<IAtom> clMap = maps.get(mapNum);
			for (int k = 0; k < map.size(); k++)
				if (clMap.contains(map.get(k)))
					return true;
		}
		
		return false;
	}
	
	
	boolean singleAtomIsomorphism()
	{	
		SMARTSAtom qa = (SMARTSAtom)query.getAtom(0);
		isomorphismFound = false;
		for (int i = 0; i < target.getAtomCount(); i++)
		{	
			if (qa.matches(target.getAtom(i)))
			{	
				isomorphismFound = true;
				break;
			}
		}	
		return(isomorphismFound);
	}
	
	
	void executeSequence(boolean stopAtFirstMapping)
	{	
		isomorphismFound = false;
		stack.clear();
				
		//Initial nodes
		QuerySequenceElement el = sequence.get(0);		
		for(int k = 0; k < target.getAtomCount(); k++)
		{
			IAtom at = target.getAtom(k);			
			if(el.center.matches(at))
			{	
				Node node = new Node();
				node.sequenceElNum = 0; 
				node.nullifyAtoms(query.getAtomCount());
				node.atoms[el.centerNum] = at;
				stack.push(node);
			}	
		}
		
		//Expanding the tree of all possible mappings
		if (stopAtFirstMapping)
		{	 
			while (!stack.isEmpty())
			{
				expandNode(stack.pop());
				if (isomorphismFound)
					break;
			}
		}
		else
		{	 
			while (!stack.isEmpty())
				expandNode(stack.pop());
		}
		
	}
	
	void executeSequenceAtPos(int pos)
	{	
		isomorphismFound = false;
		stack.clear();
				
		//Initial node
		QuerySequenceElement el = sequence.get(0);
		IAtom at = target.getAtom(pos);			
		if(el.center.matches(at))
		{	
			Node node = new Node();
			node.sequenceElNum = 0; 
			node.nullifyAtoms(query.getAtomCount());
			node.atoms[el.centerNum] = at;
			stack.push(node);
		}	
		
		//Expanding the tree of all possible mappings 
		while (!stack.isEmpty())
		{
			expandNode(stack.pop());
			if (isomorphismFound)
				break;
		}
	}
	
	void expandNode(Node node)
	{	
		//System.out.println(node.toString(target));		
		QuerySequenceElement el = sequence.get(node.sequenceElNum);
		
		if (el.center == null) //This node describers a bond that closes a ring
		{
			//Checking whether this bond is present in the target
			IAtom tAt0 = node.atoms[query.getAtomNumber(el.atoms[0])]; 
			IAtom tAt1 = node.atoms[query.getAtomNumber(el.atoms[1])];
			IBond tBo = target.getBond(tAt0,tAt1);
			if (tBo != null)
				if (el.bonds[0].matches(tBo))
				{
					node.sequenceElNum++;
					//stack.push(node); 
					if (node.sequenceElNum == sequence.size())
					{	
						//The node is not added in the stack if the end of the sequence is reached
						isomorphismFound = true;
						if (FlagStoreIsomorphismNode)
							isomorphismNodes.add(node);
					}
					else
						stack.push(node); 
				}	
		}
		else
		{
			targetAt.clear();
			IAtom tAt = node.atoms[el.centerNum];
			List<IAtom> conAt = target.getConnectedAtomsList(tAt);
			for (int i = 0; i < conAt.size(); i++)
			{
				if (!containsAtom(node.atoms,conAt.get(i)))
					targetAt.add(conAt.get(i));
			}
			
			if (el.atoms.length <= targetAt.size())			
				generateNodes(node);
		}
	}
	
	
	void generateNodes(Node node)
	{
		QuerySequenceElement el = sequence.get(node.sequenceElNum);
		
		if (el.atoms.length == 1)
		{
			for(int i = 0; i < targetAt.size(); i++)
			{
				if (el.atoms[0].matches(targetAt.get(i)))
				if(	matchBond(node, el, 0, targetAt.get(i)))
				{
					Node newNode = node.cloneNode();
					newNode.atoms[el.atomNums[0]] = targetAt.get(i);
					newNode.sequenceElNum = node.sequenceElNum+1;
					//stack.push(newNode);
					if (newNode.sequenceElNum == sequence.size())
					{	
						//The node is not added in the stack if the end of the sequence is reached
						isomorphismFound = true;
						if (FlagStoreIsomorphismNode)
							isomorphismNodes.add(newNode);
					}
					else
						stack.push(newNode);
				}
			}
			return;
		}
		
		if (el.atoms.length == 2)
		{
			for(int i = 0; i < targetAt.size(); i++)			
				if (el.atoms[0].matches(targetAt.get(i)))	
				if(	matchBond(node, el, 0, targetAt.get(i)))
					for(int j = 0; j < targetAt.size(); j++)						
						if (i != j)
							if (el.atoms[1].matches(targetAt.get(j)))
							if(	matchBond(node, el, 1, targetAt.get(j)))	
							{
								Node newNode = node.cloneNode();
								newNode.atoms[el.atomNums[0]] = targetAt.get(i);
								newNode.atoms[el.atomNums[1]] = targetAt.get(j);
								newNode.sequenceElNum = node.sequenceElNum+1;
								//stack.push(newNode);
								if (newNode.sequenceElNum == sequence.size())
								{	
									//The node is not added in the stack if the end of the sequence is reached
									isomorphismFound = true;
									if (FlagStoreIsomorphismNode)
										isomorphismNodes.add(newNode);
								}
								else
									stack.push(newNode);
							}					
			return;
		}
		
		if (el.atoms.length == 3)
		{
			for(int i = 0; i < targetAt.size(); i++)			
				if (el.atoms[0].matches(targetAt.get(i)))
				if(	matchBond(node, el, 0, targetAt.get(i)))	
					for(int j = 0; j < targetAt.size(); j++)						
						if (i != j)
							if (el.atoms[1].matches(targetAt.get(j)))
								if(	matchBond(node, el, 1, targetAt.get(j)))	
								for(int k = 0; k < targetAt.size(); k++)
									if ((k != i) && (k != j))
										if (el.atoms[2].matches(targetAt.get(k)))
										if(	matchBond(node, el, 2, targetAt.get(k)))	
										{
											Node newNode = node.cloneNode();
											newNode.atoms[el.atomNums[0]] = targetAt.get(i);
											newNode.atoms[el.atomNums[1]] = targetAt.get(j);
											newNode.atoms[el.atomNums[2]] = targetAt.get(k);
											newNode.sequenceElNum = node.sequenceElNum+1;
											//stack.push(newNode);
											if (newNode.sequenceElNum == sequence.size())
											{	
												//The node is not added in the stack if the end of the sequence is reached
												isomorphismFound = true;
												if (FlagStoreIsomorphismNode)
													isomorphismNodes.add(newNode);
											}	
											else
												stack.push(newNode);
										}
			return;
		}
		
		//This case should be very rare (el.atoms.length >= 4)
				
		//a stack which is used for obtaining all
		//possible mappings between el.atoms and targetAt
		//The stack element is an array t[], where t[k] means that 
		//el.atoms[k] is mapped against atom targetAt(t[k])
		//t[t.length-1] is used as a work variable which describes how many 
		//elements of the t array are mapped
		Stack<int[]> st = new Stack<int[]>();
				
		//System.out.println("el.atoms.length = " + el.atoms.length );
		
		//Stack initialization
		for(int i = 0; i < targetAt.size(); i++)
		{
			if (el.atoms[0].matches(targetAt.get(i)))
			if(	matchBond(node, el, 0, targetAt.get(i)))	
			{
				int t[] = new int[el.atoms.length+1];
				t[t.length-1] = 1;
				t[0] = i;				
				st.push(t);
			}
		}
		
		while (!st.isEmpty())
		{
			int t[] = st.pop();
			int n = t[t.length-1];
			
			if (n == t.length-1)  //This condition means all atoms are matched
			{
				//new node 
				Node newNode = node.cloneNode();
				for(int k = 0; k < t.length-1; k++)
					newNode.atoms[el.atomNums[k]] = targetAt.get(t[k]);				
				newNode.sequenceElNum = node.sequenceElNum+1;
				//stack.push(newNode);
				if (newNode.sequenceElNum == sequence.size())
				{	
					//The node is not added in the stack if the end of the sequence is reached
					isomorphismFound = true;
					if (FlagStoreIsomorphismNode)
						isomorphismNodes.add(newNode);
				}
				else
					stack.push(newNode);
				continue;
			}
			
			for(int i = 0; i < targetAt.size(); i++)
			{
				//Check whether i is among first elements of t
				boolean Flag = true;
				for (int k = 0; k < n; k++)
					if ( i == t[k]) 
					{
						Flag = false;
						break;
					}
				
				if (Flag)
					if (el.atoms[n].matches(targetAt.get(i)))
					if(	matchBond(node, el, n, targetAt.get(i)))	
					{	
						//new stack element
						int tnew[] = new int[el.atoms.length+1];
						for(int k = 0; k < n; k++)
							tnew[k] = t[k];
						tnew[n] = i;
						tnew[t.length-1] = n+1;
						st.push(tnew);
					}
			}
		}
		
	}
	
	boolean matchBond(Node node, QuerySequenceElement el, int qAtNum, IAtom taAt)
	{
		IBond taBo = target.getBond(taAt, node.atoms[el.centerNum]);
		return(el.bonds[qAtNum].matches(taBo));
	}
	
	//public Vector getAllIsomorphisms(IAtomContainer container)
	//{
	//	Vector res = new Vector();
	//	return(res);
	//}
	
	public void printDebugInfo()
	{
		System.out.println("Query Atoms Topological Layers");
		for (int i = 0; i < query.getAtomCount(); i++)						
			System.out.println(""+i+"  "+
					query.getAtom(i).getProperty(TopLayer.TLProp).toString());
		
		System.out.println();
		System.out.println("Query Sequence");
		for (int i = 0; i < sequence.size(); i++)
			System.out.println(sequence.get(i).toString(query));
	}
}
