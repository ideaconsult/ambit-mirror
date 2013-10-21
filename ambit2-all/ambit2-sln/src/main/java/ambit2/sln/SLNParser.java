package ambit2.sln;

import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

public class SLNParser 
{
	String sln;
	IQueryAtomContainer container;
	
	
	public IQueryAtomContainer parse(String sln)
	{
		this.sln = sln;
		container = new QueryAtomContainer();
		init();
		parse();
		return container;
	}
	
	void init()
	{
		//TODO
	}
	
	void parse()
	{
		//TODO
	}
	
	
}
