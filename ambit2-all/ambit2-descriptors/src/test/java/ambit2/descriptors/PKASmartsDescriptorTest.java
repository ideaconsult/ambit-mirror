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


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ambit2.core.query.smarts.SmartsPatternAmbit;

public class PKASmartsDescriptorTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws Exception {
        PKASmartsDescriptor d = new PKASmartsDescriptor();
        Hashtable<Integer,PKANode> tree = d.getTree();
        Enumeration<Integer> k = tree.keys();
        SmartsPatternAmbit pattern = new SmartsPatternAmbit();
        ArrayList<String> smarts = new ArrayList<String>();
        int failedNodes = 0;
        int failedSmarts = 0;
        int nullSmarts = 0;
        while (k.hasMoreElements()) {
            PKANode node = tree.get(k.nextElement());
            try {
                if (node.getSmarts() == null) {
                    nullSmarts++;
                } 
                pattern.setSmarts(node.getSmarts());
            } catch (Exception x) {
                failedNodes ++;
                if (smarts.indexOf(node.getSmarts())<0) {
                    smarts.add(node.getSmarts());
                    failedSmarts++;
                }
            }
        }
        System.out.println("Failed nodes "+failedNodes);
        System.out.println("Failed smarts "+failedSmarts);

        for (int i=0; i < smarts.size();i++)
            System.out.println(smarts.get(i));
        
        Assert.assertEquals(1,nullSmarts); //root smarts
        Assert.assertTrue(failedNodes==0);        
    }
    
}
