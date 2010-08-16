package ambit2.smarts;

import java.util.Vector;
import java.util.BitSet;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;

public class SmartsToChemObject  extends DefaultAmbitProcessor<QueryAtomContainer, IAtomContainer>
{
	
	private static final long serialVersionUID = -5893878673124511317L;
	
	
	public static final String markProperty = "MARKED_AB";

	//When this flag is true aromatic bonds are forced between any two aromatic atoms 
	public boolean forceAromaticBondsAlways = false; 
		
	//When this flag is true aromatic bonds are forced between atoms which are not part of a ring
	//e.g. the SMARTS Query describes a part of aromatic cycle "cccc"
	//But this flag will not force the bond aromaticity for the single bond in the biphenyl.
	//c1ccccc1c2ccccc2 will be interpreted as c1ccccc1-c2ccccc2
	public boolean forceAromaticBondsForNonRingAtoms = true;
	
	//Internal work variable
	boolean mFlagConfirmAromaticBond;
	
	//Work variables which are set by functions analyzeSubExpressionsFromLowAnd and getExpressionAtomType
	int mSubAtomType, mSubAromaticity, mCurSubArom, mRecCurSubArom;
		
	
	/** 
	 * Maximal possible atom container from this query is generated.
	 * This container may be fragmented since some original atoms or bonds could be removed.
	 *  
	 * Following rule is applied: each query atom/bond which is a simple expression is  
	 * converted to a normal IAtom/IBond object
	 * Also some heuristics are applied in order to get information from more 
	 * complicated atoms expressions (when possible).  
	 * 
	 * @param query
	 * @return atomcontainer
	 */
	public IAtomContainer extractAtomContainer(QueryAtomContainer query, IRingSet ringSet)
	{
		if (query == null) return null;
		//Converting the atoms
		Vector<IAtom> atoms = new Vector<IAtom>();
		for (int i = 0; i < query.getAtomCount(); i++)
			atoms.add(toAtom(query.getAtom(i)));
		
		//Adding the atoms
		Molecule container = new Molecule();
		for (int i = 0; i < atoms.size(); i++)
		{	
			IAtom a = atoms.get(i); 
			if (a != null)
				container.addAtom(a);
		}
		
		//Converting and adding the bonds
		for (int i = 0; i < query.getBondCount(); i++)			
		{
			mFlagConfirmAromaticBond = false;
			IBond b = toBond(query.getBond(i));
			if (b != null)
			{
				IAtom[] ats = new IAtom[2];
				int atNum = query.getAtomNumber(query.getBond(i).getAtom(0));				
				ats[0] = atoms.get(atNum);
				if (ats[0] == null)
					continue;
				
				atNum = query.getAtomNumber(query.getBond(i).getAtom(1));				
				ats[1] = atoms.get(atNum);
				if (ats[1] == null)
					continue;
				b.setAtoms(ats);
				
				
				if ((mFlagConfirmAromaticBond) && 
					(ats[0].getFlag(CDKConstants.ISAROMATIC))  &&  (ats[1].getFlag(CDKConstants.ISAROMATIC)))
				{
					if (forceAromaticBondsAlways)
					{	
						b.setFlag(CDKConstants.ISAROMATIC,true);
					}
					else
					{	
						//Ring info is used to determine whether this bond must have aromatic flag
						if (ringSet == null)
						{
							if (forceAromaticBondsForNonRingAtoms)
								b.setFlag(CDKConstants.ISAROMATIC,true);
						}
						else
						{	
							if (isRingBond(query.getBond(i), ringSet))
								b.setFlag(CDKConstants.ISAROMATIC,true);
						}						
					}	
				}
				container.addBond(b);
			}
		}	
		
		return(container);
	}
	
	
	/**
	 * Maximal possible atom container from this query is generated BUT 
	 * this container is always fully connected as the original query.
	 * The "removed" atoms and bonds are marked with a set property
	 * This object is useful for visualization purposes.
	 */	
	public IAtomContainer extractAtomContainerFullyConnected(QueryAtomContainer query, IRingSet ringSet)
	{
		if (query == null) return null;
		//Converting the atoms
		Vector<IAtom> atoms = new Vector<IAtom>();
		for (int i = 0; i < query.getAtomCount(); i++)
		{	
			IAtom a = toAtom(query.getAtom(i));			
			if (a == null)			
				a = getMarkedAtom();  //a marked "dummy" atom is added instead
			atoms.add(a);
		}	
		
		//Adding the atoms
		Molecule container = new Molecule();
		for (int i = 0; i < atoms.size(); i++)
		{	
			IAtom a = atoms.get(i); 
			container.addAtom(a);
		}
		
		//Converting and adding the bonds
		for (int i = 0; i < query.getBondCount(); i++)			
		{
			mFlagConfirmAromaticBond = false;
			IBond b = toBond(query.getBond(i));
			
			//Setting the atoms incident with  bond
			IAtom[] ats = new IAtom[2];
			int atNum = query.getAtomNumber(query.getBond(i).getAtom(0));				
			ats[0] = atoms.get(atNum);
			atNum = query.getAtomNumber(query.getBond(i).getAtom(1));				
			ats[1] = atoms.get(atNum);
			
			if (b != null)
			{	
				b.setAtoms(ats);				
				
				if ((mFlagConfirmAromaticBond) && 
					(ats[0].getFlag(CDKConstants.ISAROMATIC))  &&  (ats[1].getFlag(CDKConstants.ISAROMATIC)))
				{
					if (forceAromaticBondsAlways)
					{	
						b.setFlag(CDKConstants.ISAROMATIC,true);
					}
					else
					{	
						//Ring info is used to determine whether this bond must have aromatic flag
						if (ringSet == null)
						{
							if (forceAromaticBondsForNonRingAtoms)
								b.setFlag(CDKConstants.ISAROMATIC,true);
						}
						else
						{	
							if (isRingBond(query.getBond(i), ringSet))
								b.setFlag(CDKConstants.ISAROMATIC,true);
						}						
					}	
				}
				container.addBond(b);
			}
			else  // b == null
			{
				b = getMarkedBond();
				b.setAtoms(ats);
				container.addBond(b);
			}
			
		}	
		
		return(container);
	}
	
	public QueryAtomContainer convertKekuleSmartsToAromatic (QueryAtomContainer query, IRingSet ringSet)
	{
		Vector<IRingSet> rs = getMaxCondensedRingSystems(ringSet);
		if (rs.size() == 0)
			return(query);
		
		for (int i = 0; i < rs.size(); i++)
		{
			QueryAtomContainer qac = getCondensedFragmentFromRingSets(query, rs.get(i));
			IAtomContainer container = condensedFragmentToContainer(qac);
			if (container != null)
			{	
				String smiles = SmartsHelper.moleculeToSMILES(container);
				System.out.print("condensed: " + smiles);
			}
			else
				System.out.println("condensed: null");
			//TODO - perceive aromaticity 
			
			
			//This is helper code for debugging
			IAtomContainer ac = extractAtomContainerFullyConnected(qac, ringSet);
			String smiles = SmartsHelper.moleculeToSMILES(ac);
			System.out.print("extract: " + smiles);
		}
		
		return(null);
	}
	
	
	
	public IAtomContainer process(QueryAtomContainer target)
			throws AmbitException {
		return extractAtomContainer(target);
	}
	
	/** Version of the function when the Ring data is not supplied outside*/
	public IAtomContainer extractAtomContainer(QueryAtomContainer query)
	{
		SSSRFinder sssrf = new SSSRFinder(query);
		IRingSet ringSet = sssrf.findSSSR();
		
		return(extractAtomContainer(query,ringSet));
	}
	
	public IAtomContainer extractAtomContainerFullyConnected(QueryAtomContainer query)
	{
		SSSRFinder sssrf = new SSSRFinder(query);
		IRingSet ringSet = sssrf.findSSSR();
		
		return(extractAtomContainerFullyConnected(query,ringSet));
	}
	
	public QueryAtomContainer convertKekuleSmartsToAromatic (QueryAtomContainer query)
	{
		SSSRFinder sssrf = new SSSRFinder(query);
		IRingSet ringSet = sssrf.findSSSR();
		
		return(convertKekuleSmartsToAromatic(query, ringSet));
	}
	
	
	
	
	
	/**
	 * This function tries to convert this object to a classic CDK Atom
	 * If it is impossible to determine the atom type null is returned
	 * @param a
	 * @return atom
	 */
	public  IAtom toAtom(IAtom a)
	{					
		if (a instanceof AliphaticSymbolQueryAtom)
		{	
			Atom atom = new Atom();
			atom.setSymbol(a.getSymbol());
			atom.setFlag(CDKConstants.ISAROMATIC,false);			
			return(atom);
		}	
		
		if (a instanceof AromaticSymbolQueryAtom)
		{	
			Atom atom = new Atom();
			atom.setSymbol(a.getSymbol());
			atom.setFlag(CDKConstants.ISAROMATIC,true);
			return(atom);
		}	
		
		if (a instanceof SmartsAtomExpression)
			return(smartsExpressionToAtom((SmartsAtomExpression)a));
				
		//All these cases generate null atom
		//	AliphaticAtom				
		//	AromaticAtom
		//	AnyAtom							
		return(null);
	}
	
	/** This function tries to convert the SmartsAtomExpression to
	 * a IAtom with defined atom type. When this is impossible null object is returned
	 *  
	 **/
	public  IAtom smartsExpressionToAtom(SmartsAtomExpression a)
	{			
		//In order to extract the atom type from a SmartsAtomExpresion 
		//following rules are applied:
		// 1. The expression is represented as a sequence of sub expressions 
		//    separated by "LOW_AND" operation:   sub1; sub2; sub3; ...
		// 2. Each expression is checked whether it defines clearly an atom type
		// 3. If only one expression defines an atom type then this is assigned as the 
		//    result atom type 
		//    or if two or more expressions define atom type these atom types
		//    must be the same
		
		//System.out.println("\nConverting expression: " +  a.toString());
		
		Vector<SmartsAtomExpression> subs = getSubExpressions(a, SmartsConst.LO+ SmartsConst.LO_ANDLO);
		int atType = -1;
		int isArom = -1;			
		for (int  i = 0; i < subs.size(); i++)
		{	
			analyzeSubExpressionsFromLowAnd(a, subs.get(i));
			//System.out.print("  sub-expression " +  subs.get(i).toString());
			//System.out.println("    mSubAtomType = " + mSubAtomType + "  mSubAromaticity = " + mSubAromaticity);
			if (mSubAtomType != -1)
			{
				if (atType == -1)					
					atType = mSubAtomType;				
				else
				{
					if (atType != mSubAtomType)
					{
						atType = -1; //Atom Type is not defined correctly 
						break;
					}
				}
			}
			
			//Handling aromaticity 
			if (mSubAromaticity != -1)
			{
				if (isArom == -1)
					isArom = mSubAromaticity;
				else
				{
					if (isArom != mSubAromaticity)
					{	
						isArom = -1;  //Aromaticity is not defined correctly 
						break;
					}
				}
			}
		}
		
		if ((atType != -1)&&(isArom != -1))
		{
			Atom atom = new Atom();			
			atom.setSymbol(PeriodicTable.getSymbol(atType));			
						
			//Setting the aromaticity
			if (isArom == 1)				
				atom.setFlag(CDKConstants.ISAROMATIC,true);
			else
				atom.setFlag(CDKConstants.ISAROMATIC,false);
							
			return(atom);
		}
		return(null);
	}
	
	public Vector<SmartsAtomExpression> getSubExpressions(SmartsAtomExpression a, int separator)
	{
		Vector<SmartsAtomExpression> v = new Vector<SmartsAtomExpression>();
		SmartsAtomExpression sub = new SmartsAtomExpression();
		for (int i = 0; i < a.tokens.size(); i++)
		{
			if (a.tokens.get(i).type == separator)
			{
				v.add(sub);
				sub = new SmartsAtomExpression();
			}
			else
				sub.tokens.add(a.tokens.get(i));
		}
		v.add(sub);		
		return v;
	}
	
	public void analyzeSubExpressionsFromLowAnd(SmartsAtomExpression atExp, SmartsAtomExpression sub)
	{
		//The sub expression sub is represented as a sequence of sub-sub expressions
		//separated by logical 'OR' 
		//Following rule is applied
		//	If at least one sub-sub expression has a atom type 
		//  then all other sub-subs must have the same type
		//Analogously the aromaticity is treated
		
		Vector<SmartsAtomExpression> sub_subs = getSubExpressions(sub, SmartsConst.LO+SmartsConst.LO_OR);
		int subAtType[] = new int[sub_subs.size()];
		int subArom[] = new int[sub_subs.size()];
		for (int i = 0; i <sub_subs.size(); i++)
		{	
			subAtType[i] = getExpressionAtomType(atExp,sub_subs.get(i));
			subArom[i] = mCurSubArom;
		}
		
		mSubAtomType = subAtType[0];
		for (int i = 1; i < subAtType.length; i++)
		{
			if (mSubAtomType != subAtType[i])
			{
				mSubAtomType = -1;
				break;
			}
		}
		
		mSubAromaticity = subArom[0];
		for (int i = 1; i < subAtType.length; i++)
		{
			if (mSubAromaticity != subArom[i])
			{
				mSubAromaticity = -1;
				break;
			}
		}
	}
	
	public int getExpressionAtomType(SmartsAtomExpression atExp, SmartsAtomExpression sub)
	{
		//'sub' expression is represented only by HI_AND and NOT operations		
		mCurSubArom = -1;
		
		//Getting the positions of HI_AND tokens
		int pos[] = new int[sub.tokens.size()+2];
		pos[0] = -1;
		int n = 0; 
		for (int i = 0; i < sub.tokens.size(); i++)
		{
			if (sub.tokens.get(i).type == SmartsConst.LO+SmartsConst.LO_AND)
			{
				n++;   
				pos[n] = i;
			}
		}
		
		n++;   
		pos[n] = sub.tokens.size();
		
		int expAtType = -1;
		boolean FlagNot;
		SmartsExpressionToken seTok;
		
		//using 1-based indexing for 'pos' array.
		//pos[0] = -1 and  pos[n] = sub.tokens.size() have special use for both ends of the token sequence
		for (int i = 1; i <= n; i++)
		{			
			//Handling the tokens between pos[i-1] and pos[i]
			FlagNot = false;
			for (int k = pos[i-1]+1; k < pos[i]; k++)
			{
				seTok = sub.tokens.get(k);
				if (seTok.isLogicalOperation())
				{
					if (seTok.getLogOperation() == SmartsConst.LO + SmartsConst.LO_NOT)					
						FlagNot = !FlagNot;
					
					if (seTok.getLogOperation() == SmartsConst.LO_AND)					
						FlagNot = false;  //'Not' flag is reseted 
					
					continue;
				}
				
				//Handling atom primitives. 
				//When given primitive defines an atom type it must not be negated 				
				switch (seTok.type)
				{	
				case SmartsConst.AP_a:					
					if (seTok.param > 0)
						if (!FlagNot)
						{
							expAtType = seTok.param; 
							mCurSubArom = 1;
						}
					break;
					
				case SmartsConst.AP_A:
					if (seTok.param > 0)
						if (!FlagNot)
						{
							expAtType = seTok.param; 
							mCurSubArom = 0;
						}
					break;	
					
				case SmartsConst.AP_AtNum:
					if (seTok.param > 0)
						if (!FlagNot)
							expAtType = seTok.param;
					break;	
					
				case SmartsConst.AP_Recursive:
					int recExpAtType = getRecursiveExpressionAtomType(atExp,seTok.param);
					if (recExpAtType > 0)
						if (!FlagNot)
						{	
							expAtType = recExpAtType;
							mCurSubArom = mRecCurSubArom;
						}	
					break;
					
				//All other token types do not effect function result
				}
			}
		}		
		
		return(expAtType);
	}
	
	public int getRecursiveExpressionAtomType(SmartsAtomExpression atExp, int n)
	{
		//Recursive call is performed. That is way the values of the global variables are stored
		//and afterwards restored
		int mSubAtomType_old, mSubAromaticity_old, mCurSubArom_old;
		mSubAtomType_old = mSubAtomType;
		mSubAromaticity_old = mSubAromaticity;
		mCurSubArom_old = mCurSubArom;
		
		//Potential recursion here		
		IAtom a0 = atExp.recSmartsContainers.get(n).getAtom(0);
		IAtom anew =  toAtom(a0);
		if (a0.getFlag(CDKConstants.ISAROMATIC))
			mRecCurSubArom = 1;
		else
			mRecCurSubArom = 0;
		
		//Restoring the global work variables
		mSubAtomType = mSubAtomType_old;
		mSubAromaticity = mSubAromaticity_old;
		mCurSubArom = mCurSubArom_old;
		
		if (anew == null)			
			return -1;
		else
			return(SmartsConst.getElementNumber(anew.getSymbol()));
	}
	
	
	public  IBond toBond(IBond b)
	{		
		if (b instanceof SmartsBondExpression)
			return(smartsExpressionToBond((SmartsBondExpression)b));
				
		if (b instanceof SingleOrAromaticBond)
		{	
			Bond bond = new Bond();
			bond.setOrder(IBond.Order.SINGLE);
			mFlagConfirmAromaticBond = true;			
			return(bond);
		}	
		
		if (b instanceof AromaticQueryBond)
		{	
			Bond bond = new Bond();
			bond.setOrder(b.getOrder());
			bond.setFlag(CDKConstants.ISAROMATIC,true);
			return(bond);
		}	
		
		if (b instanceof OrderQueryBond)
		{	
			Bond bond = new Bond();			
			bond.setOrder(b.getOrder());
			return(bond);
		}	
		
		//All these cases generate null bond:
		//	AnyOrderQueryBond
		//	RingQueryBond	
		return(null);
	}
	
	
	public  IBond smartsExpressionToBond(SmartsBondExpression b)
	{	
		return(null);
	}
	
	boolean isRingBond(IBond b, IRingSet ringSet)
	{
		IRingSet atom0Rings = ringSet.getRings(b.getAtom(0));
		IRingSet atom1Rings = ringSet.getRings(b.getAtom(1));
		
		for (int i  = 0; i < atom0Rings.getAtomContainerCount(); i++)
		{	
			IAtomContainer c = atom0Rings.getAtomContainer(i);
			for (int k  = 0; k < atom1Rings.getAtomContainerCount(); k++)
				if (atom1Rings.getAtomContainer(k) == c)
					return(true);
		}
		return(false);
	}
	
	IAtom getMarkedAtom()
	{
		Atom a = new Atom("C");
		a.setProperty(SmartsToChemObject.markProperty,new Integer(1));
		return(a);
	}
	
	IBond getMarkedBond()
	{
		Bond b = new Bond();
		b.setOrder(Order.SINGLE);
		b.setProperty(SmartsToChemObject.markProperty,new Integer(1));
		return(b);	
	}
	
	/**
	 * This function finds all condensed ring systems and returns
	 * a vector where each element of is a maximal condensed system
	 */
	Vector<IRingSet> getMaxCondensedRingSystems(IRingSet ringSet)
	{			
		Vector<IRingSet> v = new Vector<IRingSet>();
		int n = ringSet.getAtomContainerCount();
		if (n == 0)
			return(v);  //empty container is returned
		
		IRingSet workRS = new RingSet();
		workRS.add(ringSet);
		//workRS = filterAromaticSmartsRings(workRS);
		
		while (workRS.getAtomContainerCount() > 0)
		{	
			IAtomContainer ac = workRS.getAtomContainer(0);
			IRingSet rs = getCondenzedRingsTo(ac, ringSet);
			v.add(rs);
			//Removing the rs elements from workRS
			for (int i = 0; i < rs.getAtomContainerCount();i++)
			{
				workRS.removeAtomContainer(rs.getAtomContainer(i));
			}
		}
		return v;
	}
	
	
	IRingSet getCondenzedRingsTo(IAtomContainer startAC, IRingSet ringSet)
	{			
		RingSet condRS = new RingSet();
		int curRing = 0;
		condRS.addAtomContainer(startAC);
		
		while (curRing < condRS.getAtomContainerCount())
		{
			//Adding all neighbors of the currentRing which are not in rs container
			IRingSet rsConnected = ringSet.getConnectedRings((IRing)condRS.getAtomContainer(curRing));
			for (int i = 0; i < rsConnected.getAtomContainerCount(); i++)
			{
				if (!condRS.contains(rsConnected.getAtomContainer(i)))
					condRS.addAtomContainer(rsConnected.getAtomContainer(i));
			}
			curRing++; //The cycle will stop if curRing is the last one and no other rings are added
		}
		
		return (condRS);
	}
	
	QueryAtomContainer getCondensedFragmentFromRingSets(QueryAtomContainer query, IRingSet rs)
	{
		//All atoms and bonds from each ring are added
		//Check for duplication of atoms and bonds is done
		
		QueryAtomContainer qac = new QueryAtomContainer();
		for (int i = 0; i < rs.getAtomContainerCount(); i++)
		{
			IAtomContainer ac = rs.getAtomContainer(i);
			//Adding atoms
			for (int k = 0; k < ac.getAtomCount(); k++)
			{
				IAtom a = ac.getAtom(k);
				if (!qac.contains(a))
					qac.addAtom(a);
			}
			//Adding bonds
			for (int k = 0; k < ac.getBondCount(); k++)
			{
				IBond b = ac.getBond(k);
				if (!qac.contains(b))
					qac.addBond(b);
			}
		}
				
		return(qac);
	}
		
	
	IAtomContainer condensedFragmentToContainer(QueryAtomContainer frag)
	{
		Molecule container = new Molecule();
		
		//Converting atoms
		Vector<IAtom> atoms = new Vector<IAtom>();
		for (int i = 0; i < frag.getAtomCount(); i++)
		{
			IAtom a = toAtom(frag.getAtom(i));
			if (a == null)
				return(null);
			if (a.getFlag(CDKConstants.ISAROMATIC))
				return(null);
			atoms.add(a);
		}
		
		//Adding atoms
		for (int i = 0; i < atoms.size(); i++)
		{	
			IAtom a = atoms.get(i);
			container.addAtom(a);
		}
				
		for (int i = 0; i < frag.getBondCount(); i++)			
		{
			mFlagConfirmAromaticBond = false;
			IBond b = toBond(frag.getBond(i));
			if (b != null)
			{
				IAtom[] ats = new IAtom[2];
				int atNum = frag.getAtomNumber(frag.getBond(i).getAtom(0));				
				ats[0] = atoms.get(atNum);
				atNum = frag.getAtomNumber(frag.getBond(i).getAtom(1));				
				ats[1] = atoms.get(atNum);
				b.setAtoms(ats);
				
				container.addBond(b);
			}
		}
		
		return (container);
	}
}
