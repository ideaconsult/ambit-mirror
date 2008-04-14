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

import ambit2.repository.IDBProcessor;
import ambit2.repository.SessionID;

/**
 * Sets {@link Connection}, taken from workflow context get(DBWorkflowContext.DATASOURCE) and calls {@link IDBProcessor#process}.
 * @author nina
 *
 * @param <Target>
 * @param <Result>
 */
public class DBProcessorPerformer<Target,Result> extends ProcessorPerformer<IDBProcessor<Target,Result>,Target,Result> {
    
    public DBProcessorPerformer(IDBProcessor<Target, Result> processor) {
		super(processor);
	}

    @Override
    public Result execute() {
        if (processor == null) return null;
        try {
        	DataSource datasource = (DataSource)get(DBWorkflowContext.DATASOURCE);
            processor.setConnection(datasource.getConnection());
            SessionID session  = (SessionID)get(DBWorkflowContext.SESSION);
            processor.setSession(session);
            return processor.process(getTarget());
        } catch (Exception e) {
        	e.printStackTrace();
            context.put(errorTag, e);
            return null;
        }
    }
}


