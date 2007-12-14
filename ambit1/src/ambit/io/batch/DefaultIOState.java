package ambit.io.batch;

import ambit.io.IInputOutputState;

public class DefaultIOState implements IInputOutputState {
	protected long currentRecord;
	protected String responseType = "text/plain";
	public DefaultIOState() {
		super();
		currentRecord = 0;
	}

	public void setCurrentRecord(long currentRecord) {
		this.currentRecord = currentRecord;

	}

	public long getCurrentRecord() {
		return currentRecord;
	}
    public String getResponseType() {
    	return responseType;
    }
    public void setResponseType(String responseType) {
    	this.responseType = responseType;
    }

}
