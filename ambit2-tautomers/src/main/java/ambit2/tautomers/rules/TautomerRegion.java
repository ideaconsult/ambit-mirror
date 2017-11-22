package ambit2.tautomers.rules;

import java.util.ArrayList;
import java.util.List;

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
	List<Integer> includeAtomIndices = null;
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

	public List<Integer> getIncludeAtomIndices() {
		return includeAtomIndices;
	}

	public void setIncludeAtomIndices(List<Integer> includeAtomIndices) {
		this.includeAtomIndices = includeAtomIndices;
	}
	
	
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
		//TODO
		return true;
	}
	
	public void calculateRegion(IAtomContainer mol)
	{
		//TODO
	}
	
}
