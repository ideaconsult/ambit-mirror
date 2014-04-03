package ambit2.rest.task;

import java.util.Iterator;
import java.util.UUID;

import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.ITaskStorage;

public class FilteredTasksIterator<USERID> implements Iterator<UUID> {
	protected Iterator<UUID> keys;
	protected UUID task_id;
	protected int num;
	protected ITaskStorage<USERID> storage ;
	
	public FilteredTasksIterator(ITaskStorage<USERID> storage) {
		this.storage = storage;
		this.keys =  storage.getTasks();
		num = 0;
	}
	public boolean hasNext() {
		
		while (keys.hasNext()) {
			task_id = keys.next();
			if (accepted(storage.findTask(task_id))) {
				num++;
				return true;
			}
		}
		task_id = null;
		return false;
	}

	public int getNum() {
		return num;
	}

	public UUID next() {
		return task_id;
	}

	public void remove() {
	}
	protected boolean accepted(ITask<ITaskResult,USERID> task) {
		return task==null?false:true;
	}
	
}
