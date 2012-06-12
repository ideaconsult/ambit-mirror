package ambit2.rest.task;

import org.restlet.data.Reference;



public class TaskResult {
	protected float percentCompleted = 0;
	protected String uri;
	protected boolean newResource = true;

	public boolean isNewResource() {
		return newResource;
	}
	public void setNewResource(boolean newResource) {
		this.newResource = newResource;
	}
	public String getUri() {
		return uri;
	}
	public Reference getReference() {
		return new Reference(uri);
	}
	public void setUri(String uri) {
		this.uri = uri;
	}

	public TaskResult(String uri) {
		setUri(uri);
	}
	public TaskResult(String uri,boolean newResource) {
		setUri(uri);
		setNewResource(newResource);
	}
	public float getPercentCompleted() {
		return percentCompleted;
	}
	public void setPercentCompleted(float percentCompleted) {
		this.percentCompleted = percentCompleted;
	}

	@Override
	public String toString() {
		return uri;
	}
}
