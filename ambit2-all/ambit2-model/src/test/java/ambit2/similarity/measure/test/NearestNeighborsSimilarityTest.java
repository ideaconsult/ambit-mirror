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

package ambit2.similarity.measure.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import ambit2.model.IDataset;
import ambit2.model.IModelStatistics;
import ambit2.model.dataset.DoubleArrayDataset;
import ambit2.similarity.knn.NearestNeighborsSimilarity;
import ambit2.similarity.measure.IDistanceFunction;
import ambit2.similarity.stats.Bin;
import ambit2.similarity.stats.DoubleValuesHistogram;

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
    /*
    public void testAtomEnvironments() {
        NNAtomEnvironmentSimilarity nns = new NNAtomEnvironmentSimilarity(3);
        IAtomContainerSet a = SilentChemObjectBuilder.getInstance().newAtomContainerSet();
        for (int i=6; i < 12; i++) {
            IAtomContainer a1 = MoleculeFactory.makeAlkane(i);
            a.addAtomContainer(a1);
        }
        runTest(nns,a);

    } 
    */   
    /*
    public void testFingerprints() {
        NNFingerprintSimilarity nns = new NNFingerprintSimilarity(5);
        IAtomContainerSet a = SilentChemObjectBuilder.getInstance().newAtomContainerSet();
        for (int i=2; i < 12; i++) {
            IAtomContainer a1 = MoleculeFactory.makeAlkane(i);
            a.addAtomContainer(a1);
        }
        runTest(nns,a);

    }
    */
    public void testFloat() throws Exception {
        NearestNeighborsSimilarity<String,Float> nns = new NearestNeighborsSimilarity<String,Float>(
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
        	/*
            @Override
            protected Float calculateComparableProperty(Object object) throws AmbitException {
                return (Float) ((IAtomContainer) object).getProperty("NNTest");
            }
			*/
			@Override
			protected Float getSelectedAttributes(Float attributes) {
				return attributes;
			}
            
        };
        
        double[][] values = new double[10][1];
        double[] observed = new double[10];
        for (int i=0; i < values.length; i++) {
        	values[i][0] = i+2.0;
        	observed[i] = i+2.0;
        }
        DoubleArrayDataset dataset = new DoubleArrayDataset(values,observed);
        Assert.assertEquals(10,dataset.size());
        runTest(nns,dataset,new DoubleValuesHistogram<Bin<Double>>("test"));
        
        dataset.first();
        while (dataset.next()) {
        	//System.out.println(dataset.getPredicted());
        }
    }
    public void runTest(NearestNeighborsSimilarity nns, IDataset a, IModelStatistics stats) throws Exception{
            nns.buildInitialize();
            nns.learn(a);
            
            assertEquals(a.size(),nns.getNearestNeighborsSearch().size());
            

           // System.out.println(nns);
            nns.predict(a, stats);
    }

}
