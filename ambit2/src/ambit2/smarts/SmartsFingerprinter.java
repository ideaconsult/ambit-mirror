package ambit2.smarts;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
//import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
//import org.openscience.cdk.isomorphism.matchers.smarts.AromaticAtom;
//import org.openscience.cdk.isomorphism.matchers.smarts.AnyAtom;
//import org.openscience.cdk.isomorphism.matchers.smarts.AliphaticAtom;
import org.openscience.cdk.ringsearch.SSSRFinder;

import java.util.Vector;

public class SmartsFingerprinter 
{
	//When this flag is true atomatic bonds are forced between two aromatic atoms
	public boolean forceAromaticBonds = false; 
	
	//Internal work variable
	boolean mFlagConfirmAromaticBond;
		
	
	/** 
	 * Maximal possible atom container from this query is generated.
	 * This container may be fragmented since some original atoms or bonds could be missing.
	 *  
	 * Following rule is applied: each query atom/bond which is a simple expressaion is  
	 * converted to a normal IAtom/IBond object
	 * Also some heuristics are applied in order to get information from more 
	 * comlicated atoms expressions.  
	 * 
	 * @param query
	 * @return
	 */
	public IMolecule extractAtomContainer(QueryAtomContainer query)
	{
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
				
				if (mFlagConfirmAromaticBond)
				{
					if (forceAromaticBonds)
					{	
						b.setFlag(CDKConstants.ISAROMATIC,true);
					}
					else
					{	
						//Ring info should be used
						//TODO
					}	
				}
				container.addBond(b);
			}
		}	
		
		return(container);
	}
	
	
	/**
	 * This function tries to convert this object to a classic CDK Atom
	 * If it is imposible to determine the atom type null is returned
	 * @param a
	 * @return
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
	
	public  IAtom smartsExpressionToAtom(SmartsAtomExpression a)
	{			
		return(null);
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
			bond.setOrder(IBond.Order.SINGLE);			
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
	 
}
