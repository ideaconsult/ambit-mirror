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
	
	public void configure(HAtomEnvironment hae)
	{
		//TODO
	}
	
}
