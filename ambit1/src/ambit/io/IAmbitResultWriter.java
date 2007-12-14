package ambit.io;

import ambit.exceptions.AmbitException;
import ambit.processors.IAmbitResult;

/**
 * Writes {@link ambit.processors.IAmbitResult}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public interface IAmbitResultWriter {
	void writeResult(IAmbitResult result) throws AmbitException;
}
