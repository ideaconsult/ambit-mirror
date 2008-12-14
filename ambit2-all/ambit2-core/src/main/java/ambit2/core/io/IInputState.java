package ambit2.core.io;

import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.core.exceptions.AmbitIOException;
/**
 * Status of the input reader.  
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public interface IInputState extends IInputOutputState {
	IIteratingChemObjectReader getReader() throws AmbitIOException;
}
