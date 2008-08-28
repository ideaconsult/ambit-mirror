/* FileSystemXMLApplicationContextTest.java
 * Author: Nina Jeliazkova
 * Date: Jul 5, 2008 
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

package nplugins.test.shell.application;

import java.io.StringReader;

import junit.framework.TestCase;
import nplugins.demo.DemoPlugin;
import nplugins.shell.application.FileSystemXMLApplicationContext;

import org.xml.sax.InputSource;

public class FileSystemXMLApplicationContextTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public String getConfig() {
        return 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
        "<beans>\n"+
        "<bean id=\"DemoPlugin\" class=\"nplugins.demo.DemoPlugin\">\n"+
        "    <property name=\"Name\">\n"+
        "        <value>My Demo Plugin</value>\n"+
        "    </property>\n"+
        "</bean>\n"+
        
        "<bean id=\"WorkflowPlugin\" class=\"nplugins.workflow.MWorkflowPlugin\">\n"+
        "    <property name=\"Workflow\">\n"+
        "        <ref local=\"Workflow\"/>\n"+
        "    </property>\n"+
        "</bean>\n"+        
        
        "<bean id=\"Workflow\" class=\"com.microworkflow.process.Workflow\">\n"+
        "    <property name=\"name\">\n"+
        "        <value>TestWorkflow</value>\n"+
        "    </property>\n"+
        "</bean>\n"+
        "</beans>\n";
    }
    public void test() throws Exception {        
        FileSystemXMLApplicationContext ctx = new FileSystemXMLApplicationContext(
                new InputSource(new StringReader(getConfig())));
        Object o = ctx.getBean("DemoPlugin");
        assertNotNull(o);
        assertTrue(o instanceof DemoPlugin);
        assertEquals("My Demo Plugin",((DemoPlugin)o).getName());
    }
}
