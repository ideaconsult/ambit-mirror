package ambit2.rest.legacy;

@Deprecated
public class OTFibonacciSequence {
	protected int[] Fn = new int[]{0,1};
	protected int n = 0;
	
	public OTFibonacciSequence() {
	}
	
	public int next() {
		int result = Fn[n];
		Fn[n] = Fn[0]+Fn[1];
		n = (n+1) % 2;
		return result;
	}
	/**
	 * Calculates sleep interval
	 * @param milliseconds
	 * @param randomize
	 * @param msResetThreshold
	 * @return milliseconds * next() ; if randomized Math.random()*milliseconds * next() ; 
	 * if result > resetThreshold, resets the Fibonacci sequence
	 */
	public synchronized long sleepInterval(long milliseconds,boolean randomize, int msResetThreshold) {
		double next = next() * milliseconds;
		if (randomize) next = next * Math.random();
		if (next > msResetThreshold) {
			Fn[0] = 0; Fn[1] = 1;
			n = 0;
		}
		return (long) next;
		
	}
}
