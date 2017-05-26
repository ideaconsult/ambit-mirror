package ambit2.sln.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

import ambit2.sln.SLNAtom;
import ambit2.sln.SLNBond;
import ambit2.sln.SLNContainer;
import ambit2.smarts.SMIRKSReaction;

/**
 * 
 * @author nick
 * Conversion of SLNContainer to chemical objects represented 
 * on top of CDK (AtomContainer, QueryAtomContainer)
 */

public class SLN2ChemObject 
{
	private List<String> conversionErrors = new ArrayList<String>();
	private List<String> conversionWarnings = new ArrayList<String>();
	
	private String currentConversionError = null;
	private String currentConversionWarning = null;

	public List<String> getConversionErrors() {
		return conversionErrors;
	}
	
	public List<String> getConversionWarnings() {
		return conversionWarnings;
	}
	
	public void clearAllErrorsAndWarnings(){
		conversionErrors.clear();
		conversionWarnings.clear();
	}
	
	public String getAllErrors()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < conversionErrors.size(); i++)
			sb.append(conversionErrors.get(i) + "\n");
		return sb.toString();
	}
	
	public LinearNotationType getCompatibleNotation(SLNContainer slnContainer)	{
		//TODO
		return null;
	}
	
	public SLNContainer atomContainerToSLNContainer(IAtomContainer container)
	{
		SLNContainer slnContainer = new SLNContainer(null);

		Map<IAtom, IAtom> convertedAtoms = new HashMap<IAtom, IAtom>();
		for (int i = 0; i < container.getAtomCount(); i++)
		{
			IAtom atom = container.getAtom(i);
			SLNAtom slnAtom = atomToSLNAtom(atom);
			slnContainer.addAtom(slnAtom);
			convertedAtoms.put(atom, slnAtom);
		}

		for (int i = 0; i < container.getBondCount(); i++)
		{
			IBond bond = container.getBond(i);
			SLNBond slnBond = bondToSLNBond(bond);
			IAtom newAtoms[] = new IAtom[2];
			newAtoms[0] = convertedAtoms.get(bond.getAtom(0));
			newAtoms[1] = convertedAtoms.get(bond.getAtom(1));
			slnBond.setAtoms(newAtoms);
			slnContainer.addBond(slnBond);
		}

		return slnContainer;
	}
	
	public IAtomContainer  slnContainerToAtomContainer(SLNContainer container)
	{
		//TODO
		return null;
	}
	
	public IQueryAtomContainer slnContainerToQueryAtomContainer(SLNContainer container)
	{
		//TODO
		return null;
	}
	
	public  SLNContainer QueryAtomContainerToSLNContainer(IQueryAtomContainer query)
	{
		//TODO
		return null;
	}
	
	public SMIRKSReaction slnContainerToSMIRKSReaction(SLNContainer container)
	{
		//TODO
		return null;
	}
	
	public  SLNContainer SMIRKSReactionToSLNContainer(SMIRKSReaction reaction)
	{
		//TODO
		return null;
	}
	
	public SLNAtom atomToSLNAtom(IAtom atom)
	{
		currentConversionError = null;
		currentConversionWarning = null;
		//TODO
		return null;
	}
	
	/*
	 * Convert only the bond type/expression info
	 * connected atoms info is not handled 
	 */
	public SLNBond bondToSLNBond(IBond bond)
	{
		currentConversionError = null;
		currentConversionWarning = null;
		//TODO
		return null;
	}
	
	public IAtom slnAtomToAtom(SLNAtom slnAt)
	{
		currentConversionError = null;
		currentConversionWarning = null;
		//TODO
		return null;
	}
	
	/*
	 * Convert only the bond type/expression info
	 * connected atoms info is not handled 
	 */
	public IBond slnBondToBond(SLNBond slnBo)
	{
		currentConversionError = null;
		currentConversionWarning = null;
		//TODO
		return null;
	}
	
	public SLNAtom queryAtomToSLNAtom(IQueryAtom queryAtom)
    {
        currentConversionError = null;
        currentConversionWarning = null;
        //TODO
        return null;
    }

    public SLNBond queryBondToSLNBond(IQueryBond queryBond)
    {
        currentConversionError = null;
        currentConversionWarning = null;
        //TODO
        return null;
    }

    public IQueryAtom slnAtomToQueryAtom(SLNAtom slnAt)
    {
        currentConversionError = null;
        currentConversionWarning = null;
        //TODO
        return null;
    }

    public IQueryBond slnBondToQueryBond(SLNBond slnBo)
    {
        currentConversionError = null;
        currentConversionWarning = null;
        //TODO
        return null;
    }

	
}
