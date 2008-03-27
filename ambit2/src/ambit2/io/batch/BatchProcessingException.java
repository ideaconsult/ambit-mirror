/*
 * Created on 2005-9-4
 *
 */
package ambit2.io.batch;

import ambit2.exceptions.AmbitIOException;

/**
 * An {@link java.lang.Exception} to be used in {@link ambit2.io.batch.IBatch}
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-9-4
 */
public class BatchProcessingException extends AmbitIOException {
	protected IBatch batch = null;

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 6837815972092687580L;

	/**
	 * 
	 */
	public BatchProcessingException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public BatchProcessingException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	public BatchProcessingException(String message, IBatch batch) {
		super(message);
		this.batch = batch;
	}
	public BatchProcessingException(Throwable cause, IBatch batch) {
		super(cause);
		this.batch = batch;
	}
	/**
	 * @param cause
	 */
	public BatchProcessingException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BatchProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
	public BatchProcessingException(String message, Throwable cause, IBatch batch) {
		super(message, cause);
		this.batch = batch;
	}

}
