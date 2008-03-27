package ambit2.io;

import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitResult;

/**
 * Writes {@link ambit2.processors.IAmbitResult}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public interface IAmbitResultWriter {
	void writeResult(IAmbitResult result) throws AmbitException;
}
