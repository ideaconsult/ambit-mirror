package ambit2.groupcontribution.groups;


public interface IGroup 
{
	public enum Type{
		ATOM,BOND,B_GROUP,D_GROUP,L_GROUP
	}
	   
	public double getContribution();
	public String getDesignation();	
	public GroupInfo getInfo();
	public IGroupSet getGroupSet();
	public Type getType();
	//public ArrayList<GroupInstance> getInstances();
}
