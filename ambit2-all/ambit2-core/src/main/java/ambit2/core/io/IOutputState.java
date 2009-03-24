package ambit2.core.io;

import org.openscience.cdk.io.IChemObjectWriter;

import ambit2.base.exceptions.AmbitIOException;
import ambit2.base.interfaces.IInputOutputState;

/**
 * Status of the output writer. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public interface IOutputState extends IInputOutputState {
	IChemObjectWriter getWriter() throws AmbitIOException;
}
