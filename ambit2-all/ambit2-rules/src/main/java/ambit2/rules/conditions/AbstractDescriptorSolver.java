package ambit2.rules.conditions;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDescriptorSolver implements IDescriptorSolver 
{
	protected ArrayList<String> descriptorList = new ArrayList<String>();
	
	@Override
	public List<String> getDescriptorList() {
		return descriptorList;
	}

	@Override
	public boolean isDescriptorSupported(String descrName) {
		return descriptorList.contains(descrName);
	}
	
	public void addDescriptor(String descrName){
		if (!descriptorList.contains(descrName))
			descriptorList.add(descrName);
	}
}
