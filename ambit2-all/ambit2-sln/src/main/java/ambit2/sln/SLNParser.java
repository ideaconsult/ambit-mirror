package ambit2.sln;


public class SLNParser 
{
	String sln;
	SLNContainer container;
	SLNDictionary globalDictionary = null;
	
	
	public SLNParser()
	{
		globalDictionary = PredefinedSLNDictionary.getDictionary();
	}
	
	public SLNParser(SLNDictionary globalDictionary)
	{
		this.globalDictionary = globalDictionary;
	}
	
	
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
