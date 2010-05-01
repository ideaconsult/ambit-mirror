package ambit2.rest;

import java.util.Iterator;
import java.util.concurrent.Callable;

import org.restlet.Application;
import org.restlet.data.Reference;

import ambit2.rest.task.CallablePOST;
import ambit2.rest.task.ITaskStorage;
import ambit2.rest.task.Task;
import ambit2.rest.task.TaskStorage;

public class TaskApplication<USERID> extends Application {
	protected ITaskStorage<USERID> taskStorage;
	public TaskApplication() {
		super();
		taskStorage = new TaskStorage<USERID>(getName(),getLogger());
	}
	
	public ITaskStorage<USERID> getTaskStorage() {
		return taskStorage;
	}

	public void setTaskStorage(ITaskStorage<USERID> taskStorage) {
		this.taskStorage = taskStorage;
	}

	public Iterator<Task<Reference,USERID>> getTasks() {
		return taskStorage.getTasks();
	}

	@Override
	protected void finalize() throws Throwable {
		taskStorage.removeTasks();
		super.finalize();
	}
	
	public void removeTasks() {
		taskStorage.removeTasks();
	}

	public synchronized Task<Reference,USERID> addTask(String taskName, 
			Callable<Reference> callable, 
			Reference baseReference) {
		return addTask(taskName,callable,baseReference,!(callable instanceof CallablePOST));
	}
	
	public synchronized Task<Reference,USERID> addTask(String taskName, 
			Callable<Reference> callable, 
			Reference baseReference,boolean internal) {
		return taskStorage.addTask(taskName,callable,baseReference,(USERID) "guest",internal);
	}
	public synchronized Task<Reference,USERID> findTask(String id) {
		return taskStorage.findTask(id);
	}
	
}
