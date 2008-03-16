/* TestMicroworkflow.java
 * Author: Nina Jeliazkova
 * Date: Mar 15, 2008 
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

package ambit2.test.workflow;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Activity;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.While;
import com.microworkflow.process.Workflow;
import com.microworkflow.process.WorkflowContext;

public class TestMicroworkflow extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    public void test() throws Exception {
        WorkflowContext wc=new WorkflowContext();
        wc.put("Source",new IteratingSMILESReader(new StringReader("c1ccccc1\nCC\nCCC\nCCCC\nCCCCC")));
        wc.put("Molecule",null);
        wc.put("Writer",new ArrayList<Integer>());
        
        ConsoleHandler ch = new ConsoleHandler();
        //ch.setLevel(Level.FINEST);
        Logger.getLogger("com.microworkflow.execution.WorkflowContext").addHandler(ch);
        Logger.getLogger("com.microworkflow.execution.WorkflowContext").setLevel(Level.FINEST);
        Logger.getLogger("com.microworkflow.execution.Scheduler").addHandler(ch);
        Logger.getLogger("com.microworkflow.execution.Scheduler").setLevel(Level.FINEST);
        Logger.getLogger("com.microworklfow.process.workflow").addHandler(ch);
        Logger.getLogger("com.microworklfow.process.workflow").setLevel(Level.FINEST);        
        
        
        Workflow molWorkflow=new Workflow();
        molWorkflow.setDefinition(getWorkflow());
        WorkflowContext result=molWorkflow.executeWith(wc);
        Object r = wc.get("Writer");
        System.out.println("Writer = " + r);
        assertNull(wc.get("Source"));
        assertTrue(r instanceof ArrayList);
        ArrayList<Integer> ri = (ArrayList<Integer>)r;
        assertEquals(5,ri.size());
        assertEquals(new Integer(6),ri.get(0));
        assertEquals(new Integer(2),ri.get(1));
        assertEquals(new Integer(3),ri.get(2));
        assertEquals(new Integer(4),ri.get(3));
        assertEquals(new Integer(5),ri.get(4));
        
    }
    protected Activity getWorkflow() {

        Primitive readMolecule  =
            new Primitive("Source","Molecule",
                new Performer() {
                    public Object execute() {
                        IIteratingChemObjectReader reader=(IIteratingChemObjectReader)getTarget();
                        return reader.next();
                    }
                }
            );

        
        Primitive close =
            new Primitive("Source","Source",
                new Performer() {
                    public Object execute() {
                        IIteratingChemObjectReader reader = (IIteratingChemObjectReader)getTarget();
                        try {
                            if (reader != null)
                                reader.close();
                        } catch (Exception x) {
                            x.printStackTrace();
                        }
                        reader = null;
                        return reader;
                    }
                }
            );

        Primitive calc =
            new Primitive("Molecule","Result",
                new Performer() {
                    public Object execute() {
                        IMolecule m=(IMolecule)getTarget();
                        if (m ==null) return 0;
                        return m.getAtomCount();
                    }
                }
            );
        //write somewhere
        Primitive write =
            new Primitive("Result",
                new Performer() {
                    public Object execute() {
                        Object m=getTarget();
                        ((ArrayList<Integer>)get("Writer")).add((Integer)m);
                        return null;
                    }
                }
            );        
        
        
        While w = new While();
        w.setBody(readMolecule.addStep(calc).addStep(write));
        w.setTestCondition(new TestCondition() {
            public boolean evaluate() {
                IIteratingChemObjectReader reader=(IIteratingChemObjectReader)get("Source");
                return reader.hasNext();
            }});
        
        /*
        Conditional ifhasNext =
            new Conditional(new TestCondition() {
                public boolean evaluate() {
                    IIteratingChemObjectReader reader=(IIteratingChemObjectReader)get("Source");
                    return reader.hasNext();
                }},
                readMolecule,close);
        */
                        


        
        
        return w.addStep(close);
    }    
}
