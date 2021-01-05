package ambit2.sln;

import java.util.ArrayList;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Element;
import org.openscience.cdk.Isotope;
import org.openscience.cdk.interfaces.IAtom;

import ambit2.smarts.CMLUtilities;




public class SLNAtomExpression 
{
	public int atomID = -1;
	public int valences[] = null; //used for macro and markush atoms
	
	public ArrayList<SLNExpressionToken> tokens = new ArrayList<SLNExpressionToken>(); 
	//TODO handle brackets within expression

	public boolean matches(IAtom atom) 
	{	
		//Empty token list is used for SLNLogicalExpression encapsulating only an atom ID
		if (tokens.isEmpty())
			return true;  
		
		SLNLogicalExpression sle = new SLNLogicalExpression();
		for (int i = 0; i< tokens.size(); i++)
		{
			SLNExpressionToken tok = tokens.get(i);
			if (tok.type < SLNConst.LO)
			{	
				sle.addArgument(getArgument(tok, atom));
			}	
			else
				sle.addLogOperation(tok.type - SLNConst.LO);
		}
		return (sle.getValue()); 

	}

	boolean getArgument(SLNExpressionToken tok, IAtom atom)
	{
		switch (tok.type)
		{
		case SLNConst.A_ATTR_charge:{
			int charge = 0;
			if (atom.getFormalCharge() != null)
				charge = atom.getFormalCharge();
		
			if (SLNConst.compare(charge, tok.param, tok.comparisonOperation))
				return(true);
			else
				return(false);
		}	

		case SLNConst.A_ATTR_fcharge:
		{	
			//floating point charge (this is not formal charge)
			//When charge is unspecified false is returned
			if (atom.getCharge() == null)
				return false;
				
			if(SLNConst.compare(atom.getCharge(), tok.doubleParam, tok.comparisonOperation))
				return(true);
			else
				return(false);
		}	

		case SLNConst.A_ATTR_I:    		
			//When atom mass is unspecified false is returned
			if (atom.getMassNumber()== null) 
				return(false);

			if (SLNConst.compare(atom.getMassNumber(), tok.param, tok.comparisonOperation))
				return(true);
			else
				return(false);

		case SLNConst.A_ATTR_s:
			//TODO get targetStereo
			int targetStereo = SLNConst.A_STEREO_U; //temporary code
			return match_stereo(tok.param, targetStereo);
			
		case SLNConst.A_ATTR_spin:
			//TODO
			return false;

		case SLNConst.QA_ATTR_mapNum:
			//Atom map number used in reaction.
			//Nothing is done therefore return true
			return true;

		case SLNConst.QA_ATTR_c:
			Object covered = atom.getProperty("COVERED");
			if (covered == null)
			{
				if (tok.param == SLNConst.QA_COVERED_n || tok.param == SLNConst.QA_COVERED_o)
					return true;
				else
					return false;
			}
			else
			{
				if (tok.param == SLNConst.QA_COVERED_y || tok.param == SLNConst.QA_COVERED_o)
					return true;
				else
					return false;
			}
			
		case SLNConst.QA_ATTR_f:
			//filled valences
			//atom.getValency() gives the filled valences
			//atom.getBondOrderSum() gives the theoretically possible valences ??
			
			//System.out.println("   atom.getValency()=" + atom.getValency() + "  atom.getBondOrderSum()=" + atom.getBondOrderSum());
			if (atom.getValency() != null)
				if (atom.getBondOrderSum() != null)
					if (atom.getValency().doubleValue() == atom.getBondOrderSum())
						return (true);
			
			return (false);
			
		case SLNConst.QA_ATTR_is:
			//TODO  "is" attribute may define a sort of recursive SLN
			return false;

		case SLNConst.QA_ATTR_n:
			//not-covered flag.
			//It should be used not as a query attribute but as
			//an attribute for property definition
			return false;
			
		case SLNConst.QA_ATTR_not:
			//TODO "not" attribute may define a sort of recursive SLN
			return false;

		case SLNConst.QA_ATTR_v:
			//TODO Markush and macro atom valence
			return false;

		case SLNConst.QA_ATTR_r:
			int atomRings[] = (int[])atom.getProperty(CMLUtilities.RingData);
			if (atomRings != null)			
				return(true);
			else
				return(false);

		case SLNConst.QA_ATTR_hac:  
			if (tok.param == atom.getFormalNeighbourCount())
				return(true);
			else	
				return(false);

		case SLNConst.QA_ATTR_hc:
		{	
			Integer hci = atom.getImplicitHydrogenCount();
			int totalH = 0;
			if (hci != null)
				totalH = hci.intValue();    		

			Integer explicitH = (Integer)atom.getProperty(CMLUtilities.ExplicitH);
			if (explicitH != null)
				totalH+=explicitH.intValue();
			if (tok.param == totalH)
				return(true);
			else	
				return(false);	
		}	

		case SLNConst.QA_ATTR_htc:
		{
			Integer hci = atom.getImplicitHydrogenCount();
		
			int totalH = 0;
			int totalC = 0;
			if (hci != null)
				totalH = hci.intValue();    		

			Integer explicitH = (Integer)atom.getProperty(CMLUtilities.ExplicitH);
			if (explicitH != null)
				totalH+=explicitH.intValue();
			
			if (atom.getSymbol().equals('C'))
				totalC++;
			
			if (tok.param == (atom.getFormalNeighbourCount() -(totalC + totalH)))
				return(true);
			else	
				return(false);
		}
		
		case SLNConst.QA_ATTR_ntc:
			//TODO number of nonterminal atoms
			return false;

		case SLNConst.QA_ATTR_rbc:  
			//TODO improve: use comparison operation
			int atomRings1[] = (int[])atom.getProperty(CMLUtilities.RingData);
			return(match_rbc(atomRings1, tok.param, atom));

		case SLNConst.QA_ATTR_src:    		 
			int atomRings2[] = (int[])atom.getProperty(CMLUtilities.RingData);    		
			return(match_src(atomRings2, tok.param, tok.comparisonOperation, atom));	

		case SLNConst.QA_ATTR_tbo:
			if (tok.param == atom.getValency())
				return(true);
			else	
				return(false);	

		case SLNConst.QA_ATTR_tac:
		{	
			Integer hci = atom.getImplicitHydrogenCount();
			int hc = 0;
			if (hci != null)
				hc = hci.intValue();

			if (tok.param == atom.getFormalNeighbourCount()  + hc)
				return(true);
			else	
				return(false);	
		}	   		
		}
		return true; //by default
	}

	public boolean match_rbc(int atomRings[], int param,  IAtom atom)
	{
		
		if (atomRings == null)
		{
			if (param == 0)
				return(true);
			else
				return(false);
		}
		else
		{	
			if (param == -1) //This is a special value for default definition "rbc" only without an integer
			{
				if (atomRings.length > 0)
					return(true);
				else
					return(false);
			}
			else
			{
				//TODO to improve the rbc determination
				//This is not correct for spiro atoms: atomRings.length = 2 but rbc = 4;
				
				int rbc = atomRings.length + 1;
					
				if (param == rbc)
					return(true);
				else
					return(false);
			}
		}
	}

	public boolean match_src(int atomRings[], int param, int comparisonOperation, IAtom atom)
	{
		int src = 0;
		if (atomRings != null)
			src = atomRings.length;
		
		if (SLNConst.compare(src, param, comparisonOperation))
			return(true);
		else
			return(false);
	}
	
	boolean match_stereo(int param, int targetStereo)
    {
		if (param == SLNConst.A_STEREO_R || param == SLNConst.A_STEREO_N || param == SLNConst.A_STEREO_D ||
				param == SLNConst.A_STEREO_RE || param == SLNConst.A_STEREO_NE || param == SLNConst.A_STEREO_DE ||
				param == SLNConst.A_STEREO_R_ || param == SLNConst.A_STEREO_N_ || param == SLNConst.A_STEREO_D_ ||
				param == SLNConst.A_STEREO_RM || param == SLNConst.A_STEREO_NM || param == SLNConst.A_STEREO_DM)
		{
			if (targetStereo == SLNConst.A_STEREO_R)
				return true;
			else
				return false;
		}

		if (param == SLNConst.A_STEREO_S || param == SLNConst.A_STEREO_I || param == SLNConst.A_STEREO_L ||
				param == SLNConst.A_STEREO_SE || param == SLNConst.A_STEREO_IE || param == SLNConst.A_STEREO_LE ||
				param == SLNConst.A_STEREO_S_ || param == SLNConst.A_STEREO_I_ || param == SLNConst.A_STEREO_L_ ||
				param == SLNConst.A_STEREO_SM || param == SLNConst.A_STEREO_IM || param == SLNConst.A_STEREO_LM)
		{
			if (targetStereo == SLNConst.A_STEREO_L)
				return true;
			else
				return false;
		}
		
		return false;
    }




	boolean commonRingBond(int atomRingData1[], int atomRingData2[])
	{    	
		for (int i = 0; i < atomRingData1.length; i++)
			for (int k = 0; k < atomRingData2.length; i++)
			{	
				if (atomRingData1[i] == atomRingData1[k])
					return(true);
			}

		return(false);
	}

	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		
		if (atomID >= 0)
		{	
			sb.append(atomID);
			if (!tokens.isEmpty())
				sb.append(":");
		}
		
		//expression tokens
		for (int i=0; i < tokens.size(); i++)
			sb.append(tokens.get(i).toString(true));
		
		//valences
		if (valences != null)
		{	
			sb.append("v=");
			for (int i = 0; i < valences.length; i++)
			{	
				if (i > 0)
					sb.append(",");
				sb.append(valences[i]);
			}	
		}
		
		sb.append("]");
		return sb.toString();
	}
}
