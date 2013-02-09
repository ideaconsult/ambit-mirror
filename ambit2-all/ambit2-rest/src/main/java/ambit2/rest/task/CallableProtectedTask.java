package ambit2.rest.task;

import java.util.UUID;
import java.util.logging.Logger;

import org.opentox.dsl.aa.IAuthToken;
import org.opentox.dsl.task.ClientResourceWrapper;

public abstract class CallableProtectedTask<USERID> implements ICallableTask, IAuthToken {
	protected static Logger logger = Logger.getLogger(CallableProtectedTask.class.getName());
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
