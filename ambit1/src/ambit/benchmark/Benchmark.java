/**
 * Created on 2004-11-5
 *
 */
package ambit.benchmark;

import java.io.PrintStream;

/**
 * A simple benchmarking facilities 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class Benchmark {
	private long base_time;
	private long elapsed_time;

	private static final long UNIT = 1000;

	/**
	 * 
	 */
	public Benchmark() {
		clear();
	}
	public void mark() {
		base_time = System.currentTimeMillis();
	}

	public void clear() {
		elapsed_time = 0;
	}

	public void record() {
		elapsed_time += (System.currentTimeMillis() - base_time);
	}

	public float elapsed() {
		return ((float) elapsed_time) / UNIT;
	}

	public void report(PrintStream pstream) {
		float elapsed_seconds = elapsed();
		pstream.println("Time " + elapsed_seconds + " sec");
	}
	public void report() {
		report(System.out);
	}
	public long getElapsedTime() {
		return elapsed_time;
	}

}
