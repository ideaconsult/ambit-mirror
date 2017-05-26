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

		Map<IAtom, SLNAtom> convertedAtoms = new HashMap<IAtom, SLNAtom>();
		for (int i = 0; i < container.getAtomCount(); i++)
		{
			IAtom atom = container.getAtom(i);
			SLNAtom slnAtom = atomToSLNAtom(atom);
			if (currentConversionWarning != null)
				conversionWarnings.add(currentConversionWarning + " for atom: " + (i+1));
			if (slnAtom == null)
			{
				conversionErrors.add(currentConversionError + " for atom: " + (i+1));
				continue;
			}
			slnContainer.addAtom(slnAtom);
			convertedAtoms.put(atom, slnAtom);
		}

		for (int i = 0; i < container.getBondCount(); i++)
		{
			IBond bond = container.getBond(i);
			SLNBond slnBond = bondToSLNBond(bond);
			if (currentConversionWarning != null)
				conversionWarnings.add(currentConversionWarning + " for bond: " + (i+1));
			if (slnBond == null)
			{
				conversionErrors.add(currentConversionError + " for bond: " + (i+1));
				continue;
			}
			SLNAtom newAtoms[] = new SLNAtom[2];
			newAtoms[0] = convertedAtoms.get(bond.getAtom(0));
			newAtoms[1] = convertedAtoms.get(bond.getAtom(1));
			if (newAtoms[0] == null || newAtoms[1] == null)
				continue; //one of the atoms is not converted
			slnBond.setAtoms(newAtoms);
			slnContainer.addBond(slnBond);
		}

		return slnContainer;
	}
	
	public IAtomContainer  slnContainerToAtomContainer(SLNContainer slnContainer)
	{
		 IAtomContainer container = new AtomContainer();
		 
		 Map<SLNAtom, IAtom> convertedAtoms = new HashMap<SLNAtom, IAtom>();

		 for (int i = 0; i < slnContainer.getAtomCount(); i++)
		 {
			 SLNAtom slnAtom = (SLNAtom) slnContainer.getAtom(i);
			 IAtom atom = slnAtomToAtom(slnAtom);
			 if (currentConversionWarning != null)
				 conversionWarnings.add(currentConversionWarning + " for atom: " + (i+1));
			 if (atom == null)
			 {
				 conversionErrors.add(currentConversionError + " for atom: " + (i+1));
				 continue;
			 }
			 container.addAtom(atom);
			 convertedAtoms.put(slnAtom, atom);
		 }

		 for (int i = 0; i < slnContainer.getBondCount(); i++)
		 {
			 SLNBond slnBbond = (SLNBond) container.getBond(i);
			 IBond bond = slnBondToBond(slnBbond);
			 if (currentConversionWarning != null)
				 conversionWarnings.add(currentConversionWarning + " for bond: " + (i+1));
			 if (bond == null)
			 {
				 conversionErrors.add(currentConversionError + " for bond: " + (i+1));
				 continue;
			 }
			 IAtom newAtoms[] = new IAtom[2];
			 newAtoms[0] = convertedAtoms.get(slnBbond.getAtom(0));
			 newAtoms[1] = convertedAtoms.get(slnBbond.getAtom(1));
			 if (newAtoms[0] == null || newAtoms[1] == null)
				continue; //one of the atoms is not converted
			 bond.setAtoms(newAtoms);
			 container.addBond(bond);
		 }

		 return container;
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
	 * Converts only the bond type/expression info
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
	 * Converts only the bond type/expression info
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
	
	/*
	 * Converts only the bond type/expression info
	 * connected atoms info is not handled 
	 */
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

    /*
	 * Converts only the bond type/expression info
	 * connected atoms info is not handled 
	 */
    public IQueryBond slnBondToQueryBond(SLNBond slnBo)
    {
        currentConversionError = null;
        currentConversionWarning = null;
        //TODO
        return null;
    }

	
}
