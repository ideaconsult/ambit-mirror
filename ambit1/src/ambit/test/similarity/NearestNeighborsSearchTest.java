/* NearestNeighborsSearchTest.java
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

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import ambit.similarity.DistanceComparable;
import ambit.similarity.IDistanceFunction;
import ambit.similarity.NearestNeighborsSearch;

public class NearestNeighborsSearchTest extends TestCase {

    public NearestNeighborsSearchTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetNearestNeighbors() {
        NearestNeighborsSearch<Float> nn = new NearestNeighborsSearch<Float>();
        nn.setDistanceFunction(new IDistanceFunction<Float>() {
            public float getDistance(Float object1, Float object2) {
                
                return Math.abs(object1 - object2);
            }
            public float getNativeComparison(Float object1, Float object2) throws Exception {
                // TODO Auto-generated method stub
                return -getDistance(object1, object2);
            }
        });
        
        nn.addObject(new Float(3), 4);
        nn.addObject(new Float(1), 2);
        nn.addObject(new Float(2), 3);
        nn.addObject(new Float(1.5), 2.5);
        nn.addObject(new Float(77), 78);
        nn.addObject(new Float(2.77), 3.77);
        
        try {
            nn.setLowestDistanceIsClosest(true);
            List<DistanceComparable<Float>> knn = nn.getNearestNeighbors(new Float(2), 3);
            assertEquals(3,knn.size());
            double[] d = {0.0,0.5,0.77};
            Iterator<DistanceComparable<Float>> i = knn.iterator();
            for (int k=0; i.hasNext(); k++) {
                DistanceComparable<Float> dc = i.next();
                assertEquals(d[k],dc.getDistance(),0.001);
                System.out.println(dc.getProperty());
            }
            
            //System.out.println(knn);
            nn.setLowestDistanceIsClosest(false);
            knn = nn.getNearestNeighbors(new Float(2.5), 4);
            assertEquals(4,knn.size());
            float[] d4 = {74.5f,1.5f,1.0f,0.5f};
            float[] f4 = {77.0f,1.0f,1.5f,3.0f};
            i = knn.iterator();
            for (int k=0; i.hasNext(); k++) {
                DistanceComparable o = i.next();
                assertEquals(d4[k],o.getDistance());
                assertEquals(f4[k],((Float)o.getObject()));
            }
            //System.out.println(knn);
        } catch (Exception x) {
            fail(x.getMessage());
        }
        
    }

}
