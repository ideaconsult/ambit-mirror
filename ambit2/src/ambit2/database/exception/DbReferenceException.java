package ambit2.database.exception;

import ambit2.data.AmbitObject;

/**
 * Exceptions when processing literature references {@link ambit2.data.literature.LiteratureEntry} from database.

 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class DbReferenceException extends DbAmbitException {

	public DbReferenceException(AmbitObject object) {
		super(object);
		// TODO Auto-generated constructor stub
	}

	public DbReferenceException(AmbitObject object, String arg0) {
		super(object, arg0);
		// TODO Auto-generated constructor stub
	}

	public DbReferenceException(AmbitObject object, Throwable arg0) {
		super(object, arg0);
		// TODO Auto-generated constructor stub
	}

	public DbReferenceException(AmbitObject object, String arg0, Throwable arg1) {
		super(object, arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
