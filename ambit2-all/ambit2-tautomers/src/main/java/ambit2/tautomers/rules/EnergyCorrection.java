package ambit2.tautomers.rules;

import java.util.HashMap;

public class EnergyCorrection 
{
	public String correctionName = null;
	public double energy = 0.0;
	public HashMap<Integer, AtomCondition> atomConditiions = new HashMap<Integer, AtomCondition>();
}
