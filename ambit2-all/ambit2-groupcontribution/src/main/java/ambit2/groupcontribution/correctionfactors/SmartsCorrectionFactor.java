package ambit2.groupcontribution.correctionfactors;

import org.openscience.cdk.interfaces.IAtomContainer;import ambit2.base.exceptions.EmptyMoleculeException;
import ambit2.groupcontribution.transformations.IValueTransformation;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.SmartsConst.SSM_MODE;
import ambit2.smarts.groups.GroupMatch;

public class SmartsCorrectionFactor implements ICorrectionFactor
{
	private String smarts = null;
	private double contribution = 0.0;
	private double sd = 0.0;
	private String designation = "";
	private IValueTransformation transformation = null;
	
	private GroupMatch groupMatch = null;
		
	public SmartsCorrectionFactor(String smarts, 
			SmartsParser parser, 
			IsomorphismTester isoTester) throws Exception
	{
		this.smarts = smarts;
		designation = "G(" + smarts + ")";
		groupMatch = new GroupMatch(smarts, parser, isoTester);		
	}	
	
	public SmartsCorrectionFactor(String smarts, 
			SmartsParser parser, 
			IsomorphismTester isoTester,
			IValueTransformation transformation) throws Exception
	{
		this(smarts, parser, isoTester);
		this.transformation = transformation;		
	}
	
	public SmartsCorrectionFactor(String smarts, 
			SmartsParser parser, 
			IsomorphismTester isoTester,
			SSM_MODE flagSSMode,
			IValueTransformation transformation) throws Exception
	{
		this(smarts, parser, isoTester);
		groupMatch.setFlagSSMode(flagSSMode);
		this.transformation = transformation;		
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
	public double getSD() {
		return sd;
	}
	
	@Override
	public void setSD(double sd) {
		this.sd = sd;
	}

	@Override
	public String getDesignation() {
		return designation;
	}

	@Override
	public double calculateFor(IAtomContainer mol)  
	{
		double matchCount = 0;
		try {
			matchCount = groupMatch.matchCount(mol);
		} catch (EmptyMoleculeException x) {
			return 0.0;
		}
		if (transformation == null)
			return matchCount;
		else
		{	
			try {
				double res = transformation.transform(matchCount);
				return res;
			}
			catch (Exception x) {
				//???
				return 0.0;
			}
		}		
	}

	@Override
	public Type getType() {
		return Type.SMARTS;
	}
		
	public IValueTransformation getValueTransformation()
	{
		return transformation;
	}
	
	public void setValueTransformation(IValueTransformation transformation)
	{
		this.transformation  = transformation;
	}
	
	public String getError()
	{
		return (groupMatch.getError());
	}

	public String getSmarts() {
		return smarts;
	}

}
