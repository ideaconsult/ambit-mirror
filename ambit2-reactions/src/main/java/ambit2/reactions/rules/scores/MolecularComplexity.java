package ambit2.reactions.rules.scores;

import org.openscience.cdk.interfaces.IAtomContainer;

public class MolecularComplexity 
{
	protected IAtomContainer target = null;
	protected AtomComplexity atomComplexityCalculator = new AtomComplexity();
	
	
	public IAtomContainer getTarget() {
		return target;
	}

	public void setTarget(IAtomContainer target) {
		this.target = target;
	}

	public AtomComplexity getAtomComplexityCalculator() {
		return atomComplexityCalculator;
	}

	public void setAtomComplexityCalculator(AtomComplexity atomComplexityCalculator) {
		this.atomComplexityCalculator = atomComplexityCalculator;
	}

	/**
	 * Proudfood paper, CM = sum(CA_i) 
	 * @return
	 */
	public double calcMolecularComplexity01()
	{
		return 0.0;
	}
	
	/**
	 * Proudfood paper, CM* = log(sum(2^CA_i))
	 * @return
	 */
	public double calcMolecularComplexity02()
	{
		return 0.0;
	}
}
