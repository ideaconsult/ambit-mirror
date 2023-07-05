package ambit2.smarts;

public class StereoChiralAtTransformation 
{
	//Reactant
	public int reactChiralAtom = -1;
	public int reactLigands[] = null; //atom indices include mapped and unmapped atoms
	public int reactTerminal1 = -1;
	public int reactTerminal2 = -1;
	public int reactChirality = SmartsConst.ChC_Unspec;
	
	//Product
	public int prodChiralAtom = -1;
	public int prodLigands[] = null; //atom indices include mapped and unmapped atoms
	public int prodLigandsReactMap[] = null; //if mapped reactant partners may not be in the same order as specified in reactLigand
	public int prodTerminal1 = -1;
	public int prodTerminal2 = -1;
	public int prodChirality = SmartsConst.ChC_Unspec;
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("  reactChiralAtom = " + reactChiralAtom);
		if (reactLigands != null) {
			sb.append("  reactLigands= ");
			for (int i = 0; i < reactLigands.length; i++)
				sb.append(" " + reactLigands[i]);
		}		
		if (reactTerminal1 != -1)
			sb.append("  reactTerminal1 = " + reactTerminal1);
		if (reactTerminal2 != -1)
			sb.append("  reactTerminal2 = " + reactTerminal2);	
		sb.append("  reactChirality = " + reactChirality);
		sb.append("\n");
		
		sb.append("  prodChiralAtom = " + prodChiralAtom);
		if (prodLigands != null) {
			sb.append("  prodLigands= ");
			for (int i = 0; i < prodLigands.length; i++)
				sb.append(" " + prodLigands[i]);
		}
		if (prodLigandsReactMap != null) {
			sb.append("  prodLigandsReactMap= ");
			for (int i = 0; i < prodLigandsReactMap.length; i++)
				sb.append(" " + prodLigandsReactMap[i]);
		}
		if (prodTerminal1 != -1)
			sb.append("  prodTerminal1 = " + prodTerminal1);
		if (prodTerminal2 != -1)
			sb.append("  prodTerminal2 = " + prodTerminal2);	
		sb.append("  prodChirality = " + prodChirality);
				
		return sb.toString();
	}
}
