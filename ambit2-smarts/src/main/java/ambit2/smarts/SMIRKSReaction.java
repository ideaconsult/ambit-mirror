package ambit2.smarts;

import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.interfaces.IAtom;


import java.util.Vector;
import java.util.HashMap;



public class SMIRKSReaction 
{	
	public String reactantsSmarts, agentsSmarts, productsSmarts;
	public SmartsFlags reactantFlags = new SmartsFlags();
	public SmartsFlags agentFlags = new SmartsFlags();
	public SmartsFlags productFlags = new SmartsFlags();
	
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
	Vector<Integer> reactantAtomNum = new Vector<Integer>();
	Vector<Integer> reactantFragmentNum = new Vector<Integer>();
	Vector<Integer> productMapIndex = new Vector<Integer>();
	Vector<Integer> productAtomNum = new Vector<Integer>();
	Vector<Integer> productFragmentNum = new Vector<Integer>();
		
	//Transformation Data
	//Vector<Integer> reactantAtCharge
	//Vector<Integer> productAtCharge
	//Vector<Integer> reactantAtChirality
	//Vector<Integer> productAtChirality
	
	Vector<Integer> reactantAt1Index = new Vector<Integer>();
	Vector<Integer> reactantAt2Index = new Vector<Integer>();
	Vector<Integer> reactantBoType = new Vector<Integer>();
	Vector<Integer> productBoType = new Vector<Integer>();
	
		
	
	public void checkMappings()
	{
		//Register all mappings
		for (int i = 0; i < reactants.size(); i++)
			registerMappings("Reactant",reactants.get(i), i,  reactantMapIndex, reactantAtomNum, reactantFragmentNum);
		
		for (int i = 0; i < products.size(); i++)
			registerMappings("Product", products.get(i), i, productMapIndex, productAtomNum, productFragmentNum);
						
		
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
		for (int i = 0; i < reactantAtomNum.size(); i++)
		{
			int rAtNum = reactantAtomNum.get(i).intValue();
			int rNum = reactantFragmentNum.get(i).intValue();
			Integer rMapInd = reactantMapIndex.get(i); 
			IAtom ra = reactants.get(rNum).getAtom(rAtNum);
			
			int pIndex = getIntegerObjectIndex(productMapIndex, rMapInd);
			int pAtNum = productAtomNum.get(pIndex).intValue();
			int pNum = productFragmentNum.get(pIndex).intValue();
			IAtom pa = products.get(pNum).getAtom(pAtNum);
			
			//System.out.println("Map #" + rMapInd.intValue() + "  P" + rNum + " A"+rAtNum + "  -->  R"+pNum+" A"+pAtNum+""	);
			
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
			
			//TODO
			//Use:     SmartsToChemObject -->  IAtom toAtom(IAtom a)
			
		}
		
		//Check for mapping correctness on CLG
		//TODO - not clear if it is needed	
	}
	
	
	void registerMappings(String compType, QueryAtomContainer fragment, int curFragNum, 
			Vector<Integer> mIndex, Vector<Integer> atNum, Vector<Integer> fragNum)
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
					atNum.add(new Integer(i));
					fragNum.add(new Integer(curFragNum));
				}
			}
		}
	}
	
	
	void generateTransformationData()
	{
		//Atom Transformation
		//TODO
		
		
		
		//Bond Transformation
		//TODO
		
		
	}
	
	
	
	//Helper functions
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
	
}
