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

import ambit2.core.exceptions.AmbitException;
import ambit2.db.DatasourceFactory;
import ambit2.db.SourceDataset;
import ambit2.db.search.IStoredQuery;

import com.microworkflow.process.WorkflowContext;

public class DBWorkflowContext extends WorkflowContext {
	public static String SESSION = "ambit2.workflow.DBWorkflowContext.SESSION";
    public static String DATASOURCE = "ambit2.workflow.DBWorkflowContext.DATASOURCE";
    public static String DBCONNECTION_URI = "ambit2.workflow.DBWorkflowContext.DBCONNECTION_URI";
    public static String LOGININFO = "ambit2.workflow.DBWorkflowContext.LOGININFO";
    public static String DATASET = "ambit2.workflow.DBWorkflowContext.DATASET";
    public static String QUERY = "ambit2.workflow.DBWorkflowContext.QUERY";
    public static String STOREDQUERY = "ambit2.workflow.DBWorkflowContext.STOREDQUERY";    
    public static String ERROR = "ambit2.workflow.DBWorkflowContext.ERROR";
    public static String BATCHSTATS = "ambit2.workflow.DBWorkflowContext.BatchStatistics";
    public static String PAGE = "ambit2.workflow.DBWorkflowContext.PAGE";
    public static String PAGESIZE = "ambit2.workflow.DBWorkflowContext.PAGESIZE";
    public static String USERINTERACTION = "ambit2.workflow.DBWorkflowContext.USERINTERACTION";
    public static String STRUCTURES = "ambit2.workflow.DBWorkflowContext.STRUCTURES";
    public static String PROFILE = "ambit2.workflow.DBWorkflowContext.PROFILE";
    public static String TEMPLATES = "ambit2.workflow.DBWorkflowContext.TEMPLATES";
    public static String DESCRIPTORS = "ambit2.workflow.DBWorkflowContext.DESCRIPTORS";
    public static String ENDPOINTS = "ambit2.workflow.DBWorkflowContext.ENDPOINTS";
    public static String REPORT = "ambit2.workflow.DBWorkflowContext.REPORT";      
    
    
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
    /*
    public IBatchStatistics getBatchStatistics() {
        Object o = get(BATCHSTATS);
        if ((o == null) || !(o instanceof IBatchStatistics)) {
            IBatchStatistics bs = new DefaultBatchStatistics();
            setBatchStatistics(bs);
            return bs;
        } else return (IBatchStatistics) o;
    }
    
    public void setBatchStatistics(IBatchStatistics bs) {
        put(BATCHSTATS,bs);
    }    
    */
    public SourceDataset getDataset() {
        Object o = get(DATASET);
        if ((o == null) || !(o instanceof SourceDataset)) {
            SourceDataset ds = new SourceDataset();
            setDataset(ds);
            return ds;
        } else return (SourceDataset) o;
    }
    
    public void setDataset(SourceDataset dataset) {
        put(DATASET,dataset);
    }
    public void setQuery(IStoredQuery query) {
        put(QUERY,query);
    }    
    public IStoredQuery getQuery() {
        return (IStoredQuery)get(QUERY);
    }
    private void setInt(String key,int value) {
        put(key,value);
    } 
    private int getInt(String key,int defaultvalue) {
        Object o = get(key);
        if (o == null) {
            setInt(key, defaultvalue);
            return defaultvalue;
        } else return ((Integer) o).intValue();
    }       
    public void setPage(int page) {
        setInt(PAGE,page);
    }    
    public int getPage() {
        return getInt(PAGE,1);
    }
    public void setPageSize(int pagesize) {
        setInt(PAGESIZE,pagesize);
    }    
    public int getPagesize() {
        return getInt(PAGESIZE,25);
    }    
}
