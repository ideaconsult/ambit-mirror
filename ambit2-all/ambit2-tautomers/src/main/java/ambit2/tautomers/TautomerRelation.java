package ambit2.tautomers;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

public class TautomerRelation 
{
	public IAtomContainer tautomer1 = null;
	public IAtomContainer tautomer2 = null;
	public int tautomer1Index = 0;
	public int tautomer2Index = 0;
	public List<List<Integer>> instanceAtomIndices = new ArrayList<List<Integer>>();
	public List<Rule> rules = new ArrayList<Rule>();
	public List<Integer> tautomer1RuleStates = new ArrayList<Integer>();
	public List<Integer> tautomer2RuleStates = new ArrayList<Integer>();
	
	public String toString()
	{
		//printing 1-based indices
		StringBuffer sb = new StringBuffer();
		sb.append(tautomer1Index);
		sb.append("   ");
		sb.append(tautomer2Index);
		sb.append("   ");
		for (int i = 0; i< rules.size(); i++)
		{
			sb.append("rule: ");
			sb.append(rules.get(i).name);
			sb.append(" atoms ");
			List<Integer> atomIndices = instanceAtomIndices.get(i);
			for (int k = 0; k < atomIndices.size(); k++)
			{
				sb.append(atomIndices.get(k)+1); 
				sb.append(" ");
			}
			sb.append("states ");
			sb.append(tautomer1RuleStates.get(i)+1);
			sb.append(" ");
			sb.append(tautomer2RuleStates.get(i)+1);
			sb.append(" ");
		}
		return sb.toString();
	}
}
