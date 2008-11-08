package ambit2.hashcode.test;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

import junit.framework.TestCase;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import toxTree.data.MoleculesFile;
import toxTree.io.MyIteratingMDLReader;

public class MoleculesFileHashTest extends TestCase {
	public void test() {
		try {
			DefaultChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
			MoleculesFile mf = new MoleculesFile(new File("C:/ambit/ambit2-hashcode/src/main/resources/einecs_structures_V13Apr07.sdf"),b);
			MyIteratingMDLReader reader = new MyIteratingMDLReader(new FileReader("C:/ambit/ambit2-hashcode/src/main/resources/einecs_structures_V13Apr07.sdf"),b);
			ambit2.hashcode.MoleculeAndAtomsHashing molHash = new ambit2.hashcode.MoleculeAndAtomsHashing();
			int record=0;
            int found = 0;
            long now = System.currentTimeMillis();
			//System.out.print(System.currentTimeMillis());
			while (reader.hasNext()) {
				Object o = reader.next();
				if (o instanceof IAtomContainer && !((IAtomContainer)o).getProperty("GENERATED_SMILES").toString().equals(".") && !((IAtomContainer)o).getProperty("GENERATED_SMILES").toString().equals("C")&& !((IAtomContainer)o).getProperty("GENERATED_SMILES").toString().equals("[C]")) {
                    AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms((IAtomContainer)o);
					//boolean aromatic = CDKHueckelAromaticityDetector.detectAromaticity((IAtomContainer)o);
					
					System.out.print(record);
                    System.out.print('\t');                    
                    System.out.print(((IAtomContainer)o).getProperty("MF"));                    
					System.out.print('\t');
					System.out.print(((IAtomContainer)o).getProperty("GENERATED_SMILES"));
					System.out.print('\t');
					try{
					System.out.println(molHash.getMoleculeHash((IAtomContainer)o));
					}
					catch(Exception e){}
					//int index = mf.indexOf("GENERATED_SMILES",((IAtomContainer)o).getProperty("GENERATED_SMILES"));
					/*System.out.print("\tAromatic ");
					System.out.print(aromatic);
					System.out.print('\t');
					int index = mf.indexOf("SMILES",((IAtomContainer)o).getProperty("SMILES"));
					if (index >-1) {
						System.out.print("found ");
						System.out.print(System.currentTimeMillis()-now);
						System.out.print(" ms\tMR\t");
						System.out.print(mf.getAtomContainer(index).getProperty("MR"));
                        System.out.print(" ms\tB5STM\t");
                        System.out.println(mf.getAtomContainer(index).getProperty("B5STM"));                        
                        found++;
					} else System.out.println("not found"); */
					System.out.print('\n');
					record++;

				}
				
			}
			System.out.print(Long.toString(System.currentTimeMillis()-now));
			//assertEquals(record,found);
		} catch (Exception x) {
            x.printStackTrace();
			fail(x.getMessage());
		}
	}
}
