package ambit2.reactions.fragmentation;

import java.util.ArrayList;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import ambit2.smarts.IsomorphismTester;

public class FragmentationRule 
{
	enum Type {
		DISCONNECT_BOND
	}
	
	public String smarts = "C-C";
	public IQueryAtomContainer query = null;
	public Type ruleType = Type.DISCONNECT_BOND;
	public IsomorphismTester isoTester = null;
	
	public FragmentationRule()
	{		
	}
	
	public FragmentationRule(String smarts, IQueryAtomContainer query, IsomorphismTester isoTester)
	{
		ruleType = Type.DISCONNECT_BOND;
		this.isoTester = isoTester;
		this.query = query;
	}
	
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
