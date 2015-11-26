package ambit2.rules.test;

import java.util.ArrayList;

import ambit2.rules.conditions.AbstractDescriptorSolver;


/**
 * This class is intended inly for testing purposes!
 * @author nick
 */

public class DescriptorSolver0 extends AbstractDescriptorSolver 
{
	protected ArrayList<Double> descriptorValues = new ArrayList<Double>();
	
	public void addDescriptor(String descrName, Double d){
		if (!descriptorList.contains(descrName))
		{	
			descriptorList.add(descrName);
			descriptorValues.add(d);
		}	
	}
	
	@Override
	public Object calculateDescriptor(String descrName, Object target) {
		for (int i = 0; i < descriptorList.size(); i++)
		{	
			if (descriptorList.get(i).equals(descrName))
				return descriptorValues.get(i);
		}	
		return null;
	}
}
