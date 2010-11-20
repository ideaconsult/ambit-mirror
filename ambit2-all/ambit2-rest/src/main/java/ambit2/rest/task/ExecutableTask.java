package ambit2.rest.task;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.rest.task.Task.TaskStatus;

public class ExecutableTask<USERID> extends FutureTask<Reference> {
	protected Task<Reference,USERID> task;
	
	public Task<Reference, USERID> getTask() {
		return task;
	}
	public void setTask(Task<Reference, USERID> task) {
		this.task = task;
	}
	public ExecutableTask(Callable<Reference> callable,Task<Reference,USERID> task) {
		super(callable);
		this.task = task;
	}
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		task.setStatus(TaskStatus.Cancelled);
		task.setTimeCompleted(System.currentTimeMillis());
		task = null;
		return super.cancel(mayInterruptIfRunning);
	}
	@Override
	protected void done() {
		try {
			Reference ref = get(100, TimeUnit.MILLISECONDS);
			task.setTimeCompleted(System.currentTimeMillis());
			task.setStatus(TaskStatus.Completed);
			task.setUri(ref);

		} catch (TimeoutException x) {
		} catch (ExecutionException x) {
			task.setStatus(TaskStatus.Error);
			Throwable err = x.getCause()==null?x:x.getCause();
			if (err instanceof ResourceException) task.setError((ResourceException) err);
			else task.setError(new ResourceException(err));
		} catch (InterruptedException x) {
			task.setError(null);
			task.setStatus(TaskStatus.Cancelled);
		} catch (CancellationException x) {
			task.setError(null);
			task.setStatus(TaskStatus.Cancelled);
		}		
		task = null;
		super.done();
	}
	@Override
	protected void set(Reference v) {
		super.set(v);
		task.setUri(v);
	}
	@Override
	public void run() {
		task.setStatus(TaskStatus.Running);
		super.run();
	}
	
	@Override
	protected void setException(Throwable x) {
		super.setException(x);
		task.setStatus(TaskStatus.Error);
		Throwable err = x.getCause()==null?x:x.getCause();
		if (err instanceof ResourceException) task.setError((ResourceException) err);
		else task.setError(new ResourceException(err));
	}
	/*
	public synchronized void update()  {
		
		try {
			if (future!=null) {
				Reference ref = future.get(100, TimeUnit.MILLISECONDS);
				future = null;
				completed = System.currentTimeMillis();
				status = TaskStatus.Completed;
				setUri(ref);
			}
		} catch (TimeoutException x) {
		} catch (ExecutionException x) {
			Throwable err = x.getCause()==null?x:x.getCause();
			if (err instanceof ResourceException) error = (ResourceException) err;
			else error = new ResourceException(err);
			status = TaskStatus.Error;
		} catch (InterruptedException x) {
			error = null;
			status = TaskStatus.Cancelled;
		} catch (CancellationException x) {
			error = null;
			status = TaskStatus.Cancelled;
		}
	}
	*/
	@Override
	public String toString() {
		
		return super.toString();
	}
}
