package ambit2.tautomers.zwitterion;

public interface IBasicCenter extends ICenter
{	
	public static enum State {
		NEUTRAL, CATION
	}
	
	public State getState();
	public void setState(State state);
	public void shiftState();
}
