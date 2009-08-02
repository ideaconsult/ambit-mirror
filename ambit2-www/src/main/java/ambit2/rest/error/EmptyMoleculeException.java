package ambit2.rest.error;

import ambit2.base.exceptions.AmbitException;

public class EmptyMoleculeException extends AmbitException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7714390627755560814L;
	public EmptyMoleculeException() {
		super("Empty molecule");
	}
}
