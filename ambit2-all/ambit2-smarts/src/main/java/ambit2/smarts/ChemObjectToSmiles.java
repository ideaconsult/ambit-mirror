package ambit2.smarts;

import java.util.Vector;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

public class ChemObjectToSmiles 
{
	//When this flag is true the aromaticity is encoded with small letters.
	public boolean mAromaticity = true;   
	
	IAtomContainer mol;
	int NA;
	int walkedAtoms[];
	Vector<ClosureBond> closures = new Vector<ClosureBond>();
	String closureNums[];
	int curIndex;
		
			
	public String getSMILES(IAtomContainer ac)
	{	
		mol = ac;
		TopLayer.setAtomTopLayers(mol, TopLayer.TLProp);
		NA = mol.getAtomCount();
		closures.clear();
		closureNums = new String[NA];
		walkedAtoms = new int [NA];
		
		for (int i = 0; i < NA; i++)
			closureNums[i] = "";		
		for (int i = 0; i < NA; i++)		
			walkedAtoms[i] = 0;
		
		curIndex = 1;		
		walkedAtoms[0] = 1;
		walkMolecule(0,-1);
		
		for (int i = 0; i < NA; i++)
			walkedAtoms[i] = 0;
		setClosureNums();
		walkedAtoms[0] = 1;
		return(getAtomWalkString(0,-1));
	}
	
	String getAtom(int atNum)
	{
		return(getAtom(mol.getAtom(atNum)));
	}
	
	String getAtom(IAtom at)
	{
		if (at.getSymbol().equals("B") ||				
			at.getSymbol().equals("C") ||
			at.getSymbol().equals("N") ||
			at.getSymbol().equals("O") ||
			at.getSymbol().equals("S") ||
			at.getSymbol().equals("P") ||
			at.getSymbol().equals("Cl")||
			at.getSymbol().equals("F") ||
			at.getSymbol().equals("Br")||
			at.getSymbol().equals("I")
			)
		{
			if (mAromaticity)
			{	
				if (at.getFlag(CDKConstants.ISAROMATIC))				
					return(at.getSymbol().toLowerCase());
				else
					return(at.getSymbol());
			}	
			else
				return(at.getSymbol());
		}
		else
			return("["+at.getSymbol()+"]");
		
	}
	
	void setClosureNums()
	{
		for (int i = 0; i < closures.size(); i++)
		{
			ClosureBond cb = closures.get(i);
			closureNums[cb.at1]+= cb.getIndexAt1();
			closureNums[cb.at2]+= cb.getIndexAt2(mAromaticity);
		}
	}
	
	void walkMolecule(int atNum, int parentAt)
	{	
		TopLayer tl = (TopLayer)mol.getAtom(atNum).getProperty(TopLayer.TLProp);
		int a;	
		for (int i = 0; i < tl.atoms.size(); i++)
		{
			a = mol.getAtomNumber(tl.atoms.get(i));
			if (a == parentAt)
				continue;
			
			if (walkedAtoms[a] == 0)
			{			
				walkedAtoms[a] = 1;
				walkMolecule(a,atNum);
			}
			else
			{
				ClosureBond  cb = new ClosureBond();
				cb.at2 = atNum;
				cb.at1 = a;
				cb.bt = tl.bonds.get(i);
				cb.index = curIndex;
				addClosureBond(cb);			
			}
		}
	}
	
	void addClosureBond(ClosureBond cb)
	{	
		for (int i = 0; i < closures.size(); i++)
			if (closures.get(i).isEqualTo(cb))
				return;
		closures.add(cb);
		curIndex++;
	}
	
	
	String getAtomWalkString(int atNum, int parentAt)
	{	
		//strings for the neighbors atoms
		Vector<String> v = new Vector<String>();   
		TopLayer tl = (TopLayer)mol.getAtom(atNum).getProperty(TopLayer.TLProp);
		
		int a;	
		for (int i = 0; i < tl.atoms.size(); i++)
		{	
			a = mol.getAtomNumber(tl.atoms.get(i));
			if (a == parentAt)
				continue;

			if (walkedAtoms[a] == 0)
			{			
				walkedAtoms[a] = 1;	
				IBond b = tl.bonds.get(i);
				v.add(SmartsHelper.smilesBondToString(b,mAromaticity)+getAtomWalkString(a,atNum));			
			}		
		}
		
		StringBuffer sb = new StringBuffer();
		
		//Adding current atom and its indexes if any	
		sb.append(getAtom(atNum));
		sb.append(closureNums[atNum]);
		
		//Adding the neighbor strings
		if (!v.isEmpty())
		{	
			for (int i = 0; i < v.size(); i++)
			{	
				if (i == v.size()-1)
					sb.append(v.get(i));
				else
					sb.append("(" + v.get(i) + ")");
			}	
		}
		
		return(sb.toString());
	}

}
