package ambit2.tautomers;

public class RankingRule 
{
	Rule masterRule = null;
	String OriginalRuleString = "";
	double stateEnergies[] = null;
	String name = null;
	String RuleInfo = "";
	
	
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("RANKING RULE \n");
		sb.append("NAME = " + name  + "\n");		
				
		return(sb.toString());
	}
}
