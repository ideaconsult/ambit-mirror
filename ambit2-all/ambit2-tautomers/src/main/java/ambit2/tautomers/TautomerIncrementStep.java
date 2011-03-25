package ambit2.tautomers;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomContainer;

public class TautomerIncrementStep 
{
	Vector<IRuleInstance> usedRuleInstances = new Vector<IRuleInstance>();
	Vector<IRuleInstance> unUsedRuleInstances = new Vector<IRuleInstance>();
		
	//Struct container is a virtual molecule which describes the current state of the 
	//structure at this incremental step
	//It contains all atoms/bonds from the original molecule that are not part of the used rule instances
	//The other atoms/bonds are clones of the original atoms
	IAtomContainer struct;
	
	
}
