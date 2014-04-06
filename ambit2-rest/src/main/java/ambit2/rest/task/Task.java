package ambit2.rest.task;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.TaskStatus;

import org.restlet.resource.ResourceException;

import ambit2.base.json.JSONUtils;

public abstract class Task<REFERENCE,USERID> implements Serializable,ITask<REFERENCE,USERID> /*, PropertyChangeListener */ {

	public enum TaskProperty {
		//this is not really used, to be removed
		PROPERTY_NAME {
			@Override
			public void update(Task task,Object value) {
				if (value!=null) task.setName(value.toString());
			}
		};
		/*
		PROPERTY_PERCENT {
			@Override
			public void update(Task task,Object value) {
				try {
				if (value!=null) task.setPercentCompleted(Float.parseFloat(value.toString()));
				} catch (Exception x) {}
			}			
		};
		*/
		public abstract void update(Task task,Object value);
	}
 
	/**
	 * 
	 */
	private static final long serialVersionUID = -646087833848914553L;

	
	//protected FutureTask<Reference> future;
	protected REFERENCE result;
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
	public long getTimeCompleted() {
		return completed;
	}
	public void setTimeCompleted(long completed) {
		this.completed = completed;
	}
	protected float percentCompleted = 0;
	protected USERID userid;
	protected UUID uuid = UUID.randomUUID();
	protected ResourceException error = null;
	protected Exception policyError = null;
	
	public Exception getPolicyError() {
		return policyError;
	}
	public void setPolicyError(Exception policyError) {
		this.policyError = policyError;
	}
	public void setError(ResourceException error) {
		this.error = error;
	}
	public ResourceException getError() {
		return error;
	}
	protected TaskStatus status= TaskStatus.Queued;
	
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
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
	public TaskStatus getStatus() {
		return status;

	}
	public abstract float getPercentCompleted();
	
	//public abstract void setPercentCompleted(float percentCompleted);
	
	public long getStarted() {
		return started;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public synchronized REFERENCE getUri() {
		return result;
	}
	public synchronized void setUri(REFERENCE uri) {
		this.result = uri;
	}
	
	public Task(USERID user) {
		
		this.userid = user;
	}


	public boolean isDone() {
		return TaskStatus.Completed.equals(status) || TaskStatus.Error.equals(status)  || TaskStatus.Cancelled.equals(status);
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
	/*
	public void propertyChange(PropertyChangeEvent evt) {
		try {
			TaskProperty p = TaskProperty.valueOf(evt.getPropertyName());
			p.update(this, evt.getNewValue());
		} catch (Exception x) {
			
		}
		
	}
	*/
	/**
	* does nothing, should be autoupdated by ExecutableTask
	 */
	public synchronized void update()  {

	}
	/**
	 * Does nothing so far, but should register the result URI to the policy server
	 */
	public void setPolicy() throws Exception {
		
	}
	private static String format = "\n{\n\t\"uri\":\"%s\",\n\t\"id\": \"%s\",\n\t\"name\": \"%s\",\n\t\"error\": \"%s\",\n\t\"policyError\": \"%s\",\n\t\"status\": \"%s\",\n\t\"started\": %d,\n\t\"completed\": %d,\n\t\"result\": \"%s\",\n\t\"user\": \"%s\"\n}";
	
	@Override
	public String toJSON() {
			String uri = getUri()==null?null:getUri().toString();
			return String.format(format,
					uri,
					toString(),
					getName()==null?"":JSONUtils.jsonEscape(getName()),
					getError()==null?"":JSONUtils.jsonEscape(getError().toString()),
					getPolicyError()==null?"":JSONUtils.jsonEscape(getPolicyError().toString()),
					getStatus()==null?"":getStatus(),
					getStarted(),
					getTimeCompleted(),
					getUri()==null?"":JSONUtils.jsonEscape(getUri().toString()),
					getUserid()==null?"":getUserid()
					);
	}
}
