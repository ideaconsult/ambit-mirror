package ambit2.swing.interfaces;

import java.io.Serializable;

/**
 * 
 * Interface for job status
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public interface IJobStatus extends Serializable {
	public final static int STATUS_NOTSTARTED = 0;
	public final static int STATUS_RUNNING = 1;
	public final static int STATUS_DONE = 2;
	public final static int STATUS_PAUSED = 3;
	public final static int STATUS_ABORTED = 4;
	public final static int STATUS_NOTINITIALIZED = 5;
	public static String[] statusMsg = {
		"Batch processing not started", "Batch running",
		"Batch processing finished", "Batch processing paused",
		"Batch processing aborted", "Batch processing not configured" };

	public boolean isRunning();
	public boolean isPaused();
	public boolean isCancelled();
	public boolean isStatus(int status);
	public int getStatus();
	public void setStatus(int status);
	public String toString();
	public boolean isModified();
	public void setModified(boolean value);
	
	Exception getError();
	void setError(Exception x);
}
