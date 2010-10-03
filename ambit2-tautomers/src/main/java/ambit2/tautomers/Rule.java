package ambit2.tautomers;

import org.openscience.cdk.interfaces.IAtomContainer;
import java.util.Vector;


public class Rule 
{
	String name;
	String RuleDescription = "";
	int type = TautomerConst.RT_MobileGroup;
	String mobileGroup;
	int nStates = 2;
	String smatrsStates[];
	IAtomContainer fragmentStates[];
	int mobileGroupPos[];
	
	public Vector<RuleInstance>  applyRule(IAtomContainer mol)
	{
		Vector<RuleInstance> instances = new Vector<RuleInstance>(); 
		return instances;
	}
		
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		return(sb.toString());
	}
}
