package ambit2.reactions;

import java.util.ArrayList;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

public class FragmentationRule 
{
	enum Type {
		DISCONNECT_BOND
	}
	
	public String bondSmarts = "C-C";
	public Type ruleType = Type.DISCONNECT_BOND;
	
	public void applyRule(IAtomContainer container)
	{
		//TODO
	}
	
	public static void disconectBonds(IAtomContainer container, ArrayList<IBond> bonds)
	{
		for (IBond bo: bonds)
			container.removeBond(bo);
	}
	
	public static void disconectBonds(IAtomContainer container, int bondIndex[])
	{
		for (int i = 0; i < bondIndex.length; i++)
			container.removeBond(i);
	}	
}
