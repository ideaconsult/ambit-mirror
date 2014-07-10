package ambit2.groupcontribution.correctionfactors;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface ICorrectionFactor 
{
	public enum Type{
		PREDEFINED, ATOM_PAIR, SMARTS
	}
	   
	public double getContribution();
	public void setContribution(double contribution);
	public String getDesignation();	
	public int calculateFor(IAtomContainer mol);
	public Type getType();
}
