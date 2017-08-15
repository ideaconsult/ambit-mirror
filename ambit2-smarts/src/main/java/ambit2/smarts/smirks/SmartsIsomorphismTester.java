package ambit2.smarts.smirks;

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

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.Node;
import ambit2.smarts.QuerySequenceElement;
import ambit2.smarts.TopLayer;

public class SmartsIsomorphismTester 
{
	protected boolean FlagCheckStereoElements = false; 
	
	protected IQueryAtomContainer query;
	protected IQueryAtomContainer target;
	protected boolean isomorphismFound;
	protected boolean FlagStoreIsomorphismNode = false;
	protected List<Node> isomorphismNodes = new ArrayList<Node>(); 
	protected Stack<Node> stack = new Stack<Node>();
	protected List<IAtom> targetAt = new ArrayList<IAtom>(); //a work container
	protected List<QuerySequenceElement> sequence = new ArrayList<QuerySequenceElement>();
	protected List<IQueryAtom> sequencedAtoms = new ArrayList<IQueryAtom>();
	protected List<IQueryAtom> sequencedBondAt1 = new ArrayList<IQueryAtom>();
	protected List<IQueryAtom> sequencedBondAt2 = new ArrayList<IQueryAtom>();
	
	public SmartsMatch smartsMatch = new SmartsMatch();
	
	
	public boolean isFlagCheckStereoElements() {
		return FlagCheckStereoElements;
	}

	public void setFlagCheckStereoElements(boolean flagCheckStereoElements) {
		FlagCheckStereoElements = flagCheckStereoElements;
	}
	
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
	
	boolean singleAtomSmartsIsomorphism()
	{	
		SMARTSAtom qa = (SMARTSAtom)query.getAtom(0);
		isomorphismFound = false;
		for (int i = 0; i < target.getAtomCount(); i++)
		{	
			if (smartsMatch.match(qa, target.getAtom(i)))
			{	
				isomorphismFound = true;
				break;
			}
		}
		return(isomorphismFound);
	}
	
	public boolean hasIsomorphism(IQueryAtomContainer container)
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
	
	public List<Integer> getIsomorphismPositions(IQueryAtomContainer container)
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
				if (smartsMatch.match(qa, target.getAtom(i)))
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
	
	public boolean checkIsomorphismAtPosition(IQueryAtomContainer container, int atomNum)
	{
		if ((atomNum < 0) || (atomNum >= container.getAtomCount()))
			return false;
		
		target = container;	
		FlagStoreIsomorphismNode = false;
		isomorphismNodes.clear();
		
		if (query.getAtomCount() == 1)
		{
			SMARTSAtom qa = (SMARTSAtom)query.getAtom(0);
			return smartsMatch.match(qa, target.getAtom(atomNum));
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
	public List<IAtom> getIsomorphismMapping(IQueryAtomContainer container)
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
				if (smartsMatch.match(qa, target.getAtom(i)))
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
	
	public List<IBond> generateBondMapping(IQueryAtomContainer container, List<IAtom> atomMapping)
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
	
	boolean singleAtomIsomorphism()
	{	
		SMARTSAtom qa = (SMARTSAtom)query.getAtom(0);
		isomorphismFound = false;
		for (int i = 0; i < target.getAtomCount(); i++)
		{	
			if (smartsMatch.match(qa, target.getAtom(i)))
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
			if(smartsMatch.match(el.center, at))      
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
		if(smartsMatch.match(el.center, at))       
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
				if (smartsMatch.match(el.bonds[0], tBo))
				{
					node.sequenceElNum++;
					//stack.push(node); 
					if (node.sequenceElNum == sequence.size())
					{	
						//The node is not added in the stack if the end of the sequence is reached
						boolean FlagOK = true;
						//if (FlagCheckStereoElements)
							//FlagOK = checkStereoMatching(node);
						
						if (FlagOK)
						{	
							isomorphismFound = true;
							if (FlagStoreIsomorphismNode)
								isomorphismNodes.add(node);
						}
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
		
	}
	
	
}
