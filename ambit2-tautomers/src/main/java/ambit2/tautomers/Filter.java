package ambit2.tautomers;

import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

public class Filter 
{
	public static final int FT_WARNING = 1;
	public static final int FT_EXCLUDE = 2;
	
	
	public String fragmentSmarts;
	public IQueryAtomContainer fragmentQuery;
	RuleStateFlags fragmentFlags;
	public int type; 
}
