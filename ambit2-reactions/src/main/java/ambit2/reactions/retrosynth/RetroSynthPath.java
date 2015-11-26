package ambit2.reactions.retrosynth;

import java.util.ArrayList;

public class RetroSynthPath 
{
	public ArrayList<RetroSynthStep> steps = new ArrayList<RetroSynthStep>();
	
	public double getSyntPathScore()
	{
		//TODO
		return 0.0;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();	
		for (int i = 0; i < steps.size(); i++)
			sb.append("step " + i + "\n" + steps.get(i).toString() + "\n");
		return sb.toString();
	}
}
