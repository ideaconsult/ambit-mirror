package ambit.io;

import org.openscience.cdk.io.IChemObjectWriter;

import ambit.exceptions.AmbitIOException;

/**
 * Status of the output writer. Used in {@link ambit.io.batch.DefaultBatchProcessing}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public interface IOutputState extends IInputOutputState {
	IChemObjectWriter getWriter() throws AmbitIOException;
}
