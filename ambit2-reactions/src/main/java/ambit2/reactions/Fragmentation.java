package ambit2.reactions;

import java.util.ArrayList;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtomContainer;



public class Fragmentation 
{	
	public static final String defaultFragmenationRules[] = new String[]{
		"[*;R]-[*;!R]"
	};
	
	ArrayList<String> fragmenationRules = null;
	
	public Fragmentation()
	{	
	}
	
	public void setFragmenationRules(ArrayList<String> fragmenationRules)
	{
		this.fragmenationRules = fragmenationRules;
	}
	
	public ArrayList<String> getFragmenationRules()
	{
		return fragmenationRules;
	}
		
	public void disconectBonds(IAtomContainer container, ArrayList<IBond> bonds)
	{
		for (IBond bo: bonds)
			container.removeBond(bo);
	}
	
	public void disconectBonds(IAtomContainer container, int bondIndex[])
	{
		for (int i = 0; i < bondIndex.length; i++)
			container.removeBond(i);
	}
	
}
