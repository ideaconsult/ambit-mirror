package ambit2.swing.interfaces;



/**
 * A class to provide application status.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public class JobStatus extends DefaultStatus {
	protected int persentage = 0;
	protected String message = "";
	protected Object job = null;
	protected boolean indeterminated = true;
	public JobStatus() {
		super();
	}
	/**
	 * 
	 * @return percent of completed task
	 */
	public int getPersentage() {
		return persentage;
	}
	/**
	 * @param persentage completed task
	 */
	public void setPersentage(int persentage) {
		this.persentage = persentage;
		setChanged();
		notifyObservers();		
	}
	/**
	 * 
	 * @return String representation of the status
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * 
	 * @param message sets String representation of the status
	 */
	public void setMessage(String message) {
		this.message = message;
		setChanged();
		notifyObservers(message);		
	}
	/**
	 * 
	 * @return current job. Generally this is {@link ambit.io.batch.IBatch}
	 */
	public Object getJob() {
		return job;
	}
	/**
	 * @param job  Generally this is {@link ambit.io.batch.IBatch}
	 */
	public void setJob(Object job) {
		this.job = job;
		setChanged();
		notifyObservers(job);				
	}
	/**
	 * 
	 * @return true if the length is indeterminated
	 */
	public boolean isIndeterminated() {
		return indeterminated;
	}
	/**
	 * 
	 * @param indeterminated  true if we can't determine the length of the task
	 */
	public void setIndeterminated(boolean indeterminated) {
		this.indeterminated = indeterminated;
	}

}
