package ambit2.waffles.learn;

import ambit2.base.external.ShellException;
import ambit2.waffles.ShellWafflesLearn;
import ambit2.waffles.learn.options.WafflesLearnAlgorithm;

public class WafflesLearnRandomForest extends ShellWafflesLearn {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2904715887567191667L;

	public WafflesLearnRandomForest() throws ShellException {
		super();
		this.algorithm = WafflesLearnAlgorithm.randomforest;
	}

}
