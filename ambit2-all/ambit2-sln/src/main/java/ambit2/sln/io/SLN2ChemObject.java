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
import ambit2.sln.io.SLN2ChemObjectConfig.ComparisonConversion;
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
		clearAllErrorsAndWarnings();
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
		clearAllErrorsAndWarnings();
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
		clearAllErrorsAndWarnings();
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
		clearAllErrorsAndWarnings();
		//TODO
		return null;
	}
	
	public SMIRKSReaction slnContainerToSMIRKSReaction(SLNContainer container)
	{
		clearAllErrorsAndWarnings();
		//TODO
		return null;
	}
	
	public  SLNContainer SMIRKSReactionToSLNContainer(SMIRKSReaction reaction)
	{
		clearAllErrorsAndWarnings();
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
        
        if ((slnAt.atomType > 0)  && (slnAt.atomType < SLNConst.GlobalDictOffseet))
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
		if (slnAt.atomType < SLNConst.GlobalDictOffseet)
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
    	if (slnAt.numHAtom > 0)
    	{
    		//Insert logical operation AND_LO 
        	atExpr.tokens.add(new SmartsExpressionToken(SmartsConst.LO + SmartsConst.LO_ANDLO, 0));
    		atExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_H, slnAt.numHAtom));
    	}	
    	
    	if (slnAt.atomExpression == null || slnAt.atomExpression.tokens.isEmpty())
    		return atExpr;
    	
    	//Insert logical operation AND_LO 
    	atExpr.tokens.add(new SmartsExpressionToken(SmartsConst.LO + SmartsConst.LO_ANDLO, 0));
    	
    	//Converting SLN atom expression into SMARTS atom expression
    	for (int i = 0; i < slnAt.atomExpression.tokens.size(); i++)
    	{	
    		SLNExpressionToken slnTok = slnAt.atomExpression.tokens.get(i);
    		
    		if (slnTok.isLogicalOperation())
    		{
    			SmartsExpressionToken logOpTok = slnLogOperationToSmartsExpressionToken(slnTok.getLogOperation());
    			atExpr.tokens.add(logOpTok);
    		}
    		else
    		{
    			SmartsExpressionToken smToks[] = slnExpressionTokenToSmartsExpressionTokenArray(slnTok);
    			if (smToks != null)
    			{	
    				for (int k = 0; k < smToks.length; k++)
    					atExpr.tokens.add(smToks[k]);
    			}
    			else
    			{
    				//Handle logical operation primitives (due to the token omitted)
    				
    				//Check for preceding NOT
    				if (!atExpr.tokens.isEmpty())
    				{
    					int n = atExpr.tokens.size();
    					SmartsExpressionToken tok = atExpr.tokens.get(n-1);
    					if (tok.type == SmartsConst.LO + SmartsConst.LO_NOT)
    						atExpr.tokens.remove(n-1);
    				}
    				
    				//TODO Improve: check for the type of LO
    				if (!atExpr.tokens.isEmpty())
    				{
    					int n = atExpr.tokens.size();
    					SmartsExpressionToken tok = atExpr.tokens.get(n-1);
    					if (tok.isLogicalOperation())
    						atExpr.tokens.remove(n-1);
    				}
    				
    			}
    		}
    	}	
    	
    	return atExpr;
    }
    
        
    public SmartsBondExpression slnBondToSmartsBondExpression(SLNBond slnBo)
    {
    	SmartsBondExpression boExpr = new SmartsBondExpression(SilentChemObjectBuilder.getInstance());
    	
    	//TODO
    	return boExpr;
    }
    
    public SmartsExpressionToken slnLogOperationToSmartsExpressionToken(int logOp) 
    {
    	switch (logOp)
		{
		case SLNConst.LO_NOT:
			return new SmartsExpressionToken(SmartsConst.LO + SmartsConst.LO_NOT, 0);
			
		case SLNConst.LO_AND:
			return new SmartsExpressionToken(SmartsConst.LO + SmartsConst.LO_AND, 0);
		
		case SLNConst.LO_OR:
			return new SmartsExpressionToken(SmartsConst.LO + SmartsConst.LO_OR, 0);
		
		case SLNConst.LO_ANDLO:
			return new SmartsExpressionToken(SmartsConst.LO + SmartsConst.LO_ANDLO, 0);
		}
    	return null;
    }
    
    public SmartsExpressionToken[] slnExpressionTokenToSmartsExpressionTokenArray(SLNExpressionToken slnTok)
    {
    	int conversionStatus = 0;//-1 do not convert, 0 convert as equal, 1 generate list of alternative values
    	String warning = null;
    	
    	//Determine conversionStatus
    	switch(slnTok.comparisonOperation)
		{
		case SLNConst.CO_EQUALS:
			conversionStatus = 0;			
			break;
			
		case SLNConst.CO_LESS_THAN:
		case SLNConst.CO_GREATER_THAN:	
			switch (conversionConfig.FlagComparisonConversion) 
			{
			case omit:
				conversionStatus = -1;
				warning = "logical primitive is not converted";
				break;
			case convert_as_equal:
				conversionStatus = 0;
				warning = "logical primitive converted as equal comparison";
				break;
			case convert_as_equal_if_equal_is_present:
				conversionStatus = -1;
				warning = "logical primitive is not converted";
				break;
			case convert_as_equal_if_not_nonequality:
				conversionStatus = 0;
				warning = "logical primitive converted as equal comparison";
				break;
			case convert_as_value_list:
				conversionStatus = 1;
				warning = "logical primitive converted as value list";
				break;	
			}
			break;
			
		case SLNConst.CO_LESS_OR_EQUALS:
		case SLNConst.CO_GREATER_OR_EQUALS:	
			switch (conversionConfig.FlagComparisonConversion) 
			{
			case omit:
				conversionStatus = -1;
				warning = "logical primitive is not converted";
				break;
			case convert_as_equal:
				conversionStatus = 0;
				warning = "logical primitive converted as equal comparison";
				break;
			case convert_as_equal_if_equal_is_present:
				conversionStatus = 0;
				warning = "logical primitive converted as equal comparison";
				break;
			case convert_as_equal_if_not_nonequality:
				conversionStatus = 0;
				warning = "logical primitive converted as equal comparison";
				break;
			case convert_as_value_list:
				conversionStatus = 1;
				warning = "logical primitive converted as value list";
				break;	
			}
			break;
			
		case SLNConst.CO_DIFFERS:
			switch (conversionConfig.FlagComparisonConversion) 
			{
			case omit:
				conversionStatus = -1;
				warning = "logical primitive is not converted";
				break;
			case convert_as_equal:
				conversionStatus = 0;
				warning = "logical primitive converted as equal comparison";
				break;
			case convert_as_equal_if_equal_is_present:
				conversionStatus = -1;
				warning = "logical primitive is not converted";
				break;
			case convert_as_equal_if_not_nonequality:
				conversionStatus = -1;
				warning = "logical primitive is not converted";
				break;
			case convert_as_value_list:
				conversionStatus = 1;
				warning = "logical primitive converted as value list";
				break;	
			}
			break;
		}    
    	
    	if (warning != null)
			addToCurrentConversionWarning(slnTok.toString(true) + "  " + warning);
    	
    	if (conversionStatus < 0)
    		return null;
    	else if (conversionStatus == 0)
    	{
    		SmartsExpressionToken tok = slnExpressionTokenToSmartsExpressionToken(slnTok);    		
    		if (tok != null)
    			return new SmartsExpressionToken[] {tok};	
    	}
    	else
    	{
    		//Generate an array of tokens based on the alternative values;
    		int altValues[] = getSmartsTokenAlternativeValues(slnTok);
    		//TODO
    	}
    	
    	return null;
    }
    
    public SmartsExpressionToken slnExpressionTokenToSmartsExpressionToken(SLNExpressionToken slnTok)
    {
    	//htc, ntc and rbc cannot be converted
    	
    	switch (slnTok.type)
    	{
    	case SLNConst.A_ATTR_charge:
    		return new SmartsExpressionToken(SmartsConst.AP_Charge, slnTok.param);
    		
    	case SLNConst.QA_ATTR_r:
    		return new SmartsExpressionToken(SmartsConst.AP_R, 1);
    	case SLNConst.QA_ATTR_hac:
    		return new SmartsExpressionToken(SmartsConst.AP_D, slnTok.param);	
    	case SLNConst.QA_ATTR_hc:
    		return new SmartsExpressionToken(SmartsConst.AP_H, slnTok.param);	
    	case SLNConst.QA_ATTR_tac:
    		return new SmartsExpressionToken(SmartsConst.AP_X, slnTok.param);
    	case SLNConst.QA_ATTR_tbo:
    		return new SmartsExpressionToken(SmartsConst.AP_v, slnTok.param);
    	case SLNConst.QA_ATTR_src:
    		return new SmartsExpressionToken(SmartsConst.AP_R, slnTok.param);
    	case SLNConst.QA_ATTR_type:
    		return new SmartsExpressionToken(SmartsConst.AP_AtNum, slnTok.param);
    	
    	}
    	
    	//TODO improve warning for attributes that cannot be converted e.g. user defined
    	
    	addToCurrentConversionWarning("Atom attribute: " + slnTok.attrName + " is not converted");
    	return null;
    }
    
    public int[] getSmartsTokenAlternativeValues(SLNExpressionToken slnTok)
    {
    	//Set min and max values;
    	int minValue=0;
    	int maxValue=0;
    	
    	switch (slnTok.type)
    	{
    	case SLNConst.A_ATTR_charge:
    		minValue = conversionConfig.min_charge;
    		maxValue = conversionConfig.max_charge;
    		break;    		
    	//case SLNConst.QA_ATTR_r:
    	//not handled here
    	case SLNConst.QA_ATTR_hac:
    		minValue = conversionConfig.min_hac;
    		maxValue = conversionConfig.max_hac;
    		break;
    	case SLNConst.QA_ATTR_hc:
    		minValue = conversionConfig.min_hc;
    		maxValue = conversionConfig.max_hc;
    		break;	
    	case SLNConst.QA_ATTR_tac:
    		minValue = conversionConfig.min_tac;
    		maxValue = conversionConfig.max_tac;
    		break;
    	case SLNConst.QA_ATTR_tbo:
    		minValue = conversionConfig.min_tbo;
    		maxValue = conversionConfig.max_tbo;
    		break;
    	case SLNConst.QA_ATTR_src:
    		minValue = conversionConfig.min_tbo;
    		maxValue = conversionConfig.max_tbo;
    		break;    	
    	case SLNConst.QA_ATTR_type:
    		minValue = conversionConfig.min_type;
    		maxValue = conversionConfig.max_type;
    		break;
    	default:
    		//This token type is not handled
    		return null;	
    	} 
    	    	
    	//Set start and end values according to the
    	//param value and comparison operation
    	int startVal = minValue;
    	int endVal = maxValue;
    	Integer excludeVal = null;
    	    	
    	switch(slnTok.comparisonOperation)
		{
		case SLNConst.CO_EQUALS:
			startVal = slnTok.param;
			endVal = slnTok.param;
			break;
		case SLNConst.CO_LESS_THAN:
			endVal = slnTok.param-1;
			break;
		case SLNConst.CO_LESS_OR_EQUALS:
			endVal = slnTok.param;
			break;	
		case SLNConst.CO_GREATER_THAN:	
			startVal = slnTok.param+1;
			break;
		case SLNConst.CO_GREATER_OR_EQUALS:	
			startVal = slnTok.param;
		case SLNConst.CO_DIFFERS:			
			excludeVal = slnTok.param;
			break;
		}
    	
    	int n = endVal - startVal + 1;
    	
    	if (excludeVal != null)
    		if ( (excludeVal >= startVal) &&
    				(excludeVal <= endVal) )
    			n--;
    	
    	if (n <= 0)
    		return null;
    	
    	int values[] = new int[n];
    	
    	
    	if (excludeVal == null)
    	{	
    		for (int i = 0; i <n; i++)
    			values[i] = startVal + i;
    	}
    	else
    	{	
    		int curIndex = 0;
    		for (int i = 0; i <n; i++)
    		{	
    			int val = startVal + i;
    			if (val == excludeVal)
    				continue;
    			values[curIndex] = startVal + i;
    			curIndex++;
    		}	
    	}
    	
    	return values;
    }
    
    public SmartsExpressionToken getAlternativeSmartsToken(SLNExpressionToken slnTok, int altValue)
    {
    	//htc, ntc and rbc cannot be converted
    	
    	switch (slnTok.type)
    	{
    	case SLNConst.A_ATTR_charge:
    		return new SmartsExpressionToken(SmartsConst.AP_Charge, altValue);
    		
    	//case SLNConst.QA_ATTR_r:
    	//	return new SmartsExpressionToken(SmartsConst.AP_R, 1); - Not needed
    		
    	case SLNConst.QA_ATTR_hac:
    		return new SmartsExpressionToken(SmartsConst.AP_D, altValue);	
    	case SLNConst.QA_ATTR_hc:
    		return new SmartsExpressionToken(SmartsConst.AP_H, altValue);	
    	case SLNConst.QA_ATTR_tac:
    		return new SmartsExpressionToken(SmartsConst.AP_X, altValue);
    	case SLNConst.QA_ATTR_tbo:
    		return new SmartsExpressionToken(SmartsConst.AP_v, altValue);
    	case SLNConst.QA_ATTR_src:
    		return new SmartsExpressionToken(SmartsConst.AP_R, altValue);
    	case SLNConst.QA_ATTR_type:
    		return new SmartsExpressionToken(SmartsConst.AP_AtNum, altValue);
    	
    	}
    	
    	//No error is generated.
    	
    	return null;
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
    
    void addToCurrentConversionError(String err)
    {
    	if (currentConversionError == null)
    		currentConversionError = err;
    	else
    		currentConversionError += (err + " ");
    }

    void addToCurrentConversionWarning(String warning)
    {
    	if (currentConversionWarning == null)
    		currentConversionWarning = warning;
    	else
    		currentConversionWarning += (warning + " ");
    }
	
}
