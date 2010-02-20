package ambit2.rest.task;

import java.util.ArrayList;
import java.util.List;

public class RemoteTaskPool {
	protected List<RemoteTask> pool = new ArrayList<RemoteTask>();
	public RemoteTaskPool() {
		
	}
	public void add(RemoteTask task) {
		pool.add(task);
	}
	public int running() {
		int running = 0;
		for (RemoteTask task : pool) 
			running += task.isDone()?0:1;
		return running;
	}
	public int poll() {
		int running = 0;
		for (RemoteTask task : pool) 
			running += task.poll()?0:1;
		return running;
	}	
	public void clear() {
		pool.clear();
	}
	public void run() {
		try {
			while (running()>0) {
				poll();
				for (RemoteTask task : pool) System.out.println(task); 
				this.wait(5000); //5 sec
			}	
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}