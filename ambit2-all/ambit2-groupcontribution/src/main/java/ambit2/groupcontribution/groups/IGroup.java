package ambit2.groupcontribution.groups;

import ambit2.groupcontribution.transformations.IValueTransformation;

public interface IGroup 
{
	public enum Type{
		ATOM,
		BOND,
		B_GROUP,
		D_GROUP,
		G_GROUP,
		L_GROUP, 
		RING3, 
		RING4, 
		RING5, 
		RING6,
		/*
		ATOM_TOP_LAYER_1,
		ATOM_TOP_LAYER_2, 
		ATOM_TOP_LAYER_3,
		ATOM_TOP_LAYER_12,  //This is layers 1 and 2 together 
		ATOM_TOP_LAYER_123, //This is layers 1, 2 and 3 together
		*/
	}
	   
	public double getContribution();
	public void setContribution(double contribution);
	public IValueTransformation getValueTransformation();
	public void setValueTransformation(IValueTransformation transformation);
	public String getDesignation();	
	public GroupInfo getInfo();
	public IGroupSet getGroupSet();
	public Type getType();
	public boolean isMissing();
	public void setMissing(boolean missing);
	//public ArrayList<GroupInstance> getInstances();
}
