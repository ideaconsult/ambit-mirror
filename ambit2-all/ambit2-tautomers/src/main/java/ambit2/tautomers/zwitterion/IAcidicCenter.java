package ambit2.tautomers.zwitterion;

public interface IAcidicCenter 
{
	public static enum State {
		NEUTRAL, ANION
	}
	
	public State getState();
	public void setState(State state);
	public void shiftState();
	
}
