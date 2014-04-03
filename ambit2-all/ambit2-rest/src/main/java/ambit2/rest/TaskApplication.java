package ambit2.rest;

import java.util.concurrent.TimeUnit;

import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskApplication;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.ITaskStorage;

import org.restlet.Application;
import org.restlet.data.Reference;

import ambit2.rest.task.CallablePOST;
import ambit2.rest.task.TaskStorage;

public class TaskApplication<USERID> extends Application implements ITaskApplication<USERID>{
	protected ITaskStorage<USERID> taskStorage;
	public TaskApplication() {
		super();
		taskStorage = createTaskStorage();
	}
	
	protected TaskStorage<USERID> createTaskStorage() {
		return new TaskStorage<USERID>(getName(),getLogger());
	}
	
	public ITaskStorage<USERID> getTaskStorage() {
		return taskStorage;
	}

	public void setTaskStorage(ITaskStorage<USERID> taskStorage) {
		this.taskStorage = taskStorage;
	}
	/*
	public Iterator<Task<Reference,USERID>> getTasks() {
		return taskStorage.getTasks();
	}
	*/

	@Override
	protected void finalize() throws Throwable {
		taskStorage.removeTasks();
		super.finalize();
	}
	@Override
	public synchronized void stop() throws Exception {
		taskStorage.shutdown(1,TimeUnit.MILLISECONDS);
		super.stop();
	}
	
	
	public void removeTasks() {
		taskStorage.removeTasks();
	}
	@Override
	public ITask<ITaskResult,USERID> addTask(String taskName, ICallableTask callable, Reference baseReference, USERID user) {
		return addTask(taskName,callable,baseReference,!(callable instanceof CallablePOST), user);
	};
	
	public synchronized ITask<ITaskResult,USERID> addTask(String taskName, 
			ICallableTask callable, 
			Reference baseReference,boolean internal, USERID user) {
		return taskStorage.addTask(taskName,callable,baseReference,user,internal);
	}
	public synchronized ITask<ITaskResult,USERID> findTask(String id) {
		return taskStorage.findTask(id);
	}
	/*
	@Override
	public ApplicationInfo getApplicationInfo(Request request, Response response) {
	        ApplicationInfo result = super.getApplicationInfo(request, response);

	        DocumentationInfo docInfo = new DocumentationInfo(
	                "TaskApplication");
	        docInfo.setTitle("Task application");
	        result.setDocumentation(docInfo);

	        return result;
    }
    */
	
}
