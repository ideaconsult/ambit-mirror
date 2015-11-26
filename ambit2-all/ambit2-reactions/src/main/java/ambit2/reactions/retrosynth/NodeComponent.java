package ambit2.reactions.retrosynth;

import org.openscience.cdk.interfaces.IAtomContainer;

public class NodeComponent 
{
	IAtomContainer fragment = null;
	boolean isTerminal = false;
	
	
	public NodeComponent()
	{	
	}
	
	public NodeComponent(IAtomContainer fragment)
	{	
		this.fragment = fragment;
	}
	
	public IAtomContainer getFragment()
	{
		return fragment;
	}	
	
	
	
	public static boolean checkIsTerminalFragment(IAtomContainer ac)
	{
		//TODO 
		//check is this a starting material 
		
		return false;
	}
	
	
	
}
