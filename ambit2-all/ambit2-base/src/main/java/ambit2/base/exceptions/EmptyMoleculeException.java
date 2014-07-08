package ambit2.base.exceptions;

import net.idea.modbcum.i.exceptions.AmbitException;


public class EmptyMoleculeException extends AmbitException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7714390627755560814L;
	public EmptyMoleculeException() {
		super("Empty molecule");
	}
}
