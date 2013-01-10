package ambit2.markush.test;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.io.InputStream;

import ambit2.core.data.MoleculeTools;
import ambit2.core.io.MDLV2000ReaderExtended;

public class TestUtils 
{
	static String path = "/ambit2-all/ambit2-core/src/test/resources/ambit2/core/data/mdl/";
	
	public static void main(String[] args) throws Exception
	{
		testSuppleAtomFromMLD("/temp2/A.mol");
	}
	

	static protected IChemObject readMDLV2000Extended(String file) throws Exception 
	{
		InputStream in = MDLV2000ReaderExtended.class.getClassLoader().getResourceAsStream(file);
		if (in == null)
		{
			System.out.println("********* null");
		}
		
		MDLV2000ReaderExtended reader = new MDLV2000ReaderExtended(in, IChemObjectReader.Mode.RELAXED);
		
		IMolecule mol = MoleculeTools.newMolecule(SilentChemObjectBuilder.getInstance());
		IChemObject newMol = reader.read(mol);
		reader.close();
		return newMol;
	}
	
	static void testSuppleAtomFromMLD(String mdlFile) throws Exception
	{
		readMDLV2000Extended(mdlFile);
		
	}
	

}
