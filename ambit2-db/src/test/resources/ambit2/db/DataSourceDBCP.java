package ambit2.db.pool;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;


public class DataSourceDBCP implements IDataSourcePool {

	protected volatile PoolingDataSource datasource;
	protected volatile PoolableConnectionFactory poolableConnectionFactory;
	
	public DataSource getDatasource() {
		return datasource;
	}
	public ObjectPool getPool() {
		ObjectPool pool = poolableConnectionFactory.getPool();
		System.out.println(String.format("Active %d Idle %d",pool.getNumActive(),pool.getNumIdle()));
		return pool;
	}

	public DataSourceDBCP(String connectURI)  throws Exception {
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
        

/*
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

*/
        Properties jdbcProperties = new Properties();
        
        jdbcProperties.setProperty("autoReconnectForPools", "true");
        jdbcProperties.setProperty("testOnBorrow", "false");
        jdbcProperties.setProperty("testWhileIdle", "false");
        jdbcProperties.setProperty("timeBetweenEvictionRunsMillis", "-1");
        jdbcProperties.setProperty("minEvictableIdleTimeMillis", "1000 * 60 * 30");
        jdbcProperties.setProperty("numTestsPerEvictionRun", "3");
        jdbcProperties.setProperty("maxActive", "100");
        jdbcProperties.setProperty("maxIdle", "100");
        jdbcProperties.setProperty("minIdle", "0");
        jdbcProperties.setProperty("maxWait", "1000");
       //jdbcProperties.setProperty("validationQuery", "/* ping */SELECT 1"); ///* ping */
        
        jdbcProperties.setProperty("removeAbandoned", "false");
        jdbcProperties.setProperty("removeAbandonedTimeout", "300");
        jdbcProperties.setProperty("logAbandoned", "false");
        
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