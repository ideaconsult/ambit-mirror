package ambit2.tautomers;

public interface IRuleInstance 
{
	public int firstState();
	public int nextState();	
	public int getNumberOfStates();
	public int getCurrentState();
	public Rule getRule();
	
}
