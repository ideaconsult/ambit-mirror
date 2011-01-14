package ambit2.rest.task;

import org.restlet.data.Reference;



public class TaskResult {
	public String uri;

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

	@Override
	public String toString() {
		return uri;
	}
}
