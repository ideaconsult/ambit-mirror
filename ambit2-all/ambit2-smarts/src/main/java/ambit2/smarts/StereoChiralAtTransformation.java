package ambit2.smarts;

public class StereoChiralAtTransformation 
{
	//Reactant
	public int reactChiralAtom = -1;
	public int reactLigands[] = null;
	public int reactTerminal1 = -1;
	public int reactTerminal2 = -1;
	public int reactChirality = SmartsConst.ChC_Unspec;
	
	//Product
	public int prodChiralAtom = -1;
	public int prodLigands[] = null;
	public int prodLigandsReactMap[] = null; //if mapped reactant partners may not be in the same order as specified in reactLigand
	public int prodTerminal1 = -1;
	public int prodTerminal2 = -1;
	public int prodChirality = SmartsConst.ChC_Unspec;
}
