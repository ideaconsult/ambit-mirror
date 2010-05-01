package ambit2.rest.task;

import java.util.Iterator;

import org.restlet.data.Reference;

public class FilteredTasksIterator<USERID> implements Iterator<Task<Reference,USERID>> {
	protected Iterator<Task<Reference,USERID>> tasks;
	protected Task<Reference, USERID> task;
	protected int num;
	
	public FilteredTasksIterator(ITaskStorage<USERID> tasks) {
		this.tasks = tasks.getTasks();
		num = 0;
	}
	public boolean hasNext() {

		while (tasks.hasNext()) {
			task = tasks.next();
			if (accepted(task)) {
				num++;
				return true;
			}
		}
		task = null;
		return false;
	}

	public int getNum() {
		return num;
	}

	public Task<Reference, USERID> next() {
		return task;
	}

	public void remove() {
	}
	protected boolean accepted(Task<Reference,USERID> task) {
		return task==null?false:true;
	}
	
}
