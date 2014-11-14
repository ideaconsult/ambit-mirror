package ambit2.reactions;


import java.util.Stack;

import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;

public class RetroSynthNode 
{
	public RetroSynthPath path;
	public Stack<IAtomContainer> components = new Stack<IAtomContainer>(); //these are the unhandled components
	public IAtomContainerSet unresolved = new AtomContainerSet(); //these are unresolved components
	public IAtomContainerSet resolved= new AtomContainerSet();  //these are resolved components
	
	public int getNodeLevel()
	{
		return path.steps.size();
	}
	
	@Override
	public RetroSynthNode clone()
	{
		RetroSynthNode cloneNode = new RetroSynthNode();
		cloneNode.components.addAll(components);
		cloneNode.unresolved.add(unresolved);
		cloneNode.resolved.add(resolved);
		return cloneNode;	
	}
}
