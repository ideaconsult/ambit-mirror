/* DatasourceFactory.java
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

package ambit2.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import ambit2.base.exceptions.AmbitException;

public class DatasourceFactory {
    protected static final String slash="/";
    protected static final String qmark="?";
    protected static final String colon=":";
    protected static final String eqmark="=";
    protected static final String amark="&";
    protected Map<String, DataSourceAndPool> datasources;
    private DatasourceFactory() {
        datasources = new ConcurrentHashMap<String, DataSourceAndPool>();
    }
    private static class DatasourceFactoryHolder { 
        private final static DatasourceFactory instance = new DatasourceFactory();
      }
    
    public static DatasourceFactory getInstance() {
        return DatasourceFactoryHolder.instance;
    }
    
    public static DataSource getDataSource(String connectURI) throws AmbitException {
        if (connectURI == null) throw new AmbitException("Connection URI not specified!");
        DataSourceAndPool ds = getInstance().datasources.get(connectURI);
        if (ds == null) {
        	ds = setupDataSource(connectURI);
            getInstance().datasources.put(connectURI, ds);
            
        }
        if (ds!= null) return ds.getDatasource();
        else return null;

    }
    /**
     * Does nothing, the pool will be still active
     * @param connectURI
     * @throws AmbitException
     */
    public static void logout(String connectURI) throws AmbitException {
    	
    	/*

        if (connectURI == null) throw new AmbitException("Connection URI not specified!");
        DataSourceAndPool dspool = getInstance().datasources.get(connectURI);
        if (dspool != null) {
            getInstance().datasources.remove(connectURI);
            try {
            	
            } catch (Exception x) {x.printStackTrace();};
        }
    	 * 
    	 */        
  	
    }
    public static Connection getConnection(String connectURI) throws AmbitException {
        try {
            Connection connection = getDataSource(connectURI).getConnection();
            if (connection.isClosed()) 
            	return getDataSource(connectURI).getConnection();
            else
            	return connection;
        } catch (SQLException x) {
            throw new AmbitException(x);
        }
    }    
    public static DataSourceAndPool setupDataSource(String connectURI) throws AmbitException {
        try {
            DataSourceAndPool dataSource = new DataSourceAndPool(connectURI);
            
            return dataSource;
        } catch (Exception x) {
            throw new AmbitException(x);
        }
    }
    /**
     * Assembles connection URI
     * @param scheme
     * @param hostname
     * @param port
     * @param database
     * @param user
     * @param password
     * @return    scheme://{Hostname}:{Port}/{Database}?user={user}&password={password}
     */
    public static String getConnectionURI(String scheme,String hostname,String port,
                String database,String user,String password) {
        
        StringBuilder b = new StringBuilder();
        b.append(scheme).append(colon).
        append(slash).append(slash);
        
        if (hostname==null) b.append("localhost");
        else b.append(hostname);
        if (port != null) b.append(colon).append(port);
        b.append(slash).
        append(database);
        String q = qmark;
        if (user != null) {
            b.append(q); q = amark;
            b.append("user").append(eqmark).append(user);
        }
        if (password != null) {
            b.append(q); q = amark;            
            b.append("password").append(eqmark).append(password);
        }
        b.append(amark);
        b.append("&useUnicode=true&characterEncoding=UTF8&characterSetResults=UTF-8");
       // b.append("[validationQuery]=[SELECT 1]");
        return b.toString();
    }
    /**
     * 
     * @param scheme
     * @param hostname
     * @param port
     * @param database
     * @return    scheme://{Hostname}:{Port}/{Database}
     */
    public static String getConnectionURI(String scheme,String hostname,String port,
            String database) {
        return getConnectionURI(scheme, hostname, port, database,null,null);
    }    
    public static String getConnectionURI(String hostname,String port,
            String database) {
        return getConnectionURI("jdbc:mysql", hostname, port, database,null,null);
    }
    public static boolean ping(LoginInfo li) {
    	Connection connection = null;
    	Statement st = null;
    	try {
	        String dburi = DatasourceFactory.getConnectionURI(
	                    li.getScheme(), li.getHostname(), li.getPort(), 
	                    li.getDatabase(), li.getUser(), li.getPassword());    	    					
	
	        connection = DatasourceFactory.getConnection(dburi.toString());
	        st = connection.createStatement();
	        st.execute("SELECT 1");
	        return true;    
    	} catch (Exception x) {
    		System.out.println(x);
    		return false;
    	} finally {
    		if (st != null) try {st.close();} catch (Exception x) {};
            if (connection != null) try {connection.close();} catch (Exception x) {};
    	}
    }
}

class DataSourceAndPool {
	protected PoolingDataSource datasource;
	protected PoolableConnectionFactory poolableConnectionFactory;
	public PoolingDataSource getDatasource() {
		return datasource;
	}
	public ObjectPool getPool() {
		return poolableConnectionFactory.getPool();
	}

	public DataSourceAndPool(String connectURI)  throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        //
        // First, we'll need a ObjectPool that serves as the
        // actual pool of connections.
        //
        // We'll use a GenericObjectPool instance, although
        // any ObjectPool implementation will suffice.
        //
        ObjectPool connectionPool = new GenericObjectPool(null);

        //
        // Next, we'll create a ConnectionFactory that the
        // pool will use to create Connections.
        // We'll use the DriverManagerConnectionFactory,
        // using the connect string passed in the command line
        // arguments.
        //
        Properties jdbcProperties = new Properties();
        jdbcProperties.setProperty("autoReconnectForPools", "true");
        jdbcProperties.setProperty("testOnBorrow", "true");
        jdbcProperties.setProperty("testWhileIdle", "false");
        jdbcProperties.setProperty("timeBetweenEvictionRunsMillis", "60000");
        jdbcProperties.setProperty("minEvictableIdleTimeMillis", "1800000");
        jdbcProperties.setProperty("numTestsPerEvictionRun", "3");
        jdbcProperties.setProperty("maxActive", "100");
        jdbcProperties.setProperty("maxIdle", "3");
        jdbcProperties.setProperty("maxWait", "15000");
        jdbcProperties.setProperty("validationQuery", "SELECT 1");
        jdbcProperties.setProperty("removeAbandoned", "true");
        jdbcProperties.setProperty("removeAbandonedTimeout", "300");
        jdbcProperties.setProperty("logAbandoned", "true");
        /*
        If you enable "useUsageAdvisor=true", the driver will log where in your application
        results and statements were created that were never closed once the connection closes (as
        long as you haven't enabled "dontTrackOpenResources", that is).
        */
        //jdbcProperties.setProperty("useUsageAdvisor", "true");
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI,jdbcProperties);		
        //
        // Now we'll create the PoolableConnectionFactory, which wraps
        // the "real" Connections created by the ConnectionFactory with
        // the classes that implement the pooling functionality.
        //
        poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,connectionPool,null,null,false,true);
        //poolableConnectionFactory.setValidationQuery("select idmajor,idminor from version");
		
		datasource = new PoolingDataSource(connectionPool);

	}
	public void close() throws Exception {
		
		if (poolableConnectionFactory.getPool() != null)
			poolableConnectionFactory.getPool().close();

		datasource = null;
	}
	@Override
	protected void finalize() throws Throwable {
		try {
		close();
		} catch (Exception x) {
			
		}
		super.finalize();
	}
}


