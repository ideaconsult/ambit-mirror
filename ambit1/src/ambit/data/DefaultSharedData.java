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

package ambit.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;

import ambit.database.data.DefaultSharedDbData;
import ambit.io.batch.DefaultBatchStatistics;
import ambit.io.batch.IBatchStatistics;
import ambit.io.batch.LoggedBatchStatistics;
import ambit.log.AmbitLogger;

public abstract class DefaultSharedData  extends Observable implements ISharedData {
	protected static AmbitLogger logger = new AmbitLogger(DefaultSharedDbData.class);
	protected String confFile = "ambitdb.xml";
	protected DefaultData defaultData = null;
	protected JobStatus jobStatus = null;
	protected IBatchStatistics batchStatistics;	

	
    public DefaultSharedData(String configFile) {
        super();
        this.confFile = configFile;
        init();
    }
	protected abstract void init() ;
    public void saveConfiguration() {
        File file = new File(confFile);
        try {
			//ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file,false));
			//os.writeObject(defaultData);
			//os.close();
            FileOutputStream out = new FileOutputStream(file);
            defaultData.writeXML(out);
            out.close();
        } catch (Exception x) {
            logger.error(x);
        } 
    }
    public void loadConfiguration() {
    	try {
	        File file = new File(confFile);
	        if (file.exists()) 
	            try {
	                FileInputStream in = new FileInputStream(file);
	                defaultData = new DefaultData();
	                defaultData.readXML(in);
	                System.out.println(defaultData);
	                in.close();
					return;
		        } catch (IOException x) {
		        	defaultData = new DefaultData();
		            logger.error(x);
		        }
		   else {
		       defaultData = new DefaultData();
		       logger.info(confFile + " not found. Loading default "+defaultData.toString());
		   }
    	} catch (Exception x) {
    		defaultData = new DefaultData();
    	}
	        

    }
    public String getDefaultDir() {
        if (defaultData == null) return "";
        Object o = defaultData.get(DefaultData.DEFAULT_DIR);
        if (o == null) return "";
        else return o.toString();
    }
    public void setDefaultDir(String defaultDir) {
        if (defaultData == null) defaultData = new DefaultData(); 
        defaultData.put(DefaultData.DEFAULT_DIR,defaultDir);
    }    

	public JobStatus getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}
    
    public String getTMPFile() {
        if (defaultData == null) return "QUERY.TXT";
        Object o = defaultData.get(DefaultData.TMPFILE);
        if (o == null) return "QUERY.TXT";
        else return o.toString();
    }
    public void setTMPFile(String tmpFilename) {
        if (defaultData == null) defaultData = new DefaultData(); 
        defaultData.put(DefaultData.TMPFILE,tmpFilename);
    }

    public String getTemplateDir() {
        if (defaultData == null) return "";
        Object o = defaultData.get(DefaultData.TEMPLATES_DIR);
        if (o == null) return "";
        else return o.toString();
    }
    public void setTemplateDir(String defaultDir) {
        if (defaultData == null) defaultData = new DefaultData(); 
        defaultData.put(DefaultData.TEMPLATES_DIR,defaultDir);
    }	
    public synchronized IBatchStatistics getBatchStatistics() {
        return batchStatistics;
    }
    public synchronized void setBatchStatistics(
            DefaultBatchStatistics batchStatistics) {
        this.batchStatistics = batchStatistics;
    }
    
}
