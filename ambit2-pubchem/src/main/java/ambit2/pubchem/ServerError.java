package ambit2.pubchem;

import ambit2.base.exceptions.AmbitException;

public class ServerError extends AmbitException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2702476035421353328L;
	protected int status = 500;
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
