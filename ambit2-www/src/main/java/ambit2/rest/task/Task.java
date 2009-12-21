package ambit2.rest.task;

import java.io.Serializable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Task<Reference> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -646087833848914553L;

	public enum taskStatus {Running,Cancelled,Completed};
	protected Future<Reference> future;
	protected Reference uri;
	protected String name = "Default";
	protected long started = System.currentTimeMillis();
	protected float percentCompleted = 0;
	
	public String getStatus() {
		return isDone()?taskStatus.Completed.toString():taskStatus.Running.toString();
	}
	public float getPercentCompleted() {
		return percentCompleted;
	}
	public void setPercentCompleted(float percentCompleted) {
		this.percentCompleted = percentCompleted;
	}
	public long getStarted() {
		return started;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Reference getUri() {
		return uri;
	}
	public void setUri(Reference uri) {
		this.uri = uri;
	}
	public Task(Future<Reference> future) {
		this.future = future;
	}
	public Future<Reference> getFuture() {
		return future;
	}
	public Reference getReference() throws ExecutionException , InterruptedException, CancellationException {
		try {
			if (future!=null) {
				Reference ref = future.get(100, TimeUnit.MILLISECONDS);
				future = null;
				setUri(ref);
			}
			return uri; 
		} catch (TimeoutException x) {
			return uri;
		}
	}
	public boolean isDone() {
		return getFuture()==null?true:getFuture().isDone();
	}
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (getFuture()!=null) return getFuture().cancel(mayInterruptIfRunning);
		else return false;
	}

	@Override
	public String toString() {
		try {
		return String.format("%s",getReference().toString());
		} catch (Exception x) {
			return x.getMessage();
		}
	}
}
