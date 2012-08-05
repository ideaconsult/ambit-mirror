package ambit2.waffles;

import ambit2.base.external.ShellException;

public class ShellWafflesCluster extends ShellWaffles {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4954738675605663899L;

	public ShellWafflesCluster() throws ShellException {
		super();
	}
	
	@Override
	protected void initialize() throws ShellException {
		this.tool= "waffles_cluster";
		initialize(tool);
	}
}
