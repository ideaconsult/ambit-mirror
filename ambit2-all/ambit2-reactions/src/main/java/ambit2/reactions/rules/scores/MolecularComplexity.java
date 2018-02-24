package ambit2.reactions.rules.scores;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.TetrahedralChirality;

public class MolecularComplexity 
{
	protected IAtomContainer target = null;
	protected AtomComplexity atomComplexityCalculator = new AtomComplexity();
	protected List<Double> atomComplexities = new ArrayList<Double>();
	
	public IAtomContainer getTarget() {
		return target;
	}

	public void setTarget(IAtomContainer target) {
		this.target = target;
		nullify();
	}

	public AtomComplexity getAtomComplexityCalculator() {
		return atomComplexityCalculator;
	}

	public void setAtomComplexityCalculator(AtomComplexity atomComplexityCalculator) {
		this.atomComplexityCalculator = atomComplexityCalculator;
	}
	
	public List<Double> getAtomComplexities() {
		return atomComplexities;
	}

	public void setAtomComplexities(List<Double> atomComplexities) {
		this.atomComplexities = atomComplexities;
	}
	
	void nullify()
	{
		atomComplexities = null;
	}

	/**
	 * Proudfood paper, CM = sum{CA_i} 
	 * @return
	 */
	public double calcMolecularComplexity01()
	{
		if (target == null)
			return 0.0;
		calcAtomComplexities();
		double cM = 0.0;
		for (int i = 0; i < atomComplexities.size(); i++)
			cM += atomComplexities.get(i);
		return cM;
	}
	
	/**
	 * Proudfood paper, CM* = log(sum{2^CA_i})
	 * @return
	 */
	public double calcMolecularComplexity02()
	{
		if (target == null)
			return 0.0;
		calcAtomComplexities();
		double cM2 = 0.0;
		for (int i = 0; i < atomComplexities.size(); i++)
			cM2 += Math.pow(2,atomComplexities.get(i));
		return ChemComplexityUtils.log2(cM2);
	}
	
	void calcAtomComplexities()
	{	
		if (atomComplexities != null)
			return;
		atomComplexities = new ArrayList<Double>();
		for (int i = 0; i < target.getAtomCount(); i++)
		{
			double c = atomComplexityCalculator.calcAtomComplexity(target.getAtom(i), target);
			atomComplexities.add(c);
		}
	}
	
	
	public static int numberOfStereoElements(IAtomContainer mol)
	{	
		int n = 0;
		for (IStereoElement element : mol.stereoElements())
			n++;
		return n;
	}
	
	public static double weightedNumberOfStereoElements(IAtomContainer mol)
	{	
		double n = 0;
		for (IStereoElement element : mol.stereoElements())
		{	
			if (element instanceof DoubleBondStereochemistry)
				n+=0.3;
			else
				n+=1.0;
		}	
		return n;
	}
	
	public static int numberOfTetrahedralStereoElements(IAtomContainer mol)
	{	
		int n = 0;
		for (IStereoElement element : mol.stereoElements())
			if (element instanceof TetrahedralChirality)
				n++;
		return n;
	}
	
}
