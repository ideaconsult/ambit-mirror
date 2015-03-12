package ambit2.tautomers.rules;

import java.util.ArrayList;
import java.util.List;

public class EnergyRule 
{	
	public List<String> errors = null;
	public String ruleName = null;
	public String id = null;
	
	public int state = 0;
	public String stateInfo = null;
	public double stateEnergy = 0;
	
	public List<EnergyCorrection> energyCorrections = new ArrayList<EnergyCorrection>();
	
	
	void addError(String err)
	{
		if (errors == null)
			errors = new ArrayList<String>();
		errors.add(err);
	}
	
}
