package ambit2.sln;

public class SLNConst
{	

	//Logical operations
	public static char LogOperationChars[] = {'!', '&', '|', ';'};
	public static final int LO_NOT = 0;
	public static final int LO_AND = 1;
	public static final int LO_OR = 2;
	public static final int LO_ANDLO = 3;
	public static final int LO = 1000;
	
	
	//public static char AtomAttrChars[] = {'*' //doublet spin state};

	//Atom Attributes (predefined)   A_ATTR_*
	public static final int A_ATTR_atomtID = 0;	 // doesn't equal to atomNumber
	public static final int A_ATTR_charge = 1;	 // specifies formal charge -/+n
	public static final int A_ATTR_I = 2;		 // specifies atom isotope
	public static final int A_ATTR_fcharge = 3;	// specifies a partial charge
	public static final int A_ATTR_s = 4; 		//specifies stereo-chemistry at tetrahedral atoms !!! R,S,N,I,D,L,U,E,R,M
	public static final int A_ATTR_spin = 5;	//specifies that the atom is a radical !!! s, d, t	
	
	public static final int A_ATTR_USER_DEFINED = 500;
	
	
	//Atom stereo-chemistry values (for attribute s)
	public static final int A_STEREO_R = 0;
	public static final int A_STEREO_S = 1;
	
	
	//Bond Attributes (predefined)   B_ATTR_*
	
	public static final int B_ATTR_s = 1;	//indicates stereochemistry about a doouble bond !!! C,T,E,Z,N,I,N,I,U,U*
	
	public static final int B_ATTR_USER_DEFINED = 500;
	

	//Bond types
	public static char BondChars[] = {'~','-','=','#',':'};
	
	public static final int BT_ANY = 0;
	public static final int BT_SINGLE = 1;
	public static final int BT_DOUBLE = 2;
	public static final int BT_TRIPLE = 3;
	public static final int BT_AROMATIC = 4;
	public static final int BT_UNDEFINED = 100;
	
	
	public static int getBondCharNumber (char ch)
	{
		for (int i=0; i < BondChars.length; i++)
		{
			if (ch == BondChars[i])
				return (i);
		}
		return(-1);
	}	
	
	
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
