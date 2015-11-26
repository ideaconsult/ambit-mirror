package ambit2.tautomers.ranking;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

public class TautomerStringCode 
{
	public static String getCode0(IAtomContainer tautomer, boolean treatAromBondsAsEquivalent)
	{
		StringBuffer sb = new StringBuffer();
		
		//All bonds are described in canonical numbering (i < j) 
		for (int i = 0; i < tautomer.getAtomCount(); i++)
		{
			for (int j = i+1; j < tautomer.getAtomCount(); j++)
			{
				IBond bo = tautomer.getBond(tautomer.getAtom(i), tautomer.getAtom(j));
				if (bo != null)
				{	
					if (bo.getFlag(CDKConstants.ISAROMATIC))
					{	
						if (treatAromBondsAsEquivalent)
							sb.append("a");
						else
							sb.append(bo.getOrder().ordinal());
					}		
					else							
						sb.append(bo.getOrder().ordinal());
				}	
			}
		}
		
		return(sb.toString());
	}
	
	
	//This information can be used for more efficient calculation 
	//of the string code
	public static int[] getBondIndexSequence(IAtomContainer tautomer)
	{
		int bondIndices[] = new int [tautomer.getBondCount()];
		
		int n = 0;
		//All bonds are scanned in a canonical numbering of the atoms (i < j) 
		for (int i = 0; i < tautomer.getAtomCount(); i++)
		{
			for (int j = i+1; j < tautomer.getAtomCount(); j++)
			{
				IBond bo = tautomer.getBond(tautomer.getAtom(i), tautomer.getAtom(j));
				if (bo != null)
				{	
					//Found next bond for the code description
					bondIndices[n] = tautomer.getBondNumber(bo);
					n++;
				}	
			}
		}

		return bondIndices;
	}
	
	public static String getCode(IAtomContainer tautomer, boolean treatAromBondsAsEquivalent, int bondSequence[])
	{	
		StringBuffer sb = new StringBuffer();
		
		//All bonds are described in bondSequence[]
		for (int i = 0; i < bondSequence.length; i++)
		{	
			IBond bo = tautomer.getBond(bondSequence[i]);
			if (bo.getFlag(CDKConstants.ISAROMATIC))
			{	
				if (treatAromBondsAsEquivalent)
					sb.append("a");
				else
					sb.append(bo.getOrder().ordinal());
			}		
			else							
				sb.append(bo.getOrder().ordinal());
		}
		
		return(sb.toString());
	}
	
}
