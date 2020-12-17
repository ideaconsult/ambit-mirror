package ambit2.sln.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.sln.SLNAtom;
import ambit2.sln.SLNAtomExpression;
import ambit2.sln.SLNBond;
import ambit2.sln.SLNConst;
import ambit2.sln.SLNContainer;
import ambit2.sln.SLNExpressionToken;
import ambit2.smarts.AliphaticSymbolQueryAtom;
import ambit2.smarts.DoubleBondAromaticityNotSpecified;
import ambit2.smarts.DoubleNonAromaticBond;
import ambit2.smarts.SMIRKSReaction;
import ambit2.smarts.SingleBondAromaticityNotSpecified;
import ambit2.smarts.SingleNonAromaticBond;
import ambit2.smarts.SingleOrAromaticBond;
import ambit2.smarts.SmartsAtomExpression;
import ambit2.smarts.SmartsBondExpression;
import ambit2.smarts.SmartsConst;
import ambit2.smarts.SmartsExpressionToken;

/**
 * 
 * @author nick
 * Conversion of SLNContainer to chemical objects represented 
 * on top of CDK (AtomContainer, QueryAtomContainer)
 */

public class SLN2ChemObject 
{
	public static class ExpressionAtomInfo
	{
		public Integer charge = null;
		public Integer isotope = null;
	}
	
	private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
	private SLN2ChemObjectConfig conversionConfig = null;
	
	private List<String> conversionErrors = new ArrayList<String>();
	private List<String> conversionWarnings = new ArrayList<String>();
	
	private String currentConversionError = null;
	private String currentConversionWarning = null;
	
	public SLN2ChemObject() {
		conversionConfig = new SLN2ChemObjectConfig();
	}
	
	public SLN2ChemObject(SLN2ChemObjectConfig conversionConfig) {
		this.conversionConfig = conversionConfig;
	}
		
	public SLN2ChemObjectConfig getConversionConfig() {
		return conversionConfig;
	}

	public boolean hasConversionErrors() {
		return (conversionErrors.size() > 0);
	}
	
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
			{	
				conversionErrors.add("One of the atoms for bond: " + (i+1) + " is not converted");
				continue; //one of the atoms is not converted
			}
			slnBond.setAtoms(newAtoms);
			slnContainer.addBond(slnBond);
		}

		return slnContainer;
	}
	
	public IAtomContainer slnContainerToAtomContainer(SLNContainer slnContainer)
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
			 SLNBond slnBbond = (SLNBond) slnContainer.getBond(i);
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

		 try {
			 processMolecule(container);
		 }
		 catch (Exception x) {
			 conversionErrors.add(x.getMessage());
		 } 
		 
		 return container;
	}
	
	public IQueryAtomContainer slnContainerToQueryAtomContainer(SLNContainer slnContainer)
	{
		IQueryAtomContainer container = new QueryAtomContainer(SilentChemObjectBuilder.getInstance()); 		
		Map<SLNAtom, IQueryAtom> convertedAtoms = new HashMap<SLNAtom, IQueryAtom>();
		
		 for (int i = 0; i < slnContainer.getAtomCount(); i++)
		 {
			 SLNAtom slnAtom = (SLNAtom) slnContainer.getAtom(i);
			 IQueryAtom atom = slnAtomToQueryAtom(slnAtom);
			 
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
			 SLNBond slnBbond = (SLNBond) slnContainer.getBond(i);
			 IQueryBond bond = slnBondToQueryBond(slnBbond);
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
	
	public SLNContainer QueryAtomContainerToSLNContainer(IQueryAtomContainer query)
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
		if (atom == null)
		{	
			currentConversionError = "Atom is null";
			return null;
		}	
		SLNAtom slnAt = new SLNAtom(builder);
		String atomName = atom.getSymbol();
		for (int i = 0; i < SLNConst.elSymbols.length; i++)
			if (atomName.equals(SLNConst.elSymbols[i]))
			{	
				slnAt.atomType = i;
				break;
			}	
		slnAt.atomName = atomName;
		
		if (atom.getImplicitHydrogenCount() != null)
			slnAt.numHAtom = atom.getImplicitHydrogenCount();
		
		//TODO handle: isotope, charge
		
		return slnAt;
	}
	
	/*
	 * Converts only the bond type/expression info
	 * connected atoms info is not handled 
	 */
	public SLNBond bondToSLNBond(IBond bond)
	{
		currentConversionError = null;
		currentConversionWarning = null;
		if (bond == null)
		{	
			currentConversionError = "Bond is null";
			return null;
		}	
		SLNBond slnBo = new SLNBond(builder);
		slnBo.bondType = bond.getOrder().ordinal() + 1;
		return slnBo;
	}
	
	public IAtom slnAtomToAtom(SLNAtom slnAt)
	{
		currentConversionError = null;
		currentConversionWarning = null;
		if (slnAt == null)
        {
            currentConversionError = "SNLAtom is null";
            return null;
        }
        
        if ((slnAt.atomType > 0)  && (slnAt.atomType < SLNConst.GlobDictOffseet))
        {
        	if (slnAt.atomType < SLNConst.elSymbols.length)
        	{	
        		IAtom atom = new Atom();
        		atom.setSymbol(SLNConst.elSymbols[slnAt.atomType]);
        		atom.setAtomicNumber(PeriodicTable.getAtomicNumber(SLNConst.elSymbols[slnAt.atomType]));
        		atom.setImplicitHydrogenCount(slnAt.numHAtom);
        		
        		if (slnAt.atomExpression != null)
        		{
        			Integer charge = extractFormalCharge(slnAt.atomExpression);
        			if (charge != null)
        				if (charge != 0)
        					atom.setFormalCharge(charge);
        			
        			//TODO isotope
        		}
        		return atom;
        	}
        	else
        	{
        		currentConversionError = "SNLAtom type is incorrect: " + slnAt.atomType;
            	return null;
        	}
        }
        else
        {
        	currentConversionError = "SNLAtom type is not defined";
        	return null;
        }  
	}
	
	/*
	 * Converts only the bond type/expression info
	 * connected atoms info is not handled 
	 */
	public IBond slnBondToBond(SLNBond slnBo)
	{
		currentConversionError = null;
		currentConversionWarning = null;
		if (slnBo == null)
        {
            currentConversionError = "Bond is null";
            return null;
        }
		
		if (slnBo.bondType != 0)
		{
			IBond bond = new Bond();
			switch (slnBo.bondType)
			{
			case 1:
				bond.setOrder(IBond.Order.SINGLE);
				break;
			case 2:
				bond.setOrder(IBond.Order.DOUBLE);
				break;
			case 3:
				bond.setOrder(IBond.Order.TRIPLE);
				break;	
			}
			return bond;
		}
		else
		{		
			currentConversionError = "Bond type is not defined";
			return null;
		}	
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
		if (slnAt == null)
        {           
			currentConversionError = "SNLAtom is null";
            return null;
        }
		
		if (slnAt.atomType == 0)
		{			
			if (slnAt.atomExpression == null || slnAt.atomExpression.tokens.isEmpty())
			{	
				if (slnAt.numHAtom == 0)
					return new AnyAtom(SilentChemObjectBuilder.getInstance());
			}	
			
			//Handle Any atom as an expression
			//slnAt.numHAtom > 0 info is added into the expression
			
			SmartsAtomExpression atExpr = slnAtomToSmartsAtomExpression(slnAt);
			return atExpr;	
		}
		
		//slnAt.atomType > 0
		if (slnAt.atomType < SLNConst.GlobDictOffseet)
		{
			if (slnAt.atomType < SLNConst.elSymbols.length)
			{	
				if (slnAt.atomExpression == null || slnAt.atomExpression.tokens.isEmpty())
				{
					if (slnAt.numHAtom == 0)
					{
						AliphaticSymbolQueryAtom atom = new AliphaticSymbolQueryAtom(SilentChemObjectBuilder.getInstance());
						String symb = SLNConst.elSymbols[slnAt.atomType];
						atom.setSymbol(symb);
						atom.setAtomicNumber(PeriodicTable.getAtomicNumber(symb));
						return atom;
					}
					else
					{
						//Since there are H atoms they define an expression
						//slnAt.numHAtom > 0 info is added into the expression
						
					}
				}
				
				//Create an expression
				SmartsAtomExpression atExpr = slnAtomToSmartsAtomExpression(slnAt);
				return atExpr;				
			}
			else
			{
				//TODO
				return null;
			}
		}
		else
		{
			//TODO
			return null;
		}
    }

    /*
	 * Converts only the bond type/expression info
	 * connected atoms info is not handled 
	 */
    public IQueryBond slnBondToQueryBond(SLNBond slnBo)
    {              
        currentConversionError = null;
		currentConversionWarning = null;
		if (slnBo == null)
        {
            currentConversionError = "Bond is null";
            return null;
        }
		
		if (slnBo.bondType == 0)
		{
			if (slnBo.bondExpression == null)
				return new AnyOrderQueryBond(SilentChemObjectBuilder.getInstance());
			
			//TODO handle bond expression
		}
		else
		{
			//slnBo.bondType > 0
			
			if (slnBo.bondExpression == null)
			{			 
				IQueryBond bond = null;
				switch (slnBo.bondType)
				{
				case 1:
					if (conversionConfig.FlagSLNSingleBondToSingleOrAromaticBond)
						bond = new SingleOrAromaticBond(SilentChemObjectBuilder.getInstance());					
					else if (conversionConfig.FlagSupportSingleBondAromaticityNotSpecified)
						bond = new SingleBondAromaticityNotSpecified(SilentChemObjectBuilder.getInstance());
					else
						bond = new SingleNonAromaticBond(SilentChemObjectBuilder.getInstance());
					break;					
				case 2:
					if (conversionConfig.FlagSupportDoubleBondAromaticityNotSpecified)
						bond = new DoubleBondAromaticityNotSpecified(SilentChemObjectBuilder.getInstance());
					else
						bond = new DoubleNonAromaticBond(SilentChemObjectBuilder.getInstance());
					break;
				case 3:
					bond = new OrderQueryBond(IBond.Order.TRIPLE, SilentChemObjectBuilder.getInstance());
					break;	
				}
				
				return bond;
			}
			
			//TODO handle bond expression
			
		}
		
		return null;
			
    }
    
    
    public SmartsAtomExpression slnAtomToSmartsAtomExpression(SLNAtom slnAt)
    {
    	SmartsAtomExpression atExpr = new SmartsAtomExpression(SilentChemObjectBuilder.getInstance());
    	
    	//Adding atom name
    	if (slnAt.atomType == 0)
    	{
    		atExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_ANY, 0));
    	}
    	else
    	{
    		if (slnAt.atomType < SLNConst.elSymbols.length)
    			atExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_A, slnAt.atomType));
    		else
    		{
    			//Handle dictionary info
    		}
    	}
    	
    	//Adding implicit H atoms from the name
    	//Insert logical operation AND 
    	atExpr.tokens.add(new SmartsExpressionToken(SmartsConst.LO + SmartsConst.LO_AND, 0));
    	if (slnAt.numHAtom > 0)
    		atExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_H, slnAt.numHAtom));
    	
    	//TODO
    	return atExpr;
    }
    
    public SmartsBondExpression slnBondToSmartsBondExpression(SLNBond slnBo)
    {
    	SmartsBondExpression boExpr = new SmartsBondExpression(SilentChemObjectBuilder.getInstance());
    	
    	//TODO
    	return boExpr;
    }
    
    public static ExpressionAtomInfo extractSimpleAtomInfoFromExprresion(SLNAtomExpression slnAE)
    {
    	ExpressionAtomInfo eai = new ExpressionAtomInfo();
    	eai.charge = extractFormalCharge(slnAE);
    	return eai;		
    }
    
     
    public static Integer extractFormalCharge(SLNAtomExpression slnAE)
    {
    	//Simple extraction from expression
    	for (int i = 0; i < slnAE.tokens.size(); i++)
    	{
    		SLNExpressionToken tok = slnAE.tokens.get(i);
    		if (tok.type == SLNConst.A_ATTR_charge)
    		{
    			if (tok.comparisonOperation == SLNConst.CO_EQUALS)
    				return tok.param;
    		}
    	}
    	return null;
    }
    
    public static void processMolecule(IAtomContainer mol) throws Exception
    {
    	AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
    	CDKHueckelAromaticityDetector.detectAromaticity(mol);
    }

	
}
