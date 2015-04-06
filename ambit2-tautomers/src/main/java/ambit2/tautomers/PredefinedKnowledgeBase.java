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
		//"$$NAME=amidine/imidine        $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#7]=[#6][#7] [#7][#6]=[#7]   $$GROUP_POS=3,1   $$INFO= N=CN",
		"$$NAME=amidine/imidine        $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#7]=[#6][#7H1] [#7][#6]=[#7H0]   $$GROUP_POS=3,1   $$INFO= N=CN",
		"$$NAME=diazoamino/diazoamino  $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#7]=[#7][#7] [#7][#7]=[#7]   $$GROUP_POS=3,1   $$INFO= N=NN",
		"$$NAME=thioamide/iminothiol   $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#16]=[#6][#7] [#16][#6]=[#7] $$GROUP_POS=3,1   $$INFO= S=CN",
		"$$NAME=nitrosamine/diazohydroxide $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#8]=[#7][#7] [#8][#7]=[#7]   $$GROUP_POS=3,1   $$INFO= O=NN",
		
		"$$NAME=S=NN                   $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#16]=[#7][#7] [#16][#7]=[#7]   $$GROUP_POS=3,1 $$INFO= S=NN",
		"$$NAME=S=NS                   $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#16]=[#7][#16] [#16][#7]=[#16] $$GROUP_POS=3,1 $$INFO= S=NS",
		"$$NAME=N=SC                   $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#7]=[#16][#6] [#7][#16]=[#6]   $$GROUP_POS=3,1 $$INFO= N=SC",
		"$$NAME=O=SC                   $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#8]=[#16][#6] [#8][#16]=[#6]   $$GROUP_POS=3,1 $$INFO= O=SC",
		
		"$$NAME=1.5_shift              $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#8,#7,#16]=[#6][#6]=[#6][#6] [#8,#7,#16][#6]=[#6][#6]=[#6]  " +
																												 "$$GROUP_POS=5,1  $$INFO=",
		"$$NAME=1.7_shift              $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#6]=[#6][#6]=[#6][#6]=[#6][#8,#7,#16] [#6][#6]=[#6][#6]=[#6][#6]=[#8,#7,#16]  " +
																												"$$GROUP_POS=7,1  $$INFO=",
		
		"$$NAME=[O,S,N][C,S]#[C,S]     $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= [#8,#16,#7][#6,#16;!R]#[#6,#16] [#8,#16,#7]=[#6,#16;!R]=[#6,#16]    $$GROUP_POS=1,3  $$INFO= ",
		
		//Cl rules
		"$$NAME=Cl_keto/enol           $$TYPE=MOBILE_GROUP $$GROUP=Cl   $$STATES= [#8]=[#6][#6] [#8][#6]=[#6]   $$GROUP_POS=3,1   $$INFO= O=CC",
		
		//Ring Chain Rules
		"$$NAME=3-exo-Trig            $$TYPE=RING_CHAIN    $$GROUP=H   $$STATES= [C,O,N,S;!R][C][C]=[C,O,N] [C,O,N,S]1[C][C]1[C,O,N]   $$GROUP_POS=1,4   $$INFO=favoured; r3",
	};
	
	
	//Warning Filters	
	public static final String warningFragments[] =
	{	
		"[#6;!R;^1]"   // sp carbon     *=C=*  or *#C-*      This is old version:  "[#6;!R](=*)=*",     //allene atom
	};
	
	
	/*
	//Warning Filter original State Exceptions	
	public static final String warningFragmentsOriginalStateException[] =
	{	
		"[#6;!R]#*",        //if original state of the allene atoms is triple bond then this warning rule is not applied
	};
	*/
	
	//Exclude Filters	
	public static final String excludeFragments[] =
	{	
		"[*;r4,r5,r6,r7,r8](=*)=*"  //allene atom in a cycle (up to 8 atoms)
	};
	
	
	//Rules used for tautomer ranking
	public static final String rankingRules[] = 
	{
		"$$NAME=keto/enol    $$STATE_ENERGY=  0, 0.315   $$INFO= O=CC",
		"$$NAME=amin/imin    $$STATE_ENERGY=  0, 0.037   $$INFO= N=CC",
		"$$NAME=amide/imid   $$STATE_ENERGY=   0, 0.673  $$INFO= O=CN",
		"$$NAME=nitroso/oxime   $$STATE_ENERGY=   0.025, 0  $$INFO= O=NC",
		"$$NAME=azo/hydrazone   $$STATE_ENERGY=   0, 0.137  $$INFO= N=NC",
		"$$NAME=thioketo/thioenol   $$STATE_ENERGY=   0.246, 0  $$INFO= S=CC",
		"$$NAME=thionitroso/thiooxime  $$STATE_ENERGY=   0.983, 0  $$INFO= S=NC",
		"$$NAME=amidine/imidine    $$STATE_ENERGY=  0, 0.137  $$INFO= N=CN",
		"$$NAME=diazoamino/diazoamino  $$STATE_ENERGY=  0, 0.173  $$INFO= N=NN",
		"$$NAME=thioamide/iminothiol   $$STATE_ENERGY=  0, 0.068  $$INFO= S=CN",
		"$$NAME=nitrosamine/diazohydroxide $$STATE_ENERGY=  0, 0.639  $$INFO= O=NN",
	};
	
	public static final double aromaticAtomEnergy = -0.1;
	
}
