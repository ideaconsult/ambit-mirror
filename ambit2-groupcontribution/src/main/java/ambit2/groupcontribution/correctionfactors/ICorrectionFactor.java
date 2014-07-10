package ambit2.groupcontribution.correctionfactors;


public interface ICorrectionFactor 
{
	public enum Type{
		PREDEFINED, ATOM_PAIR, SMARTS
	}
	   
	public double getContribution();
	public void setContribution(double contribution);
	public String getDesignation();		
	public Type getType();
	
}
