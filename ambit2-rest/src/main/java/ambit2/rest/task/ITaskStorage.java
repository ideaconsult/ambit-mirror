package ambit2.rest.task;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.restlet.data.Reference;

public interface ITaskStorage<USERID> {
	Iterator<UUID> getTasks();
	void removeTasks();
	Task<Reference,USERID> addTask(String taskName, Callable<Reference> callable, Reference baseReference,USERID user, boolean internal);
	Task<Reference,USERID> findTask(String id);
	Task<Reference,USERID> findTask(UUID id);
	void shutdown(long timeout,TimeUnit unit) throws Exception;
}
