/*
Copyright (C) 2005-2008  

Contact: 

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.hashcode.test;

import java.io.FileReader;

import junit.framework.TestCase;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.io.MyIteratingMDLReader;


public class MoleculesFileHashTest extends TestCase {
	public void test() {
		try {
			DefaultChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
			//MoleculesFile mf = new MoleculesFile(new File("C:/ambit/ambit2-hashcode/src/main/resources/einecs_structures_V13Apr07.sdf"),b);
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
