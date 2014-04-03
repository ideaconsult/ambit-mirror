package ambit2.rest.task;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.TaskStatus;

import org.restlet.resource.ResourceException;

public class ExecutableTask<USERID> extends FutureTask<ITaskResult> {
	protected ITask<ITaskResult,USERID> task;
	
	public ITask<ITaskResult, USERID> getTask() {
		return task;
	}
	public void setTask(ITask<ITaskResult, USERID> task) {
		this.task = task;
	}
	public ExecutableTask(Callable<ITaskResult> callable,ITask<ITaskResult,USERID> task) {
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
			ITaskResult ref = get(100, TimeUnit.MILLISECONDS);
			task.setTimeCompleted(System.currentTimeMillis());
			task.setStatus(TaskStatus.Completed);
			task.setUri(ref);
			
			try {
				task.setPolicy();
			} catch (Exception x) {
				task.setPolicyError(x);
			}
			
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
	protected void set(ITaskResult v) {
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
