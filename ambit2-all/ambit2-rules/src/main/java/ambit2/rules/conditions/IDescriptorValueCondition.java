package ambit2.rules.conditions;

public interface IDescriptorValueCondition extends IValueCondition 
{
	public boolean isTrue(Object target);
	public void setDescriptorSolver(IDescriptorSolver solver);
	public IDescriptorSolver getDescriptorSolver();
	public void setDescriptorName(String name);
	public String getDescriptorName();
}
