package ambit2.rest.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RemoteTaskPool {
	protected List<RemoteTask> pool = Collections.synchronizedList(new ArrayList<RemoteTask>());

	public RemoteTaskPool() {
		
	}
	public Iterable<RemoteTask> getTasks() {
		return pool;
	}
	public void add(RemoteTask task) {
		synchronized (pool) {
			pool.add(task);
		}
	}
	public int running() {
		int running = 0;
		synchronized (pool) {
			for (RemoteTask task : pool) 
				running += task.isDone()?0:1;			
		}

		return running;
	}
	public int poll() {
		int running = 0;
		synchronized (pool) {
		for (RemoteTask task : pool) 
			running += task.poll()?0:1;
		}
		return running;
	}	
	public void clear() {
		pool.clear();
	}
	public void run() {
		try {
			while (running()>0) {
				poll();
				//for (RemoteTask task : pool) System.out.println(task);
				Thread.sleep(1500);
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