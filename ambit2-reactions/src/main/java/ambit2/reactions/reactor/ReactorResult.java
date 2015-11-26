package ambit2.reactions.reactor;

import java.util.ArrayList;
import java.util.List;

public class ReactorResult 
{
	public List<ReactorNode> successNodes = new ArrayList<ReactorNode>(); 
	public List<ReactorNode> failedNodes = new ArrayList<ReactorNode>();
	
	public int numSuccessNodes = 0;
	public int numFailedNodes = 0;
	public int numReactions = 0;
	public int numReactorNodes = 0;
	public int curMaxLevel = 0;
	
	public int logStep = 1;
	
	
	public String getStatusInfo()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" numSuccessNodes = " + numSuccessNodes);
		sb.append(" numFailedNodes = " + numFailedNodes);
		sb.append(" numReactions = " + numReactions);
		sb.append(" numReactorNodes = " + numReactorNodes);
		
		return sb.toString();
	}
}
