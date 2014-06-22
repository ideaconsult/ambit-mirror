package ambit2.groupcontribution.groups;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface IGroup 
{
	public int getCount();   
	public double getContribution();
	public String getDesignation();
	public String getName();
	public String getInfo();
	public IGroupSet getGroupSet();
}
