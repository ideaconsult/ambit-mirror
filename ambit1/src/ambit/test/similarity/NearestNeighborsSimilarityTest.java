/* NearestNeighborsSimilarityTest.java
 * Author: Nina Jeliazkova
 * Date: Feb 4, 2007 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
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

package ambit.test.similarity;

import junit.framework.TestCase;

import org.openscience.cdk.SetOfAtomContainers;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit.exceptions.AmbitException;
import ambit.similarity.IDistanceFunction;
import ambit.similarity.NNAtomEnvironmentSimilarity;
import ambit.similarity.NNFingerprintSimilarity;
import ambit.similarity.NearestNeighborsSimilarity;

public class NearestNeighborsSimilarityTest extends TestCase {

    public NearestNeighborsSimilarityTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    public void testAtomEnvironments() {
        NNAtomEnvironmentSimilarity nns = new NNAtomEnvironmentSimilarity(3);
        SetOfAtomContainers a = new SetOfAtomContainers();
        for (int i=6; i < 12; i++) {
            IAtomContainer a1 = MoleculeFactory.makeAlkane(i);
            a.addAtomContainer(a1);
        }
        runTest(nns,a);

    }    
    public void testFingerprints() {
        NNFingerprintSimilarity nns = new NNFingerprintSimilarity(5);
        SetOfAtomContainers a = new SetOfAtomContainers();
        for (int i=2; i < 12; i++) {
            IAtomContainer a1 = MoleculeFactory.makeAlkane(i);
            a.addAtomContainer(a1);
        }
        runTest(nns,a);

    }
    public void testFloat() {
        NearestNeighborsSimilarity<Float> nns = new NearestNeighborsSimilarity<Float>(
                new IDistanceFunction<Float>(){
                    public float getDistance(Float object1, Float object2) throws Exception {
                        return Math.abs(object1 - object2);
                    }
                    public float getNativeComparison(Float object1, Float object2) throws Exception {
                        // TODO Auto-generated method stub
                        return -getDistance(object1, object2);
                    }
                    @Override
                    public String toString() {
                        return "Float";
                    }
                },4) {
            @Override
            protected Float calculateComparableProperty(Object object) throws AmbitException {
                return (Float) ((IAtomContainer) object).getProperty("NNTest");
            }

			@Override
			protected Float getSelectedAttributes(Float attributes) {
				return attributes;
			}
            
        };
        SetOfAtomContainers a = new SetOfAtomContainers();
        for (int i=2; i < 12; i++) {
            IAtomContainer a1 = MoleculeFactory.makeAlkane(i);
            a1.setProperty("NNTest", new Float(i));
            a.addAtomContainer(a1);
        }
        runTest(nns,a);
        
        for (int i=0; i < a.getAtomContainerCount(); i++) 
            try {
                //System.out.println(a.getAtomContainer(i).getProperty(nns.getProperties()[0]));
                Object r = a.getAtomContainer(i).getProperty(nns.getProperties()[0]);
                if ((i==0) || (i==(a.getAtomContainerCount()-1))) {
                    assertEquals(1.5, r);
                } else assertEquals(1.0, r);
            } catch (Exception x) {
                x.printStackTrace();
                fail(x.getMessage());
            }        
    }
    public void runTest(NearestNeighborsSimilarity nns, SetOfAtomContainers a) {

        try {
            nns.setPredicting(false);
            nns.buildInitialize();
            for (int i=0; i < a.getAtomContainerCount(); i++) 
                try {
                    nns.process(a.getAtomContainer(i));
                } catch (Exception x) {
                    fail(x.getMessage());
                }
            nns.buildCompleted();
            
            assertEquals(a.getAtomContainerCount(),nns.getNearestNeighborsSearch().size());
            
            nns.setPredicting(true);
            System.out.println(nns);
            for (int i=0; i < a.getAtomContainerCount(); i++) 
                try {
                    nns.process(a.getAtomContainer(i));
                    System.out.println(a.getAtomContainer(i).getProperty(nns.getProperties()[0]));

                } catch (Exception x) {
                    x.printStackTrace();
                    fail(x.getMessage());
                }
            
        } catch (Exception x) {
            
        }
    }

}
