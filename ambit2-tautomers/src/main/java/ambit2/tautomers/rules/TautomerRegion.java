package ambit2.tautomers.rules;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.tautomers.RuleInstance;

public class TautomerRegion 
{
	boolean useRegion = false;	
	boolean tautomerizeNitroGroup = false;
	boolean tautomerizeNitroGroupPartially = false;
	boolean tautomerizeNitroxides = false;	
	boolean tautomerizeAromaticSystems = true;
	boolean useCustomRegions = false;
	
	List<Integer> includeAtomIndices = null;
	List<Integer> excludeAtomIndices = new ArrayList<Integer>();
	
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
	
	public List<Integer> getExcludeAtomIndices() {
		return excludeAtomIndices;
	}

	public void setExcludeAtomIndices(List<Integer> excludeAtomIndices) {
		this.excludeAtomIndices = excludeAtomIndices;
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
