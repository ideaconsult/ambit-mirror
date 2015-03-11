package ambit2.tautomers.rules;

import java.util.ArrayList;
import java.util.List;

public class EnergyRule 
{	
	public String error = null;	
	public String ruleName = null;
	public String id = null;
	
	public int state = 0;
	public double stateEnergy = 0;
	
	public List<EnergyCorrection> energyCorrections = new ArrayList<EnergyCorrection>();
	
}
