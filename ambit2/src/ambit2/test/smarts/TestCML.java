package ambit2.test.smarts;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;

import ambit2.smarts.SmartsHelper;

public class TestCML 
{
	
	public static void main(String[] args)
	{
		testCML1("CCCC");
		//testCML2("c1ccccc1");
	}
	
	public static void testCML1(String smiles)
	{		
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);
		mol.getAtom(0).setProperty("Prop1","Test1");
		mol.getAtom(0).setProperty("Prop2","Test2");		
		printAtomProperties(mol.getAtom(0));
		
		try
		{	
			System.out.println("Writing " + smiles + " to CML ");
			StringWriter output = new StringWriter();			
			CMLWriter cmlwriter = new CMLWriter(output);
			cmlwriter.write(mol);
			cmlwriter.close();
			String cmlcode = output.toString();
			System.out.println(cmlcode);
			
			
			IChemFile chemFile = parseCMLString(cmlcode);			
			IMolecule mol2 = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
			printAtomProperties(mol2.getAtom(0));
			
		}	
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
		
	}	
	
	public static void testCML2(String smiles)
	{		
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smiles);				
		printAromaticity(mol);
		
		try
		{	
			System.out.println("Writing " + smiles + " to CML ");
			StringWriter output = new StringWriter();			
			CMLWriter cmlwriter = new CMLWriter(output);
			cmlwriter.write(mol);
			cmlwriter.close();
			String cmlcode = output.toString();
			System.out.println(cmlcode);
			
			
			IChemFile chemFile = parseCMLString(cmlcode);			
			IMolecule mol2 = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
			printAromaticity(mol2);
		}	
		
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
		
	}	
	
	
	public static void printAtomProperties(IAtom atom)
	{	
		Object keys[] = atom.getProperties().keySet().toArray();
		for (int i = 0; i < keys.length; i++)
		{	
			System.out.println(keys[i].toString()+" = "+ atom.getProperties().get(keys[i]));
		}
	}
	
	public static void printAromaticity(IMolecule mol)
	{	
		for (int i = 0; i < mol.getAtomCount(); i++)
		{
			IAtom atom = mol.getAtom(i);
			System.out.println("Atom " + i + "  aromatic = " +atom.getFlag(CDKConstants.ISAROMATIC));
		}
		
		for (int i = 0; i < mol.getBondCount(); i++)
		{
			IBond bond = mol.getBond(i);
			System.out.println("Bond " + i + "  aromatic = " +bond.getFlag(CDKConstants.ISAROMATIC));
		}
	}
	
	public static IChemFile parseCMLString(String cmlString) throws Exception 
	{
        IChemFile chemFile = null;
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));
        chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());
        return chemFile;
    }
}
