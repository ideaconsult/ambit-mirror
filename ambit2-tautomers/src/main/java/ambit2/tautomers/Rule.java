package ambit2.tautomers;


import org.openscience.cdk.interfaces.IAtomContainer;
import java.util.Vector;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

public class Rule 
{
	int HAtomMode = TautomerConst.HRM_Implicit;
	String name = null;	
	int type = TautomerConst.RT_MobileGroup;
	String mobileGroup = null;
	int nStates = 2;
	String smartsStates[] = null;
	int mobileGroupPos[] = null;
	String RuleInfo = "";
	QueryAtomContainer statePaterns[] = null;
	
	
	public Vector<RuleInstance>  applyRule(IAtomContainer mol)
	{
		Vector<RuleInstance> instances = new Vector<RuleInstance>(); 
		return instances;
	}
		
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("TAUTOMER RULE\n");
		sb.append("NAME = " + name  + "\n");
		sb.append("TYPE = " + type  + "\n");
		sb.append("GROUP = " + mobileGroup  + "\n");
		return(sb.toString());
	}
}
