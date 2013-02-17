package ambit2.db.pool;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DataSourceC3P0 implements IDataSourcePool {
	protected volatile ComboPooledDataSource datasource;

	public DataSourceC3P0(String connectURI)  throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
         
        /*
http://www.mchange.com/projects/c3p0/index.html#using_c3p0 
         */
        datasource = new ComboPooledDataSource();  // create a new datasource object
     	datasource.setJdbcUrl(connectURI);
     	//datasource.setDebugUnreturnedConnectionStackTraces(true);
     	//datasource.setUnreturnedConnectionTimeout(50);
    	//datasource.setUser("guest");				// set the username
    	//datasource.setPassword("guest");	
       
    	/*
		cpds.setMinPoolSize(5); 
		cpds.setAcquireIncrement(5); 
		cpds.setMaxPoolSize(20); 
    	 */
     	datasource.setMaxPoolSize(512); 
      

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

