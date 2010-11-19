package ambit2.rest.task;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.restlet.resource.ResourceException;

public class Task<Reference,USERID> implements Serializable, PropertyChangeListener {
	public enum TaskProperty {
		PROPERTY_NAME {
			@Override
			public void update(Task task,Object value) {
				if (value!=null) task.setName(value.toString());
			}
		},
		PROPERTY_PERCENT {
			@Override
			public void update(Task task,Object value) {
				try {
				if (value!=null) task.setPercentCompleted(Float.parseFloat(value.toString()));
				} catch (Exception x) {}
			}			
		};
		public abstract void update(Task task,Object value);
	}
 
	/**
	 * 
	 */
	private static final long serialVersionUID = -646087833848914553L;

	public enum TaskStatus {Running,Cancelled,Completed,Error};
	protected FutureTask<Reference> future;
	protected Reference uri;
	protected String name = "Default";
	protected long started = System.currentTimeMillis();
	protected long completed = -1;
	protected boolean internal = true;
	public boolean isInternal() {
		return internal;
	}
	public void setInternal(boolean internal) {
		this.internal = internal;
	}
	public long getCompleted() {
		return completed;
	}
	protected float percentCompleted = 0;
	protected USERID userid;
	protected UUID uuid = UUID.randomUUID();
	protected ResourceException error = null;
	public ResourceException getError() {
		return error;
	}
	protected TaskStatus status= TaskStatus.Running;
	
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	public USERID getUserid() {
		return userid;
	}
	public void setUserid(USERID userid) {
		this.userid = userid;
	}
	public boolean isExpired(long lifetime) {
		return (System.currentTimeMillis()-started) > lifetime;
	}
	public String getStatus() {
		return status.toString();

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
	
	public Task(Callable<Reference> callable,USERID user) {
		
		this.userid = user;
		this.future = new FutureTask<Reference>(callable) {
			@Override
			public void run() {
				status = TaskStatus.Running;
				super.run();
			}
			@Override
			protected void done() {
				super.done();
				completed = System.currentTimeMillis();
				update();
			}
			
		};
		/*
		this.future = new FutureTask<Reference>(callable) {
			@Override
			protected void done() {
				//status = TaskStatus.Completed;
				super.done();
				update();
			}
			
			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				boolean ok = super.cancel(mayInterruptIfRunning);
				update();
				status = TaskStatus.Cancelled;
				return ok;
			}
			@Override
			public void run() {
				status = TaskStatus.Running;
				super.run();
			}
			
		};
	*/
	}
	

	public FutureTask<Reference> getFuture() {
		return future;
	}
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
	public boolean isDone() {
		return TaskStatus.Completed.equals(status) || TaskStatus.Error.equals(status);
	}
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (getFuture()!=null) return getFuture().cancel(mayInterruptIfRunning);
		else return false;
	}

	@Override
	public String toString() {
		try {
		return String.format("%s [%s] Started %s Completed %s [%s] [%s] %s",
				name==null?"?":name,
				getUri(),
				new Date(started),
				((completed>0)?new Date(completed):"-"),
				getStatus(),
				userid,
				error==null?"":error.getMessage()
				);
		} catch (Exception x) {
			return x.getMessage();
		}
	}
	public void propertyChange(PropertyChangeEvent evt) {
		try {
			TaskProperty p = TaskProperty.valueOf(evt.getPropertyName());
			p.update(this, evt.getNewValue());
		} catch (Exception x) {
			
		}
		
	}
}
