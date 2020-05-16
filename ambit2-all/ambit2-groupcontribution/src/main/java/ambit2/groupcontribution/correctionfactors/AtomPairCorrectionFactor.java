package ambit2.groupcontribution.correctionfactors;

import org.openscience.cdk.interfaces.IAtomContainer;
import ambit2.groupcontribution.transformations.IValueTransformation;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.groups.GroupMatch;

public class AtomPairCorrectionFactor implements ICorrectionFactor
{
	private String smarts1 = null;
	private String smarts2 = null;
	private int distance = -1;
	
	private double contribution = 0.0;
	private double sd = 0.0;
	private String designation = "";
	private IValueTransformation transformation = null;
	
	private GroupMatch groupMatch1 = null;
	private GroupMatch groupMatch2 = null;
	
	public AtomPairCorrectionFactor(String smarts1, String smarts2, 
			int distance,
			SmartsParser parser,
			IsomorphismTester isoTester) throws Exception
	{
		this.smarts1 = smarts1;
		this.distance = distance;
		designation = "AP(" + smarts1 + "," + smarts2 + 
				((distance>=0)?(""+distance):"") + ")";
		groupMatch1 = new GroupMatch(smarts1, parser, isoTester);
		groupMatch2 = new GroupMatch(smarts2, parser, isoTester);
	}
	
	public AtomPairCorrectionFactor(String smarts1, String smarts2, 
			SmartsParser parser,			
			IsomorphismTester isoTester) throws Exception
	{
		this(smarts1, smarts2, -1, parser, isoTester);
	}
	
	public AtomPairCorrectionFactor(String smarts1, String smarts2, 
			int distance,
			SmartsParser parser,
			IsomorphismTester isoTester,
			IValueTransformation transformation) throws Exception
	{
		this(smarts1, smarts2, distance, parser, isoTester);
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
		//TODO
		return 0;
	}

	@Override
	public Type getType() {
		return Type.ATOM_PAIR;
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
