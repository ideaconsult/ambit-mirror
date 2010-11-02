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
		"$$NAME=keto/enol    $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= O=CC,OC=C  $$GROUP_POS=3,1   $$INFO=",
		"$$NAME=amide/imid   $$TYPE=MOBILE_GROUP $$GROUP=H   $$STATES= O=CN,OC=N  $$GROUP_POS=3,1   $$INFO=",
		
	};
}
