package ambit2.tautomers;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;

import java.util.HashMap;


/**
 * Utilities for generation of specific structures needed
 */


public class RuleStructureFactory 
{
	SmilesParser smilesParser;

	
	public RuleStructureFactory()
	{
		setUp();
	}
	
	public void setUp()
	{
		smilesParser  = new SmilesParser(SilentChemObjectBuilder.getInstance());
	}


	public IAtomContainer connectStructures(IAtomContainer str1,int numAt1, 
			IAtomContainer str2, int numAt2, IBond.Order order) throws Exception
	{	
		IAtom at1 = null;
		IAtom at2 = null;
		IAtomContainer mol = new AtomContainer();
		
		//clone str1
		for(int i = 0; i < str1.getAtomCount(); i++)
		{
			IAtom at = (IAtom)str1.getAtom(i).clone();
			mol.addAtom(at);
			if (numAt1 == i)
				at1 = at;
		}
		
		for(int i = 0; i < str1.getBondCount(); i++)
		{
			IBond bo = str1.getBond(i);
			int at0Num = str1.getAtomNumber(bo.getAtom(0));
			int at1Num = str1.getAtomNumber(bo.getAtom(1));
			IBond newBo = new Bond();
			newBo.setOrder(bo.getOrder());
			newBo.setAtom(mol.getAtom(at0Num), 0);
			newBo.setAtom(mol.getAtom(at1Num), 1);
			mol.addBond(newBo);
		}
		
		//clone str2
		for(int i = 0; i < str2.getAtomCount(); i++)
		{
			IAtom at = (IAtom)str2.getAtom(i).clone();
			mol.addAtom(at);
			if (numAt2 == i)
				at2 = at;
		}
		for(int i = 0; i < str2.getBondCount(); i++)
		{
			IBond bo = str2.getBond(i);
			int at0Num = str2.getAtomNumber(bo.getAtom(0));
			int at1Num = str2.getAtomNumber(bo.getAtom(1));
			IBond newBo = new Bond();
			newBo.setOrder(bo.getOrder());
			newBo.setAtom(mol.getAtom(at0Num + str1.getAtomCount()), 0);
			newBo.setAtom(mol.getAtom(at1Num + str1.getAtomCount()), 1);
			mol.addBond(newBo);
		}
		
		//Adding a new bond connecting bond
		IBond newBo = new Bond();
		newBo.setOrder(order);
		newBo.setAtom(at1, 0);
		newBo.setAtom(at2, 1);
		mol.addBond(newBo);

		return mol;
	}

	public IAtomContainer connectStructures(String smiles1, int numAt1, String smiles2, int numAt2, IBond.Order order) throws Exception 
	{
		IAtomContainer mol1 = smilesParser.parseSmiles(smiles1);
		IAtomContainer mol2 = smilesParser.parseSmiles(smiles2);

		return connectStructures(mol1, numAt1, mol2, numAt2, order);
	}


	public IAtomContainer connectStructures_Spiro(IAtomContainer str1,int numAt1, 
			IAtomContainer str2, int numAt2, IBond.Order order) throws Exception
	{
		//TODO
		return null;
	}
	
	
	public IAtomContainer condenseStructures(String smiles1,int str1At0, int str1At1, 
			String smiles2, int str2At0, int str2At1) throws Exception
	{
		IAtomContainer mol1 = smilesParser.parseSmiles(smiles1);
		IAtomContainer mol2 = smilesParser.parseSmiles(smiles2);

		return condenseStructures(mol1, str1At0, str1At1, mol2, str2At0, str2At1);
	}
	
	
	
	public IAtomContainer condenseStructures(IAtomContainer str1,int str1At0, int str1At1, 
			IAtomContainer str2, int str2At0, int str2At1) throws Exception
	{
		IAtom at0 = null;
		IAtom at1 = null;
		
		IAtomContainer mol = new AtomContainer();
		
		//clone str1
		for(int i = 0; i < str1.getAtomCount(); i++)
		{
			IAtom at = (IAtom)str1.getAtom(i).clone();
			mol.addAtom(at);
			if (str1At0 == i)
				at0 = at;

			if (str1At1 == i)
				at1 = at;
		}
		
		for(int i = 0; i < str1.getBondCount(); i++)
		{
			IBond bo = str1.getBond(i);
			int at0Num = str1.getAtomNumber(bo.getAtom(0));
			int at1Num = str1.getAtomNumber(bo.getAtom(1));
			IBond newBo = new Bond();
			newBo.setOrder(bo.getOrder());
			newBo.setAtom(mol.getAtom(at0Num), 0);
			newBo.setAtom(mol.getAtom(at1Num), 1);
			mol.addBond(newBo);
		}
		
		
		HashMap<IAtom,IAtom> map = new HashMap<IAtom,IAtom>(); 
		
		//clone str2
		for(int i = 0; i < str2.getAtomCount(); i++)
		{			
			IAtom at = str2.getAtom(i);
			if (str2At0 == i)
				map.put(at, at0);
			else
				if (str2At1 == i)
					map.put(at, at1);
				else
				{
					IAtom new_at = (IAtom)str2.getAtom(i).clone();
					mol.addAtom(new_at);
					map.put(at, new_at);
				}
		}
		
		for(int i = 0; i < str2.getBondCount(); i++)
		{
			IBond bo = str2.getBond(i);
			IAtom boAt0 = bo.getAtom(0);
			IAtom boAt1 = bo.getAtom(1);
			int at0Num = str2.getAtomNumber(boAt0);
			int at1Num = str2.getAtomNumber(boAt1);
			
			if ( ((at0Num == str2At0) && (at1Num == str2At1)) || 
				 ((at0Num == str2At1) && (at1Num == str2At0))    ) 
			{
				//This is the condensation bond
				continue;	
			}
			else
			{
				IBond newBo = new Bond();
				newBo.setOrder(bo.getOrder());
				newBo.setAtom(map.get(boAt0), 0);
				newBo.setAtom(map.get(boAt1), 1);
				mol.addBond(newBo);
			}
			
		}

		return mol;
	}
	
	
}