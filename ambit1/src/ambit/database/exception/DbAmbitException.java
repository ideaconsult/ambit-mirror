package ambit.database.exception;

import ambit.data.AmbitObject;
import ambit.exceptions.AmbitException;

/**
 * Generic database exception
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class DbAmbitException extends AmbitException {
	protected AmbitObject object;
	public DbAmbitException(AmbitObject object) {
		super();
		this.object = object;
	}

	public DbAmbitException(AmbitObject object,String arg0) {
		super(arg0);
		this.object = object;
	}

	public DbAmbitException(AmbitObject object,Throwable arg0) {
		super(arg0);
		this.object = object;
	}

	public DbAmbitException(AmbitObject object,String arg0, Throwable arg1) {
		super(arg0, arg1);
		this.object = object;
	}

	public AmbitObject getObject() {
		return object;
	}
	public String getMessage() {
		if (object != null)
			return super.getMessage() + object.toString();
		else
			return super.getMessage();
		
	}
}
