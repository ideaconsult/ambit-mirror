package ambit2.smarts;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;



public class SMIRKSReaction 
{	
	public String reactantsSmarts, agentsSmarts, productsSmarts;
	public SmartsFlags reactantFlags = new SmartsFlags();
	public SmartsFlags agentFlags = new SmartsFlags();
	public SmartsFlags productFlags = new SmartsFlags();
	public List<SmartsAtomExpression> reactantRecursiveAtoms = null;  
	public List<SmartsAtomExpression> productRecursiveAtoms = null;
	
	//Single container representation 
	public IQueryAtomContainer reactant = new QueryAtomContainer(SilentChemObjectBuilder.getInstance());
	public IQueryAtomContainer agent = new QueryAtomContainer(SilentChemObjectBuilder.getInstance());
	public IQueryAtomContainer product = new QueryAtomContainer(SilentChemObjectBuilder.getInstance());
	
	//Multi-container representation
	public List<IQueryAtomContainer> reactants = new ArrayList<IQueryAtomContainer>();
	public List<IQueryAtomContainer> agents = new ArrayList<IQueryAtomContainer>();
	public List<IQueryAtomContainer> products = new ArrayList<IQueryAtomContainer>();
	
	public List<Integer> reactantCLG = new ArrayList<Integer>();
	public List<Integer> agentsCLG = new ArrayList<Integer>();
	public List<Integer> productsCLG = new ArrayList<Integer>();
	
		
	public  List<String> mapErrors = new ArrayList<String>(); 
	//HashMap<Integer, Integer> mapping = new HashMap<Integer, Integer>();
	
	//Mapping data
	//Atom numbers are described in two ways
	//1.<Global atom numbers> i.e. regardless of the reactant/product fragmentation
	//2.<Frag Num>  + <Local atom Num> 
	
	List<Integer> reactantMapIndex = new ArrayList<Integer>();  //Container with the mapping indexes as they appeared in the SMIRKS
	List<Integer> reactantFragAtomNum = new ArrayList<Integer>(); //local atom fragment number
	List<Integer> reactantAtomNum = new ArrayList<Integer>(); //global atom number
	List<Integer> reactantFragmentNum = new ArrayList<Integer>();
	List<Integer> reactantNotMappedAt = new ArrayList<Integer>(); //The global numbers of not-mapped atoms
	
	List<Integer> productMapIndex = new ArrayList<Integer>();
	List<Integer> productFragAtomNum = new ArrayList<Integer>(); //local atom fragment number
	List<Integer> productAtomNum = new ArrayList<Integer>(); //global atom number
	List<Integer> productFragmentNum = new ArrayList<Integer>();
	List<Integer> productNotMappedAt = new ArrayList<Integer>(); //The global numbers of not-mapped atoms
	
		
	//Transformation Data is described in terms of global numbers	
	//Atom transformation is designated for the mapped atoms
	List<Integer> reactantAtCharge = new ArrayList<Integer>();
	List<Integer> productAtCharge = new ArrayList<Integer>();
	//List<Integer> reactantAtChirality
	//List<Integer> productAtChirality	
	
	//Bond transformation
	List<Integer> reactAt1 = new ArrayList<Integer>();
	List<Integer> reactAt2 = new ArrayList<Integer>();	
	List<IBond.Order> reactBo = new ArrayList<IBond.Order>();
	List<Integer> prodAt1 = new ArrayList<Integer>();
	List<Integer> prodAt2 = new ArrayList<Integer>();
	List<IBond.Order> prodBo = new ArrayList<IBond.Order>();
	
	protected IChemObjectBuilder builder;
	protected SmartsToChemObject mSTCO = null;
	
	public SMIRKSReaction(IChemObjectBuilder builder) {
		super();
		this.builder = builder;
	}
	
	public SmartsToChemObject getSmartsToChemObjectConvertor()
	{
		if (mSTCO == null)
			mSTCO = new SmartsToChemObject(builder);
		return mSTCO;
	}
	
	public void checkMappings()
	{
		//Register all mappings
		for (int i = 0; i < reactants.size(); i++)
			registerMappings("Reactant", reactant, reactants.get(i), i,  reactantMapIndex, reactantNotMappedAt, 
					reactantFragAtomNum, reactantAtomNum, reactantFragmentNum);
		
		for (int i = 0; i < products.size(); i++)
			registerMappings("Product", product, products.get(i), i, productMapIndex, productNotMappedAt,  
					productFragAtomNum, productAtomNum, productFragmentNum);
						
		
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
		
		
		if (!mapErrors.isEmpty())
		{	
			//At least one mapping error is found
			//The errors may cause exceptions in the following code hence the checking is stopped    
			return;
		}
			
		SmartsToChemObject stco = getSmartsToChemObjectConvertor();
		
		//Checking for atom typing and some properties correctness for all mapped atoms
		
		//reactantFragAtomNum, reactantFragmentNum  and  reactantAtomNum  have the same size
		//The first two represent one way to number (access the mapped atoms)
		//The third container represents another way (global atom numbers) currently utilized 
		
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
				mapErrors.add("Critical Error: Inconsistency between global and fragment atom treatment.");
			
			if (glob_pa != pa)
				mapErrors.add("Critical Error: Inconsistency between global and fragment atom treatment.");
			
			//System.out.println("Map #" + rMapInd.intValue() + "  P" + rNum + " A"+rAtNum + "  -->  R"+pNum+" A"+pAtNum+"  "
			//		+ "     " + rGlobAtNum + " --> " + pGlobAtNum);
			
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
	
	
	void registerMappings(String compType, IQueryAtomContainer globalContainer, IQueryAtomContainer fragment, 
			int curFragNum, List<Integer> mapIndex, List<Integer> notMappedAt, 
			List<Integer> atFragNum, List<Integer> atGlobalNum, List<Integer> fragNum)
	{
		SmartsToChemObject stco = new SmartsToChemObject(builder);
		
		for (int i = 0; i < fragment.getAtomCount(); i++)
		{						
			IAtom a = fragment.getAtom(i);
			Integer iObj = (Integer)a.getProperty("SmirksMapIndex");
			if (iObj != null)
			{
				//System.out.println("   Map index = " + iObj);
				
				if (containsInteger(mapIndex, iObj))
					mapErrors.add(compType + " Map Index " + iObj.intValue()+ " is repeated!");
				else
				{	
					mapIndex.add(iObj);
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
				//System.out.println("   not mapped atom");
				if (compType.equals("Product"))
				{
					IAtom a0 = getSmartsToChemObjectConvertor().toAtom(a);
					if (a0 == null)
						mapErrors.add("Unmapped product atom with undefined type!" );
				}
				
				int globalAtNum = globalContainer.getAtomNumber(a);
				notMappedAt.add(new Integer(globalAtNum));
			}
		}
	}
	
	
	void generateTransformationData()
	{
		SmartsToChemObject stco = new SmartsToChemObject(builder);
		
		//Atom Transformation. Different atom properties are handled
		generateChargeTransformation();
		
		//TODO - other atom properties transformation: chirality, isotops, (??? eventually implicit H atoms and aromaticity special treatment)
		
		
		//Bond Transformation
		for (int i = 0; i < reactant.getBondCount(); i++)
		{	
			IBond rb = reactant.getBond(i);
			IBond rb0 = stco.toBond(rb);
			
			IAtom ra1 = rb.getAtom(0);
			IAtom ra2 = rb.getAtom(1);
			Integer raMapInd1 = (Integer)ra1.getProperty("SmirksMapIndex");
			Integer raMapInd2 = (Integer)ra2.getProperty("SmirksMapIndex");
			
			if (raMapInd1 == null)
			{	
				//A bond transformation is not registered.
				//The reactant bond will be removed together with the unmapped atom 
			}
			else
			{
				if (raMapInd2 == null)
				{
					//A bond transformation is not registered.
					//The reactant bond will be removed together with the unmapped atom 
				}
				else
				{
					//System.out.println("    raMapIndices: " + raMapInd1 + " " + raMapInd2);
					int pAt1Num = getMappedProductAtom(raMapInd1);
					int pAt2Num = getMappedProductAtom(raMapInd2);
					int rAt1Num = reactant.getAtomNumber(ra1);
					int rAt2Num = reactant.getAtomNumber(ra2);
					int pbNum = product.getBondNumber(product.getAtom(pAt1Num), product.getAtom(pAt2Num));
					
					
					boolean FlagRegisterTransform = false;
					if (pbNum == -1)
					{	
						prodBo.add(null); //Bond deletion is registered 
						FlagRegisterTransform = true;
					}	
					else
					{	
						IBond pb = product.getBond(pbNum);
						IBond pb0 = stco.toBond(pb);
						
						if (rb0 == null)
						{	
							if (pb0 == null)
							{
								//check pb and rb expressions compatibility
								//TODO
							}
							else
							{
								//This is not treated as an error since the product bond order is defined and can be created
								FlagRegisterTransform = true;
								prodBo.add(pb0.getOrder());
							}
						}
						else
						{
							if (pb0 == null)
							{	
								//Handle an error!
								mapErrors.add("A product bond with undefined order: " + SmartsHelper.bondToString(pb));
							}
							else //Both reactant and product bonds have defined orders 
							{	
								if (rb instanceof SingleOrAromaticBond) //Special treatment for "single or aromatic" reactant bond
								{
									if (!(pb instanceof SingleOrAromaticBond))
									{
										//In this case transformation is registered
										//For example "-,:" --> "-" would be registered although in some occasions it is not needed 
										// CC >> C-C is not needed but cc >> C-C is needed.
										FlagRegisterTransform = true;
										prodBo.add(pb0.getOrder());
									}	
								}
								else
									if (rb0.getOrder() != pb0.getOrder())
									{
										FlagRegisterTransform = true;
										prodBo.add(pb0.getOrder());
									}	
							}
						}
					}	
					
					if (FlagRegisterTransform)
					{	
						prodAt1.add(new Integer(pAt1Num));
						prodAt2.add(new Integer(pAt2Num));
						
						reactAt1.add(new Integer(rAt1Num));
						reactAt2.add(new Integer(rAt2Num));
						
						if (rb0 == null)
							reactBo.add(IBond.Order.QUADRUPLE);  //Quadruple order is used for the reactant bond with undefined order
						else
							reactBo.add(rb0.getOrder());
					}
				}
			}	
				
		}
		
		
		//Check for bonds that must be created in product (these do not exist in the reactant)
		for (int i = 0; i < product.getBondCount(); i++)
		{
			IBond pb = product.getBond(i);
			IBond pb0 = stco.toBond(pb);
			
			IAtom pa1 = pb.getAtom(0);
			IAtom pa2 = pb.getAtom(1);
			int pAt1Num = product.getAtomNumber(pa1);
			int pAt2Num = product.getAtomNumber(pa2);
			Integer paMapInd1 = (Integer)pa1.getProperty("SmirksMapIndex");
			Integer paMapInd2 = (Integer)pa2.getProperty("SmirksMapIndex");
			
			if (paMapInd1 == null)
			{	
				if (pb0 == null)
				{
					mapErrors.add("A product bond with undefined order: " + SmartsHelper.bondToString(pb));
					continue;
				}
				
				if (paMapInd2 == null)
				{	
					//This is a bond between two unmapped atoms in the product
					prodBo.add(pb0.getOrder());
					prodAt1.add(new Integer(pAt1Num));
					prodAt2.add(new Integer(pAt2Num));
					reactBo.add(null);
					reactAt1.add(new Integer(SmartsConst.SMRK_UNSPEC_ATOM));
					reactAt2.add(new Integer(SmartsConst.SMRK_UNSPEC_ATOM));
					
				}
				else
				{
					//This is a bond between unmapped atom and mapped atom in the product
					//at1 is unmapped, at2 is mapped
					prodBo.add(pb0.getOrder());
					prodAt1.add(new Integer(pAt1Num));
					prodAt2.add(new Integer(pAt2Num));
					reactBo.add(null);
					reactAt1.add(new Integer(SmartsConst.SMRK_UNSPEC_ATOM));
					int rAt2Num = getMappedReactantAtom(paMapInd2);
					reactAt2.add(new Integer(rAt2Num));	
				}
			}
			else
			{
				if (paMapInd2 == null)
				{
					if (pb0 == null)
					{
						mapErrors.add("A product bond with undefined order: " + SmartsHelper.bondToString(pb));
						continue;
					}
					
					//This is a bond between unmapped atom and mapped atom in the product
					//at2 is unmapped, at1 is mapped
					prodBo.add(pb0.getOrder());
					prodAt1.add(new Integer(pAt1Num));
					prodAt2.add(new Integer(pAt2Num));
					reactBo.add(null);
					int rAt1Num = getMappedReactantAtom(paMapInd1);
					reactAt1.add(new Integer(rAt1Num));
					reactAt2.add(new Integer(SmartsConst.SMRK_UNSPEC_ATOM));
				}
				else
				{
					//This is a bond between two mapped atoms in the product which is not present in the reactant
					int rAt1Num = getMappedReactantAtom(paMapInd1);
					int rAt2Num = getMappedReactantAtom(paMapInd2);
					int rbNum = reactant.getBondNumber(reactant.getAtom(rAt1Num), reactant.getAtom(rAt2Num));
					if (rbNum == -1)
					{	
						if (pb0 == null)
						{
							mapErrors.add("A product bond with undefined order: " + SmartsHelper.bondToString(pb));
							continue;
						}
						
						prodBo.add(pb0.getOrder());
						prodAt1.add(new Integer(pAt1Num));
						prodAt2.add(new Integer(pAt2Num));
						reactBo.add(null);
						reactAt1.add(new Integer(rAt1Num));
						reactAt2.add(new Integer(rAt2Num));
					}
					
				} 
			}
			
		}
		
	}
	
	
	void generateChargeTransformation()
	{
		SmartsToChemObject stco = new SmartsToChemObject(builder);
		
		//TODO Improve charge treatment in some cases when atom type is not defined but charge info is defined
		
		for (int i = 0; i < reactant.getAtomCount(); i++)
		{
			IAtom a = stco.toAtom(reactant.getAtom(i));
			if (a == null)
				reactantAtCharge.add(null);
			else
				reactantAtCharge.add(a.getFormalCharge());
		}
		
		
		for (int i = 0; i < product.getAtomCount(); i++)
		{
			IAtom a = stco.toAtom(product.getAtom(i));
			if (a == null)
				productAtCharge.add(null);
			else
				productAtCharge.add(a.getFormalCharge());
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
	
	
	//so far not used
	int getTransformationReactantBondIndex(int nBondsToCheck, int rAt1, int rAt2)
	{
		for (int i = 0; i < nBondsToCheck; i++)
		{
			if ((reactAt1.get(i).intValue() == rAt1) && (reactAt2.get(i).intValue() == rAt2))
				return (i);
			
			if ((reactAt1.get(i).intValue() == rAt2) && (reactAt2.get(i).intValue() == rAt1))
				return (i);	
		}
		
		return(-1);
	}
	
	
	boolean containsInteger(List<Integer> v, Integer iObj)
	{
		for (int i = 0; i < v.size(); i++)
			if (iObj.intValue() == v.get(i).intValue())
				return(true);
		
		return false;
	}
	
	int getIntegerObjectIndex(List<Integer> v, Integer iObj)
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
		
		sb.append("Mappings:\n");
		for (int i = 0; i < reactantMapIndex.size(); i++)
		{	
			Integer rMapInd = reactantMapIndex.get(i);
			int rGlobAtNum = reactantAtomNum.get(i).intValue();
			IAtom glob_ra = reactant.getAtom(rGlobAtNum);
			int pIndex = getIntegerObjectIndex(productMapIndex, rMapInd);			
			int pGlobAtNum = productAtomNum.get(pIndex).intValue();
			IAtom glob_pa = product.getAtom(pGlobAtNum);
			
			sb.append("Map #" + rMapInd.intValue()+ 
					//"  P" + rNum + " A"+rAtNum + "  -->  R"+pNum+" A"+pAtNum+"  " +
				 "     " + 
				 "at# " + rGlobAtNum + 	
				 "  Charge = " +  reactantAtCharge.get(rGlobAtNum) +
				 "   -->  " + 
				 "at# " + pGlobAtNum +
				 "  Charge = " +  productAtCharge.get(pGlobAtNum) +				  
				 "         pIndex = " + pIndex
				 + "\n");
		}
		
		
		for (int i = 0; i <  prodBo.size(); i++)
		{
			String bo;
			sb.append("BondTransform: (");
			sb.append(reactAt1.get(i).intValue() + ", ");
			sb.append(reactAt2.get(i).intValue() + ", ");
			if (reactBo.get(i) == null)
				bo = "null";
			else	
				bo = reactBo.get(i).toString();
			
			sb.append( bo + ")  -->  (");
			sb.append(prodAt1.get(i).intValue() + ", ");
			sb.append(prodAt2.get(i).intValue() + ", ");
			if (prodBo.get(i) == null)
				bo = "null";
			else	
				bo = prodBo.get(i).toString();
			sb.append(bo.toString() + ")" );
			sb.append("\n");
		}
		return(sb.toString());
		
	}
	
}
