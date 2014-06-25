package ambit2.groupcontribution.descriptors;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class LDAtomSymbol implements ILocalDescriptor
{	

	private boolean FlagUsed = true;
	
	@Override
	public int calcForAtom(IAtom atom, IAtomContainer mol) {		
		return atom.getAtomicNumber();
	}

	@Override
	public String getDesignation(int value) {		
		return mElementSymbol[value];
	}

	@Override
	public String getShortName() {
		return "A";
	}

	@Override
	public String getName() {		
		return "AtomSymbol";
	}

	@Override
	public String getInfo() {		
		return "AtomSymbol";
	}

	@Override
	public Type getType() {		
		return Type.PREDEFINED;
	}
	
	@Override
	public boolean isUsed() {
		return FlagUsed;
	}

	@Override
	public void setIsUsed(boolean used) {
		FlagUsed = used;
	}

	public static String mElementSymbol[] =
		{
		"*",
		"H","He","Li","Be","B","C","N","O","F","Ne",           //1-10
		"Na","Mg","Al","Si","P","S","Cl","Ar","K","Ca",        //11-20
		"Sc","Ti","V","Cr","Mn","Fe","Co","Ni","Cu","Zn",      //21-30
		"Ga","Ge","As","Se","Br","Kr","Rb","Sr","Y", "Zr",     //31-40
		"Nb","Mo","Tc","Ru","Rh","Pd","Ag","Cd","In","Sn",     //41-50
		"Sb","Te","I","Xe","Cs","Ba","La","Ce","Pr","Nd",      //51-60
		"Pm","Sm","Eu","Gd","Tb","Dy","Ho","Er","Tm","Yb",     //61-70
		"Lu", "Hf","Ta","W","Re","Os","Ir","Pt","Au","Hg",     //71-80
		"Tl","Pb","Bi","Po","At","Rn","Fr","Ra","Ac","Th",     //81-90
		"Pa", "U","Np","Pu","Am","Cm","Bk","Cf","Es","Fm",     //91-100
		"Md","No","Lr","Rf","Db","Sg","Bh","Hs","Mt","Ds",     //101-110
		"Uuu","Uub" , "Uut", "Uuq", "Uup", "Uuh"               //111-116
		}; 

}
