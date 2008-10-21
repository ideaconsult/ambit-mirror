/* PKASmartsDescriptorTest.java
 * Author: Nina Jeliazkova
 * Date: Oct 3, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
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

package ambit2.descriptors;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.io.DelimitedFileWriter;
import ambit2.core.io.IteratingDelimitedFileReader;
import ambit2.core.query.smarts.SmartsPatternAmbit;

public class PKASmartsDescriptorTest {
	protected PKASmartsDescriptor pka;
    @Before
    public void setUp() throws Exception {
    	 pka = new PKASmartsDescriptor();
    }

    @After
    public void tearDown() throws Exception {
    	pka = null;
    }	

    @Test
    public void test() throws Exception {
        Hashtable<Integer,PKANode> tree = pka.getTree();
        Enumeration<Integer> k = tree.keys();
        SmartsPatternAmbit pattern = new SmartsPatternAmbit();
        ArrayList<String> smarts = new ArrayList<String>();
        int failedNodes = 0;
        int failedSmarts = 0;
        int nullSmarts = 0;
        int allnodes =0;
        while (k.hasMoreElements()) {
            PKANode node = tree.get(k.nextElement());
            allnodes++;
            try {
                if (node.getSmarts() == null) {
                    nullSmarts++;
                } else
                	pattern.setSmarts(node.getSmarts());
            } catch (Exception x) {
            	
            	
                failedNodes ++;
                if (smarts.indexOf(node.getSmarts())<0) {
                    smarts.add(node.getSmarts());
                    failedSmarts++;
                }
            }
        }

        if (smarts.size()>0) {
            System.out.println("Failed nodes "+failedNodes);
            System.out.println("Failed smarts "+failedSmarts);        	
	        for (int i=0; i < smarts.size();i++)
	            System.out.println('\''+smarts.get(i)+'\'');
        }
        
        Assert.assertEquals(1,nullSmarts); //root smarts
        Assert.assertTrue(failedNodes==0);
        Assert.assertEquals(1527,allnodes);
        
    }

    @Test
    public void testPredictions() throws Exception {
    	File file = new File("src/test/resources/ambit2/descriptors/pka/ambit_results.csv");
    	System.out.println(file.getAbsolutePath());
    	DelimitedFileWriter writer = new DelimitedFileWriter(new FileOutputStream(file));
    	
    	InputStream in = PKASmartsDescriptor.class.getClassLoader().getResourceAsStream("ambit2/descriptors/pka/benchmark_new.csv");
    	IteratingDelimitedFileReader reader = new IteratingDelimitedFileReader(in);
    	while (reader.hasNext()) {
    		Object o = reader.next();
    		Assert.assertTrue(o instanceof IAtomContainer);

    		
    		IAtomContainer a = (IAtomContainer)o;
        	AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(a);
            CDKHueckelAromaticityDetector.detectAromaticity(a);    		
    		DescriptorValue value = pka.calculate(a);
    		PKADescriptorResult<String> result = (PKADescriptorResult<String>)value.getValue(); 
    		a.setProperty(value.getNames()[0], result.doubleValue());
    		a.setProperty(value.getNames()[0]+"-trace", result.getTrace());
    		
    		Double d = Double.valueOf(a.getProperty("SMARTS pKa").toString());
    		if (!d.equals(result.doubleValue())) {
        		System.out.print(result.doubleValue());
        		System.out.print('\t');
        		System.out.print(a.getProperty("SMARTS pKa"));
        		System.out.print('\t');
        		System.out.println(a.getProperty("SMILES"));
    			
    		}

    		writer.write(a);
    	}
    	in.close();
    	writer.close();
    	
    }
}
