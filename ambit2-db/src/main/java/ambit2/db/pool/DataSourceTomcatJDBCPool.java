package ambit2.db.pool;


import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class DataSourceTomcatJDBCPool  implements IDataSourcePool{
	protected volatile DataSource datasource;

	public DataSourceTomcatJDBCPool(String connectURI)  throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
         
        /*
http://www.mchange.com/projects/c3p0/index.html#using_c3p0 
         */
        PoolProperties p = new PoolProperties();
        p.setDriverClassName("com.mysql.jdbc.Driver");
        /*
        p.setUrl("jdbc:mysql://localhost:3306/mysql");
        p.setDriverClassName("com.mysql.jdbc.Driver");
        p.setUsername("root");
        p.setPassword("password");
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(100);
        p.setInitialSize(10);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
          "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        DataSource datasource = new DataSource();
          
        */
        datasource = new DataSource();  // create a new datasource object
     	datasource.setUrl(connectURI);
     	datasource.setPoolProperties(p);       
       
    	/*
		cpds.setMinPoolSize(5); 
		cpds.setAcquireIncrement(5); 
		cpds.setMaxPoolSize(20); 
    	 */

      

	}
	public void close() throws Exception {
		
		if (datasource!= null)	datasource.close();		

	}
	@Override
	protected void finalize() throws Throwable {
		try {
		close();
		} catch (Exception x) {
			
		}
		super.finalize();
	}
	@Override
	public DataSource getDatasource() {
		return datasource;
	}
}
