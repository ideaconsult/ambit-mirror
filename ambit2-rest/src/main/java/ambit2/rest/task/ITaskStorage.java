package ambit2.rest.task;

import java.util.Iterator;
import java.util.concurrent.Callable;

import org.restlet.data.Reference;

public interface ITaskStorage<USERID> {
	Iterator<Task<Reference,USERID>> getTasks();
	void removeTasks();
	Task<Reference,USERID> addTask(String taskName, Callable<Reference> callable, Reference baseReference,USERID user, boolean internal);
	Task<Reference,USERID> findTask(String id);
}
