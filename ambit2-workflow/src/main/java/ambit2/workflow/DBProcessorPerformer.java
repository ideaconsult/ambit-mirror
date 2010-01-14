/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.workflow;

import java.sql.Connection;

import javax.sql.DataSource;

import ambit2.db.IDBProcessor;
import ambit2.db.SessionID;
import ambit2.db.processors.ProcessorCreateSession;

/**
 * Sets {@link Connection}, taken from workflow context get(DBWorkflowContext.DATASOURCE) and calls {@link IDBProcessor#process}.
 * @author nina
 *
 * @param <Target>
 * @param <Result>
 */
public class DBProcessorPerformer<P extends IDBProcessor<Target,Result>,Target,Result> extends ProcessorPerformer<P,Target,Result> {
	protected boolean skipSession = false;
	public DBProcessorPerformer(P processor,boolean skipSession) {
		super(processor);
		this.skipSession = skipSession;
	}
    public DBProcessorPerformer(P processor) {
		this(processor,false);
	}

    @Override
    public synchronized P getProcessor() {
    	return super.getProcessor();
    }
    @Override
    public Result execute() throws Exception {
        if (processor == null) return null;
        try {
        	DataSource datasource = (DataSource)get(DBWorkflowContext.DATASOURCE);
            if (!skipSession) {

	            SessionID session  = (SessionID)get(DBWorkflowContext.SESSION);
	            if ((session == null) || (session.getId()<=0)) {
	            	ProcessorCreateSession createsession = new ProcessorCreateSession();
	            	Connection c = datasource.getConnection();
	            	createsession.setConnection(c);
	            	session = createsession.process(new SessionID());
	            	context.put(DBWorkflowContext.SESSION,session);
	            	createsession.close();
	            }
	            processor.setSession(session);
            }
        	Connection c = datasource.getConnection();
            processor.setConnection(c);            
            processor.open();
            Target t = getTarget();
            Result result = processor.process(t);
            processor.close();          
            processor.setConnection(null);     
            if (!c.isClosed()) c.close();

            return result;
        } catch (Exception e) {
        	e.printStackTrace();
            context.put(errorTag, e);
            return null;
        } catch (Throwable x) {
        	x.printStackTrace();
            context.put(errorTag, x);
            return null;        	
        }
    }
}


