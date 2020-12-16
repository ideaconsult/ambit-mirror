package ambit2.sln.io;


public class SLN2ChemObjectConfig 
{
	
	public static enum ComparisonConversion {
		omit,
		convert_as_equal,
		convert_as_equal_if_not_nonequality,
		convert_as_equal_if_eqaul_is_present,
		convert_as_value_list;
		
		public static ComparisonConversion fromString(String text) {
	        for (ComparisonConversion  x : ComparisonConversion.values()) {
	            if (x.name().equalsIgnoreCase(text)) {
	                return x;
	            }
	        }
	        return null;
	    }
	}
	
	
	public ComparisonConversion FlagComparisonConversion = ComparisonConversion.omit;
	
	//Limit values for atom/bond attributes 
	//used for converting in equality expressions
	public int maxCharge = 4;
	public int minCharge = -4;
	public int maxHCount = 4;
	public int minHCount = 0;
	
	//For SMARTS conversion
	public boolean FlagSupportSingleBondAromaticityNotSpecified = false;
	public boolean FlagSupportDoubleBondAromaticityNotSpecified = false;
	
}
