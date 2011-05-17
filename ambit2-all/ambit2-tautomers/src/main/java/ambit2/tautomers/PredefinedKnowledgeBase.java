package ambit2.tautomers;

public class PredefinedKnowledgeBase 
{
	/**
	 * GROUP_POS describes the position of mobile group i.e. the number of atom 
	 * it is attached to (SMILES position, 1-based atom indexing)
	 * 
	 */
	
	public static final String rules[] = 
	{
		"$$NAME=keto/enol              $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= O=CC OC=C  $$GROUP_POS=3,1   $$INFO=",
		"$$NAME=amin/imin              $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= N=CC NC=C  $$GROUP_POS=3,1   $$INFO=",
		"$$NAME=amide/imid             $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= O=CN OC=N  $$GROUP_POS=3,1   $$INFO=",
		"$$NAME=nitroso/oxime          $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= O=NC ON=C  $$GROUP_POS=3,1   $$INFO=",
		"$$NAME=azo/hydrazone          $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= N=NC NN=C  $$GROUP_POS=3,1   $$INFO=",
		"$$NAME=thioketo/thioenol      $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= S=CC SC=C  $$GROUP_POS=3,1   $$INFO=",
		"$$NAME=thionitroso/thiooxime  $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= S=NC SN=C  $$GROUP_POS=3,1   $$INFO=",
		"$$NAME=amidine/imidine        $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= N=CN NC=N  $$GROUP_POS=3,1   $$INFO=",
		"$$NAME=diazoamino/diazoamino  $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= N=NN NN=N  $$GROUP_POS=3,1   $$INFO=",
		"$$NAME=thioamide/iminothiol   $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= S=CN SC=N  $$GROUP_POS=3,1   $$INFO=",
		"$$NAME=nitrosamine/diazohydroxide  $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= O=NN ON=N  $$GROUP_POS=3,1   $$INFO="
			
	};
	
	
	//Warning Filters	
	public static final String warningFragments[] =
	{	
		//"[C;!R](=*)=*",     //allene atom
	};
	
	
	//Exclude Filters	
	public static final String excludeFragments[] =
	{
		//"[*;r4,r5,r6,r7,r8](=*)=*",  //allene atom in a cycle (up to 8 atoms)
		
	};
	
}
