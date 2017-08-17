package ambit2.smarts.smirks;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SMIRKSReaction;
import ambit2.smarts.SmartsParser;

public class ReactionSearch 
{
	SmartsParser parser = new SmartsParser();
	SmartsIsomorphismTester smartsIsoTester = new SmartsIsomorphismTester();
	IsomorphismTester isoTester = new IsomorphismTester();
	
	List<String> errors = new ArrayList<String>();
	
	String querySmarts = null;
	IQueryAtomContainer query = null;
	String querySmirks = null;
	SMIRKSReaction queryReaction = null;
	
	
	boolean matchReactant()
	{
		//TODO
		return false;
	}
}
