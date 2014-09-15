package ambit2.tautomers;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

public class TautomerRanking 
{
	private double T = 300; //K
	public static final double kB = 1.3806488E-23; //  [J/K]	
	public static final double KeVtoJ = 1.602176565E-19;    //1eV = 1.602176565e-19 J = 1.602176565 10-19 J
	public double energyConst;
	public double eShift = 0; //eV;
	
	
	public TautomerRanking()
	{	
		setT(300);
	}
	
	public TautomerRanking(double temperature)
	{	
		setT(temperature);  
	}
	
	public double getProbability(double rank)
	{
		double p = Math.exp(-(rank-eShift)*energyConst);
		return p;
	}
	
	public void setT(double T)
	{
		this.T = T;
		energyConst = KeVtoJ/(T*kB);
	}
	
	public double getT()
	{
		return T;
	}
	
	
	public double[] getProbabilityDistribution(List<IAtomContainer> tautomers)
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
