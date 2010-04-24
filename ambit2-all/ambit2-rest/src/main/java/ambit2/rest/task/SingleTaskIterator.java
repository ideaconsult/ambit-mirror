package ambit2.rest.task;

import java.util.Iterator;

import org.restlet.data.Reference;

/**
 * Iterator for a single task
 * @author nina
 *
 * @param <USERID>
 */
public class SingleTaskIterator<USERID> implements Iterator<Task<Reference,USERID>>  {
	protected Task<Reference,USERID> task;
	
	public SingleTaskIterator(Task<Reference,USERID> task) {
		this.task = task;
	}
	public boolean hasNext() {
		return task != null;
	}

	public Task<Reference, USERID> next() {
		Task<Reference,USERID> theTask = task;
		task = null;
		return theTask;
	}

	public void remove() {
	}
	
}