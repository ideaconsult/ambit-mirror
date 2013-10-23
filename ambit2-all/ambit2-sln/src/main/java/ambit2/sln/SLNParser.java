package ambit2.sln;


public class SLNParser 
{
	String sln;
	SLNContainer container;
	
	
	public SLNContainer parse(String sln)
	{
		this.sln = sln;
		container = new SLNContainer();
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
