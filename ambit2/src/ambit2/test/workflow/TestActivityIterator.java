/* TestIterativeReader.java
 * Author: Nina Jeliazkova
 * Date: Apr 13, 2008 
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


import java.io.File;
import java.io.FileReader;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

import ambit2.io.RawIteratingSDFReader;
import ambit2.repository.IProcessor;
import ambit2.repository.ProcessorException;
import ambit2.repository.processors.ProcessorFile2FileReader;
import ambit2.workflow.ActivityIterator;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.jukebox.JB_Iterator;

import com.microworkflow.process.Activity;
import com.microworkflow.process.Workflow;
import com.microworkflow.process.WorkflowContext;

public class TestActivityIterator extends TestCase {

    @Before
    public void setUp() throws Exception {
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
    }

    @After
    public void tearDown() throws Exception {
    }
    public void testActivityIterator() throws Exception {
        final Workflow wc = new Workflow();       
        final WorkflowContext wcc = new DBWorkflowContext();

        String filename = "data/misc/test_properties.sdf";
        RawIteratingSDFReader reader = new RawIteratingSDFReader(new FileReader(filename));
        
        wcc.put("reader", reader);
        wcc.put("count",new Integer(0));
        ActivityIterator iterative = new ActivityIterator("reader","structure","count",
                new IProcessor<String, Integer>() {
           public Integer process(String target) throws ProcessorException {
                Integer c = (Integer) wcc.get("count");
                return new Integer(c+1);
            } 
        });
        wc.setDefinition(iterative);
        wc.executeWith(wcc);
        assertEquals(7,wcc.get("count"));
        reader.close();
    }    
    public void testIterativeReaderWorkflow() throws Exception {
        final Workflow wc = new Workflow();       
        final WorkflowContext wcc = new DBWorkflowContext();

        String filename = "data/misc/test_properties.sdf";
        
        wcc.put("file", new File(filename));
        
        JB_Iterator<String,Integer> jb = new JB_Iterator<String, Integer>();
        Activity a = jb.createReaderIterationBlock(
                "file", "reader","structure","count","error",
                new ProcessorFile2FileReader(),
                new IProcessor<String, Integer>() {
                   public Integer process(String target) throws ProcessorException {
                        Object o = wcc.get("count");
                        if (o==null) return new Integer(1);
                        return 
                            new Integer(((Integer)o)+1);
                    } 
                 }                
                );
               
 
        wc.setDefinition(a);

        wc.executeWith(wcc);
        assertEquals(7,wcc.get("count"));
        assertNull(wcc.get("error"));
    }      
}
