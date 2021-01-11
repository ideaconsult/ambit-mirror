package ambit2.sln.dictionary;

import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;

import ambit2.sln.SLNAtom;
import ambit2.sln.SLNContainer;
import ambit2.smarts.TopLayer;

public class Expander 
{
	SLNContainer container = null;
	SLNContainer expContainer = null;
	Map<IAtom,IAtom> oldToNewAtoms = new HashMap<IAtom,IAtom>();	
	
	public SLNContainer getExpandedSLNContainer(SLNContainer container) 
	{
		//All dictionary objects SLNContainers atoms and bonds are added
		//and linked to the atoms of the container
		this.container = container;
		expContainer = new SLNContainer(container.getBuilder());
		oldToNewAtoms.clear();
		
			
		//List<SLNAtom> dictAtoms = getDictionaryAtoms();		
		TopLayer.setAtomTopLayers(container, TopLayer.TLProp);

		//Iterating all atoms and expanding the macro atoms
		for (int i = 0; i < container.getAtomCount(); i++)
		{
			SLNAtom at = (SLNAtom)container.getAtom(i);
			if (at.dictObj == null)
			{
				SLNAtom newAt = at.clone();
				oldToNewAtoms.put(at, newAt);
				expContainer.addAtom(newAt);
			}
			else
				expandDictionaryAtom (at);
		}
		
		//Iterating all bonds
		//TODO

		return expContainer;
	}
	
	public void expandDictionaryAtom (SLNAtom at) 
	{
		//Handle a dictionary object
		if (at.dictObj instanceof AtomDictionaryObject)
		{
			//TODO
		}
		else if (at.dictObj instanceof MacroAtomDictionaryObject)
		{
			expandMacroAtomDictionaryObject(at, (MacroAtomDictionaryObject) at.dictObj);
		}
		//TODO handle other type of dictionary objects
	}
	
	public void expandMacroAtomDictionaryObject(SLNAtom at, MacroAtomDictionaryObject maDO)
	{
		//TODO
	}
	
	/*
	public List<SLNAtom> getDictionaryAtoms() {
		List<SLNAtom> dictAtoms = new ArrayList<SLNAtom>();
		for (int i = 0; i < getAtomCount(); i++)
		{
			SLNAtom at = (SLNAtom)getAtom(i);
			if (at.dictObj != null)
				dictAtoms.add(at);
		}
		return dictAtoms;
	}
	*/
}
