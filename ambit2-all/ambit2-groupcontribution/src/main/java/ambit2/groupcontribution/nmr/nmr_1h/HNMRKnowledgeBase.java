package ambit2.groupcontribution.nmr.nmr_1h;

import java.util.ArrayList;
import java.util.List;

public class HNMRKnowledgeBase 
{
	public List<HAtomEnvironment> hAtomEnvironments = new ArrayList<HAtomEnvironment>();
	
	public List<String> errors = new ArrayList<String>();
	
	public HNMRKnowledgeBase()
	{	
	}
	
	public void configure()
	{
		for (int i = 0; i < hAtomEnvironments.size(); i++)
		{
			HAtomEnvironment hae = hAtomEnvironments.get(i);
			configure(hae);
		}
	}
	
	protected void configure(HAtomEnvironment hae)
	{
		//TODO
	}
	
	public String getAllErrorsAsString() 
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < errors.size(); i++)
			sb.append(errors.get(i) + "\n");
		return sb.toString();
	}
	
}
