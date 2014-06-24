package ambit2.groupcontribution;

import java.util.ArrayList;

import ambit2.groupcontribution.descriptors.ILocalDescriptor;


public class GroupContributionModel 
{
	private String modelName = "";
	private ArrayList<ILocalDescriptor> localDescriptors = new ArrayList<ILocalDescriptor>();
	
	
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
		//TODO
	}
}
