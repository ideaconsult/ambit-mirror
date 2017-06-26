package ambit2.tautomers;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

public class TautomerRelation 
{
	public IAtomContainer tautomer1 = null;
	public IAtomContainer tautomer2 = null;
	public List<List<Integer>> instanceAtomIndices = new ArrayList<List<Integer>>();
	public List<Rule> rules = new ArrayList<Rule>();
	public List<Integer> ruleStatesTautomer1 = new ArrayList<Integer>();
	public List<Integer> ruleStatesTautomer2 = new ArrayList<Integer>();		
}
