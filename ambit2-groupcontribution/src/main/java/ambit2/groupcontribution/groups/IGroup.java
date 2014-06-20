package ambit2.groupcontribution.groups;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface IGroup 
{
	//public int getCount(IAtomContainer mol);  //to be moved to other classes ??? 
	//public double getContribution();
	public String getDesignation();
	public String getName();
	public String getInfo();
	public IGroupSet getGroupSet();
}
