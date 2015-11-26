package ambit2.tautomers.tools;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;

public class CustomTautomerFilters 
{	
	//the bond indices  of possible allene C atoms
	List<int[]> alleneAtBondIndices = new ArrayList<int[]>();  
	
	//the indices of possible allene C atoms	
	List<Integer> alleneAtNums = new ArrayList<Integer>(); 
	
	//the indices of initial allene C atoms	
	Set<Integer> initialAlleneAtNums = new HashSet<Integer>();
	
	BitSet ringAtoms = new BitSet();
	
	public void setup(IAtomContainer mol)
	{
		setAlleneAtBondIndices(mol);
		setRingFlags(mol, null);
	}
	
	public void setup(IAtomContainer mol, IRingSet rSet)
	{
		setAlleneAtBondIndices(mol);
		setRingFlags(mol, rSet);
	}
	
	
	public boolean checkNewAlleneAtoms(IAtomContainer mol, boolean allowNewAlleneAtom, boolean allowNewCyclicAlleneAtom)
	{	
			 
		if (allowNewAlleneAtom) 
		{
			if (allowNewCyclicAlleneAtom)
			{	
				//no check is required
				return true;
			}
			else
			{
				return !hasNewAlleneAtomsInCycle(mol);
			}
		}
		else
		{
			//allowNewAlleneAtom = false
			return !hasNewAlleneAtoms(mol);
		}
	}
	
	
	public boolean hasNewAlleneAtoms(IAtomContainer mol)
	{
		for (int i = 0; i < alleneAtBondIndices.size(); i++)
		{
			int boInd[] = alleneAtBondIndices.get(i);
			if (isAlleneAtom(boInd, mol))
				if (initialAlleneAtNums.contains(alleneAtNums.get(i)))
					return true;
		}
		return false;
	}
	
	public boolean hasNewAlleneAtomsInCycle(IAtomContainer mol)
	{
		for (int i = 0; i < alleneAtBondIndices.size(); i++)
		{
			int atNum = alleneAtNums.get(i);
			if (ringAtoms.get(atNum))
			{
				int boInd[] = alleneAtBondIndices.get(i);
				if (isAlleneAtom(boInd, mol))
					if (initialAlleneAtNums.contains(atNum))
						return true;
			}
		}
		return false;
	}
	
	
	public boolean hasAlleneAtomsInCycle(IAtomContainer mol)
	{
		//TODO
		return false;
	}
	
	boolean isAlleneAtom(int boInd[], IAtomContainer mol)
	{
		int nDB = 0;
		for (int i = 0; i < boInd.length; i++)
		{	
			IBond bo = mol.getBond(boInd[i]);
			if (bo.getOrder() == IBond.Order.DOUBLE)
				nDB++;
		}
		return (nDB >= 2);
	}
	
	

	void setAlleneAtBondIndices(IAtomContainer mol) 
	{
		alleneAtNums.clear();
		alleneAtBondIndices.clear();
		initialAlleneAtNums.clear();
		
		for (int i = 0; i < mol.getAtomCount(); i++)
		{
			IAtom at = mol.getAtom(i);
			if (at.getSymbol() == null)
				continue;
			if (!at.getSymbol().equals("C"))
				continue;
			
			List<IBond> bonds = mol.getConnectedBondsList(at);
			if (bonds.size() < 2)
				continue;
			
			//Register the only potential bonds for allene atoms			
			List<IBond> regBo = new ArrayList<IBond>();
			for (IBond bo : bonds)
			{
				IAtom a = bo.getAtom(0);
				if (a == at)
					a = bo.getAtom(1);
				if (a.getSymbol() == null)
					continue;
				
				if (a.getSymbol().equals("H") || a.getSymbol().equals("Cl"))
					continue;
				
				//Register the bond
				regBo.add(bo);
			}
			
			if (regBo.size() < 2)
				continue;
			
			//Atom #i is a potential allene atom
			int boInd[] = new int[regBo.size()];			
			for (int k = 0; k < regBo.size(); k++)			
				boInd[k] = mol.getBondNumber(regBo.get(k));
			
			alleneAtNums.add(i);
			alleneAtBondIndices.add(boInd);
			
			//Check for initial allene atom
			if (isAlleneAtom(boInd, mol))
				initialAlleneAtNums.add(i);
		}
	}
	
	void setRingFlags(IAtomContainer mol, IRingSet rSet) 
	{
		//Reset ring flags
		ringAtoms.clear();
		
		IRingSet ringSet = rSet; 
		if (ringSet == null)
			ringSet = Cycles.sssr(mol).toRingSet();
		
		for (IAtomContainer ring : ringSet.atomContainers())
		{
			for (IAtom at : ring.atoms())
			{
				int k = mol.getAtomNumber(at);
				ringAtoms.set(k);
			}	
		}
	}
	
	
}
