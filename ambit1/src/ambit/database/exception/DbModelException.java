package ambit.database.exception;

import ambit.data.AmbitObject;
/**
 * Exceptions when processing models {@link ambit.data.model.Model} from database.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class DbModelException extends DbAmbitException {

	public DbModelException(AmbitObject object) {
		super(object);
		// TODO Auto-generated constructor stub
	}

	public DbModelException(AmbitObject object, String arg0) {
		super(object, arg0);
		// TODO Auto-generated constructor stub
	}

	public DbModelException(AmbitObject object, Throwable arg0) {
		super(object, arg0);
		// TODO Auto-generated constructor stub
	}

	public DbModelException(AmbitObject object, String arg0, Throwable arg1) {
		super(object, arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
