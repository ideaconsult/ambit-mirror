package ambit2.smarts.smirks;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SMIRKSReaction;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SmartsParser;

public class ReactionSearch 
{
	SmartsParser parser = new SmartsParser();
	SMIRKSManager smirksMan = new SMIRKSManager(SilentChemObjectBuilder.getInstance());
	SmartsIsomorphismTester smartsIsoTester = new SmartsIsomorphismTester();
	IsomorphismTester isoTester = new IsomorphismTester();
	
	List<String> errors = new ArrayList<String>();
	
	String querySmarts = null;
	IQueryAtomContainer query = null;
	String querySmirks = null;
	SMIRKSReaction queryReaction = null;
	
	
	public void reset()
	{
		errors.clear();
	}
	
	public void setQuerySmarts(String querySmarts) {
		this.querySmarts = querySmarts;
		query = parser.parse(querySmarts);
		parser.setNeededDataFlags();
		String errorMsg = parser.getErrorMessages();
		if (!errorMsg.equals("")) {
			errors.add(errorMsg);
			query = null;
			return;
		}

		if (parser.hasRecursiveSmarts) {
			//getRecursiveAtoms();			
		}
	}
	
	
	public void setQuerySmirks(String querySmirks) {
		this.querySmirks = querySmirks;
		
		//TODO
	}
	
	boolean matchComponent(IAtomContainer comp)
	{
		//TODO
		return false;
	}
	
	
	boolean matchReaction(String targetSmirks)
	{
		//TODO
		return false;
	}
	
	boolean matchReaction(SMIRKSReaction targetReaction)
	{
		//TODO
		return false;
	}
	
	boolean matchReaction(List<IAtomContainer> reactants, List<IAtomContainer> product)
	{
		//TODO
		return false;
	}
}
