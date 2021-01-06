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
	
	
	public ComparisonConversion FlagComparisonConversion = ComparisonConversion.convert_as_equal;
	
	//Limit values for atom/bond attributes 
	//used for converting in equality expressions
	public int min_charge = -4;
	public int max_charge = 4;
	public int min_hac = 0;
	public int max_hac = 6;
	public int min_hc = 0;
	public int max_hc = 4;
	public int min_htc = 0;
	public int max_htc = 6;
	public int min_ntc = 0;
	public int max_ntc = 4;
	public int min_tac = 0;
	public int max_tac = 6;
	public int min_tbo = 0;
	public int max_tbo = 6;
	public int min_rbc = 0;
	public int max_rbc = 4;
	public int min_src = 0;
	public int max_src = 4;
		
	
	//For SMARTS conversion
	public boolean FlagSLNSingleBondToSingleOrAromaticBond = true;
	public boolean FlagSupportSingleBondAromaticityNotSpecified = false;
	public boolean FlagSupportDoubleBondAromaticityNotSpecified = false;
	
	public boolean FlagSmartsSupportMOEExtension = true;
	public boolean FlagSmartsUseMOEvPrimitive = false;
	public boolean FlagSmartsSupportOpenEyeExtension = true;
	public boolean FlagSmartsSupportOpenBabelExtension = true;
	
}
