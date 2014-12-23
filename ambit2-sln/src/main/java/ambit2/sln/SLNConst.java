package ambit2.sln;

public class SLNConst
{	

	//Logical operations
	//	public static char LogOperationChars[] = {'!', '&', '|', ';'};
	public static final int LO_NOT = 0;
	public static final int LO_AND = 1;
	public static final int LO_OR = 2;
	public static final int LO_ANDLO = 3;
	public static final int LO = 1000;


	//Atom Attributes (predefined)   A_ATTR_*	
	public static final int A_ATTR_charge = 1;	 // specifies formal charge -/+n
	public static final int A_ATTR_I = 2;		 // specifies atom isotope
	public static final int A_ATTR_fcharge = 3;	// specifies a partial charge
	public static final int A_ATTR_s = 4; 		//specifies stereo-chemistry at tetrahedral atoms 
	public static final int A_ATTR_spin = 5;	//specifies that the atom is a radical 	
	public static final int A_ATTR_USER_DEFINED = 500; //  == other
	//boolean  - the attribute's name is specified, as in C[backbone]
	//valued - the name is specified, followed by an equal sign and the value, as in C-[chemshift=7.2]

	//Query Atom Attributes used only in query QA_ATTR_*
	public static final int QA_ATTR_mapNum = 100; // #1,#2,... map number to an atom "a"
	public static final int QA_ATTR_c = 101; // control how atoms in the target are matched "convered"  by pattern atoms "a"
	public static final int QA_ATTR_f = 102; // boolean attribute indicating that the atom is filled "a"
	public static final int QA_ATTR_is = 103; // Specifies patterns that the qualified atom in a query must match if it is to match the query atom "a"
	public static final int QA_ATTR_n = 104; // atom that matches a query atom from being covered "a"
	public static final int QA_ATTR_not = 105; // Specifies patterns that the qualified atom in a query must not match "a"
	public static final int QA_ATTR_r = 106;// Boolean attribute used to specify that a bond or atom is in a ring "a"
	public static final int QA_ATTR_v = 107; // Conveys Markush and macro atom valence information
	public static final int QA_ATTR_hac = 108; // heavy atom count "b"
	public static final int QA_ATTR_hc = 109; // hydrogen count "b"
	public static final int QA_ATTR_htc = 110; // hetero atom count "b"
	public static final int QA_ATTR_ntc = 111; // number of nonterminal atoms "b"
	public static final int QA_ATTR_tac = 113; // total number of atoms attached to the qualified atom "b"
	public static final int QA_ATTR_tbo = 114; // total bond order of an atom "b"
	public static final int QA_ATTR_rbc = 115; // ring bond count "b"
	public static final int QA_ATTR_src = 116; // the smallest ring count "b"
	public static final int QA_ATTR_mw = 117; // The molecular weight attribute is used with group atoms (R, X, and Rx)to specify the cumulative atomic weights that atoms that match the group atom to match the query "b"
	

	public static String atomAttributeToSLNString(int attr)
	{
		switch (attr)
		{
		case A_ATTR_charge:
			return "charge";
		case A_ATTR_I:
			return "I";
		case A_ATTR_fcharge:
			return "fcharge";
		case A_ATTR_s:
			return "s";
		case A_ATTR_spin:
			return "spin";

		case A_ATTR_USER_DEFINED:
			return "userDef";

		case QA_ATTR_mapNum:
			return "#";
		case QA_ATTR_c:
			return "c";
		case QA_ATTR_f:
			return "f";	
		case QA_ATTR_is:
			return "is";
		case QA_ATTR_n:
			return "n";
		case QA_ATTR_not:
			return "not";
		case QA_ATTR_r:
			return "r";
		case QA_ATTR_v:
			return "v";
		case QA_ATTR_hac:
			return "hac";
		case QA_ATTR_hc:
			return "hc";
		case QA_ATTR_htc:
			return "htc";
		case QA_ATTR_ntc:
			return "ntc";
		case QA_ATTR_tac:
			return "tac";
		case QA_ATTR_tbo:
			return "tbo";
		case QA_ATTR_rbc:
			return "rbc";
		case QA_ATTR_src:
			return "src";
		case QA_ATTR_mw:
			return "mw";

		default:
			return "";
		}
	}

	//Atom stereo-chemistry values (for attribute s)
	public static final int A_STEREO_R = 0; // CIP configurations
	public static final int A_STEREO_S = 1; // CIP configurations
	public static final int A_STEREO_N = 2; // normal with respect to atom ID sequence
	public static final int A_STEREO_I = 3; // inverted with respect to atom ID sequence
	public static final int A_STEREO_D = 4; // relative to glyceraldehyde
	public static final int A_STEREO_L = 5; // relative to glyceraldehyde
	public static final int A_STEREO_U = 6; // unknown
	public static final int A_STEREO_E = 7; // explicit
	public static final int A_STEREO_Rel = 8; // relative == * 
	public static final int A_STEREO_M = 9; // mixture
	public static final int A_STEREO_RE = 10; //absolute stereochemistry CIP configurations
	public static final int A_STEREO_SE = 11; //absolute stereochemistry CIP configurations
	public static final int A_STEREO_DE = 12; //Absolute stereochemistry is known and specified based on similarity of chiral center to D-glyceraldehyde.
	public static final int A_STEREO_LE = 13; //Absolute stereochemistry is known and specified based on similarity of chiral center to D-glyceraldehyde.
	public static final int A_STEREO_NE = 14; //Absolute stereochemistry is known and specified based on ordering of atoms in the SLN
	public static final int A_STEREO_IE = 15; //Absolute stereochemistry is known and specified based on ordering of atoms in the SLN
	public static final int A_STEREO_UE = 16; //Chirality of center is unknown. Compound may be a single stereoisomer or may be a mixture of stereoisomers.
	public static final int A_STEREO_R_ = 17; //R* - Unresolved center. Represents single pure stereoisomer whose absolute configuration is unknown, 
												//but configuration of one chiral center is known relative to another chiral center, specified using CIP rules
	public static final int A_STEREO_S_ = 18; //S*
	public static final int A_STEREO_D_ = 19; //D* - Unresolved center
	public static final int A_STEREO_L_ = 20; //L* - Unresolved center
	public static final int A_STEREO_N_ = 21; //N* - Unresolved center
	public static final int A_STEREO_I_ = 22; //I* - Unresolved center
	public static final int A_STEREO_U_ = 23; //U* - Unresolved center. Represents a single pure isomer but of unknown stereochemistry.
	public static final int A_STEREO_RM = 24; // mixture of stereoisomers
	public static final int A_STEREO_SM = 25; // mixture of stereoisomers
	public static final int A_STEREO_DM = 26; // mixture of stereoisomers
	public static final int A_STEREO_LM = 27; // mixture of stereoisomers
	public static final int A_STEREO_NM = 28; // mixture of stereoisomers
	public static final int A_STEREO_IM = 29; // mixture of stereoisomers
	public static final int A_STEREO_UM = 30; // Unknown mixture - represents mixture of both possible orientations at this atom
	
	
	public static String atomStereoChemAttrToSLNString(int attr)
	{
		switch (attr)
		{
		case A_STEREO_R:
			return "R";
		case A_STEREO_S:
			return "S";
		case A_STEREO_N:
			return "N";
		case A_STEREO_I:
			return "I";
		case A_STEREO_D:
			return "D";
		case A_STEREO_L:
			return "L";
		case A_STEREO_U:
			return "U";
		case A_STEREO_E:
			return "E";
		case A_STEREO_Rel:
			return "R*";  // or *
		case A_STEREO_M:
			return "M";
		case A_STEREO_RE:
			return "RE";
		case A_STEREO_SE:
			return "SE";
		case A_STEREO_DE:
			return "DE";
		case A_STEREO_LE:
			return "LE";
		case A_STEREO_NE:
			return "NE";
		case A_STEREO_IE:
			return "IE";
		case A_STEREO_UE:
			return "UE";
		case A_STEREO_R_:
			return "R*";
		case A_STEREO_S_:
			return "S*";
		case A_STEREO_D_:
			return "D*";
		case A_STEREO_L_:
			return "L*";
		case A_STEREO_N_:
			return "N*";
		case A_STEREO_I_:
			return "I*";
		case A_STEREO_U_:
			return "U*";
		case A_STEREO_RM:
			return "RM";
		case A_STEREO_SM:
			return "SM";
		case A_STEREO_DM:
			return "DM";
		case A_STEREO_LM:
			return "LM";
		case A_STEREO_NM:
			return "NM";
		case A_STEREO_IM:
			return "IM";
		case A_STEREO_UM:
			return "UM";

		default:
			return "";
		}
	}

	public static int SLNStringToAtomStereoChemAttr(String stereo)
	{
		if (stereo.equals("R"))
			return A_STEREO_R;		
		if (stereo.equals("S"))
			return A_STEREO_S;
		if (stereo.equals("N"))
			return A_STEREO_N;
		if (stereo.equals("I"))
			return A_STEREO_I;
		if (stereo.equals("D"))
			return A_STEREO_D;
		if (stereo.equals("L"))
			return A_STEREO_L;
		if (stereo.equals("U"))
			return A_STEREO_U;
		if (stereo.equals("E"))
			return A_STEREO_E;
		if (stereo.equals("Rel") || stereo.equals("*")) //!!!
			return A_STEREO_Rel;
		if (stereo.equals("M"))
			return A_STEREO_M;
		if (stereo.equals("RE"))
			return A_STEREO_RE;
		if (stereo.equals("SE"))
			return A_STEREO_SE;
		if (stereo.equals("DE"))
			return A_STEREO_DE;
		if (stereo.equals("LE"))
			return A_STEREO_LE;
		if (stereo.equals("NE"))
			return A_STEREO_NE;
		if (stereo.equals("IE"))
			return A_STEREO_IE;
		if (stereo.equals("UE"))
			return A_STEREO_UE;
		if (stereo.equals("R*"))
			return A_STEREO_R_;
		if (stereo.equals("S*"))
			return A_STEREO_S_;
		if (stereo.equals("D*"))
			return A_STEREO_D_;
		if (stereo.equals("L*"))
			return A_STEREO_L_;
		if (stereo.equals("N*"))
			return A_STEREO_N_;
		if (stereo.equals("I*"))
			return A_STEREO_I_;
		if (stereo.equals("U*"))
			return A_STEREO_U_;
		if (stereo.equals("RM"))
			return A_STEREO_RM;
		if (stereo.equals("SM"))
			return A_STEREO_SM;
		if (stereo.equals("DM"))
			return A_STEREO_DM;
		if (stereo.equals("LM"))
			return A_STEREO_LM;
		if (stereo.equals("NM"))
			return A_STEREO_NM;
		if (stereo.equals("IM"))
			return A_STEREO_IM;
		if (stereo.equals("UM"))
			return A_STEREO_UM;

		return -1;
	}

	//Atom radical values (for attribute spin)
	public static final int A_spin_s = 0; // singlet
	public static final int A_spin_d = 1; // doublet == * !!!
	public static final int A_spin_t = 2; // triplet

	public static String atomSpinAttrToSLNString(int attr)
	{
		switch (attr)
		{
		case A_spin_s:
			return "s";
		case A_spin_d:
			return "d"; //or *
		case A_spin_t:
			return "t";

		default:
			return "";
		}
	}

	public static int SLNStringToSpinAttr(String spin)
	{
		if (spin.equals("s"))
			return A_spin_s;
		if (spin.equals("d") || spin.equals("*"))
			return A_spin_d;
		if (spin.equals("t"))
			return A_spin_t;

		return -1;
	}

	//Values of coverage attribute
	public static final int QA_COVERED_n = 0; // atom must not have been matched previously
	public static final int QA_COVERED_o = 1; // atom's coverage flags are ignored
	public static final int QA_COVERED_y = 2; // atom must be covered by previous search

	public static String coverageQueryAttributeToSLNString(int attr)
	{
		switch (attr)
		{
		case QA_COVERED_n:
			return "n";
		case QA_COVERED_o:
			return "o";
		case QA_COVERED_y:
			return "y";

		default:
			return "";
		}
	}

	public static int SLNStringToCoverageQueryAttr(String c)
	{
		if (c.equals("n"))
			return QA_COVERED_n;
		if (c.equals("o"))
			return QA_COVERED_o;
		if (c.equals("y"))
			return QA_COVERED_y;

		return -1;
	}

	//Bond Attributes (predefined)   B_ATTR_*
	//public static final int B_ATTR_type = 0; // overrides the type specified by the bond character
	public static final int B_ATTR_s = 1;	//indicates stereo-chemistry about a double bond

	public static final int B_ATTR_USER_DEFINED = 500;

	//Query Bond Attributes QB_ATTR_*
	public static final int QB_ATTR_src = 100; // the smallest ring count "b"
	public static final int QB_ATTR_type = 101; // bond type specified by the bond character !!! -,=,#,:,1,2,3,aromatic,.,~
	public static final int QB_ATTR_r = 102;// Boolean attribute used to specify that a bond or atom is in a ring "a"
	public static final int QB_ATTR_rbc = 103; // ring bond count "b"
	
	public static String bondAttributeToSLNString(int attr)
	{
		switch (attr)
		{
		//case B_ATTR_type:
		//	return "type";
		case B_ATTR_s:
			return "s";

		case B_ATTR_USER_DEFINED:
			return "userDef";

		case QB_ATTR_src:
			return "src";
		case QB_ATTR_type:
			return "type"; // specified by the bond character !!! -,=,#,:,1,2,3,aromatic,.,~
		case QB_ATTR_rbc:
			return "rbc";
			
		default:
			return "";
		}
	}

	// Predefined values for bond type 
	public static final int B_TYPE_ANY = 0; // equal to ~
	public static final int B_TYPE_1 = 1; // equal to -
	public static final int B_TYPE_2 = 2; // equal to =
	public static final int B_TYPE_3 = 3; // equal to #
	public static final int B_TYPE_aromatic = 4; // equal to :
	public static final int B_TYPE_USER_DEFINED = 100; //???

	public static String bondTypeAttributeToSLNString(int attr)
	{
		switch (attr)
		{
		case B_TYPE_ANY:
			return "~";
		case B_TYPE_1:
			return "-";
		case B_TYPE_2:
			return "=";	
		case B_TYPE_3:
			return "#";
		case B_TYPE_aromatic:
			return ":";

		case B_TYPE_USER_DEFINED:
			return "userDef";
		default:
			return "";
		}
	}
	
	public static String bondTypeAttributeToSLNString(int attr, boolean FlagDefaultSingleBond)
	{
		if (FlagDefaultSingleBond)
			if (attr == B_TYPE_1)
				return "";
		
		return bondTypeAttributeToSLNString(attr);
	}

	public static int SLNStringToBondTypeAttr(String type)
	{
		if (type.equals("any")|| type.equals("~") || type.equals(""))
			return B_TYPE_ANY;		
		if (type.equals("-"))
			return B_TYPE_1;
		if (type.equals("="))
			return B_TYPE_2;
		if (type.equals("#"))
			return B_TYPE_3;
		if (type.equals(":"))
			return B_TYPE_aromatic;

		return -1;
	}

	public static int SLNCharToBondTypeAttr(char symbol)
	{
		switch (symbol)
		{
		case '~':
			return B_TYPE_ANY;
		case '-':
			return B_TYPE_1;
		case '=':
			return B_TYPE_2;
		case '#':
			return B_TYPE_3;
		case ':':
			return B_TYPE_aromatic;
		}

		return -1;
	}

	//Bond stereo-chemistry values (for attribute s)
	public static final int B_STEREO_C = 0; // Cis
	public static final int B_STEREO_T = 1; // Trans
	public static final int B_STEREO_E = 2; // Entgegen
	public static final int B_STEREO_Z = 3; // Zusammen
	public static final int B_STEREO_N = 4; // Normal, similar to E
	public static final int B_STEREO_I = 5; // Inverted, similar to Z
	public static final int B_STEREO_U = 6; // Unknown, structure might represent single, mixture or both config.
	public static final int B_STEREO_U_= 7; // unknown, structure represent single configuration

	public static String bondStereoChemistryAttributeToSLNString(int attr)
	{
		switch (attr)
		{
		case B_STEREO_C:
			return "C";
		case B_STEREO_T:
			return "T";
		case B_STEREO_E:
			return "E";	
		case B_STEREO_Z:
			return "Z";
		case B_STEREO_N:
			return "N";
		case B_STEREO_I:
			return "I";
		case B_STEREO_U:
			return "U";
		case B_STEREO_U_:
			return "U*";

		default:
			return "";
		}
	}

	public static int SLNStringToBondStereoChemAttr(String type)
	{
		if (type.equals("C"))
			return B_STEREO_C;		
		if (type.equals("T"))
			return B_STEREO_T;
		if (type.equals("E"))
			return B_STEREO_E;
		if (type.equals("Z"))
			return B_STEREO_Z;
		if (type.equals("N"))
			return B_STEREO_N;
		if (type.equals("I"))
			return B_STEREO_I;
		if (type.equals("U"))
			return B_STEREO_U;
		if (type.equals("U*"))
			return B_STEREO_U_;

		return -1;
	}

	/*
	//Bond types
	public static char BondChars[] = {'~','-','=','#',':'};

	public static int getBondCharNumber (char ch)
	{
		for (int i=0; i < BondChars.length; i++)
		{
			if (ch == BondChars[i])
				return (i);
		}
		return(-1);
	}
	 */	

	/*	public static String queryAttributeToSLNString(int attr)
	{
		switch (attr)
		{
		default:
			return "";
		}
	}
	 */

	//Matrix with the operation priorities {pij}
	//p[i][j] < 0 means that priority(i) < priority(j)
	//p[i][j] = 0 means that priority(i) = priority(j)
	//p[i][j] > 0 means that priority(i) > priority(j)
	public static int priority[][] = {
		//         !  &  |  ;
		/* ! */  {-1, 1, 1, 1},
		/* & */  {-1, 0, 1, 1},
		/* | */  {-1,-1, 0, 1},
		/* ; */  {-1,-1,-1, 0},
	};

	public static final int GlobDictOffseet = 1000;
	public static final int LocalDictOffseet = 1000000;


	public static final String elSymbols[] =
		{
		"",                                                  
		"H","He","Li","Be","B",
		"C","N","O","F","Ne",         
		"Na","Mg","Al","Si","P",
		"S","Cl","Ar","K","Ca",      
		"Sc","Ti","V","Cr","Mn",
		"Fe","Co","Ni","Cu","Zn",    
		"Ga","Ge","As","Se","Br",
		"Kr","Rb","Sr","Y","Zr",    
		"Nb","Mo","Tc","Ru","Rh",
		"Pd","Ag","Cd","In","Sn",   
		"Sb","Te","I","Xe","Cs",
		"Ba","La","Ce","Pr","Nd",    
		"Pm","Sm","Eu","Gd","Tb",
		"Dy","Ho","Er","Tm","Yb",   
		"Lu","Hf","Ta","W","Re",
		"Os","Ir","Pt","Au","Hg",    
		"Tl","Pb","Bi","Po","At",
		"Rn","Fr","Ra","Ac","Th",   
		"Pa","U","Np","Pu","Am",
		"Cm","Bk","Cf","Es","Fm",    
		"Md","No","Lr","Rf","Db",
		"Sg","Bh","Hs","Mt","Ds", 
		"Rg"  		//up to element #111
		};
}
