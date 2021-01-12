package ambit2.sln.dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

import ambit2.sln.SLNAtom;
import ambit2.sln.SLNBond;
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
	Map<IAtom,Object> oldToNewAtoms = new HashMap<IAtom,Object>(); //Object could be IAtom or List<IAtom>
		
	HashMap<IAtom,ExpAtomNode> nodes = new HashMap<IAtom,ExpAtomNode>();
	HashMap<IAtom,TopLayer> firstSphere = new HashMap<IAtom,TopLayer>();
	//List<IBond> ringClosures = new ArrayList<IBond>();
	
	List<SLNAtom> markushAtoms = null;
	int markushTokensCount[] = null;
	int markushPos[] = null;
		
	public List<SLNContainer> generateMarkushCombinatorialList (SLNContainer container)
	{
		this.container = container;
		determineFirstSheres(container);
		markushAtoms = getMarkushAtoms();
		fillMarkushAtomsInfo(markushAtoms);
		
		//TODO generate all combinations of markushPos combinations ...
		
		return null;
	}
	
	public SLNContainer generateExpandedSLNContainer (SLNContainer container)
	{
		this.container = container;
		determineFirstSheres(container);
		//clearMarkushAtomsInfo();
		markushAtoms = getMarkushAtoms();
		fillMarkushAtomsInfo(markushAtoms);
				
		return getExpandedSLNContainer();
	}
	
	
	public SLNContainer getExpandedSLNContainer() 
	{
		//All dictionary objects SLNContainers atoms and bonds are added
		//and linked to the atoms of the container
		
		expContainer = new SLNContainer(container.getBuilder());
		oldToNewAtoms.clear();
		
		for (int i = 0; i < container.getAtomCount(); i++)
			handleAtom(container.getAtom(i));
		
		for (int i = 0; i < container.getBondCount(); i++)
			handleBond(container.getBond(i));
		
		return expContainer;
	}
	
	
	public void handleAtom(IAtom atom) 
	{	
		//Cloning or expanding atom
		SLNAtom at = (SLNAtom)atom;
		List<IAtom> expandAtoms = null;
		if (at.dictObj == null)
		{
			SLNAtom newAt = at.clone();
			oldToNewAtoms.put(at, newAt);
			expContainer.addAtom(newAt);
		}
		else
		{	
			expandAtoms = expandDictionaryAtom (at);
			oldToNewAtoms.put(at, expandAtoms);
		}
	}
	
	
	public void handleBond(IBond bond) 
	{
		SLNBond bo = (SLNBond)bond;
		SLNBond newBo = bo.clone();
		
		IAtom at0 = bo.getAtom(0);
		IAtom at1 = bo.getAtom(1);
		int conectionInd0 = getConnectionIndexOfTheBond(bond, at0);
		int conectionInd1 = getConnectionIndexOfTheBond(bond, at1);
		
		//TODO improved valencePos index handling taking into account attribute v if present
		int valencePos0Index = conectionInd0;
		int valencePos1Index = conectionInd1;
				
		Object newObj0 = oldToNewAtoms.get(at0);
		Object newObj1 = oldToNewAtoms.get(at1);
				
		IAtom newAt0 = getNewAtomWhichIsValenceConection(valencePos0Index, newObj0, (SLNAtom) at0);
		IAtom newAt1 = getNewAtomWhichIsValenceConection(valencePos1Index, newObj1, (SLNAtom) at1);
		newBo.setAtoms(new IAtom[] {newAt0, newAt1});
		
		expContainer.addBond(newBo);
	}
	
	int getConnectionIndexOfTheBond(IBond bo, IAtom at)
	{
		List<IBond> list = container.getConnectedBondsList(at);
		return list.indexOf(bo);
	}
	
	
	/*
	public SLNContainer getExpandedSLNContainer0() 
	{
		//All dictionary objects SLNContainers atoms and bonds are added
		//and linked to the atoms of the container
		//This is RECURSIVE procedure
		
		expContainer = new SLNContainer(container.getBuilder());
		oldToNewAtoms.clear();
		nodes.clear();
		ringClosures.clear();
		
		ExpAtomNode node = new ExpAtomNode();
		node.parent = null;
		node.atom = (SLNAtom)container.getAtom(0);
		nodes.put(node.atom, node);
		
		//recursive approach
		expand(node.atom);

		return expContainer;
	}
	
	public void expand(IAtom atom) 
	{		
		ExpAtomNode curNode = nodes.get(atom);
		
		//Expanding atom
		SLNAtom at = (SLNAtom)atom;
		List<IAtom> expandAtoms = null;
		if (at.dictObj == null)
		{
			SLNAtom newAt = at.clone();
			oldToNewAtoms.put(at, newAt);
			expContainer.addAtom(newAt);
		}
		else
		{	
			expandAtoms = expandDictionaryAtom (at);
			oldToNewAtoms.put(at, expandAtoms);
		}
		
		//Generating connection to the atom from previous recursion level - curNode.parent
		if (curNode.parent != null)
		{
			SLNBond bo = (SLNBond)container.getBond(atom, curNode.parent);
			SLNBond newBo = bo.clone();
			
			Object o = oldToNewAtoms.get(curNode.parent);
			//IAtom newAt1 = getNewAtomWhichIsValenceConectionAtPos(int valencePos, Object newObj, SLNAtom originalAt)
			
		}
		
		//Handling the neighbors of the atom (next recursion level)
		TopLayer afs = firstSphere.get(atom);
		
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
					expand(neighborAt);
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
	*/
	
	
	/*
	 * Input newObj is an atom generated from a dictionary object or an ordinary IAtom
	 */
	IAtom getNewAtomWhichIsValenceConection(int valencePosIndex, Object newObj, SLNAtom originalAt)
	{
		//valencePosIndex is the 0-based index of the valence position
		//e.g. for CH3CH2CH3<v=1,8>  valencePosIndex=0 for v=1 and valencePosIndex=1 for v=8 
		
		if (newObj instanceof IAtom)
			return (IAtom)newObj;
		else
		{
			List<IAtom> list = (List<IAtom>) newObj;
			//Original atom (originalAt) is a dictionary object
			
			if (originalAt.dictObj instanceof AtomDictionaryObject)
			{
				//Only one atom in AtomDictionaryObject. Always return list.get(0)
				return list.get(0);
			}
			
			if (originalAt.dictObj instanceof MacroAtomDictionaryObject)
			{
				MacroAtomDictionaryObject maDO = (MacroAtomDictionaryObject)originalAt.dictObj; 
				int atIndex;

				int valenceAtomIndices[] = maDO.getValenceAtomIndices();
				if (valenceAtomIndices == null)
				{	
					//list.size() should be same as originalAt.dictObj.getSLNContainer().getAtomCount()
					if (valencePosIndex >= list.size() ) 
						atIndex = list.size()-1;
					else
						atIndex = valencePosIndex;
				}
				else
					atIndex = valenceAtomIndices[valencePosIndex];
				
				//System.out.println("getNewAtom... valencePos = " + valencePosIndex + "  atIndex = " + atIndex);
				
				return (list.get(atIndex));
			}
			
		}		
		
		return null;
	}
	
	
	public List<IAtom> expandDictionaryAtom (SLNAtom at) 
	{
		//Handle a dictionary object
		if (at.dictObj instanceof AtomDictionaryObject)
		{
			AtomDictionaryObject aDO = (AtomDictionaryObject)at.dictObj;
			SLNAtom newAt = aDO.atom.clone();
			expContainer.addAtom(newAt);
			List<IAtom> list = new ArrayList<IAtom>();
			list.add(newAt);
			return list;
		}
		else if (at.dictObj instanceof MacroAtomDictionaryObject)
		{
			return expandMacroAtomDictionaryObject((MacroAtomDictionaryObject) at.dictObj);
		}
		
		//TODO handle other type of dictionary objects
		
		return null;
	}
	
	
	public List<IAtom> expandMacroAtomDictionaryObject(MacroAtomDictionaryObject maDO)
	{
		//Clone all atoms from maDO and add to the expanded container
		List<IAtom> newAtoms = new ArrayList<IAtom>();
		for (int i = 0; i < maDO.container.getAtomCount(); i++)
		{
			SLNAtom at = (SLNAtom) maDO.container.getAtom(i);
			SLNAtom newAt = at.clone();
			expContainer.addAtom(newAt);
			newAtoms.add(newAt);
		}
		
		//Also, clone all bonds from maDO and add to the expanded container 
		for (int i = 0; i < maDO.container.getBondCount(); i++)
		{
			SLNBond bo = (SLNBond) maDO.container.getBond(i);
			SLNBond newBo = bo.clone();
			int index0 = maDO.container.indexOf(bo.getAtom(0));
			int index1 = maDO.container.indexOf(bo.getAtom(1));
			newBo.setAtoms(new IAtom[] {newAtoms.get(index0), newAtoms.get(index1)});	
			expContainer.addBond(newBo);
		}
		
		return newAtoms;
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
		markushTokensCount = new int[maList.size()];
		markushPos = new int[maList.size()];
		
		for (int i = 0; i < maList.size(); i++)
		{
			markushTokensCount[i] = ((MarkushAtomDictionaryObject)maList.get(i).dictObj).macroAtoms.size();
			//Default initial position = 0
			markushPos[i] = 0;
		}
	}
	
	void clearMarkushAtomsInfo() {
		markushTokensCount = null;
		markushPos = null;
	}
	
}
