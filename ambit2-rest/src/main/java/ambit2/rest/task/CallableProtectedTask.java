package ambit2.rest.task;

import java.util.UUID;
import java.util.logging.Logger;

import net.idea.restnet.c.task.ClientResourceWrapper;
import net.idea.restnet.i.aa.IAuthToken;
import net.idea.restnet.i.task.ICallableTask;

public abstract class CallableProtectedTask<USERID> implements ICallableTask, IAuthToken {
	protected static Logger logger = Logger.getLogger(CallableProtectedTask.class.getName());
	protected UUID uuid;
	private USERID token;
	
	protected String logFile;

	
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
		logFile = String.format("%s/task_%s.log", System.getProperty("java.io.tmpdir"), uuid.toString());
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
	@Override
	public String getTaskCategory() {
		return null;
	}


	


}
