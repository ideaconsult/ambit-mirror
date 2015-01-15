package ambit2.base.exceptions;

import ambit2.base.data.study.ProtocolApplication;

public class NoDocumentUUIDException extends Exception {
	protected ProtocolApplication papp;
	public ProtocolApplication getPapp() {
		return papp;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -6250930399927583056L;
	public NoDocumentUUIDException(ProtocolApplication papp) {
		super();
		this.papp = papp;
	}
}
