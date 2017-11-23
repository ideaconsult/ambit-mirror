package ambit2.tautomers.rules;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import ambit2.smarts.SmartsParser;
import ambit2.tautomers.RuleInstance;
import ambit2.tautomers.RuleStateFlags;

public class TautomerRegion 
{
	boolean useRegion = false;	
	boolean tautomerizeNitroGroup = false;
	boolean tautomerizeNitroGroupPartially = false;
	boolean tautomerizeNitroxides = false;	
	boolean tautomerizeAromaticSystems = true;	
	List<String> customExcludeRegionSmarts = new ArrayList<String>();
	
	List<CustomTautomerRegion> customExcludeRegions = new ArrayList<CustomTautomerRegion>();
	//List<Integer> includeAtomIndices = null;
	List<Integer> excludeAtomIndices = new ArrayList<Integer>();
	List<String> errors = new ArrayList<String>();
	
		
	public boolean useRegion() {
		return useRegion;
	}

	public void setUseRegion(boolean useRegion) {
		this.useRegion = useRegion;
	}
	
	public boolean isTautomerizeNitroGroupPartially() {
		return tautomerizeNitroGroupPartially;
	}

	public void setTautomerizeNitroGroupPartially(boolean tautomerizeNitroGroupPartially) {
		this.tautomerizeNitroGroupPartially = tautomerizeNitroGroupPartially;
	}

	public boolean isTautomerizeNitroxides() {
		return tautomerizeNitroxides;
	}

	public void setTautomerizeNitroxides(boolean tautomerizeNitroxides) {
		this.tautomerizeNitroxides = tautomerizeNitroxides;
	}

	/*
	public List<Integer> getIncludeAtomIndices() {
		return includeAtomIndices;
	}

	public void setIncludeAtomIndices(List<Integer> includeAtomIndices) {
		this.includeAtomIndices = includeAtomIndices;
	}
	*/
	
	public boolean isTautomerizeNitroGroup() {
		return tautomerizeNitroGroup;
	}


	public void setTautomerizeNitroGroup(boolean tautomerizeNitroGroup) {
		this.tautomerizeNitroGroup = tautomerizeNitroGroup;
	}


	public boolean isTautomerizeAromaticSystems() {
		return tautomerizeAromaticSystems;
	}


	public void setTautomerizeAromaticSystems(boolean tautomerizeAromaticSystems) {
		this.tautomerizeAromaticSystems = tautomerizeAromaticSystems;
	}
	
	public List<String> getCustomExcludeRegionSmarts() {
		return customExcludeRegionSmarts;
	}

	public void setCustomExcludeRegionSmarts(List<String> customExcludeRegionSmarts) {
		this.customExcludeRegionSmarts = customExcludeRegionSmarts;
		setCustomExcludeRegions();
	}
	
	public List<Integer> getExcludeAtomIndices() {
		return excludeAtomIndices;
	}

	public void setExcludeAtomIndices(List<Integer> excludeAtomIndices) {
		this.excludeAtomIndices = excludeAtomIndices;
	}
	
	void setCustomExcludeRegions()
	{
		customExcludeRegions.clear();
		
		SmartsParser sp = new SmartsParser();
		sp.mSupportDoubleBondAromaticityNotSpecified = true;
		
		for (int i = 0; i<customExcludeRegionSmarts.size(); i++)
		{
			IQueryAtomContainer q = sp.parse(customExcludeRegionSmarts.get(i));			
			String errorMsg = sp.getErrorMessages();
			if (!errorMsg.equals(""))
			{	
				errors.add(errorMsg);
			}
			else
			{	
				sp.setNeededDataFlags();	
				RuleStateFlags flags = new RuleStateFlags();
				flags.hasRecursiveSmarts = sp.hasRecursiveSmarts;
				flags.mNeedExplicitHData = sp.needExplicitHData();
				flags.mNeedNeighbourData = sp.needNeighbourData();
				flags.mNeedParentMoleculeData = sp.needParentMoleculeData();
				flags.mNeedRingData = sp.needRingData();
				flags.mNeedRingData2 = sp.needRingData2();
				flags.mNeedValenceData = sp.needValencyData();
				
				CustomTautomerRegion ctr = new CustomTautomerRegion();
				ctr.flags = flags;
				ctr.query = q;
				ctr.smarts = customExcludeRegionSmarts.get(i);
				customExcludeRegions.add(ctr);
			}
		}
	}
	
	public boolean isRuleInstanceInRegion(RuleInstance ruleInst, IAtomContainer mol)
	{
		/*
		//Include mode takes precedence over exclude mode
		if (includeAtomIndices != null)
		{
			for (IAtom at : ruleInst.atoms)
			{	
				int atInd = mol.getAtomNumber(at);
				if (includeAtomIndices.contains(atInd))
					continue;
				else
					return false; //rule instance atom is not within include list
			}
			return true;
		}
		*/
		
		//Handle exclude atoms region
		for (int i = 0; i < excludeAtomIndices.size(); i++)
		{
			IAtom exclAt = mol.getAtom(excludeAtomIndices.get(i));
			if (ruleInst.atoms.contains(exclAt))
				return false; //rule instance contains an 'exclude' atom
		}
		return true;
	}
	
	public void calculateRegion(IAtomContainer target)
	{
		excludeAtomIndices.clear();
		
		if (tautomerizeNitroGroup)
		{
			List<IAtom[]> pos = CustomTautomerRegion.getNitroGroupPositions(target);
			for (int i = 0; i < pos.size(); i++)
			{
				IAtom[] atoms = pos.get(i);
				for (int k = 0; k < atoms.length; k++)
				{
					int atNum = target.getAtomNumber(atoms[k]);
					if (atNum == -1)
						continue;
					else
					{	
						if (!isIndexInList(atNum, excludeAtomIndices))
							excludeAtomIndices.add(atNum);
					}	
				}
					
			}
		}
		else
			if (tautomerizeNitroGroupPartially)
			{
				//TODO
			}
				
		//TODO
	}
	
	boolean isIndexInList(int index, List<Integer> list)
	{
		for (Integer i : list )
			if (index == i.intValue())
				return true;
		return false;
	}
	
}
