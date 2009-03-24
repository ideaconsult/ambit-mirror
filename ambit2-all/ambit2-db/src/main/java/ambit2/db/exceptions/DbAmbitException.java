package ambit2.db.exceptions;

import ambit2.base.exceptions.AmbitException;


/**
 * Generic database exception
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class DbAmbitException extends AmbitException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -310682833246433157L;
	protected Object object;
	public DbAmbitException(Object object) {
		super();
		this.object = object;
	}

	public DbAmbitException(Object object,String arg0) {
		super(arg0);
		this.object = object;
	}

	public DbAmbitException(Object object,Throwable arg0) {
		super(arg0);
		this.object = object;
	}

	public DbAmbitException(Object object,String arg0, Throwable arg1) {
		super(arg0, arg1);
		this.object = object;
	}

	public Object getObject() {
		return object;
	}
	public String getMessage() {
		if (object != null)
			return super.getMessage() + object.toString();
		else
			return super.getMessage();
		
	}
}
