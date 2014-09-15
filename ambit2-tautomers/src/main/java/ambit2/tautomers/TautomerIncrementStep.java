package ambit2.tautomers;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

public class TautomerIncrementStep 
{
	public static int counter = 0;
	int ID = 0;
	int parentID = 0;
	List<RuleInstance> usedRuleInstances = new ArrayList<RuleInstance>();
	List<RuleInstance> unUsedRuleInstances = new ArrayList<RuleInstance>();
		
	//Struct container is a virtual molecule which describes the current state of the 
	//structure at this incremental step
	//It contains all atoms/bonds from the original molecule that are not part of the used rule instances
	//The other atoms/bonds are clones of the original atoms
	IAtomContainer struct;
	
	String getTautomerCombination()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = usedRuleInstances.size()-1; i >= 0; i--)
			sb.append(""+usedRuleInstances.get(i).getCurrentState() + " ");
		return (sb.toString());
	}
	
	
	public String debugInfo()
	{		
		StringBuffer sb = new StringBuffer();
		sb.append("IncStep:\n");
		sb.append("  used_ri:");
		for (int i = 0; i < usedRuleInstances.size(); i++)
			sb.append("{"+usedRuleInstances.get(i).debugInfo(struct)+"} ");
		sb.append("\n");	
		sb.append("  unUsed_ri:");
		for (int i = 0; i < unUsedRuleInstances.size(); i++)
			sb.append("{"+unUsedRuleInstances.get(i).debugInfo(struct)+"} ");
		sb.append("\n");
		
		return(sb.toString());
	}
	
}
