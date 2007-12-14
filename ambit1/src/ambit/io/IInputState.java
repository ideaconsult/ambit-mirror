package ambit.io;

import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.exceptions.AmbitIOException;
/**
 * Status of the input reader. Used in {@link ambit.io.batch.DefaultBatchProcessing}. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public interface IInputState extends IInputOutputState {
	IIteratingChemObjectReader getReader() throws AmbitIOException;
}
