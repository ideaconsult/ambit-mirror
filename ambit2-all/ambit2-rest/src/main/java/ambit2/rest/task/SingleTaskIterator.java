package ambit2.rest.task;

import java.util.Iterator;
import java.util.UUID;

/**
 * Iterator for a single task
 * @author nina
 *
 * @param <USERID>
 */
public class SingleTaskIterator<USERID> implements Iterator<UUID>  {
	protected UUID task;

	public SingleTaskIterator(UUID task) {
		this.task = task;
	}
	public boolean hasNext() {
		return task != null;
	}

	public UUID next() {
		UUID theTask = task;
		task = null;
		return theTask;
	}

	public void remove() {
	}
	
}