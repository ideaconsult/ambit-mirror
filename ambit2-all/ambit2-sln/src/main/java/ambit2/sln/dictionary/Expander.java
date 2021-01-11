package ambit2.sln.dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

import ambit2.sln.SLNAtom;
import ambit2.sln.SLNContainer;
import ambit2.smarts.TopLayer;

public class Expander 
{
	class ExpAtomNode
	{
		public IAtom parent;
		public IAtom atom;
	}
	
	SLNContainer container = null;
	SLNContainer expContainer = null;
	Map<IAtom,IAtom> oldToNewAtoms = new HashMap<IAtom,IAtom>();
		
	HashMap<IAtom,ExpAtomNode> nodes = new HashMap<IAtom,ExpAtomNode>();
	HashMap<IAtom,TopLayer> firstSphere = new HashMap<IAtom,TopLayer>();
	List<IBond> ringClosures = new ArrayList<IBond>();
	
	//boolean FlagClearMarkushInfo = true;
	int markushTokensNumber[] = null;
	int markushPos[] = null;
		
	public List<SLNContainer> generateMarkushCombinatorialList (SLNContainer container)
	{
		this.container = container;
		determineFirstSheres(container);
		List<SLNAtom> maList = getMarkushAtoms();
		fillMarkushAtomsInfo(maList);
		
		
		//TODO generate all combinations of markushPos combinations ...
		
		return null;
	}
	
	SLNContainer generateExpandedSLNContainer (SLNContainer container)
	{
		this.container = container;
		determineFirstSheres(container);
		clearMarkushAtomsInfo();
		
		return getExpandedSLNContainer();
	}
	
	public SLNContainer getExpandedSLNContainer() 
	{
		//All dictionary objects SLNContainers atoms and bonds are added
		//and linked to the atoms of the container
		
		expContainer = new SLNContainer(container.getBuilder());
		oldToNewAtoms.clear();
		nodes.clear();
		ringClosures.clear();
		
		ExpAtomNode node = new ExpAtomNode();
		node.parent = null;
		node.atom = (SLNAtom)container.getAtom(0);
		nodes.put(node.atom, node);
		
		expand((SLNAtom)node.atom, null);
		

		return expContainer;
	}
	
	public void expand(IAtom atom, IAtom connectAt) 
	{		
		SLNAtom at = (SLNAtom)atom;
		List<IAtom> expandAtoms = null;
		if (at.dictObj == null)
		{
			SLNAtom newAt = at.clone();
			oldToNewAtoms.put(at, newAt);
			expContainer.addAtom(newAt);
		}
		else
			expandAtoms = expandDictionaryAtom (at);
		
		
		TopLayer afs = firstSphere.get(atom);
		ExpAtomNode curNode = nodes.get(atom);
		
		for (int i=0; i<afs.atoms.size(); i++)
		{
			IAtom neighborAt = afs.atoms.get(i);
			if (neighborAt == curNode.parent)
				continue;
			
			ExpAtomNode neighborNode = nodes.get(neighborAt);
			if (neighborNode == null) // This node has not been registered yet
			{
				//Registering a new Node (a new branch)
				ExpAtomNode newNode = new ExpAtomNode();
				newNode.atom = neighborAt;
				newNode.parent = atom;
				nodes.put(newNode.atom, newNode); 
				
				if (at.dictObj == null)
					expand(neighborAt, at);
				else
				{
					int vPos = 0;
					int valences[] = at.dictObj.getValences();
					if (valences == null)
					{
						vPos = i;
						if (vPos>= expandAtoms.size())
							vPos= expandAtoms.size()-1;
					}
					else
						vPos = valences[i];
					
					//TODO link using vPos
					
				}	
				
			}
			else
			{	
				IBond neighborBo = afs.bonds.get(i);
				//This check is needed. Otherwise the ring closure will be described twice (for both atoms of the bond)
				if (ringClosures.contains(neighborBo))  
					continue;
				
				ringClosures.add(neighborBo);
				
				//Handle a new ring closure
				
				//afs.bonds.get(i)
				
			}
		}
	}
	
	public List<IAtom> expandDictionaryAtom (SLNAtom at) 
	{
		//Handle a dictionary object
		if (at.dictObj instanceof AtomDictionaryObject)
		{
			AtomDictionaryObject aDO = (AtomDictionaryObject)at.dictObj;
			SLNAtom newAt = aDO.atom.clone();
			oldToNewAtoms.put(at, newAt);
			expContainer.addAtom(newAt);
		}
		else if (at.dictObj instanceof MacroAtomDictionaryObject)
		{
			expandMacroAtomDictionaryObject(at, (MacroAtomDictionaryObject) at.dictObj);
		}
		//TODO handle other type of dictionary objects
		
		return null;
	}
	
	public void expandMacroAtomDictionaryObject(SLNAtom at, MacroAtomDictionaryObject maDO)
	{
		//TODO
	}
	
	void determineFirstSheres(SLNContainer container)
	{
		firstSphere.clear();
		int nAtom =  container.getAtomCount();
		int nBond =  container.getBondCount();
		
		for (int i = 0; i < nAtom; i++)
		{				
			firstSphere.put(container.getAtom(i), new TopLayer());
		}	
			
		for (int i = 0; i < nBond; i++)
		{
			IBond bond = container.getBond(i);
			IAtom at0 = bond.getAtom(0);
			IAtom at1 = bond.getAtom(1);
			firstSphere.get(at0).atoms.add(at1);
			firstSphere.get(at0).bonds.add(bond);
			firstSphere.get(at1).atoms.add(at0);
			firstSphere.get(at1).bonds.add(bond);			
		}
	}
	
	
	public List<SLNAtom> getDictionaryAtoms() {
		List<SLNAtom> dictAtoms = new ArrayList<SLNAtom>();
		for (int i = 0; i < container.getAtomCount(); i++)
		{
			SLNAtom at = (SLNAtom)container.getAtom(i);
			if (at.dictObj != null)
				dictAtoms.add(at);
		}
		return dictAtoms;
	}
	
	public List<SLNAtom> getMarkushAtoms() {
		List<SLNAtom> dictAtoms = new ArrayList<SLNAtom>();
		for (int i = 0; i < container.getAtomCount(); i++)
		{
			SLNAtom at = (SLNAtom)container.getAtom(i);
			if (at.dictObj != null)
				if (at.dictObj instanceof MarkushAtomDictionaryObject)
					dictAtoms.add(at);
		}
		return dictAtoms;
	}
	
	void fillMarkushAtomsInfo(List<SLNAtom> maList) {
		//TODO
	}
	
	void clearMarkushAtomsInfo() {
		markushTokensNumber = null;
		markushPos = null;
	}
	
}
