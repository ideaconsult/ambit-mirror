package ambit2.sln;

import java.util.ArrayList;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;

import ambit2.smarts.CMLUtilities;




public class SLNAtomExpression 
{
	public int atomID = -1;
	public ArrayList<SLNExpressionToken> tokens = new ArrayList<SLNExpressionToken>(); 

	public boolean matches(IAtom atom) 
	{	
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
		case SLNConst.A_ATTR_charge:
			if (atom.getFormalCharge() == tok.param)
				return(true);
			else
				return(false);

		case SLNConst.A_ATTR_fcharge:
			if (atom.getCharge() == tok.doubleParam)
				return(true);
			else
				return(false);

		case SLNConst.A_ATTR_I:    		
			//When atom mass is unspecified false is returned
			if (atom.getMassNumber()== null) 
				return(false); 
			if (atom.getMassNumber()== 0) 
				return(false); 

			if (atom.getMassNumber()== tok.param)
				return(true);
			else
				return(false);

		case SLNConst.A_ATTR_s:
			//It is assumed that PLUS is R and MINUS is S
			if (tok.param == SLNConst.A_STEREO_R || tok.param == SLNConst.A_STEREO_N || tok.param == SLNConst.A_STEREO_D ||
				 tok.param == SLNConst.A_STEREO_RE || tok.param == SLNConst.A_STEREO_NE || tok.param == SLNConst.A_STEREO_DE ||
				 tok.param == SLNConst.A_STEREO_R_ || tok.param == SLNConst.A_STEREO_N_ || tok.param == SLNConst.A_STEREO_D_ ||
				 tok.param == SLNConst.A_STEREO_RM || tok.param == SLNConst.A_STEREO_NM || tok.param == SLNConst.A_STEREO_DM)
			{
				if (atom.getStereoParity() == CDKConstants.STEREO_ATOM_PARITY_PLUS);
					return(true);
			}
			if (tok.param == SLNConst.A_STEREO_S || tok.param == SLNConst.A_STEREO_I || tok.param == SLNConst.A_STEREO_L ||
					 tok.param == SLNConst.A_STEREO_SE || tok.param == SLNConst.A_STEREO_IE || tok.param == SLNConst.A_STEREO_LE ||
					 tok.param == SLNConst.A_STEREO_S_ || tok.param == SLNConst.A_STEREO_I_ || tok.param == SLNConst.A_STEREO_L_ ||
					 tok.param == SLNConst.A_STEREO_SM || tok.param == SLNConst.A_STEREO_IM || tok.param == SLNConst.A_STEREO_LM)
			{
				if (atom.getStereoParity() == CDKConstants.STEREO_ATOM_PARITY_MINUS)
					return (true);
			}

		case SLNConst.A_ATTR_spin:
			//TODO

		case SLNConst.QA_ATTR_mapNum:
			//TODO map number to an atom

		case SLNConst.QA_ATTR_c:
			//TODO covered

		case SLNConst.QA_ATTR_f:
			//TODO atom is filled

		case SLNConst.QA_ATTR_is:
			//TODO  atom in a query must match

		case SLNConst.QA_ATTR_n:
			//TODO atom that matches a query atom from being covered

		case SLNConst.QA_ATTR_not:
			//TODO atom in a query must not match

		case SLNConst.QA_ATTR_v:
			//TODO Markush and macro atom valence

		case SLNConst.QA_ATTR_r:
			if (atom.getFlag(CDKConstants.ISINRING))
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
			/*
        	https://sourceforge.net/tracker/?func=detail&aid=3020065&group_id=20024&atid=120024
			Integer hci = atom.getHydrogenCount();
			 */
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

		case SLNConst.QA_ATTR_rbc:    		 
			int atomRings[] = (int[])atom.getProperty(CMLUtilities.RingData);
			return(match_rbc(atomRings, tok.param, atom));

		case SLNConst.QA_ATTR_src:    		 
			int atomRings2[] = (int[])atom.getProperty(CMLUtilities.RingData);    		
			return(match_src(atomRings2, tok.param, atom));	

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
				if (param == atomRings.length)
					return(true);
				else
					return(false);
			}
		}
	}

	public boolean match_src(int atomRings[], int param,  IAtom atom)
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
			if (param < 3) // value 1 is possible here 
			{
				if (atomRings.length > 0)
					return(true);
				else
					return(false);
			}
			else
			{
				for (int i = 0; i < atomRings.length; i++)
				{
					if (atomRings[i] == param)
						return(true);
				}
				return(false);
			}
		}
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
		for (int i=0; i < tokens.size(); i++)
			sb.append(tokens.get(i).toString(true));
		sb.append("]");
		return sb.toString();
	}
}
