/* TestDBWriter.java
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


import javax.sql.DataSource;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

import ambit2.database.DatasourceFactory;
import ambit2.repository.processors.IRepositoryAccess;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.InternalProcessorPerformer;

import com.microworkflow.process.Primitive;
import com.microworkflow.process.Workflow;
import com.microworkflow.process.WorkflowContext;

public class TestDBWriterWorkflow extends TestCase {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    public void test() throws Exception {
        ActivityPrimitive<DataSource, IRepositoryAccess> p = 
            new ActivityPrimitive<DataSource, IRepositoryAccess>(
                    DBWorkflowContext.DATASOURCE,"writer",new ProcessorDBWriter());
        
        Primitive a = 
            new Primitive("structure","ids",new InternalProcessorPerformer("writer"));
        
        final Workflow wc = new Workflow();       
        final WorkflowContext wcc = new DBWorkflowContext();

        wcc.put(DBWorkflowContext.DATASOURCE, DatasourceFactory.getDataSource(
                DatasourceFactory.getConnectionURI("jdbc:mysql","localhost",null,"ambit2","root","")
                ));
        wcc.put("structure","test");
        wc.setDefinition(p.addStep(a));
        wc.executeWith(wcc);
        Object o = wcc.get("error");
        if ((o != null) && (o instanceof Exception))
            throw new Exception((Exception)o);
            
    }

}
