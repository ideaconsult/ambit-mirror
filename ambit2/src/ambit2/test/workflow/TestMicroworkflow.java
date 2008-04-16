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
import java.util.Vector;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import junit.framework.TestCase;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.RuleOfFiveDescriptor;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Activity;
import com.microworkflow.process.ICompositeActivity;
import com.microworkflow.process.IConditionalActivity;
import com.microworkflow.process.IEmbeddedActivity;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.While;
import com.microworkflow.process.Workflow;
import com.microworkflow.process.WorkflowContext;
import com.microworkflow.ui.IWorkflowContextFactory;
import com.microworkflow.ui.IWorkflowFactory;
import com.microworkflow.ui.WorkflowContextListener;
import com.microworkflow.ui.WorkflowContextPanel;
import com.microworkflow.ui.WorkflowMonitor;
import com.microworkflow.ui.WorkflowPanel;

public class TestMicroworkflow extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    void printActivity(Activity activity, int level) {
        if (activity == null)
            return;
        for (int i = 0; i < level; i++)
            System.out.print(' ');
        System.out.print(level);
        System.out.print('.');
        System.out.println(activity);
        if (activity instanceof ICompositeActivity) {
            ArrayList<Activity> a = ((ICompositeActivity) activity)
                    .getComponents();
            for (int i = 0; i < a.size(); i++)
                printActivity(a.get(i), level + 1);
        }
        if (activity instanceof IEmbeddedActivity) {
            printActivity(((IEmbeddedActivity) activity).getBody(), level + 1);
        }
        if (activity instanceof IConditionalActivity) {
            printActivity(((IConditionalActivity) activity).getThenBranch(),
                    level + 1);
            printActivity(((IConditionalActivity) activity).getElseBranch(),
                    level + 1);
        }

    }

    public void test() throws Exception {
        int n = 1000;
        final WorkflowContext wc = new WorkflowContext();
        StringBuffer b = new StringBuffer();
        StringBuffer C = new StringBuffer();
        for (int i = 0; i < n; i++) {
            if ((i % 30) == 0) C=new StringBuffer("C");
            else C.append("C");
            b.append(C);

            b.append("\n");
        }

        // wc.put("SMILES","c1ccccc1\nCC\nCCC\nCCCC\nCCCCC");
        wc.put("SMILES", b.toString());
        wc.put("Molecule", null);
        wc.put("Writer", new Vector<Double>());

        ConsoleHandler ch = new ConsoleHandler();
        // ch.setLevel(Level.FINEST);
        Logger.getLogger("com.microworkflow.execution.WorkflowContext")
                .addHandler(ch);
        Logger.getLogger("com.microworkflow.execution.WorkflowContext")
                .setLevel(Level.FINEST);
        Logger.getLogger("com.microworkflow.execution.Scheduler")
                .addHandler(ch);
        Logger.getLogger("com.microworkflow.execution.Scheduler").setLevel(
                Level.FINEST);
        Logger.getLogger("com.microworklfow.process.workflow").addHandler(ch);
        Logger.getLogger("com.microworklfow.process.workflow").setLevel(
                Level.FINEST);

        final Workflow molWorkflow = new Workflow();
        /*
         * molWorkflow.addPropertyChangeListener(new PropertyChangeListener() {
         * public void propertyChange(PropertyChangeEvent arg0) {
         * System.out.println("WFListener:\t"+arg0); if (arg0.getNewValue()
         * instanceof Activity)
         * 
         * WorkflowTools.traverseActivity((Activity)arg0.getNewValue(),0, new
         * ILookAtActivity() { public void look(Activity activity, int level) {
         * for (int i=0; i < level; i++) System.out.print(' ');
         * System.out.print(level); System.out.print('.');
         * System.out.println(activity); } }); } });
         */
        molWorkflow.setDefinition(getWorkflow());
        /*
         * wc.addPropertyChangeListener(new PropertyChangeListener() { public
         * void propertyChange(PropertyChangeEvent arg0) {
         * System.out.println("WFCListener:\t" + arg0); } });
         */
        IWorkflowFactory wff = new IWorkflowFactory() {
            public Workflow getWorkflow() {
                  return molWorkflow;
            }
        }  ;      
        IWorkflowContextFactory wfcf = new IWorkflowContextFactory() {
            public WorkflowContext getWorkflowContext() {
                return wc;
            }
        };              
        
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());        
        WorkflowMonitor panel = new WorkflowMonitor(new WorkflowPanel(
                molWorkflow), null,new WorkflowContextPanel(wfcf),
                wff,wfcf
        );
        // panel.setPreferredSize(new Dimension(200,200));

        JOptionPane.showMessageDialog(null, panel,molWorkflow.toString(),JOptionPane.PLAIN_MESSAGE);

        WorkflowContext result = molWorkflow.executeWith(wc);
        Object r = wc.get("Writer");
        System.out.println("Writer = " + r);
        assertNull(wc.get("Source"));
        assertTrue(r instanceof ArrayList);
        ArrayList<Integer> ri = (ArrayList<Integer>) r;
        assertEquals(n, ri.size());
        for (int i = 0; i < n; i++)
            assertEquals(new Double(i + 1), ri.get(i));

    }

    protected Activity getWorkflow() {

        Primitive initMolecule = new Primitive("SMILES", "Source",
                new Performer() {
                    public Object execute() {

                        ((Vector<Double>) get("Writer")).clear();
                        IIteratingChemObjectReader reader = new IteratingSMILESReader(
                                new StringReader(getTarget().toString()));
                        return reader;
                    }
                });
        initMolecule.setName("Init reader");

        Primitive readMolecule = new Primitive("Source", "Molecule",
                new Performer() {
                    public Object execute() {
                        IIteratingChemObjectReader reader = (IIteratingChemObjectReader) getTarget();
                        return reader.next();
                    }
                });
        readMolecule.setName("Read molecule");

        Primitive close = new Primitive("Source", "Source", new Performer() {
            public Object execute() {
                IIteratingChemObjectReader reader = (IIteratingChemObjectReader) getTarget();
                try {
                    if (reader != null)
                        reader.close();
                } catch (Exception x) {
                    x.printStackTrace();
                }
                reader = null;
                return reader;
            }
        });
        close.setName("Close file");
        Primitive calc = new Primitive("Molecule", "Result", new Performer() {

            public Object execute() {
                IMolecule m = (IMolecule) getTarget();
                if (m == null)
                    return 0;
                try {
                    return new Double(m.getAtomCount());
                } catch (Exception x) {
                    x.printStackTrace();
                    return 0;
                }
                // return m.getAtomCount();
            }
        });
        calc.setName("Calculate atom number");
        
        Primitive calcMore = new Primitive("Molecule",  new Performer() {
             IMolecularDescriptor d = new RuleOfFiveDescriptor();

            public Object execute() {
                IMolecule m = (IMolecule) getTarget();
                if (m == null)
                    return null;
                try {
                    DescriptorValue v = d.calculate(m);
                    return null;
                } catch (Exception x) {
                    return null;
                }
                // return m.getAtomCount();
            }
        });
        calcMore.setName("Calculate RuleOfFiveDescriptor");        
        // write somewhere
        Primitive write = new Primitive("Result", new Performer() {
            public Object execute() {
                Object m = getTarget();
                ((Vector<Double>) get("Writer")).add((Double) m);
                return null;
            }
        });

        write.setName("Write result");
        While w = new While();
        w.setName("While");
        w.setBody(readMolecule.addStep(calc).addStep(calcMore).addStep(write));
        w.setTestCondition(new TestCondition() {
            public boolean evaluate() {
                IIteratingChemObjectReader reader = (IIteratingChemObjectReader) get("Source");
                return reader.hasNext();
            }

            @Override
            public String toString() {
                // TODO Auto-generated method stub
                return "Test: hasNext()";
            }
        });

        /*
         * Conditional ifhasNext = new Conditional(new TestCondition() { public
         * boolean evaluate() { IIteratingChemObjectReader
         * reader=(IIteratingChemObjectReader)get("Source"); return
         * reader.hasNext(); }}, readMolecule,close);
         */

        return initMolecule.addStep(w.addStep(close));
    }

}
