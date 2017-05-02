package ambit2.smarts.smirks;

import java.util.List;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.data.MoleculeTools;
import ambit2.smarts.CMLUtilities;
import ambit2.smarts.SmartsConst.HandleHAtoms;

public class Transformations 
{
	public static void fixSHValence7Atoms(IAtomContainer mol)
	{
		for (IAtom at: mol.atoms())
		{
			if (!at.getSymbol().equals("S"))
				continue;
			if (at.getImplicitHydrogenCount() == null)
				continue;
			if (at.getImplicitHydrogenCount() != 1)
				continue;
			
			//If valence is not set bond order sum is calculated
			List<IBond> boList = mol.getConnectedBondsList(at);
			int boSum = 0;
			for (IBond bo: boList)
				boSum += bo.getOrder().numeric();
			
			//System.out.println("***** boSum " + boSum);
			if (boSum == 6)
				at.setImplicitHydrogenCount(0);
		}
	}
	
	public static void setAtomHNeighbours(IAtom atom, IAtomContainer target, int numH, HandleHAtoms hTransformMode)
    {
    	//For all transformation modes both implicit and explicit H atoms are taken into account
    	Integer hc_im = atom.getImplicitHydrogenCount();
    	if (hc_im == null)
    		hc_im = 0;
    	Integer hc_ex = (Integer) atom.getProperty(CMLUtilities.ExplicitH); //This flag is set
    	if (hc_ex == null)
    		hc_ex = 0;
    	
    	int totalH = hc_im + hc_ex;
    	
    	if (numH == (totalH))
    		return; //The current H atom count is exactly numH
    	
    	if (numH > totalH)
    	{
    		//Adding H Atoms
    		int hDiff = numH - totalH;
    		switch (hTransformMode)
    		{
    		case IMPLICIT:
    			hc_im = hc_im + hDiff;
    			atom.setImplicitHydrogenCount(hc_im);
    			break;
    		case EXPLICIT:
    			addExplcitHNeighbours(atom, target, hDiff);
    			break;
    		}
    	}
    	else //numH < totalH
    	{	
    		//Removing H Atoms
    		int hDiff = totalH - numH;
    		switch (hTransformMode)
    		{
    		case IMPLICIT:
    			if (hc_im >= hDiff)
    			{	
    				hc_im = hc_im - hDiff;
    				atom.setImplicitHydrogenCount(hc_im);
    			}
    			else
    			{
    				atom.setImplicitHydrogenCount(0);
    				//Additionally removing explicit H atoms
    				removeExplcitHNeighbours(atom, target, hDiff-hc_im);
    			}
    			break;
    		case EXPLICIT:
    			//TODO
    			break;
    		}
    	}
    }
    
    public static void addExplcitHNeighbours(IAtom atom, IAtomContainer target, int numH)
    {
    	for (int i = 0; i < numH; i++)
    	{
    		IAtom ha = MoleculeTools.newAtom(target.getBuilder()); 
    		ha.setSymbol("H");
    		target.addAtom(ha);
    		IBond b = MoleculeTools.newBond(target.getBuilder());
			b.setAtoms(new IAtom[] { atom, ha });
			b.setOrder(IBond.Order.SINGLE);
			target.addBond(b);
    	}
    }
    
    public static int removeExplcitHNeighbours(IAtom atom, IAtomContainer target, int numH)
    {
    	List<IBond> boList = target.getConnectedBondsList(atom);
		int nRemovedHAtoms = 0;
		for (IBond bo: boList)
		{	
			if (nRemovedHAtoms >= numH)
				break;
			
			IAtom ha = null;
			if (bo.getAtom(0).getSymbol().equals("H"))
				ha = bo.getAtom(0);
			else
			{	
				if (bo.getAtom(1).getSymbol().equals("H"))
					ha = bo.getAtom(1);
				else
					continue;
			}
			
			target.removeBond(bo);
			target.removeAtom(ha);
			nRemovedHAtoms++;
		}
		
    	return nRemovedHAtoms;
    }
	
	public static void process(IAtomContainer mol) throws Exception
	{
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
	}
}
