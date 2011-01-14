package ambit2.rest.task;

import java.util.UUID;

import org.restlet.data.Reference;

import ambit2.rest.task.dsl.ClientResourceWrapper;
import ambit2.rest.task.dsl.IAuthToken;

public abstract class CallableProtectedTask<USERID> implements ICallableTask, IAuthToken {

	protected UUID uuid;
	private USERID token;
	
	public CallableProtectedTask(USERID token) {
		this.token = token;
	}
	
	@Override
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public void setUuid(UUID uuid) {
		this.uuid = uuid;

	}

	@Override
	public String getToken() {
		return token==null?null:token.toString();
	}
	public abstract TaskResult doCall() throws Exception;
	
	@Override
	public TaskResult call() throws Exception {
		try {
			ClientResourceWrapper.setTokenFactory(this);
			return doCall();
		} catch (Exception x) {
			throw x;
		} finally {
			ClientResourceWrapper.setTokenFactory(null);
		}
	}

}
