/* Mopac7WriterTest.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-8 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.test.mopac;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import junit.framework.TestCase;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.HydrogenAdder;

import ambit2.external.Mopac7Reader;
import ambit2.external.Mopac7Writer;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-8
 */
public class Mopac7WriterTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Mopac7WriterTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for Mopac7WriterTest.
     * @param arg0
     */
    public Mopac7WriterTest(String arg0) {
        super(arg0);
    }
    public void test() {
    	fail("TODO create test");
    }
    /*
    public void test() {
        try {
            Mopac7Writer writer = new Mopac7Writer(
                    new OutputStreamWriter(new FileOutputStream("helper/ambit2.processors.mopac.dat")));
            SmilesParser p = new SmilesParser();
            IMolecule mol = p.parseSmiles("CCCCCc1cccc2cccc(c12)CCC");
            HueckelAromaticityDetector.detectAromaticity(mol);
            
            HydrogenAdder ha = new HydrogenAdder();
            ha.addExplicitHydrogensToSatisfyValency(mol);
            
            StructureDiagramGenerator g = new StructureDiagramGenerator();
            g.setMolecule(mol,false);
            g.generateCoordinates();
            

            writer.write(mol);
            writer.close();
        } catch (Exception x) {
            x.printStackTrace();
            fail();
        }
    }
    public void testReader() {
        try {
            Mopac7Reader r = new Mopac7Reader(new FileInputStream("helper/winmopac.cout"));
            IChemObject m = MoleculeFactory.makeAlkane(2);
            m = r.read(m);
            Object e = ((Molecule) m).getProperty("EIGENVALUES");
            assertNotNull(e);
            //e = ((Molecule) m).getProperty("FILLED_LEVELS");
            //assertNotNull(e);
            
        } catch (Exception x) {
            x.printStackTrace();
            fail();
        }
    }
    
    public void testNCI() {
        try {
            //Mopac7Writer writer = new Mopac7Writer(
              //      new OutputStreamWriter(new FileOutputStream("helper/MOPAC.dat")));            
            IteratingMDLReader reader = new IteratingMDLReader(
                new FileInputStream("D:\\nina\\Databases\\nciopen_3D_fixed.sdf"),
                DefaultChemObjectBuilder.getInstance()
                );
            MDLWriter wriOK = new MDLWriter(new FileOutputStream(
                    "D:\\nina\\nciopen_3D_electronic_ok.sdf"));
            MDLWriter wriErr = new MDLWriter(new FileOutputStream(
            "D:\\nina\\nciopen_3D_electronic_err.sdf"));
            
            int n = 0;
            MopacShell shell = new MopacShell("helper","ambit2.processors.mopac.exe");
            while (reader.hasNext()) {
                Object o = reader.next();
                if (o instanceof ChemObject) {
                    n++;
                    //if (n < 110) continue;
                    Molecule m = (Molecule) o;
                    //writer.write((org.openscience.cdk.interfaces.ChemObject)o);
                    
                    shell.runMopac(m);
                    if ((((Molecule) o).getProperty("EHOMO") == null) ||
                    	((Molecule) o).getProperty("ELUMO") == null) {
                        wriErr.setSdFields(m.getProperties());
                        wriErr.writeMolecule(m);
                        System.out.println("HOMO/LUMO not calculated");
                    } else {
                        wriOK.setSdFields(m.getProperties());
                        wriOK.writeMolecule(m);
                    }

                }
                
                System.out.println(n);
                //if (n > 1)
                
            }
            reader.close();
            wriOK.close();
            wriErr.close();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
    */
}
