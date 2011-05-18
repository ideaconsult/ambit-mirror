package ambit2.smarts;

import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import java.util.Vector;

public class SMIRKSReaction 
{	
	public Vector<QueryAtomContainer> reactants = new Vector<QueryAtomContainer>();
	public Vector<QueryAtomContainer> agents = new Vector<QueryAtomContainer>();
	public Vector<QueryAtomContainer> products = new Vector<QueryAtomContainer>();
	
	public Vector<Integer> reactantComponents = new Vector<Integer>();
	public Vector<Integer> agentsComponents = new Vector<Integer>();
	public Vector<Integer> productComponents = new Vector<Integer>();
}
