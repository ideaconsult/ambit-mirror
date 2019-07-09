package ambit2.groupcontribution.dataset;

import java.util.Map;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;


public class LocalProperties 
{
	public static enum Type {
		ATOM, BOND, ATOM_PAIR, ATOM_TRIPLE
	}
	
	public Type type = Type.ATOM;
	public Map<Object, double[]> properties = null;
	
	
	public String toString(IAtomContainer mol)
	{
		StringBuffer sb = new StringBuffer();
		Set<Object> keys = properties.keySet();
		for (Object obj : keys)
		{
			int ind[] = (int[]) obj;
			IAtom atoms[] = indicesToAtoms(ind, mol);
			for (int i = 0; i < ind.length; i++)
			{	
				sb.append(atoms[i].getSymbol());
				sb.append(ind[i]);
				sb.append(" ");
			}
			sb.append(" ");
			
			double props[] = properties.get(obj);
			if (props != null)
				for (int i = 0; i < props.length; i++)
				{
					sb.append(props[i]);
					sb.append(" ");
				}
			
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	
	public static IAtom[] indicesToAtoms(int ind[], IAtomContainer mol)
	{
		IAtom atoms[] = new IAtom[ind.length];
		for (int i = 0; i < ind.length; i++)
			atoms[i] = mol.getAtom(ind[i]);
		return null;
	}
	
	public static Object indicesToChemLocPropObject(int ind[], IAtomContainer mol, Type locPropType)
	{
		Object obj = null;
		switch (locPropType)
		{
		case ATOM:
			obj = mol.getAtom(ind[0]);
			break;
		case BOND:
			obj = mol.getBond(mol.getAtom(ind[0]), mol.getAtom(ind[1]));
			break;	
		case ATOM_PAIR:
		case ATOM_TRIPLE:	
			obj = indicesToAtoms(ind, mol);
			break;	
		}
		
		return obj;
	}
}
