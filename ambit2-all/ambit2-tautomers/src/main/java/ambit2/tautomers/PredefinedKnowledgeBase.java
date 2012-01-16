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
		"$$NAME=keto/enol              $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#8]=[#6][#6] [#8][#6]=[#6]   $$GROUP_POS=3,1   $$INFO= O=CC",
		"$$NAME=amin/imin              $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#7]=[#6][#6] [#7][#6]=[#6]   $$GROUP_POS=3,1   $$INFO= N=CC",
		"$$NAME=amide/imid             $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#8]=[#6][#7] [#8][#6]=[#7]   $$GROUP_POS=3,1   $$INFO= O=CN",
		"$$NAME=nitroso/oxime          $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#8]=[#7][#6] [#8][#7]=[#6]   $$GROUP_POS=3,1   $$INFO= O=NC",
		"$$NAME=azo/hydrazone          $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#7]=[#7][#6] [#7][#7]=[#6]   $$GROUP_POS=3,1   $$INFO= N=NC",
		"$$NAME=thioketo/thioenol      $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#16]=[#6][#6] [#16][#6]=[#6] $$GROUP_POS=3,1   $$INFO= S=CC",
		"$$NAME=thionitroso/thiooxime  $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#16]=[#7][#6] [#16][#7]=[#6] $$GROUP_POS=3,1   $$INFO= S=NC",
		"$$NAME=amidine/imidine        $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#7]=[#6][#7] [#7][#6]=[#7]   $$GROUP_POS=3,1   $$INFO= N=CN",
		"$$NAME=diazoamino/diazoamino  $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#7]=[#7][#7] [#7][#7]=[#7]   $$GROUP_POS=3,1   $$INFO= N=NN",
		"$$NAME=thioamide/iminothiol   $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#16]=[#6][#7] [#16][#6]=[#7] $$GROUP_POS=3,1   $$INFO= S=CN",
		"$$NAME=nitrosamine/diazohydroxide $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#8]=[#7][#7] [#8][#7]=[#7]   $$GROUP_POS=3,1   $$INFO= O=NN",
		
		"$$NAME=S=NN                   $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#16]=[#7][#7] [#16][#7]=[#7]   $$GROUP_POS=3,1 $$INFO= S=NN",
		"$$NAME=S=NS                   $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#16]=[#7][#16] [#16][#7]=[#16] $$GROUP_POS=3,1 $$INFO= S=NS",
		"$$NAME=N=SC                   $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#7]=[#16][#6] [#7][#16]=[#6]   $$GROUP_POS=3,1 $$INFO= N=SC",
		
		"$$NAME=1.5 shift              $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#8,#7,#16]=[#6][#6]=[#6][#6] [#8,#7,#16][#6]=[#6][#6]=[#6]  " +
																												 "$$GROUP_POS=5,1  $$INFO=",
		"$$NAME=1.7 shift              $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#6]=[#6][#6]=[#6][#6]=[#6][#8,#7,#16] [#6][#6]=[#6][#6]=[#6][#6]=[#8,#7,#16]  " +
																												"$$GROUP_POS=7,1  $$INFO=",
		"$$NAME=OC#C                   $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#8][#6]#[#6] [#8]=[#6]=[#6]    $$GROUP_POS=1,3  $$INFO= "
	};
	
	
	//Warning Filters	
	public static final String warningFragments[] =
	{	
		"[#6;!R](=*)=*",     //allene atom
	};
	
	//Warning Filter original State Exceptions	
	public static final String warningFragmentsOriginalStateException[] =
	{	
		"[#6;!R]#*",        //if original state of the allene atoms is triple bond then this warning rule is not applied
	};
	
	
	//Exclude Filters	
	public static final String excludeFragments[] =
	{
		"[*;r4,r5,r6,r7,r8](=*)=*",  //allene atom in a cycle (up to 8 atoms)
		
	};
	
}
