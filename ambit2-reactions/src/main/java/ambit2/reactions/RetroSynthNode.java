package ambit2.reactions;


import org.openscience.cdk.interfaces.IAtomContainer;

public class RetroSynthNode 
{
	public RetroSynthPath path;
	public IAtomContainer components;  //these are unresolved components
	public IAtomContainer resolved;   //these are resolved components
	
	public int getNodeLevel()
	{
		return path.steps.size();
	}
}
