/* DBWorkflowContext.java
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

package ambit2.workflow;

import javax.sql.DataSource;

import ambit2.database.DatasourceFactory;
import ambit2.exceptions.AmbitException;

import com.microworkflow.process.WorkflowContext;

public class DBWorkflowContext extends WorkflowContext {
	public static String SESSION = "ambit2.workflow.DBWorkflowContext.SESSION";
    public static String DATASOURCE = "ambit2.workflow.DBWorkflowContext.DATASOURCE";
    public static String DBCONNECTION_URI = "ambit2.workflow.DBWorkflowContext.DBCONNECTION_URI";
    public static String LOGININFO = "ambit2.workflow.DBWorkflowContext.LOGININFO";
    public static String DATASET = "ambit2.workflow.DBWorkflowContext.DATASET";    
    public static String ERROR = "ERROR";
    
    
    public DataSource getDataSource() throws AmbitException {
        DataSource ds = null;
        Object o = get(DATASOURCE);
        if (o == null) {
            ds = DatasourceFactory.getDataSource(get(DBCONNECTION_URI).toString());
            put(DATASOURCE, ds);
        } else if (o instanceof DataSource) {
            ds = (DataSource)o;
        } else throw new AmbitException("Found instance of "+o.getClass().getName()+ " instead of javax.sql.DataSource");
        return ds;
    }

    public String getConnectionURI() {
        return get(DBCONNECTION_URI).toString();
    }
    public void setConnectionURI(String connURI) {
        put(DBCONNECTION_URI,connURI);
    }
    @Override
    public String toString() {
        return context.toString();
    }
}
