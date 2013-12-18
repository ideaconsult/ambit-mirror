package ambit2.sln;

public class SLNConst
{
	//TODO Global values for setting the absolute CIS/TRANS configuration

	//Logical operations
	public static char LogOperationChars[] = {'!', '&', '|', ';'};
	//TODO

	//TODO Atom Primitives

	//Bond types
	public static char BondChars[] = {'~','-','=','#',':','@','/','\\'};
	public static final int BT_ANY = 0;
	public static final int BT_SINGLE = 1;
	public static final int BT_DOUBLE = 2;
	public static final int BT_TRIPLE = 3;
	public static final int BT_AROMATIC = 4;
	public static final int BT_UNDEFINED = 100;
	
	
	
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
	public static final int LocDictOffseet = 1000000;
	
	
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
