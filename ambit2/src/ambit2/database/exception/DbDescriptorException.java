package ambit2.database.exception;

import ambit2.data.AmbitObject;
/**
 * Exception when processing descriptors {@link ambit2.data.descriptors.Descriptor} from database.
 * 

 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class DbDescriptorException extends DbAmbitException {

	public DbDescriptorException(AmbitObject object) {
		super(object);
		// TODO Auto-generated constructor stub
	}

	public DbDescriptorException(AmbitObject object, String arg0) {
		super(object, arg0);
		// TODO Auto-generated constructor stub
	}

	public DbDescriptorException(AmbitObject object, Throwable arg0) {
		super(object, arg0);
		// TODO Auto-generated constructor stub
	}

	public DbDescriptorException(AmbitObject object, String arg0, Throwable arg1) {
		super(object, arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
