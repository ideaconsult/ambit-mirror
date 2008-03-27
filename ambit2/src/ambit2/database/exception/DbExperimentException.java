package ambit2.database.exception;

import ambit2.data.AmbitObject;

/**
 * Exceptions when processing experimental {@link ambit2.data.experiment.Experiment} data from database.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class DbExperimentException extends DbAmbitException {

	public DbExperimentException(AmbitObject object) {
		super(object);
		// TODO Auto-generated constructor stub
	}

	public DbExperimentException(AmbitObject object, String arg0) {
		super(object, arg0);
		// TODO Auto-generated constructor stub
	}

	public DbExperimentException(AmbitObject object, Throwable arg0) {
		super(object, arg0);
		// TODO Auto-generated constructor stub
	}

	public DbExperimentException(AmbitObject object, String arg0, Throwable arg1) {
		super(object, arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
