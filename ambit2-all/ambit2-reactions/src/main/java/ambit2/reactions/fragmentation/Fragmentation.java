package ambit2.reactions.fragmentation;

import java.util.ArrayList;

import org.openscience.cdk.interfaces.IAtomContainer;


public class Fragmentation 
{	
	
	private ArrayList<FragmentationRule> fragmenationRules = null;
	
	public Fragmentation()
	{	
		setFragmenationRules(DefaultFragmentationRules.getRules());
	}
	
	public Fragmentation(ArrayList<FragmentationRule> fragmenationRules)
	{	
		this.fragmenationRules = fragmenationRules;
	}
	
	public void setFragmenationRules(ArrayList<FragmentationRule> fragmenationRules)
	{
		this.fragmenationRules = fragmenationRules;
	}
	
	public ArrayList<FragmentationRule> getFragmenationRules()
	{
		return fragmenationRules;
	}
	
	public void apply(IAtomContainer container)
	{
		//TODO
	}
		
	
	
}
