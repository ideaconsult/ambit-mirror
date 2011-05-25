package ambit2.smarts;

import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;


import java.util.Vector;
import java.util.HashMap;



public class SMIRKSReaction 
{	
	public String reactantsSmarts, agentsSmarts, productsSmarts;
	public SmartsFlags reactantFlags = new SmartsFlags();
	public SmartsFlags agentFlags = new SmartsFlags();
	public SmartsFlags productFlags = new SmartsFlags();
	
	//Single container representation 
	QueryAtomContainer reactant = new QueryAtomContainer();
	QueryAtomContainer agent = new QueryAtomContainer();
	QueryAtomContainer product = new QueryAtomContainer();
	
	//Multi-container representation
	public Vector<QueryAtomContainer> reactants = new Vector<QueryAtomContainer>();
	public Vector<QueryAtomContainer> agents = new Vector<QueryAtomContainer>();
	public Vector<QueryAtomContainer> products = new Vector<QueryAtomContainer>();
	
	public Vector<Integer> reactantCLG = new Vector<Integer>();
	public Vector<Integer> agentsCLG = new Vector<Integer>();
	public Vector<Integer> productsCLG = new Vector<Integer>();
	
	
	//Mapping data
	public  Vector<String> mapErrors = new Vector<String>(); 
	//HashMap<Integer, Integer> mapping = new HashMap<Integer, Integer>();
	Vector<Integer> reactantMapIndex = new Vector<Integer>();
	Vector<Integer> reactantFragAtomNum = new Vector<Integer>(); //local atom fragment number
	Vector<Integer> reactantAtomNum = new Vector<Integer>(); //global atom number
	Vector<Integer> reactantFragmentNum = new Vector<Integer>();
	
	Vector<Integer> productMapIndex = new Vector<Integer>();
	Vector<Integer> productFragAtomNum = new Vector<Integer>(); //local atom fragment number
	Vector<Integer> productAtomNum = new Vector<Integer>(); //global atom number
	Vector<Integer> productFragmentNum = new Vector<Integer>();
	
		
	//Transformation Data is described in terms of global numbers	
	//Atom transformation is designated for the mapped atoms
	//Vector<Integer> reactantAtCharge
	//Vector<Integer> productAtCharge
	//Vector<Integer> reactantAtChirality
	//Vector<Integer> productAtChirality	
	
	//Bond transformation
	Vector<Integer> reactAt1 = new Vector<Integer>();
	Vector<Integer> reactAt2 = new Vector<Integer>();	
	Vector<IBond.Order> reactBo = new Vector<IBond.Order>();
	Vector<Integer> prodAt1 = new Vector<Integer>();
	Vector<Integer> prodAt2 = new Vector<Integer>();
	Vector<IBond.Order> prodBo = new Vector<IBond.Order>();
	
				
	public void checkMappings()
	{
		//Register all mappings
		for (int i = 0; i < reactants.size(); i++)
			registerMappings("Reactant", reactant, reactants.get(i), i,  reactantMapIndex, reactantFragAtomNum, 
					reactantAtomNum, reactantFragmentNum);
		
		for (int i = 0; i < products.size(); i++)
			registerMappings("Product", product, products.get(i), i, productMapIndex, productFragAtomNum, 
					productAtomNum, productFragmentNum);
						
		
		//Check mapping index correctness
		for (int i = 0; i < reactantMapIndex.size(); i++)
		{
			int index = getIntegerObjectIndex(productMapIndex, reactantMapIndex.get(i));
			if (index == -1)
				mapErrors.add("Reactant Map Index " + reactantMapIndex.get(i).intValue()+ 
						" is not valid product map index!");
			
			//This should take into account the fragment number as well
			//else
			//	mapping.put(reactantAtomNum.get(i), productAtomNum.get(index));  
			//
		}
		
		for (int i = 0; i < productMapIndex.size(); i++)
		{
			int index = getIntegerObjectIndex(reactantMapIndex, productMapIndex.get(i));
			if (index == -1)
				mapErrors.add("Product Map Index " + productMapIndex.get(i).intValue()+ 
						" is not valid reactant map index!");
		}
			
				
		SmartsToChemObject stco = new SmartsToChemObject();
		
		//Checking for atom typing and some properties correctness for all mapped atoms
		for (int i = 0; i < reactantFragAtomNum.size(); i++)
		{
			int rAtNum = reactantFragAtomNum.get(i).intValue();
			int rNum = reactantFragmentNum.get(i).intValue();
			Integer rMapInd = reactantMapIndex.get(i); 
			IAtom ra = reactants.get(rNum).getAtom(rAtNum);
			int rGlobAtNum = reactantAtomNum.get(i).intValue();
			IAtom glob_ra = reactant.getAtom(rGlobAtNum);
			
			int pIndex = getIntegerObjectIndex(productMapIndex, rMapInd);
			int pAtNum = productFragAtomNum.get(pIndex).intValue();
			int pNum = productFragmentNum.get(pIndex).intValue();
			IAtom pa = products.get(pNum).getAtom(pAtNum);
			int pGlobAtNum = productAtomNum.get(pIndex).intValue();
			IAtom glob_pa = product.getAtom(pGlobAtNum);
			
			if (glob_ra != ra)
				mapErrors.add("Critical Error: Inconsistency between global and gragment atom treatment.");
			
			if (glob_pa != pa)
				mapErrors.add("Critical Error: Inconsistency between global and gragment atom treatment.");
			
			System.out.println("Map #" + rMapInd.intValue() + "  P" + rNum + " A"+rAtNum + "  -->  R"+pNum+" A"+pAtNum+"  "
					+ "     " + rGlobAtNum + " --> " + pGlobAtNum);
			
			IAtom ra1 = stco.toAtom(ra);
			IAtom pa1 = stco.toAtom(pa);
			if (ra1 != null)
			{
				if (pa1 == null)
					mapErrors.add("Map " + rMapInd.intValue() + " atom types are inconsistent!");
				else
				{
					if (!ra1.getSymbol().equals(pa1.getSymbol()))
						mapErrors.add("Map " + rMapInd.intValue() + " atom types are inconsistent!");
				}
			}
			else
			{
				if (pa1 != null)
					mapErrors.add("Map " + rMapInd.intValue() + " atom types are inconsistent!");
			}
			
		}
		
		//Check for mapping correctness on CLG
		//TODO - not clear if it is needed for now	
	}
	
	
	void registerMappings(String compType, QueryAtomContainer globalContainer, QueryAtomContainer fragment, int curFragNum, 
			Vector<Integer> mIndex, Vector<Integer> atFragNum, Vector<Integer> atGlobalNum, Vector<Integer> fragNum)
	{
		for (int i = 0; i < fragment.getAtomCount(); i++)
		{
			IAtom a = fragment.getAtom(i);
			Integer iObj = (Integer)a.getProperty("SmirksMapIndex");
			if (iObj != null)
			{
				if (containsInteger(mIndex, iObj))
					mapErrors.add(compType + " Map Index " + iObj.intValue()+ " is repeated!");
				else
				{
					mIndex.add(iObj);
					atFragNum.add(new Integer(i));
					fragNum.add(new Integer(curFragNum));
					int globalAtNum = globalContainer.getAtomNumber(a);
					atGlobalNum.add(new Integer(globalAtNum));
					//System.out.println("*** " + compType + "   Gl# " + globalAtNum + "  Map:"+iObj.intValue());
				}
			}
			else
			{	
				//This is unmapped atom
			}
		}
	}
	
	
	void generateTransformationData()
	{
		SmartsToChemObject stco = new SmartsToChemObject();
		
		//Atom Transformation
		//TODO
				
		
		
		
		//Bond Transformation
		for (int i = 0; i < reactant.getBondCount(); i++)
		{	
			System.out.println("**** " + i);
			IBond rb = reactant.getBond(i);
			IAtom ra1 = rb.getAtom(0);
			IAtom ra2 = rb.getAtom(1);
			Integer raMapInd1 = (Integer)ra1.getProperty("SmirksMapIndex");
			Integer raMapInd2 = (Integer)ra2.getProperty("SmirksMapIndex");
			if (raMapInd1 == null)
			{
				int rAt1Num = -reactant.getAtomNumber(ra1);  
				if (raMapInd2 == null)
				{
					//int rAt2Num = -reactant.getAtomNumber(ra2);
					//This bond is not registered as a transformation
				}
				else
				{	
					//TODO
				}
			}
			else
			{
				if (raMapInd2 == null)
				{
					//TODO
				}
				else
				{
					int pAt1Num = getMappedProductAtom(raMapInd1);
					int pAt2Num = getMappedProductAtom(raMapInd1);
					int rAt1Num = reactant.getAtomNumber(ra1);
					int rAt2Num = reactant.getAtomNumber(ra2);
					int pbNum = product.getBondNumber(reactant.getAtom(rAt1Num), reactant.getAtom(rAt2Num));
					prodAt1.add(new Integer(pAt1Num));
					prodAt2.add(new Integer(pAt2Num));
					IBond rb0 = stco.toBond(rb);
					prodBo.add(rb0.getOrder());
					reactAt1.add(new Integer(rAt1Num));
					reactAt2.add(new Integer(rAt2Num));
					if (pbNum == -1)
						reactBo.add(null);
					else
					{	
						IBond pb = product.getBond(pbNum);
						IBond pb0 = stco.toBond(pb);
						reactBo.add(pb.getOrder());
					}	
				}
			}	
				
		}
		
		
		for (int i = 0; i < product.getBondCount(); i++)
		{
			//TODO
		}
		
	}
	
	
	
	
	//Helper functions
	
	int getMappedProductAtom(Integer raMapInd)
	{	
		int pIndex = getIntegerObjectIndex(productMapIndex, raMapInd);
		int pGlobAtNum = productAtomNum.get(pIndex).intValue();
		return pGlobAtNum;
	}
	
	
	int getMappedReactantAtom(Integer paMapInd)
	{	
		int rIndex = getIntegerObjectIndex(reactantMapIndex, paMapInd);
		int rGlobAtNum = reactantAtomNum.get(rIndex).intValue();
		return rGlobAtNum;
	}
	
	
	boolean containsInteger(Vector<Integer> v, Integer iObj)
	{
		for (int i = 0; i < v.size(); i++)
			if (iObj.intValue() == v.get(i).intValue())
				return(true);
		
		return false;
	}
	
	int getIntegerObjectIndex(Vector<Integer> v, Integer iObj)
	{
		for (int i = 0; i < v.size(); i++)
		{	
			Integer vi = v.get(i);
			if (iObj.intValue() == vi.intValue())
				return(i);
		}
		return -1;
	}
	
	public String transformationDataToString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i <  prodBo.size(); i++)
		{
			sb.append("BondTransform: (");
			sb.append(prodAt1.get(i).intValue() + ", ");
			sb.append(prodAt2.get(i).intValue() + ", ");
			sb.append(prodBo.get(i).toString() + ")  -->  (");
			sb.append(reactAt1.get(i).intValue() + ", ");
			sb.append(reactAt2.get(i).intValue() + ", ");
			sb.append(reactBo.get(i).toString() + ")" );
			sb.append("\n");
		}
		return(sb.toString());
		
	}
	
}
