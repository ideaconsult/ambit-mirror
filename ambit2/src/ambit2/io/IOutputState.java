package ambit2.io;

import org.openscience.cdk.io.IChemObjectWriter;

import ambit2.exceptions.AmbitIOException;

/**
 * Status of the output writer. Used in {@link ambit2.io.batch.DefaultBatchProcessing}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public interface IOutputState extends IInputOutputState {
	IChemObjectWriter getWriter() throws AmbitIOException;
}
