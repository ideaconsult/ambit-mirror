package ambit2.core.io;

import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.core.exceptions.AmbitIOException;
/**
 * Status of the input reader. Used in {@link ambit2.test.io.batch.DefaultBatchProcessing}. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public interface IInputState extends IInputOutputState {
	IIteratingChemObjectReader getReader() throws AmbitIOException;
}
