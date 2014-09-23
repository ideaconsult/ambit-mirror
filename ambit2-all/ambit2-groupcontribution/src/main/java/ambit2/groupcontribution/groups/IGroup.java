package ambit2.groupcontribution.groups;


public interface IGroup 
{
	public enum Type{
		ATOM,
		BOND,
		B_GROUP,
		D_GROUP,
		L_GROUP, 
		ATOM_TOP_LAYER_1,
		ATOM_TOP_LAYER_2, 
		ATOM_TOP_LAYER_3,
		ATOM_TOP_LAYER_12,  //This is layers 1 and 2 together 
		ATOM_TOP_LAYER_123, //This is layers 1, 2 and 3 together
	}
	   
	public double getContribution();
	public void setContribution(double contribution);
	public String getDesignation();	
	public GroupInfo getInfo();
	public IGroupSet getGroupSet();
	public Type getType();
	//public ArrayList<GroupInstance> getInstances();
}
