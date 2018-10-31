package ambit2.tautomers.zwitterion;



public class SulfinicGroup implements IAcidicCenter 
{
	State state = State.NEUTRAL;
	
	@Override
	public State getState() {
		return state;
	}
	
	@Override
	public void setState(State state) {
		this.state = state;
		// TODO 		
	}
	
	@Override
	public void shiftState() {
		//TODO 
	}
}
