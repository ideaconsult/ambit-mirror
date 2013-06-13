package ambit2.reactions;

import java.util.ArrayList;

import org.openscience.cdk.interfaces.IAtomContainer;

public class RetroSynthNode 
{
	public RetroSynthPath path; 
	public ArrayList<IAtomContainer> components = new ArrayList<IAtomContainer>();
	
			
	public ArrayList<RetroSynthNode> generateChildrenNodes()
	{
		//TODO
		
		//1.Check components status
		
		//2.Prepare initial set of components for processing
		
		//3.Prioritize the components
		
		//4.generated children one by one for each component (or with a more complicated combinations)
		
		return  null;
	}
	
	
}
