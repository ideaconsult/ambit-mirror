package ambit2.db.pool;

import javax.sql.DataSource;


import com.jolbox.bonecp.BoneCPDataSource;



public class DataSourceBoneCP implements IDataSourcePool {
	protected volatile BoneCPDataSource datasource;

	public DataSourceBoneCP(String connectURI)  throws Exception {
        Class.forName("com.mysql.jdbc.Driver");

        datasource = new BoneCPDataSource();  // create a new datasource object
     	datasource.setJdbcUrl(connectURI);
    	datasource.setUsername("guest");				// set the username
    	datasource.setPassword("guest");	
        /*
 
	Class.forName("org.hsqldb.jdbcDriver"); 	// load the DB driver
 	BoneCPDataSource ds = new BoneCPDataSource();  // create a new datasource object
 	ds.setJdbcUrl("jdbc:hsqldb:mem:test");		// set the JDBC url
	ds.setUsername("sa");				// set the username
	ds.setPassword("");				// set the password
	
	ds.setXXXX(...);				// (other config options here)
	
	Connection connection;
	connection = ds.getConnection(); 		// fetch a connection
	
	...  do something with the connection here ...
	
	connection.close();				// close the connection
	ds.close();					// close the datasource pool


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
