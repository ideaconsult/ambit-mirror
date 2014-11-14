package ambit2.groupcontribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ambit2.groupcontribution.correctionfactors.ICorrectionFactor;
import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.groups.IGroup;


public class GroupContributionModel 
{
	public enum Type {
		ATOMIC, BOND_BASED, LARGE_GROUPS, CORRECTION_FACTORS_ONLY
	}
	
	
	private String modelName = "";
	private ArrayList<ILocalDescriptor> localDescriptors = new ArrayList<ILocalDescriptor>();	
	private ArrayList<ICorrectionFactor> correctionFactors = new ArrayList<ICorrectionFactor>();
	private Map<String,IGroup> groups = new HashMap<String,IGroup>();
	private Type modelType = Type.ATOMIC;
	
	
	public String getModelName()
	{
		return modelName;
	}
	
	public void setModelName(String modelName)
	{
		this.modelName = modelName;
	}

	public ArrayList<ILocalDescriptor> getLocalDescriptors() {
		return localDescriptors;
	}

	public void setLocalDescriptors(ArrayList<ILocalDescriptor> localDescriptors) {
		this.localDescriptors = localDescriptors;
	}
	
	public void addLocalDescriptor(ILocalDescriptor localDescr)
	{
		localDescriptors.add(localDescr);
	}

	public ArrayList<ICorrectionFactor> getCorrectionFactors() {
		return correctionFactors;
	}

	public void setCorrectionFactors(ArrayList<ICorrectionFactor> correctionFactors) {
		this.correctionFactors = correctionFactors;
	}
	
	public void addCorrectionFactor(ICorrectionFactor corrFactor)
	{
		correctionFactors.add(corrFactor);
	}

	public Map<String,IGroup> getGroups() {
		return groups;
	}

	public void setGroups(Map<String,IGroup> groups) {
		this.groups = groups;
	}
	
	public void addGroup(IGroup group)
	{
		String key = group.getDesignation();
		if (!groups.containsKey(key))
			groups.put(key, group);
	}

	public Type getModelType() {
		return modelType;
	}

	public void setModelType(Type modelType) {
		this.modelType = modelType;
	}
}
