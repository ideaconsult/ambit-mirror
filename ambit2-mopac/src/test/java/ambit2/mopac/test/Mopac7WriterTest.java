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

package ambit2.mopac.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.io.MDLWriter;
import ambit2.core.smiles.SmilesParserWrapper;
import ambit2.core.smiles.SmilesParserWrapper.SMILES_PARSER;
import ambit2.mopac.DescriptorMopacShell;
import ambit2.mopac.Mopac7Reader;
import ambit2.mopac.Mopac7Writer;

/**
 * A test for {@link Mopac7Writer}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2008-12-13
 */
public class Mopac7WriterTest  {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }
    @Test
    public void test() throws Exception {
        	StringWriter w = new StringWriter();
            Mopac7Writer writer = new Mopac7Writer(w) {
            	@Override
            	public String getTitle() {
            		return "test";
            	}
            };
            SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
            IMolecule mol = p.parseSmiles("CCCCCc1cccc2cccc(c12)CCC");

    		CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
            adder.addImplicitHydrogens(mol);
            AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);            

            StructureDiagramGenerator g = new StructureDiagramGenerator();
            g.setMolecule(mol,false);
            g.generateCoordinates();

            writer.write(mol);
            writer.close();
            
            String content = 
            	"PM3 NOINTER NOMM BONDS MULLIK PRECISE\r\n"+
            	"\r\n"+
            	"test\r\n"+
            	"C -0.9098 1 10.2768 1 0 1 \r\n"+ 
            	"C -0.3967 1 8.8672 1 0 1 \r\n"+ 
            	"C -1.3609 1 7.7181 1 0 1 \r\n"+
            	"C -0.8479 1 6.3086 1 0 1 \r\n"+
            	"C -1.8121 1 5.1595 1 0 1 \r\n"+
            	"C -1.299 1 3.75 1 0 1 \r\n"+
            	"C 0 1 4.5 1 0 1 \r\n"+
            	"C 1.299 1 3.75 1 0 1 \r\n"+
            	"C 1.299 1 2.25 1 0 1 \r\n"+
            	"C 0 1 1.5 1 0 1 \r\n"+
            	"C 0 1 0 1 0 1 \r\n"+
            	"C -1.299 1 -0.75 1 0 1 \r\n"+ 
            	"C -2.5981 1 -0 1 0 1 \r\n"+
            	"C -2.5981 1 1.5 1 0 1 \r\n"+
            	"C -1.299 1 2.25 1 0 1 \r\n"+
            	"C -3.1111 1 2.9095 1 0 1 \r\n"+
            	"C -4.5883 1 3.17 1 0 1 \r\n"+
            	"C -5.5525 1 2.0209 1 0 1 \r\n"+
            	"H 0.0544 1 11.4258 1 0 1 \r\n"+
            	"H -1.8739 1 11.4258 1 0 1 \r\n"+
            	"H -2.2088 1 9.5268 1 0 1 \r\n"+
            	"H 0.5674 1 7.7181 1 0 1 \r\n"+
            	"H 0.9023 1 9.6172 1 0 1 \r\n"+
            	"H -2.3251 1 8.8672 1 0 1 \r\n"+
            	"H -2.66 1 6.9681 1 0 1 \r\n"+
            	"H 0.1163 1 5.1595 1 0 1 \r\n"+
            	"H 0.4512 1 7.0586 1 0 1 \r\n"+
            	"H -2.7762 1 6.3086 1 0 1 \r\n"+
            	"H -3.0661 1 4.4695 1 0 1 \r\n"+
            	"H -2.7762 1 3.4895 1 0 1 \r\n"+
            	"H 0.9642 1 5.6491 1 0 1 \r\n"+
            	"H -0.9642 1 5.6491 1 0 1 \r\n"+
            	"H -2.5981 1 3 1 0 1 \r\n"+
            	"H 2.7762 1 3.4895 1 0 1 \r\n"+
            	"H 1.8121 1 5.1595 1 0 1 \r\n"+
            	"H -4.0753 1 1.2395 1 0 1 \r\n"+
            	"H 1.299 1 0.75 1 0 1 \r\n"+
            	"H 1.8121 1 0.8405 1 0 1 \r\n"+
            	"H 2.7762 1 2.5105 1 0 1 \r\n"+
            	"H -4.0753 1 0.2605 1 0 1 \r\n"+
            	"H -3.1111 1 -1.4095 1 0 1 \r\n"+
            	"H -1.6339 1 3.17 1 0 1 \r\n"+
            	"H -3.1561 1 4.3495 1 0 1 \r\n"+
            	"H 0.513 1 -1.4095 1 0 1 \r\n"+
            	"H 1.4772 1 0.2605 1 0 1 \r\n"+
            	"H -2.2632 1 -1.8991 1 0 1 \r\n"+
            	"H -0.3349 1 -1.8991 1 0 1 \r\n"+
            	"H -4.0753 1 4.5796 1 0 1 \r\n"+
            	"H -5.8874 1 3.92 1 0 1 \r\n"+
            	"H -7.0297 1 2.2814 1 0 1 \r\n"+
            	"H -6.0655 1 0.6114 1 0 1 \r\n"+
            	"H -4.2535 1 1.2709 1 0 1 \r\n"+
            	"0\r\n";
            	
            Assert.assertEquals(content,w.toString());
    }
    public void testRead3Dcoordinates() throws Exception {
    	Assert.fail("Not implemented");
    }
    @Test
    public void testReader() throws Exception {
    		InputStream in = Mopac7Writer.class.getClassLoader().getResourceAsStream(
    				"ambit2/mopac/ethylene.dat.out");
            Mopac7Reader r = new Mopac7Reader(in);
            
    		SmilesParserWrapper p =  SmilesParserWrapper.getInstance(SMILES_PARSER.CDK);
    		String smiles = "C=C";
            IChemObject m = p.parseSmiles(smiles); 
            m = r.read(m);
            in.close();
            Object e = m.getProperty("EIGENVALUES");
            Assert.assertNotNull(e);
            /*
            for (int i=0; i < Mopac7Reader.parameters.length;i++)
            	System.out.println(
            			Mopac7Reader.parameters[i]
            			                        + " = " +
            			((IMolecule) m).getProperty(Mopac7Reader.parameters[i])
            			);
            			*/
            Assert.assertEquals(-10.552,
            		Double.parseDouble(m.getProperty(DescriptorMopacShell.EHOMO).toString()),
            		1E-3);
            Assert.assertEquals(1.438,
            		Double.parseDouble(m.getProperty(DescriptorMopacShell.ELUMO).toString()),
            		1E-3);   
    		for (int i=0; i < ((IMolecule)m).getAtomCount(); i++) {
    			Assert.assertNotNull(((IMolecule)m).getAtom(i).getPoint3d());
    		}            
    }
    
    @Test
    public void testReader2009() throws Exception {
    		InputStream in = Mopac7Writer.class.getClassLoader().getResourceAsStream(
    				"ambit2/mopac/mopac2009.out");
            Mopac7Reader r = new Mopac7Reader(in);
            
    		SmilesParserWrapper p =  SmilesParserWrapper.getInstance(SMILES_PARSER.CDK);
    		String smiles = "C=C";
            IChemObject m = p.parseSmiles(smiles); 
            m = r.read(m);
            in.close();
            Object e = m.getProperty("TOTAL ENERGY");
            Assert.assertEquals("-1223.60097",e.toString());
            /*
            this fails - todo check why
            Assert.assertEquals(-9.925,
            		Double.parseDouble(m.getProperty(DescriptorMopacShell.EHOMO).toString()),
            		1E-3);
            Assert.assertEquals(1.007,
            		Double.parseDouble(m.getProperty(DescriptorMopacShell.ELUMO).toString()),
            		1E-3);   
            		*/
    		for (int i=0; i < ((IMolecule)m).getAtomCount(); i++) {
    			Assert.assertNotNull(((IMolecule)m).getAtom(i).getPoint3d());
    		}            
    }
    
    public void testNCI() throws Exception {
            IteratingMDLReader reader = new IteratingMDLReader(
                new FileInputStream("D:\\nina\\Databases\\nciopen_3D_fixed.sdf"),
                SilentChemObjectBuilder.getInstance()
                );
            MDLWriter wriOK = new MDLWriter(new FileOutputStream(
                    "D:\\nina\\nciopen_3D_electronic_ok.sdf"));
            MDLWriter wriErr = new MDLWriter(new FileOutputStream(
            "D:\\nina\\nciopen_3D_electronic_err.sdf"));
            
            int n = 0;
            DescriptorMopacShell shell = new DescriptorMopacShell();
            while (reader.hasNext()) {
                Object o = reader.next();
                if (o instanceof IMolecule) {
                    n++;
                    if (n < 210) continue;
                    if (n > 219) break;
                    IMolecule m = (IMolecule) o;
                    //writer.write((org.openscience.cdk.interfaces.ChemObject)o);
                    

	                    DescriptorValue v = shell.calculate(m);
	                    DoubleArrayResult r = (DoubleArrayResult) v.getValue();
	                    String[] names = v.getNames();
	                    Assert.assertEquals(names.length,r.length());
	                    for (int g=0; g<names.length;g++) {
	                    	Assert.assertNotNull(r.get(g));
	                    	Assert.assertNull(v.getException());
	                    	//System.out.println(names[g] + "\t= "+r.get(g));
	                    } 	
              
                    /*
                    if ((m.getProperty(MopacShell.EHOMO) == null) ||
                    	m.getProperty(MopacShell.ELUMO) == null) {
                        wriErr.setSdFields(m.getProperties());
                        wriErr.writeMolecule(m);
                        System.out.println("HOMO/LUMO not calculated");
                    } else {
                        wriOK.setSdFields(m.getProperties());
                        wriOK.writeMolecule(m);
                        System.out.println("HOMO/LUMO calculated");
                    }
                    */

                }
                
                //System.out.println(n);
                //if (n > 1)
                
            }
            Assert.assertEquals(220,n);
            reader.close();
            wriOK.close();
            wriErr.close();

    }
}
