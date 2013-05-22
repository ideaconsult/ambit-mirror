package ambit2.reactions;

import java.util.ArrayList;

public class RetroSynthesisResult 
{
	public ArrayList<RetroSynthPath> paths = new ArrayList<RetroSynthPath>();
	public ArrayList<String> explanations = new ArrayList<String>();
	public ArrayList<String> log = new ArrayList<String>();
	
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < paths.size(); i++)
			sb.append(paths.get(i).toString() + "\n");
		
		return sb.toString();
	}
}
