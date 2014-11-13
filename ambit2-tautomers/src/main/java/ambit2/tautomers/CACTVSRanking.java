package ambit2.tautomers;

import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

public class CACTVSRanking 
{
	public static final double score2eV = 0.05;
	
	/**
	 * 
	 * @param mol
	 * @return tautomer score as defined in J. Comput. Aided. Mol. Des. (2010) 24:521-551 
	 * (see table2 at page 530) 
	 * additional simpler score table from J. Chem. Inf. Mod. Vol.46, No.6, 2006 (page 2349 table 2)  
	 */
	public static double calcScoreRank(IAtomContainer mol)
	{
		//int nCarbocyclicAromRing = 0;  
		int nAromAt = 0;
		//int nBenzQuin = 0;
		int nOximGroup = 0;
		int nC2O = 0;
		int nN2O = 0;
		int nP2O = 0;
		int nC2X = 0; //non aromatic bond
		int nCH3 = 0;
		//int nGuanidine = 0; + guanidine group with endocyclic double bond
		int nYH = 0; //P-H, S-H, Se-H and Te-H
		int nAciNitro = 0;
		
		for (IAtom atom : mol.atoms())
		{
			//Check aromatic
			if (atom.getFlag(CDKConstants.ISAROMATIC))
			{
				nAromAt++;
				continue;
			}			
			
			if (checkMethylGroup(atom, mol))
			{	
				nCH3++;
			}
				
			//Check oxim group (C=N[OH]) and aci-nitro group (C=N(=O)[OH])
			int res = checkOximAciNitro(atom, mol);
			if (res == 1)
			{
				nOximGroup++;
				continue;
			}
			else
				if (res == 2)
				{
					nAciNitro++;
					continue;
				}
						
			//Check Y-H (P-H, S-H, Se-H or Te-H)
			nYH+=checkPSSeTeH(atom, mol);
		}
		
		for (IBond bond : mol.bonds())
		{
			if (bond.getOrder() == IBond.Order.DOUBLE)
			{	
				IAtom at0 = bond.getAtom(0);
				IAtom at1 = bond.getAtom(1);
				
				//bonds C2O, N2O, P2O
				if (at0.getSymbol().equals("O"))
				{
					if (at1.getSymbol().equals("C"))
						nC2O++;
					else
						if (at1.getSymbol().equals("N"))
							nN2O++;
						else
							if (at1.getSymbol().equals("P"))
								nP2O++;
								
				}
				else
					if (at1.getSymbol().equals("O"))
					{
						if (at0.getSymbol().equals("C"))
							nC2O++;
						else
							if (at0.getSymbol().equals("N"))
								nN2O++;
							else
								if (at0.getSymbol().equals("P"))
									nP2O++;
					}
				
				//Non aromatic double bond C=X
				if (!at0.getFlag(CDKConstants.ISAROMATIC) && !at1.getFlag(CDKConstants.ISAROMATIC))
				{
					if (at0.getSymbol().equals("C"))
					{
						if (!at1.getSymbol().equals("C") && !at1.getSymbol().equals("H"))
							nC2X++;
					}
					else
						if (at1.getSymbol().equals("C"))
						{
							if (!at0.getSymbol().equals("C") && !at0.getSymbol().equals("H"))
								nC2X++;
						}
				}
			}		
		}
		
		//Correction due to the aci-nitro group. Aci-nitro group is not counted in N=O and C=X
		nN2O = nN2O - nAciNitro;
		nC2X = nC2X - nAciNitro;
		
		//Correction due to the oxim group. Oxim group is not counted in C=X
		nC2X = nC2X - nOximGroup;
		
		//correction for C=O. It is not counted in C=X
		nC2X = nC2X - nC2O;
		
		//System.out.println(scoreFragmentsToString( nAromAt, nOximGroup, nC2O, nN2O, nP2O, nC2X, nCH3, nYH, nAciNitro));
		
		double rank = nAromAt*(100.0/6) + nOximGroup * 4 +  
				 (nC2O + nN2O + nP2O)*2 + nC2X + nCH3 - nYH - 4*nAciNitro;
		return rank;
	}
	
	public static double getEnergyRank(IAtomContainer mol)
	{
		return (- score2eV * calcScoreRank(mol));
	}
	
	public static boolean checkMethylGroup(IAtom atom, IAtomContainer mol)
	{
		if (!atom.getSymbol().equals("C"))			
			return false;
		
		if (getHNeighbours(atom, mol)==3) 
			return true;
		else 
			return false;
	}
	
	/**
	 * 
	 * @param atom - target atom
	 * @param mol - target molecule
	 * @return 1 if oxim group (C=N[OH]), 2 if aci-nitro group (C=N(=O)[OH]), otherwise return 0
	 */	
	public static int checkOximAciNitro(IAtom atom, IAtomContainer mol)
	{	
		if (!atom.getSymbol().equals("N"))
			return 0;
		
		List<IAtom> list = mol.getConnectedAtomsList(atom);
		if (list.size() < 2)
			return 0;
		
		boolean Flag2C = false;
		boolean FlagOH = false;
		boolean Flag2O = false;
		for (IAtom at : list)
		{
			if (at.getSymbol().equals("C"))
				if (mol.getBond(atom, at).getOrder() == IBond.Order.DOUBLE)
				{	
					Flag2C = true;
					continue;
				}	
			
			if (at.getSymbol().equals("O"))
				if (mol.getBond(atom, at).getOrder() == IBond.Order.SINGLE)
				{	
					if (getHNeighbours(at, mol) == 1)
					{	
						FlagOH = true;
						continue;
					}	
				}
				else
				{
					//It must be a double bond 
					Flag2O = true;
					continue;
				}
			
		}
		
		if (Flag2C && FlagOH)
		{
			if (Flag2O)
				return 2;
			else
				return 1;
		}
		
		return 0;
	}
	
	public static int checkPSSeTeH(IAtom atom, IAtomContainer mol)
	{
		String atSy = atom.getSymbol();
		if (atSy.equals("P") || atSy.equals("S") || atSy.equals("Se") || atSy.equals("Te"))
		{	
			//System.out.println("atom " + atSy + "  H" + getHNeighbours(atom, mol));
			return getHNeighbours(atom, mol); //Each H neigbour defined a bond Y-H (Y=P,S,Se,Te)
		}	
		return 0;
	}
	
	public static int getHNeighbours(IAtom atom, IAtomContainer mol) 
	{
		List<IAtom> list = mol.getConnectedAtomsList(atom);
		Integer intNH = atom.getImplicitHydrogenCount();
		int nH = 0;
		if (intNH != null)
			nH = intNH;
		
		for (IAtom at : list)
			if (at.getSymbol().equals("H"))
				nH++;
		
		return nH;
	}
	
	public static String scoreFragmentsToString(int nAromAt, int nOximGroup, int nC2O, int nN2O, int nP2O, 
												int nC2X, int nCH3, 	int nYH, int nAciNitro)
	{
		StringBuffer sb = new StringBuffer();
		if (nAromAt > 0)
			sb.append("nAromAt = " + nAromAt + "\n");
		if (nOximGroup > 0)
			sb.append("nOximGroup = " + nOximGroup + "\n");
		if (nC2O > 0)
			sb.append("nC=O = " + nC2O + "\n");
		if (nN2O > 0)
			sb.append("nN=O = " + nN2O + "\n");
		if (nP2O > 0)
			sb.append("nP=O = " + nP2O + "\n");
		if (nC2X > 0)
			sb.append("nC=X = " + nC2X + "\n");
		if (nCH3 > 0)
			sb.append("nCH3 = " + nCH3 + "\n");
		if (nYH > 0)
			sb.append("nY-H = " + nYH + "     (Y=P,S,Se,Te,)\n");
		if (nAciNitro > 0)
			sb.append("nAciNitro = " + nAciNitro + "\n");
		
		return sb.toString();
	}
	
}
