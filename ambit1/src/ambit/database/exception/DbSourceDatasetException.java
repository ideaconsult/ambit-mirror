package ambit.database.exception;

import ambit.data.AmbitObject;

/**
 * Exceptions when  experimental dataset origin {@link ambit.data.molecule.SourceDataset} from database.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class DbSourceDatasetException extends DbAmbitException {

	public DbSourceDatasetException(AmbitObject object) {
		super(object);
		// TODO Auto-generated constructor stub
	}

	public DbSourceDatasetException(AmbitObject object, String arg0) {
		super(object, arg0);
		// TODO Auto-generated constructor stub
	}

	public DbSourceDatasetException(AmbitObject object, Throwable arg0) {
		super(object, arg0);
		// TODO Auto-generated constructor stub
	}

	public DbSourceDatasetException(AmbitObject object, String arg0,
			Throwable arg1) {
		super(object, arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
