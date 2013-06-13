package ambit2.reactions;

import java.util.ArrayList;

public class RetroSynthesisResult 
{
	public ArrayList<RetroSynthPath> paths = new ArrayList<RetroSynthPath>();
	
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < paths.size(); i++)
			sb.append("Path #" + i + "\n" + paths.get(i).toString() + "\n");
		
		return sb.toString();
	}
}
