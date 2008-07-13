/* DefaultSharedDbData.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-30 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
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

package ambit.database.data;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import ambit.data.AmbitUser;
import ambit.data.DefaultData;
import ambit.data.DefaultSharedData;
import ambit.data.JobStatus;
import ambit.database.DbAdmin;
import ambit.database.DbConnection;
import ambit.database.MySQLShell;
import ambit.database.exception.DbAccessDeniedException;
import ambit.database.writers.QueryWriter;
import ambit.exceptions.AmbitException;
import ambit.io.batch.LoggedBatchStatistics;
import ambit.log.AmbitLogger;

/**
 * A storage for various data used in database application. The data encapsulated in this class is:
 * <ul>
 * <li>Name of configuration (XML) file to store default database and directory information. Set in the constructor. 
 * <li>{@link ambit.data.DefaultData}
 * <li>{@link ambit.io.batch.DefaultBatchStatistics}
 * <li>{@link ambit.database.data.ISharedDbData#RESULTS_DESTINATION} Where to store query results.
 * <li>page and page size for queries
 * <li>Whether to stop or start MySQL
 * </ul>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-30
 */
public abstract class DefaultSharedDbData extends DefaultSharedData implements ISharedDbData {
	protected DbConnection dbConnection;
	private boolean stop_mysql;
	protected int pageSize = 600;
	protected int page = 0;
	protected int resultDestination = ISharedDbData.MEMORY_LIST;
	protected int source = ISharedDbData.MEMORY_CURRENT;

	protected MySQLShell mysqlShell;
    /**
     * 
     */
    public DefaultSharedDbData(String configFile) {
        super(configFile);

//        init();
		batchStatistics = new LoggedBatchStatistics();

		
    }
    public DefaultSharedDbData() {
        this("ambitdb.xml");
    }
	/* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        saveConfiguration();
        super.finalize();
    }
	public DbConnection getDbConnection() {
		return dbConnection;
	}

	public void setDbConnection(DbConnection dbConnection) {
		
		if ((dbConnection == null) || (this.dbConnection != dbConnection))  {
			try {
				close();
			} catch (AmbitException x) {
				logger.error(x);
			}
			this.dbConnection = dbConnection;
			if (dbConnection != null)
				jobStatus.setMessage("Connected "+dbConnection.toString());
			
		}	
		setChanged();
		notifyObservers();
	}
	public void close() throws AmbitException {
		 if (dbConnection != null) { 
		 try {
			 QueryWriter.deleteQueryPerUser(dbConnection);
		 } catch (Exception x) {
			 logger.error(x);
		 }
		 try {

			 defaultData.put(DefaultData.DATABASE,getDatabase());
			 defaultData.put(DefaultData.HOST,getHost());
			 defaultData.put(DefaultData.USER,getUser().getName());
			 
			 
			 		     
		     saveConfiguration();
		     dbConnection.close(); dbConnection = null; 	
		     jobStatus.setMessage("Database connection closed");
			 setChanged();
			 notifyObservers();		     
		     }
		 catch (SQLException ex) {
			 dbConnection = null;
			 throw new AmbitException(ex);
		 }
		 }
	}
	public void open(String host, String port, String database, String user, String password) throws AmbitException {
		open(host,port,database,user,password,true);
	}	
	public void open(String host, String port, String database, String user, String password, boolean verify) throws AmbitException {
		if (dbConnection == null) 
			dbConnection = new DbConnection(host,port,database,user,password);
		else {
			dbConnection.setDatabase(database);
			dbConnection.setHost(host);
			dbConnection.setPort(port);
			
			AmbitUser u = dbConnection.getUser();
			if (u == null) {
			    u = new AmbitUser(user);
			    dbConnection.setUser(u);
			}
			u.updateConnectionData(user,password,host);
		}
		if (dbConnection.open(verify)) {

		    jobStatus.setMessage("Connected to "+dbConnection.toString());
			defaultData.put(DefaultData.DATABASE,database);
			defaultData.put(DefaultData.HOST,host);
			defaultData.put(DefaultData.USER,user);
			defaultData.put(DefaultData.PORT,port);
			if (password.equals("")) defaultData.put(DefaultData.PASSWORD,"NO");
			else defaultData.put(DefaultData.PASSWORD,"YES");
			logger.info("\nConnection opened: host\t"+host+"\tdatabase\t"+database+"\tuser\t"+user);	
		} else logger.info("\nConnection NOT opened: host\t"+host+"\tdatabase\t"+database+"\tuser\t"+user);
		setChanged();
		notifyObservers();
		
	}	
		 
	/**
	 * This is used to send message about completing a job
	 *
	 */
	public void done() {
		
	}
	public String getHost() {
		if (dbConnection == null) return defaultData.get(DefaultData.HOST).toString();
		else return dbConnection.getHost();
	}
	public String getDatabase() {
		if (dbConnection == null) return defaultData.get(DefaultData.DATABASE).toString();
		else return dbConnection.getDatabase();
	}
	public String getPort() {
		if (dbConnection == null) return defaultData.get(DefaultData.PORT).toString();
		else return dbConnection.getPort();
	}	
	public AmbitUser getUser() {
		if (dbConnection == null) return null;
		else return dbConnection.getUser();
	}

	protected void init() {
        mysqlShell = null;
        dbConnection = null;
		AmbitLogger.configureLog4j(true);
		jobStatus = new JobStatus();
		jobStatus.setModified(true);
		
	    loadConfiguration();

		dbConnection = new DbConnection(defaultData.get(DefaultData.HOST).toString(),
		        defaultData.get(DefaultData.PORT).toString(),
		        defaultData.get(DefaultData.DATABASE).toString(),
		        defaultData.get(DefaultData.USER).toString(),
		        "");
		

	}

    
	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String host = getHost();
        if (host.equals("localhost")) host = "this computer";
        return "MySQL database " + getDatabase() + " running on " + host + 
        	" port " + getPort() + 
        	" user " + getUser();
    }
	public void tryMYSQL() {
        try {
            //First try it 
            DbAdmin dbc = new DbAdmin(
            defaultData.get(DefaultData.HOST).toString(),
            defaultData.get(DefaultData.PORT).toString(),
            defaultData.get(DefaultData.DATABASE).toString(),
            defaultData.get(DefaultData.USER).toString(),
            "");
            //DbAdmin dbc = new DbAdmin("localhost","33060","mysql","root","");
            if (dbc.open(false)) {
                logger.info("successfully connected");
                
            }
            try {
                dbc.close();
            } catch (SQLException x) {
            	jobStatus.setError(x);
                //x.printStackTrace();
            }
            logger.info("MySQL already running on " + defaultData.get(DefaultData.HOST) + " port "+defaultData.get(DefaultData.PORT));
            stop_mysql  = false;
            return;
        } catch (DbAccessDeniedException x) {
            Object pass = defaultData.get(DefaultData.PASSWORD);
            //MySQL running, but password is needed to connect
            if ((pass !=null) && pass.equals("YES")) {
	            logger.error(x);
	            logger.info("MySQL already running on " + defaultData.get(DefaultData.HOST) + " port "+defaultData.get(DefaultData.PORT));
	            stop_mysql  = false;
	            return;
            } else {
            	jobStatus.setError(x);
                logger.error(x);
            }
        } catch (AmbitException x) {
        	jobStatus.setError(x);
            logger.error(x);
        }
        try {
            mysqlShell = new MySQLShell();
            mysqlShell.startMySQL();
            stop_mysql  = true;
            
            defaultData.put(DefaultData.HOST,"localhost");
            defaultData.put(DefaultData.PORT,"33060");
            defaultData.put(DefaultData.DATABASE,"ambit");
            defaultData.put(DefaultData.USER,"root");
            defaultData.put(DefaultData.PASSWORD,"NO");
            System.out.println("MySQL started locally on port 33060");
        } catch (AmbitException x) {
            //JOptionPane.showMessageDialog(null,x);
            jobStatus.setError(x);
            logger.error(x);
        }
	}
	public void stopMySQL() throws AmbitException {
		
		if (mysqlShell != null)
	        try {
	            
	            mysqlShell.stopMySQL();
	            mysqlShell = null;
	            System.out.println("local MySQL stopped");
	        } catch (AmbitException x) {
	            JOptionPane.showMessageDialog(null,x);
	            logger.error(x);
	        } 
	}
	public int getPage() {
		return page;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public void setSource(int source) {
		this.source = source;
	}
	public int getSource() {
		return source;
	}
	
	public int getResultDestination() {
		return resultDestination;
	}
	public void setResultDestination(int resultDestination) {
		this.resultDestination = resultDestination;
		setChanged();
		notifyObservers();
	}
	
}
