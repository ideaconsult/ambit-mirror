package ambit2.tautomers;

public class TautomerConst {
	public static final String TAUTOMER_RANK = "TAUTOMER_RANK";
	public static final String CACTVS_ENERGY_RANK = "CACTVS_ENERGY_RANK";
	public static final String KeyWordPrefix = "$$";
	public static final String KeyWordSeparator = "=";
	public static final String KeyWordElementSeparator = ",";

	// Generation status
	public static final int STATUS_NONE = 0;
	public static final int STATUS_SET_STRUCTURE = 1;
	public static final int STATUS_STARTED = 2;
	public static final int STATUS_STOPPED = 5;
	public static final int STATUS_FINISHED = 10;

	// Rule Types
	public static final int RT_MobileGroup = 0;
	public static final int RT_RingChain = 1;

	// H-Atom Rule Modes
	public static final int HRM_Implicit = 0;
	public static final int HRM_Explicit = 1;
	public static final int HRM_Undefined = 2;

	// Generation Algorithm Types
	public enum GAT {
		Comb_Pure {
			@Override
			public String getShortName() {
				return "comb";
			}
		},
		Incremental {
			@Override
			public String getShortName() {
				return "incr";
			}
		},
		Comb_Improved {
			public String getShortName() {
				return "icomb";
			}
		};
		public abstract String getShortName();
	}

	/*
	 * public static final int GAT_Comb_Pure = 0; public static final int
	 * GAT_Incremental = 1; public static final int GAT_Comb_Improved = 2;
	 * //improved combinatorial approach (a set of combinations)
	 */

	// Rule Selection Method
	public enum RSM {
		ALL,
		NONE,
		RANDOM,
		RANDOM_NON_OVERLAPPING,
		RANDOM_OPTIMAL_DENSITY;
		public String getShortName() {
			return name().toLowerCase();
		}
	}
	
	// Canonical Tautomer Method
	public enum CanonicTautomerMethod {		
		ENERGY_RANK_INCHI_KEY,
		CACTVS_RANK_INCHI_KEY
	}

	// Rule selection order
	public static final int RULE_ORDER_NONE = 0;
	public static final int RULE_ORDER_BY_SIZE = 1;

	public static final int IHA_INDEX = 1000000;

	// Energy ranking method
	public static final int ERM_OLD = 0;
	public static final int ERM_NEW = 1;

}
