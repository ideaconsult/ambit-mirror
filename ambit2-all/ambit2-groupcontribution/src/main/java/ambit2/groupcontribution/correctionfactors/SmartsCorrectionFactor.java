package ambit2.groupcontribution.correctionfactors;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.groupcontribution.transformations.IValueTransformation;

public class SmartsCorrectionFactor implements ICorrectionFactor
{
	private String smarts = null;
	private double contribution = 0.0;
	private String designation = "";
	private IValueTransformation transformation = null;
	
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
		
	public IValueTransformation getValueTransformation()
	{
		return transformation;
	}
	
	public void setValueTransformation(IValueTransformation transformation)
	{
		this.transformation  = transformation;
	}

}
