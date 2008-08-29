package ambit2.core.test.io;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Iterator;

import junit.framework.TestCase;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.smiles.SmilesParser;

public class TestCML extends TestCase 
{   protected SmilesParser parser;

    @Override
        protected void setUp() throws Exception {
            super.setUp();
            parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        }
		
    protected String writeCML(IMolecule mol) throws Exception {
        StringWriter output = new StringWriter();           
        CMLWriter cmlwriter = new CMLWriter(output);
        cmlwriter.write(mol);
        cmlwriter.close();
        return output.toString();        
    }
    public void testCML_twoPropertiesForAllAtoms() throws Exception
    {       
        final String prop1 = "PropFirst_ID";
        final String prop2 = "PropSecond_ID";
        String smiles = "CC";
        
        IMolecule mol = parser.parseSmiles(smiles);
        for (int i=0; i < mol.getAtomCount(); i++) {
            String id = Integer.toString(i+1);
            mol.getAtom(i).setProperty(prop1+id,id);
            mol.getAtom(i).setProperty(prop2+id,id);
            mol.getAtom(i).setID(id);
        }

        for (int i=0; i < mol.getAtomCount(); i++) {
            String id = Integer.toString(i+1);
            assertEquals(id,mol.getAtom(i).getProperty(prop2+id));
            assertEquals(id,mol.getAtom(i).getProperty(prop2+id));
            assertEquals(2,mol.getAtom(i).getProperties().size());
        }
        
        String cmlcode = writeCML(mol);
        System.out.println(cmlcode);
            
        IChemFile chemFile = parseCMLString(cmlcode);           
        IMolecule mol2 = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
        System.out.println(writeCML(mol2));
        
        for (int i=0; i < mol2.getAtomCount(); i++) {
            String id = mol2.getAtom(i).getID();
            assertEquals(id,mol2.getAtom(i).getProperty(prop1+id));
            assertEquals(id,mol2.getAtom(i).getProperty(prop2+id));
            assertEquals(2,mol2.getAtom(i).getProperties().size());
        }
        
        
            
    }       
    
    public void xtestCML_singlePropertyForAllAtoms() throws Exception
    {       
        String smiles = "CCCC";
        IMolecule mol = parser.parseSmiles(smiles);
        for (int i=0; i < mol.getAtomCount(); i++) {
            String id = Integer.toString(i+1);
            mol.getAtom(i).setProperty("Prop"+id,id);
            mol.getAtom(i).setID(id);
        }

        printAtomProperties(mol);
        for (int i=0; i < mol.getAtomCount(); i++) {
            String id = Integer.toString(i+1);
            assertEquals(id,mol.getAtom(i).getProperty("Prop"+id));
            assertEquals(1,mol.getAtom(i).getProperties().size());
        }
        
        String cmlcode = writeCML(mol);
        System.out.println(cmlcode);
            
        IChemFile chemFile = parseCMLString(cmlcode);           
        IMolecule mol2 = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
        printAtomProperties(mol2);
        for (int i=0; i < mol2.getAtomCount(); i++) {
            String id = mol2.getAtom(i).getID();
            assertEquals(id,mol2.getAtom(i).getProperty("Prop"+id));
            assertEquals(1,mol2.getAtom(i).getProperties().size());
        }
        
        System.out.println(writeCML(mol2));
            
    }       

	
	public void xtestCML_AromaticFlag() throws Exception 
	{		
	    String smiles = "c1ccccc1";
	    IMolecule mol = parser.parseSmiles(smiles);			
		printAromaticity(mol);
		
			String cmlcode = writeCML(mol);
            Iterator<IAtom> atoms = mol.atoms();
            while (atoms.hasNext()) {
                assertTrue(atoms.next().getFlag(CDKConstants.ISAROMATIC));
            }			
			System.out.println(cmlcode);
			
			
			IChemFile chemFile = parseCMLString(cmlcode);			
			IMolecule mol2 = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
			printAromaticity(mol2);
		    atoms = mol2.atoms();
		    while (atoms.hasNext()) {
		        assertTrue(atoms.next().getFlag(CDKConstants.ISAROMATIC));
	        }
	}	
	
	public static void printAtomProperties(IMolecule mol) {
	    Iterator<IAtom> atoms = mol.atoms();
	    while (atoms.hasNext()) {
	        
	        printAtomProperties(atoms.next());
	    }
	    
	}
	public static void printAtomProperties(IAtom atom)
	{	
	    Iterator keys = atom.getProperties().keySet().iterator();
	    while (keys.hasNext()) {
	        Object key = keys.next();
			System.out.println(atom.getID() + " " + key.toString()+" = "+ atom.getProperties().get(key));
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
