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

import ambit2.base.config.Preferences;
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
	protected boolean silent = false;
	public boolean isSilent() {
		return silent;
	}
	public void setSilent(boolean silent) {
		this.silent = silent;
	}
	public LoginSequence(Activity onSuccess) {
		this(onSuccess,null);
	}
	public LoginSequence(Activity onSuccess,Activity onFailure) {
        Primitive login = new Primitive(
                DBWorkflowContext.DBCONNECTION_URI,
                DBWorkflowContext.DBCONNECTION_URI,new Performer() {
            @Override
            public Object execute() throws Exception {
                Object o = getTarget();
                if (o == null) {
                	Object ol = context.get(DBWorkflowContext.LOGININFO);
                	if ((ol == null) || !(ol instanceof LoginInfo)) {
                		ol = new LoginInfo();
                	}
                	LoginInfo li = null;
                	if (!silent) {
                		
	                    ValueLatchPair<LoginInfo> latch = new ValueLatchPair<LoginInfo>((LoginInfo)ol);
	                    context.put(DBWorkflowContext.USERINTERACTION,latch);
	                    //This is a blocking operation!
	                    
	                    try {
	                        li = latch.getLatch().getValue();
	                        if (li!=null)
	                        	context.put(DBWorkflowContext.LOGININFO,li);
	                        else
	                        	context.remove(DBWorkflowContext.LOGININFO);
	                        context.remove(DBWorkflowContext.USERINTERACTION);
	                    } catch (InterruptedException x) {
	                    	context.remove(DBWorkflowContext.USERINTERACTION);
	                        context.put(DBWorkflowContext.ERROR, x);
	                        return null;
	                    } catch (Exception x) {
	                    	context.remove(DBWorkflowContext.USERINTERACTION);
	                    	context.put(DBWorkflowContext.ERROR, x);
	                    	return null;
	                    } finally {
	                    	context.remove(DBWorkflowContext.USERINTERACTION);
	                    }
                	} else li = (LoginInfo)ol;
                	
                    if (li != null) {
                    	Preferences.setProperty(Preferences.DATABASE, li.getDatabase());
                    	Preferences.setProperty(Preferences.HOST, li.getHostname());
                    	Preferences.setProperty(Preferences.PORT, li.getPort());
                    	Preferences.setProperty(Preferences.USER, li.getUser());
                    	Preferences.setProperty(Preferences.PASSWORD, "");
                    	Preferences.saveProperties(getClass().getName());
                    	return DatasourceFactory.getConnectionURI(
                            li.getScheme(), li.getHostname(), li.getPort(), 
                            li.getDatabase(), li.getUser(), li.getPassword());
                    }
                    else throw new Exception("Cancelled");
                } else return o;    
            }
        }); 
        login.setName("Login");

        Conditional connect = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
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
                                    context.remove(DBWorkflowContext.DBCONNECTION_URI);
                                    context.put(DBWorkflowContext.ERROR, x);
                                    return false;                                	
                                }

                            } else {
                                context.remove(DBWorkflowContext.DBCONNECTION_URI);
                                return false;
                            }
                        } catch (Exception x) {
                            context.remove(DBWorkflowContext.DBCONNECTION_URI);
                            context.put(DBWorkflowContext.ERROR, x);                            
                            return false;
                        }
                    }
                }, 
                onSuccess,
                onFailure);
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
