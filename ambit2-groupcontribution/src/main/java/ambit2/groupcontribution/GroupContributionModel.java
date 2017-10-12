package ambit2.groupcontribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ambit2.groupcontribution.correctionfactors.ICorrectionFactor;
import ambit2.groupcontribution.dataset.DataSetObject;
import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.descriptors.LDAtomSymbol;
import ambit2.groupcontribution.groups.IGroup;
import ambit2.groupcontribution.utils.math.MatrixDouble;


public class GroupContributionModel 
{
	public enum Type {
		ATOMIC, BOND_BASED, CUSTOM_GROUPS, CORRECTION_FACTORS_ONLY
	}
	
	public enum GroupType {
		ATOM, BOND, G_GROUP, D_GROUP, L_GROUP, RING3, RING4, RING5, RING6
	}
		
	private String modelName = "";
	private List<ILocalDescriptor> localDescriptors = new ArrayList<ILocalDescriptor>();	
	private List<ICorrectionFactor> correctionFactors = new ArrayList<ICorrectionFactor>();
	private List<GroupType> customGroups = new ArrayList<GroupType>();	
	
	//TODO add group rules and LocalDescriptor rules
	private Map<String,IGroup> groups = new HashMap<String,IGroup>();
	private Type modelType = Type.ATOMIC;
	private String targetEndpoint = null;
	private Double colStatPercentageThreshold = null;
	
	
	public String getModelName()
	{
		return modelName;
	}
	
	public void setModelName(String modelName)
	{
		this.modelName = modelName;
	}

	public List<ILocalDescriptor> getLocalDescriptors() {
		return localDescriptors;
	}

	public void setLocalDescriptors(List<ILocalDescriptor> localDescriptors) {
		this.localDescriptors = localDescriptors;
	}
	
	public void addLocalDescriptor(ILocalDescriptor localDescr)
	{
		localDescriptors.add(localDescr);
	}

	public List<ICorrectionFactor> getCorrectionFactors() {
		return correctionFactors;
	}

	public void setCorrectionFactors(List<ICorrectionFactor> correctionFactors) {
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
	
	public void clearGroups() {
		groups.clear();
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
	
	public String getTargetEndpoint() {
		return targetEndpoint;
	}

	public void setTargetEndpoint(String targetEndpoint) {
		this.targetEndpoint = targetEndpoint;
	}
	
	public Double getColStatPercentageThreshold() {
		return colStatPercentageThreshold;
	}

	public void setColStatPercentageThreshold(Double colStatPercentageThreshold) {
		this.colStatPercentageThreshold = colStatPercentageThreshold;
	}
	
	public String getAtomDesignation(int descriptors[])
	{
		if (descriptors.length != localDescriptors.size())
			return null;
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < descriptors.length; i++)
			sb.append(getDescriptorDesignation(
					localDescriptors.get(i),	descriptors[i]) );
		
		return sb.toString();
	}
	
	String getDescriptorDesignation(ILocalDescriptor desc, int value)
	{
		if (desc instanceof LDAtomSymbol)
			return desc.getDesignation(value);
		return "" + value;
	}
	
	
	
	public String getAtomDesignationVerbose(int descriptors[])
	{
		if (descriptors.length != localDescriptors.size())
			return null;
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < descriptors.length; i++)
		{	
			//TODO
		}
		
		return sb.toString();

	}	
	
	public String getGroupsAsString()
	{
		StringBuffer sb = new StringBuffer();
		int n = groups.keySet().size();
		String groupsStr[] = groups.keySet().toArray(new String[n]);
		
		for (int i = 0; i < n; i++)
		{
			System.out.println("  " + (i+1) + "  " + groupsStr[i]);
		}

		
		return sb.toString();
	}

	
}
