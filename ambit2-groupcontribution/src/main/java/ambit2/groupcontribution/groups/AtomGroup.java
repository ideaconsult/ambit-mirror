package ambit2.groupcontribution.groups;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.groupcontribution.descriptors.ILocalDescriptor;

import java.util.ArrayList;


public class AtomGroup implements IGroup 
{
	private ArrayList<ILocalDescriptor> localDescriptors = null;  
	
	

	@Override
	public String getDesignation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGroupSet getGroupSet() {		
		return null;
	}

	public ArrayList<ILocalDescriptor> getLocalDescriptors() {
		return localDescriptors;
	}

	public void setLocalDescriptors(ArrayList<ILocalDescriptor> localDescriptors) {
		this.localDescriptors = localDescriptors;
	}

}
