package ambit2.waffles;

import ambit2.base.external.ShellException;

public class ShellWafflesRecommend extends ShellWaffles {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5534148450656324493L;
	public ShellWafflesRecommend() throws ShellException {
		super();
	}
	@Override
	protected void initialize() throws ShellException {
		this.tool= "waffles_recommend";
		initialize(tool);
	}
}
