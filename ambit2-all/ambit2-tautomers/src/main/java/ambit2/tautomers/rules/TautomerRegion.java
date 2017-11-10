package ambit2.tautomers.rules;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.tautomers.RuleInstance;

public class TautomerRegion 
{
	boolean useRegion = false;	
	boolean tautomerizeNitroGroup = false;
	boolean tautomerizeAromaticSystems = true;
	boolean useCustomRegions = false;
	
	List<Integer> regionAtomIndices = null;
	List<Integer> excludeAtomIndices = new ArrayList<Integer>();
	
	
	
	public boolean useRegion() {
		return useRegion;
	}

	public void setUseRegion(boolean useRegion) {
		this.useRegion = useRegion;
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
	

	public boolean isUseCustomRegions() {
		return useCustomRegions;
	}


	public void setUseCustomRegions(boolean useCustomRegions) {
		this.useCustomRegions = useCustomRegions;
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


	public List<Integer> getRegionAtomIndices() {
		return regionAtomIndices;
	}


	public void setRegionAtomIndices(List<Integer> regionAtomIndices) {
		this.regionAtomIndices = regionAtomIndices;
	}
	
	public List<Integer> getExcludeAtomIndices() {
		return excludeAtomIndices;
	}

	public void setExcludeAtomIndices(List<Integer> excludeAtomIndices) {
		this.excludeAtomIndices = excludeAtomIndices;
	}
	
}
