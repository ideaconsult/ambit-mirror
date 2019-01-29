package ambit2.structure2name.components;

import java.util.List;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import ambit2.smarts.SmartsHelper;

public class ComponentUtils 
{
	public static enum Comparison {
		LESS_THAN, LESS_OR_EQUAL, GREATER_THAN, GREATER_OR_EQUAL,
	}
	
	public static List<ComponentConnection> getConnections(List<ComponentConnection> conn, 
															double rankThreshold, 
															Comparison compasion)
	{
		//TODO
		return null;
	}
	
	public static ComponentConnection[] sortConnectionsByRank(List<ComponentConnection> conn)
	{
		//TODO
		return null;
	}
	
	public static IAtomContainer generateContainerForAtomList(List<IAtom> atoms, IAtomContainer mol)
	{
		IAtomContainer con = new AtomContainer();
		for (IAtom at: atoms)
			con.addAtom(at);
		for (IBond bo : mol.bonds())
		{
			if (atoms.contains(bo.getAtom(0)) && 
				atoms.contains(bo.getAtom(1)))
				con.addBond(bo);	
		}
		return con;
	}
	
	public static String getAtomListAsString(List<IAtom> atoms, IAtomContainer mol)
	{
		StringBuffer sb = new StringBuffer();
		for (IAtom at : atoms)
		{	
			sb.append(at.getSymbol());			
			sb.append(mol.indexOf(at)+1);
			sb.append(" ");
		}	
		return sb.toString();
	}
	
	public static String getComponentString(List<IAtom> atoms, IAtomContainer mol)
	{
		String s = getAtomListAsString(atoms, mol);
		IAtomContainer ac = generateContainerForAtomList(atoms, mol);
		try {
			s = s + "  " + SmartsHelper.moleculeToSMILES(ac, true);
		}
		catch (Exception e) {
		}
		return s;
	}
	
	
}
