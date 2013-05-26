package ambit2.reactions;

import java.util.ArrayList;
import org.openscience.cdk.interfaces.IAtomContainer;

public interface IRetroSynthRuleInstance 
{	
	public ArrayList<IAtomContainer> apply(IAtomContainer mol);
}
