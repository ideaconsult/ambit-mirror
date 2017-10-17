package ambit2.groupcontribution.correctionfactors;


public class DescriptorInfo 
{
	double contribution = 0.0;
	String name = "";
	
	public double getContribution()
	{
		return contribution; 
	}
	
	public void setContribution(double contribution)
	{
		this.contribution = contribution;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	//public int calculateFor(IAtomContainer mol);
	//public Type getType();
}
