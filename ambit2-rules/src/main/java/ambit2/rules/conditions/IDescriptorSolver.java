package ambit2.rules.conditions;

import java.util.List;

public interface IDescriptorSolver 
{
	public List<String> getDescriptorList();
	public boolean isDescriptorSupported(String descrName);
	public Object calculateDescriptor(String descrName, Object target);
}
