package ambit2.groupcontribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.groupcontribution.correctionfactors.DescriptorInfo;
import ambit2.groupcontribution.correctionfactors.ICorrectionFactor;
import ambit2.groupcontribution.dataset.DataSetObject;
import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.descriptors.LDAtomSymbol;
import ambit2.groupcontribution.fragmentation.Fragmentation;
import ambit2.groupcontribution.groups.IGroup;
import ambit2.groupcontribution.utils.math.MatrixDouble;
import ambit2.groupcontribution.utils.math.ValidationConfig;


public class GroupContributionModel 
{
	public static class GCMConfigInfo {
		public String trainingSetFile = null;
		public String externalSetFile = null;		
		public String localDescriptorsString = null;
		public String globalDescriptorsString = null;
		public Double columnFiltrationthreshold = null;
		public String gcmTypeString = null;
		public String validationString = null;
		public String corFactorsString = null;		
		public int fractionDigits = -1;
		
	}
	
	public enum Type {
		ATOMIC, BOND_BASED, CUSTOM_GROUPS, CORRECTION_FACTORS_ONLY
	}
	
	/*
	public enum GroupType {
		ATOM, BOND, G_GROUP, D_GROUP, L_GROUP, RING3, RING4, RING5, RING6
	}
	*/
		
	private String modelName = null;
	private String modelDescription = null;
	
	private List<ILocalDescriptor> localDescriptors = new ArrayList<ILocalDescriptor>();	
	private List<ICorrectionFactor> correctionFactors = new ArrayList<ICorrectionFactor>();
	private List<IGroup.Type> customGroups = new ArrayList<IGroup.Type>();
	private List<List<ILocalDescriptor>> customGroupLocalDescriptors = new ArrayList<List<ILocalDescriptor>>();
	private List<DescriptorInfo> descriptors = null;
	
	private Map<String,IGroup> groups = new HashMap<String,IGroup>();
	private Type modelType = Type.ATOMIC;
	private String targetProperty = null;
	private Double colStatPercentageThreshold = null;
	private GCMReportConfig reportConfig = new GCMReportConfig(); 	
	private StringBuffer report = new StringBuffer(); 
	private ValidationConfig validationConfig = new ValidationConfig(); 
	private GCMConfigInfo additionalConfig = new GCMConfigInfo();
	
	private boolean allowGroupRegistration = true;
	
	
	//TODO add group rules and LocalDescriptor rules
	
	public String getModelName()
	{
		return modelName;
	}
	
	public void setModelName(String modelName)
	{
		this.modelName = modelName;
	}
	
	public String getModelDescription() {
		return modelDescription;
	}

	public void setModelDescription(String modelDescription) {
		this.modelDescription = modelDescription;
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
	
	public List<IGroup.Type> getCustomGroups() {
		return customGroups;
	}

	public void setCustomGroups(List<IGroup.Type> customGroups) {
		this.customGroups = customGroups;
	}
	
	public List<List<ILocalDescriptor>> getCustomGroupLocalDescriptors() {
		return customGroupLocalDescriptors;
	}

	public void setCustomGroupLocalDescriptors(List<List<ILocalDescriptor>> customGroupLocalDescriptors) {
		this.customGroupLocalDescriptors = customGroupLocalDescriptors;
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
	
	public String getTargetProperty() {
		return targetProperty;
	}

	public void setTargetProperty(String targetProperty) {
		this.targetProperty = targetProperty;
	}
	
	public Double getColStatPercentageThreshold() {
		return colStatPercentageThreshold;
	}

	public void setColStatPercentageThreshold(Double colStatPercentageThreshold) {
		this.colStatPercentageThreshold = colStatPercentageThreshold;
	}
	
	public GCMReportConfig getReportConfig() {
		return reportConfig;
	}

	public void setReportConfig(GCMReportConfig reportConfig) {
		this.reportConfig = reportConfig;
	}
	
	public List<DescriptorInfo> getDescriptors() {
		return descriptors;
	}

	public void setDescriptors(List<DescriptorInfo> descriptors) {
		this.descriptors = descriptors;
	}
	
	public GCMConfigInfo getAdditionalConfig() {
		return additionalConfig;
	}

	public void setAdditionalConfig(GCMConfigInfo additionalConfig) {
		this.additionalConfig = additionalConfig;
	}

	
	/*
	public void addDescriptor(String descrName) {
		DescriptorInfo di = new DescriptorInfo();
		di.setName(descrName);
		descriptors.add(di);
	}
	*/
	
	
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
			//System.out.println("  " + (i+1) + "  " + groupsStr[i]);
			sb.append("  " + (i+1) + "  " + groupsStr[i] + "\n");
		}
		
		return sb.toString();
	}
	
	public String getCorrectionFactorsAsString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < correctionFactors.size(); i++)
		{			
			sb.append("  " + (i+1) + "  " + correctionFactors.get(i).getDesignation() + "\n");
		}
		
		return sb.toString();
	}
	
	public void addToReport(String info)
	{
		report.append(info);
	}
	
	public String getReport()
	{
		return report.toString();
	}

	public ValidationConfig getValidationConfig() {
		return validationConfig;
	}

	public void setValidationConfig(ValidationConfig validationConfig) {
		this.validationConfig = validationConfig;
	}
		
	public boolean isAllowGroupRegistration() {
		return allowGroupRegistration;
	}

	public void setAllowGroupRegistration(boolean allowGroupRegistration) {
		this.allowGroupRegistration = allowGroupRegistration;
	}

	public double calcModelValue(IAtomContainer mol)
	{
		DataSetObject dso = new DataSetObject();
		return calcModelValue(dso);
	}
	
	public double calcModelValue(DataSetObject dso)
	{
		if (dso.fragmentation == null)
		{	
			Fragmentation.makeFragmentation(dso, this);
		}		
		double value  = 0.0;
		
		//Handle groups
		Set<Entry<String, Integer>> entries =  dso.fragmentation.groupFrequencies.entrySet();
		for (Entry<String, Integer> entry : entries)
		{
			String key = entry.getKey();
			IGroup group = groups.get(key);
			if (group != null)
				value += group.getContribution() * entry.getValue();
		}
		
		//Handle correction factors
		if (!correctionFactors.isEmpty())
			for (int i = 0; i < correctionFactors.size(); i++)
			{	
				value += correctionFactors.get(i).getContribution() * 
						 dso.fragmentation.correctionFactors.get(i);
			}	
				
		//Handle descriptors
		if (descriptors != null)
			for (int i = 0; i < descriptors.size(); i++)
			{	
				DescriptorInfo di = descriptors.get(i);
				di.getName();
				Double d = dso.getPropertyDoubleValue(di.getName());
				if (d == null)
					value += di.getContribution() * d;
			}
		
		return value;
	}
	
	
	public String toJsonString()
	{
		StringBuffer sb = new StringBuffer();
		String endLine = "\n";
		String offset = "\t";
		int nFields = 0;
		
		sb.append("{" + endLine);
		if (modelName != null)
		{	
			sb.append(offset + "\"" + "MODEL_NAME" + "\" : \"" + modelName + "\"");
			nFields++;
		}	
		
		if (modelDescription != null)
		{
			if (nFields > 0)
				sb.append("," + endLine);
			sb.append(offset + "\"" + "MODEL_DESCRIPTION" + "\" : \"" + modelDescription + "\"");
			nFields++;
		}
		
		if (additionalConfig.gcmTypeString != null)
		{
			if (nFields > 0)
				sb.append("," + endLine);
			sb.append(offset + "\"" + "MODEL_TYPE" + "\" : \"" + additionalConfig.gcmTypeString + "\"");
			nFields++;
		}
		
		if (additionalConfig.localDescriptorsString != null)
		{
			if (nFields > 0)
				sb.append("," + endLine);
			sb.append(offset + "\"" + "LOCAL_DESCRIPTORS" + "\" : \"" + additionalConfig.localDescriptorsString + "\"");
			nFields++;
		}
		
		if (additionalConfig.globalDescriptorsString != null)
		{
			if (nFields > 0)
				sb.append("," + endLine);
			sb.append(offset + "\"" + "GLOBAL_DESCRIPTORS" + "\" : \"" + additionalConfig.globalDescriptorsString + "\"");
			nFields++;
		}
		
		if (additionalConfig.corFactorsString != null)
		{
			if (nFields > 0)
				sb.append("," + endLine);
			sb.append(offset + "\"" + "CORRECTION_FACTORS" + "\" : \"" + additionalConfig.corFactorsString + "\"");
			nFields++;
		}
		
		if (additionalConfig.columnFiltrationthreshold != null)
		{
			if (nFields > 0)
				sb.append("," + endLine);
			sb.append(offset + "\"" + "COLUMN_FILTRATION_THRESHOLD" + "\" : " 
				+ additionalConfig.columnFiltrationthreshold);
			nFields++;
		}
		
		if (additionalConfig.fractionDigits != -1)
		{
			if (nFields > 0)
				sb.append("," + endLine);
			sb.append(offset + "\"" + "FRACTION_DIGITS" + "\" : " 
				+ additionalConfig.fractionDigits);
			nFields++;
		}
		
		if (additionalConfig.trainingSetFile != null)
		{
			if (nFields > 0)
				sb.append("," + endLine);
			sb.append(offset + "\"" + "TRAINING_SET_FILE" + "\" : \"" + additionalConfig.trainingSetFile + "\"");
			nFields++;
		}
		
		if (additionalConfig.externalSetFile != null)
		{
			if (nFields > 0)
				sb.append("," + endLine);
			sb.append(offset + "\"" + "EXTERNAL_SET_FILE" + "\" : \"" + additionalConfig.externalSetFile + "\"");
			nFields++;
		}
		
		sb.append(endLine);
		sb.append("}" + endLine);
		
		return sb.toString();
				
	}
	
	
}
