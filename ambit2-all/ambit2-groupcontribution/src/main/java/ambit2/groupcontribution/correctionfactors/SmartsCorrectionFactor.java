package ambit2.groupcontribution.correctionfactors;

import org.openscience.cdk.interfaces.IAtomContainer;

public class SmartsCorrectionFactor implements ICorrectionFactor
{
	private String smarts = null;
	private double contribution = 0.0;
	private String designation = "";
	
	public SmartsCorrectionFactor(String smarts)
	{
		this.smarts = smarts;
	}
	
	public void configure()
	{
		//TODO
	}
	
	@Override
	public double getContribution() {
		return contribution;
	}

	@Override
	public void setContribution(double contribution) {
		this.contribution = contribution;
	}

	@Override
	public String getDesignation() {
		return designation;
	}

	@Override
	public double calculateFor(IAtomContainer mol) {
		// TODO 
		return 0.0;
	}

	@Override
	public Type getType() {
		// TODO
		return null;
	}

}
