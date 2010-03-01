package ambit2.fastox.steps;

import ambit2.base.exceptions.AmbitException;

public class StepException extends AmbitException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5619657703848594581L;
	protected String key;
	protected String value;
	
	public String getKey() {
		return key;
	}

	public StepException(String key) {
		super();
		this.key = key;
	}

	public StepException(String key,String arg0) {
		super(arg0);
		this.key = key;
	}

	public StepException(String key,Throwable arg0) {
		super(arg0);
		this.key = key;
	}

	public StepException(String key,String arg0, Throwable arg1) {
		super(arg0, arg1);
		this.key = key;
	}
}
