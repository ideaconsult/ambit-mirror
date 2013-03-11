package ambit2.waffles.learn;

import ambit2.base.external.ShellException;
import ambit2.waffles.ShellWafflesLearn;
import ambit2.waffles.learn.options.WafflesLearnAlgorithm;

public class WafflesLearnDecisionTree  extends ShellWafflesLearn {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1001072451077379406L;

	/**
	 * 
	 */


	public WafflesLearnDecisionTree() throws ShellException {
		super();
		this.algorithm = WafflesLearnAlgorithm.decisiontree;
	}

}