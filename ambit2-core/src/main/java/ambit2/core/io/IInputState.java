package ambit2.core.io;

import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.exceptions.AmbitIOException;
import ambit2.base.interfaces.IInputOutputState;
/**
 * Status of the input reader.  
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public interface IInputState extends IInputOutputState {
	IIteratingChemObjectReader getReader() throws AmbitIOException;
	public IChemFormat getFileFormat() ;
}
