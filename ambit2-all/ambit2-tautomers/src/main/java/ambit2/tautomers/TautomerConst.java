package ambit2.tautomers;

public class TautomerConst 
{	
	public static final String KeyWordPrefix = "$$";
	public static final String KeyWordSeparator = "=";
	public static final String KeyWordElementSeparator = ",";
	
	
	//Generation status
	public static final int STATUS_NONE = 0;
	public static final int STATUS_SET_STRUCTURE = 1;
	public static final int STATUS_STARTED = 2;
	public static final int STATUS_STOPPED = 5;
	public static final int STATUS_FINISHED = 10;
	
	
	//Rule Types
	public static final int RT_MobileGroup = 0;
	public static final int RT_RingChain = 1;
	
	//H-Atom Rule Modes
	public static final int HRM_Implicit = 0;
	public static final int HRM_Explicit = 1;
	public static final int HRM_Undefined = 2;
	
	//Generation Algorithm Types
	public static final int GAT_Comb_Pure = 0;
	public static final int GAT_Incremental = 1;
	public static final int GAT_Comb_Improved = 2; //improved combinatorial approach (a set of combinations)
	
	//Rule Selection Method
	public static final int RSM_ALL = 0;
	public static final int RSM_NONE = 1;
	public static final int RSM_RANDOM = 2;
	public static final int RSM_RANDOM_NON_OVERLAPPING = 3;
	public static final int RSM_RANDOM_OPTIMAL_DENSITY = 4;
	
	//Rule selection order
	public static final int RULE_ORDER_NONE = 0;
	public static final int RULE_ORDER_BY_SIZE = 1;
	
	
	
	public static final int IHA_INDEX = 1000000;
	
	
	
}
