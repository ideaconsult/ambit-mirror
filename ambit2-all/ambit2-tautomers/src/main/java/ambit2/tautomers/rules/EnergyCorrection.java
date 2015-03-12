package ambit2.tautomers.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnergyCorrection 
{
	public String correctionName = null;
	public double energy = 0.0;
	public HashMap<Integer, AtomCondition> atomConditions = new HashMap<Integer, AtomCondition>();
	
	public List<String> errors = null;
	
	void addError(String err)
	{
		if (errors == null)
			errors = new ArrayList<String>();
		errors.add(err);
	}
}
