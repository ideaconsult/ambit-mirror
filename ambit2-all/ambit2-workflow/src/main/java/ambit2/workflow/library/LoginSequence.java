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

package ambit2.workflow.library;

import java.sql.Connection;

import javax.sql.DataSource;

import ambit2.db.DatasourceFactory;
import ambit2.db.LoginInfo;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Activity;
import com.microworkflow.process.Conditional;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.ValueLatchPair;

public class LoginSequence extends Sequence {
	public LoginSequence(Activity onSuccess) {
        Primitive login = new Primitive(
                DBWorkflowContext.DBCONNECTION_URI,
                DBWorkflowContext.DBCONNECTION_URI,new Performer() {
            @Override
            public Object execute() {
            	System.out.println(this);
                Object o = getTarget();
                if (o == null) {
                    ValueLatchPair<LoginInfo> latch = new ValueLatchPair<LoginInfo>(new LoginInfo());
                    context.put(DBWorkflowContext.USERINTERACTION,latch);
                    //This is a blocking operation!
                    try {
                        LoginInfo li = latch.getLatch().getValue();
                        context.remove(DBWorkflowContext.USERINTERACTION);
                        return DatasourceFactory.getConnectionURI(
                                li.getScheme(), li.getHostname(), li.getPort(), 
                                li.getDatabase(), li.getUser(), li.getPassword());
                    } catch (InterruptedException x) {
                    	context.remove(DBWorkflowContext.USERINTERACTION);
                        context.put(DBWorkflowContext.ERROR, x);
                        return null;
                    }
                    
                } else return o;    
            }
        }); 
        login.setName("Login");

        Conditional connect = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
                    	System.out.println(this);
                        try {
                            Object o = context.get(DBWorkflowContext.DBCONNECTION_URI);
                            if (o != null) {
                            	DataSource datasource = DatasourceFactory.getDataSource(o.toString());
                                
                                try {
                                	Connection conn = datasource.getConnection();
                                	context.put(DBWorkflowContext.DATASOURCE,datasource);
                                	conn.close();
                                    return  true;
                                } catch (Exception x) {
                                	context.put(DBWorkflowContext.ERROR, x);
                                    context.remove(DBWorkflowContext.DBCONNECTION_URI);
                                    return false;                                	
                                }

                            } else {
                                context.remove(DBWorkflowContext.DBCONNECTION_URI);
                                return false;
                            }
                        } catch (Exception x) {
                            context.put(DBWorkflowContext.ERROR, x);
                            context.remove(DBWorkflowContext.DBCONNECTION_URI);
                            return false;
                        }
                    }
                }, 
                onSuccess,
                null);
        connect.setName("Connect");
        setName("[Log into database]");
        
        addStep(login);
        addStep(connect);
        
	}
	@Override
	public String toString() {
		return "Log in";
	}
}
