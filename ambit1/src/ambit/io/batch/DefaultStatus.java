package ambit.io.batch;

import java.util.Observable;

/**
 * Default implementation of {@link ambit.io.batch.IJobStatus}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public class DefaultStatus extends Observable implements IJobStatus {
	protected int status;
	protected boolean modified;
	protected Exception error = null;
	
	public DefaultStatus() {
		super();
		setStatus(STATUS_NOTSTARTED);
		modified = true;
	}

	public boolean isRunning() {
		return status == STATUS_RUNNING;
	}

	public boolean isPaused() {
		return status == STATUS_PAUSED;
	}

	public boolean isCancelled() {
		return status == STATUS_ABORTED;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		if (this.status != status) {
			this.status = status;
			if (status == STATUS_RUNNING)
				error = null;
			setChanged();
			notifyObservers();
		}

	}
	public String toString() {
		return statusMsg[status];
	}
	public boolean isStatus(int status) {
		return this.status == status;
	}
	/* (non-Javadoc)
     * @see ambit.io.batch.IJobStatus#isModified()
     */
    public boolean isModified() {
        return modified;
    }
    /* (non-Javadoc)
     * @see ambit.io.batch.IJobStatus#setModified(boolean)
     */
    public void setModified(boolean value) {
        if (modified != value) {
            modified = value;
            setChanged();
            notifyObservers();
        }
    }

	public Exception getError() {
		return error;
	}

	public void setError(Exception x) {
		this.error = x;
        setChanged();
        notifyObservers();		
	}
}
