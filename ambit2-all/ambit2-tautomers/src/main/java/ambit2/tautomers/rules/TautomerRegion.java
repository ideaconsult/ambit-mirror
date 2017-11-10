package ambit2.tautomers.rules;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.tautomers.RuleInstance;

public class TautomerRegion 
{
	boolean useRegion = false;
	boolean useCustomRegions = false;
	boolean tautomerizeNitorGroup = false;
	boolean tautomerizeAromaticSystems = true;
	
	List<Integer> regionAtomIndices = new ArrayList<Integer>();
	
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


	public boolean isTautomerizeNitorGroup() {
		return tautomerizeNitorGroup;
	}


	public void setTautomerizeNitorGroup(boolean tautomerizeNitorGroup) {
		this.tautomerizeNitorGroup = tautomerizeNitorGroup;
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
	
}
