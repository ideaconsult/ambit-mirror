package ambit2.tautomers;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomContainer;

public class TautomerRanking 
{
	public double T = 300; //K
	public static final double kB = 1.3806488E-23; //  [J/K]	
	public static final double KeVtoJ = 1.602176565E-19;    //1eV = 1.602176565e-19 J = 1.602176565 10-19 J
	public double energyConst = KeVtoJ/(T*kB);
	public double eShift = 0; //eV;
	
	
	public double getProbability(double rank)
	{
		double p = Math.exp(-(rank-eShift)*energyConst);
		return p;
	}
	
	public double[] getProbabilityDistribution(Vector<IAtomContainer> tautomers)
	{
		double distr[] = new double [tautomers.size()];
		double sum = 0;
		
		for (int i = 0; i < distr.length; i++)
		{	
			Double rank = (Double)tautomers.get(i).getProperty("TAUTOMER_RANK");
			if (rank == null)
				rank = new Double(9999);
			distr[i] = getProbability(rank);
			sum += distr[i];
		}
		
		if (sum > 0)
			for (int i = 0; i < distr.length; i++)
				distr[i] = distr[i] / sum;
				
		return distr;
	}
}
