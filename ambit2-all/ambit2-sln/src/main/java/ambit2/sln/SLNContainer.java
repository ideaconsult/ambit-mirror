package ambit2.sln;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.sln.dictionary.AtomDictionaryObject;
import ambit2.sln.dictionary.MacroAtomDictionaryObject;
import ambit2.sln.dictionary.SLNDictionary;
import ambit2.smarts.TopLayer;

public class SLNContainer extends QueryAtomContainer 
{
	public static enum SLNObjectType {
		STRUCTURE, ENSAMBLE, QUERY, REACTION 
	}
	
	
	static final long serialVersionUID = 345274336895563284L;

	private SLNDictionary localDictionary = null;
	private SLNDictionary globalDictionary = null;

	// Flags that determine what types of objects are stored in SLNContainer
	private boolean IsStructureOnly = false;
	private SLNObjectType slnObjectType =  SLNObjectType.QUERY;
	private boolean FlagHasDictionaryObjects = false;
	private boolean FlagNeedsContainerExpansionForQueryMatch = false;
	
	
	private SLNContainerAttributes attributes = new SLNContainerAttributes();
	
	
	public SLNContainer(IChemObjectBuilder builder) {
		super(builder);
	}
	
	public SLNContainerAttributes getAttributes() {
		return attributes;
	}

	public void setAttributes(SLNContainerAttributes attributes) {
		this.attributes = attributes;
	}

	public boolean getIsStructureOnly() {
		return IsStructureOnly;
	}

	public void setIsStructureOnly(boolean IsStructureOnly) {
		this.IsStructureOnly = IsStructureOnly;
	}

	public boolean checkIsStructureOnly() {
		SLNContainer container = new SLNContainer(SilentChemObjectBuilder.getInstance());
		for (int i = 0; i < container.atomCount; i++) {
			for (int j = 0; j < container.bondCount; j++) {
				if (/*atomExpression != null || bondExpression != null
						|| */  attributes != null)
					return false;
			}
		}
		return false;
	}

	public SLNDictionary getLocalDictionary() {
		return localDictionary;
	}

	public SLNDictionary getGlobalDictionary() {
		return globalDictionary;
	}

	public void setLocalDictionary(SLNDictionary dict) {
		localDictionary = dict;
	}

	public void setGlobalDictionary(SLNDictionary dict) {
		globalDictionary = dict;
	}
	
	public SLNObjectType getSlnObjectType() {
		return slnObjectType;
	}
		
	public boolean isFlagHasDictionaryObjects() {
		return FlagHasDictionaryObjects;
	}

	public boolean isFlagNeedsContainerExpansionForQueryMatch() {
		return FlagNeedsContainerExpansionForQueryMatch;
	}

	public void recognizeSlnObjectType() {
		//TODO 
		//may be remove function checkIsStructureOnly()
	}
	
	public int[] getValenceAtomIndices() {
		if (getAttributes().valences == null)
			return null;
		
		List<Integer> atNums1based = get1basedAtomIndicesCountingHAtoms();
		int n = getAttributes().valences.length;		
		int valAtInd[] = new int[n];
		
		for (int i = 0; i < n; i++)
		{
			Integer v = getAttributes().valences[i];
			valAtInd[i] = atNums1based.indexOf(v);
			//System.out.println("valAtInd = "  + valAtInd[i]);
		}
		return valAtInd;
	}
	
	public List<Integer> get1basedAtomIndicesCountingHAtoms() {
		int n = getAtomCount();
		int curNum = 1;
		List<Integer> ind = new ArrayList<Integer>();
		
		for (int i = 0; i<n; i++)
		{
			ind.add(curNum);
			curNum = curNum + ((SLNAtom)getAtom(i)).numHAtom + 1;
		}		
		return ind;
	}
	
	public SLNContainer getExpandedSLNContainer() {
		//All dictionary objects SLNContainers atoms and bonds are added
		//and linked to the this container
		SLNContainer expContainer = new SLNContainer(this.getBuilder());
		
		Map<IAtom,IAtom> oldToNewAtom = new HashMap<IAtom,IAtom>();		
		//List<SLNAtom> dictAtoms = getDictionaryAtoms();		
		TopLayer.setAtomTopLayers(this, TopLayer.TLProp);
		
		for (int i = 0; i < getAtomCount(); i++)
		{
			SLNAtom at = (SLNAtom)getAtom(i);
			if (at.dictObj == null)
			{
				SLNAtom newAt = at.clone();
				oldToNewAtom.put(at, newAt);
				expContainer.addAtom(newAt);
			}
			else
			{
				//Handle a dictionary object
				if (at.dictObj instanceof AtomDictionaryObject)
				{
					
				}
				else if (at.dictObj instanceof MacroAtomDictionaryObject)
				{
					
				}
				//TODO handle other type of dict. objects
				
			}
		}
		
		return expContainer;
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
