package ambit.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;

import ambit.data.AmbitUser;
import ambit.test.ITestDB;

/**
 * Provides pool of connections to the same database.  
 * Suitable for concurent access to the database. Used in {@link ambit.servlets}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 <pre>
 	ConnectionPool pool = new ConnectionPool(ITestDB.host,"33060",ITestDB.database,"root","",1,1);
 	//get first connection
 	Connection conn = pool.getConnection();
 	//...do some work with conn
 	
 	//get second connection
 	Connection conn1 = pool.getConnection();
 	
 	//...do some work with conn1
 	 
 	//return second connection 
 	pool.returnConnection(conn1);
 	
 	//... do some more work with first connection
 	//return second connection
 	pool.returnConnection(conn);     
 </pre>			
 */
public class ConnectionPool {
	private Hashtable connections;
	protected AmbitUser user;	
	private int increment;
	private String host = "localhost";
	private String port = "3306";
	private Connection conn;
	private String database = "ambit";
	private String driver = "jdbc:mysql://";
	private int maxConnections = 20;  

	/**
	 * Creates pool of connection to the default host = localhost, 
	 * default port = 3306, default database = ambit, default user = guest,
	 * default password = guest
	 * @throws Exception
	 */
	public ConnectionPool() throws Exception {
		this("localhost","3306","ambit","guest","guest",1,1);
	}
	/**
	 * Creates a pool of (initialConnections) connections to the database. 
	 * @param host
	 * @param port
	 * @param database
	 * @param user
	 * @param password
	 * @param initialConnections
	 * @param increment
	 * @throws Exception
	 */
	public ConnectionPool(
			String host, String port, String database, String user, String password,
            int initialConnections,
            int increment) throws Exception {
		super();
		if (!LoadDriver.isLoaded())
			LoadDriver.loadDriver();			
		this.user = new AmbitUser(user);
		this.user.updateConnectionData(user,password,host);
		this.host = host;
		this.user = new AmbitUser(user);
		this.user.updateConnectionData(user,password,host);
		this.port = port;		
		this.database = database;
		conn = null;
		if (!LoadDriver.isLoaded())
			LoadDriver.loadDriver();
		
	    this.increment = increment;

	    connections = new Hashtable();
		String url = driver + this.user.getLoggedFromHost() + ":" + port + "/" + database +
			"?user="+user + "&password="+ this.user.mysqlPassword() ;
	    // Put our pool of Connections in the Hashtable
	    // The FALSE value indicates they're unused
	    for(int i = 0; i < initialConnections; i++) {
	      connections.put(DriverManager.getConnection(url),
	                      Boolean.FALSE);
	    }

	}

	  public Connection getConnection() throws SQLException {
	    Connection con = null;

	    java.util.Enumeration cons = connections.keys();

	    synchronized (connections) {
	    int n = 0;	
	      while(cons.hasMoreElements()) {
	    	n++;  
	        con = (Connection)cons.nextElement();

	        Boolean b = (Boolean)connections.get(con);
	        if (b == Boolean.FALSE) {
	          // So we found an unused connection.
	          // Test its integrity with a quick setAutoCommit(true) call.
	          // For production use, more testing should be performed,
	          // such as executing a simple query.
	          try {
	            con.setAutoCommit(true);
	          }
	          catch(SQLException e) {
	            // Problem with the connection, replace it.
	      		String url = driver + this.user.getLoggedFromHost() + ":" + port + "/" + database +
				"?user="+user + "&password="+ this.user.mysqlPassword() ;	        	  
	            con = DriverManager.getConnection(url);
	          }
	          // Update the Hashtable to show this one's taken
	          connections.put(con, Boolean.TRUE);
	          // Return the connection
	          return con;
	        }
	      }
		    if (n >=maxConnections ) return null;	      
	    }

	    // If we get here, there were no free connections.
	    // We've got to make more.
		String url = driver + this.user.getLoggedFromHost() + ":" + port + "/" + database +
		"?user="+user + "&password="+ this.user.mysqlPassword() ;	    
	    for(int i = 0; i < increment; i++) {
	      connections.put(DriverManager.getConnection(url),
	                      Boolean.FALSE); 
	    }		
	 
	    // Recurse to get one of the new connections.
	    return getConnection();
	  }

	  public void returnConnection(Connection returned) {
	    Connection con;
	    java.util.Enumeration cons = connections.keys();
	    while (cons.hasMoreElements()) {
	      con = (Connection)cons.nextElement();
	      if (con == returned) {
	        connections.put(con, Boolean.FALSE);
	        break;
	      }
	    }
	  }

	  /**
	   * Maximum connections
	   * @return max number of connections
	   */
    public synchronized int getMaxConnections() {
        return maxConnections;
    }
    /**
     * Maximum connections
     * @param maxConnections
     */
    public synchronized void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }
    //TODO close
    @Override
    protected void finalize() throws Throwable {
    	// TODO Auto-generated method stub
    	super.finalize();
    }
}
