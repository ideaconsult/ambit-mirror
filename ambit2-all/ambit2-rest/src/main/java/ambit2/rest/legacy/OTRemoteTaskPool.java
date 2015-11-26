package ambit2.rest.legacy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Deprecated
public class OTRemoteTaskPool {
	protected List<OTRemoteTask> pool = Collections.synchronizedList(new ArrayList<OTRemoteTask>());
	
	public OTRemoteTaskPool() {
		
	}
	public Iterable<OTRemoteTask> getTasks() {
		return pool;
	}
	public void add(OTRemoteTask task) {
		synchronized (pool) {
			pool.add(task);
		}
	}
	public int running() {
		int running = 0;
		synchronized (pool) {
			for (OTRemoteTask task : pool) 
				running += task.isDone()?0:1;			
		}

		return running;
	}
	public int poll() {
		int running = 0;
		synchronized (pool) {
		for (OTRemoteTask task : pool) { 
			running += task.poll()?0:1;
		}	
		
		}
		return running;
	}	
	public void clear() {
		pool.clear();
	}
	public void run() {
		run(500);
	}
	public void run(int sleepInterval) {
		try {
			OTFibonacciSequence seq = new OTFibonacciSequence();
			while (running()>0) {
				poll();

				Thread.sleep(seq.sleepInterval(sleepInterval,true,1000 * 60 * 5)); //
				Thread.yield();
				
			}	
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	public int size() {
		return pool==null?0:pool.size();
	}
	

}