package ambit2.smarts;

import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import java.util.Vector;

public class SMIRKSReaction 
{	
	public String reactantsSmarts, agentsSmarts, productsSmarts;
	public SmartsFlags reactantFlags = new SmartsFlags();
	public SmartsFlags agentFlags = new SmartsFlags();
	public SmartsFlags productFlags = new SmartsFlags();
	
	
	public Vector<QueryAtomContainer> reactants = new Vector<QueryAtomContainer>();
	public Vector<QueryAtomContainer> agents = new Vector<QueryAtomContainer>();
	public Vector<QueryAtomContainer> products = new Vector<QueryAtomContainer>();
	
	public Vector<Integer> reactantCLG = new Vector<Integer>();
	public Vector<Integer> agentsCLG = new Vector<Integer>();
	public Vector<Integer> productsCLG = new Vector<Integer>();
}
