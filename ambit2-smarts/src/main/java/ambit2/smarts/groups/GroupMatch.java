package ambit2.smarts.groups;

public class GroupMatch 
{
	String smarts = null;
	String error = null;
	
	public GroupMatch(String smarts)
	{
		this.smarts = smarts;
		configure();
	}
	
	void configure()
	{
		//TODO
	}
	
	public String getError()
	{
		return error;
	}
}
