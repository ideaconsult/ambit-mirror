/**
 * Created on 2004-11-5
 */
package ambit2.database;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ambit2.database.exception.DbAccessDeniedException;
import ambit2.database.exception.DbConnectionRefused;
import ambit2.exceptions.AmbitException;
import ambit2.exceptions.AmbitUserException;
import ambit2.log.AmbitLogger;
import ambit2.data.AmbitUser;

import com.mysql.jdbc.CommunicationsException;


class LoadDriver { 
    private static String driverName = "com.mysql.jdbc.Driver";	
    private static boolean driverLoaded = false;
    public static void loadDriver() { 
        try { 
            // The newInstance() call is a work around for some 
            // broken Java implementations

            Class.forName(driverName).newInstance(); 
	    driverLoaded = true;            
        } catch (Exception ex) { 
            // handle the defaultError 
            driverLoaded = false;
        }
    }    
    public static boolean isLoaded() {
    	return driverLoaded;
    }    
}

/**
 * Database API<br>
 * A class to establish connection to the mySQL database through jdbc
 * User information is stored in an {@link ambit2.data.AmbitUser} field  .<br>
 * Example:<br>
 * <pre>
 DbConnection connection = new DbConnection("localhost","3306","ambit","guest","guest");<br>
 connection.open();
 //... do some work
 connection.close(); 
 
 </pre>
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DbConnection  {
	protected AmbitLogger logger = new AmbitLogger(DbConnection.class);
	protected AmbitUser user;
	private boolean useSSL = false;
	private String host = "localhost";
	private String port = "3306";
	private Connection conn;
	private String database = "ambit";
	private String driver = "jdbc:mysql://";
	
	private static final String AMBIT_getUser = "select idambituser,mysqluser,mysqlhost,title,firstname,lastname,email,webpage,affiliation,address,city,country,usertype,regstatus from ambituser";
	
	/**
	 * Defaults:<br >
	 *  host = localhost<br >
	 *  port = 3306 <br >
	 *  database = "ambit" <br >
	 *  
	 *
	 */
	public DbConnection() {
		conn = null;
		if (!LoadDriver.isLoaded())
			LoadDriver.loadDriver();			
	}	
	
	/**
	 * connectiong with default host,database,user,pass
	 */
	public DbConnection(String database) {
		this.database = database;
		conn = null;
		if (!LoadDriver.isLoaded())
			LoadDriver.loadDriver();			
	}
	public DbConnection(String host, String port, String database, String user, String password) {
		this.host = host;
		this.user = new AmbitUser(user);
		this.user.updateConnectionData(user,password,host);
		this.port = port;		
		this.database = database;
		conn = null;
		if (!LoadDriver.isLoaded())
			LoadDriver.loadDriver();			
	}
	
	public DbConnection(DbConnection conn) throws AmbitException{
		
		this.conn = conn.getConn();
		
		if (this.conn == null) { 
			if (!LoadDriver.isLoaded())
				LoadDriver.loadDriver();
		} else {
			try {
				this.user = (AmbitUser) conn.getUser().clone();
			} catch (CloneNotSupportedException x) {
				this.user = new AmbitUser();
			}
			//;verifyUser();
		}
	}
	/*
	public DbConnection(Connection conn) throws AmbitException{
		
		this.conn = conn;
		
		if (this.conn == null) { 
			if (!LoadDriver.isLoaded())
				LoadDriver.loadDriver();
		} else {
			try {
				this.user = (AmbitUser) conn.getUser().clone();
			} catch (CloneNotSupportedException x) {
				this.user = new AmbitUser();
			}
			//;verifyUser();

			 
		}
	}	
	*/
	public boolean open() throws AmbitException {
		return open(true);
	}
	public boolean open(boolean verifyUser) throws AmbitException {
		try {
		     
			 String url = driver + user.getLoggedFromHost() + ":" + port + "/" + database + "?user="+user + "&password="+ user.mysqlPassword() ;
		    
			 if (useSSL) url = url + "&useSSL=true"; //&requireSSL=true";
			 conn = DriverManager.getConnection(url);			  
			
			 try {
				 if (verifyUser)
					 if (verifyUser()) {
						 logger.debug("open");
						 return true;
					 } else return false;
				 else return true;
			 } catch (AmbitUserException x) {
				 conn.close();
				 conn = null;
				 throw new AmbitException(x);
			 }
        } catch (SQLException x) {
            	if (x instanceof CommunicationsException) 
            	    throw new DbConnectionRefused(null,"Open database"+x.getMessage(),x);
            	else 
            	    throw new DbAccessDeniedException(null,"Open database:\t"+x.getMessage(),x);
            	/*
	            logger.error("SQLException: " + x.getMessage()); 
	            logger.error("SQLState: " + x.getSQLState()); 
	            logger.error("VendorError: " + x.getErrorCode());
	             
	        	throw new AmbitException("Open database:\t"+x.getMessage(),x);
	        	*/
        }
	}	
	
	
	public void close() throws SQLException {
		if (conn == null) return;
		conn.close();
		conn = null;
	}
	
	public void commit() throws SQLException {
		if (conn == null) return;
		conn.commit();
		logger.debug("commit");
	}

	public Statement createBidirectional() throws SQLException  {
		Statement stmt = getConn().createStatement(); 					    
	    return stmt;
	}		
	public Statement createUnidirectional() throws SQLException  {
		Statement stmt = getConn().createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
		  	  				ResultSet.CONCUR_READ_ONLY); 					    
		stmt.setFetchSize(Integer.MIN_VALUE);
	    return stmt;
	}	
	public Statement createUpdatable() throws SQLException {
		Statement stmt = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                java.sql.ResultSet.CONCUR_UPDATABLE);
	    return stmt;
	}
	/**
	 * @return Returns the conn.
	 */
	public Connection getConn() {
		return conn;
	}
	/**
	 * @return Returns the user.
	 */
	public AmbitUser getUser() {
		return user;
	}
	protected boolean readUser(ResultSet rs,AmbitUser anUser) throws SQLException {
		//"select idambituser,mysqluser,mysqlhost,title,firstname,lastname,email,webpage,affiliation,address,city,country,usertype,regstatus from ambituser"
		if (rs.next()) {
			anUser.setId(rs.getInt(1));
			anUser.setName(rs.getString(2));
			anUser.updatemysqlHostAllowed(rs.getString(3));
			anUser.setTitle(rs.getString(4));
			anUser.setFirstName(rs.getString(5));
			anUser.setLastName(rs.getString(6));
			anUser.setEmail(rs.getString(7));
			anUser.setWww(rs.getString(8));
			anUser.setAffiliation(rs.getString(9));
			anUser.setAddress(rs.getString(10));
			anUser.setCity(rs.getString(11));
			anUser.setCountry(rs.getString(12));
			anUser.setUserType(rs.getString(13));
		
			return true;
		} else return false;
	}
	/**
	 * Verifies if it is an user listed in ambituser table
	 * @return true if yes
	 * @throws AmbitUserException
	 */
	public boolean verifyUser() throws AmbitUserException {
		try {
			boolean ok = false;
			Statement t = createUpdatable();
			ResultSet rs = t.executeQuery(AMBIT_getUser + 
					" where mysqluser='"+ user.getName() + "';");
			while (readUser(rs,user)) {
				if (user.isLoginAllowed()) {
					ok = true;
					break;
				}
			}
			t.close();
			if (ok) return true;
			else throw new AmbitUserException("User " + user.getName() + " not allowed to logger in from host "+user.getLoggedFromHost() + " to database "+getDatabase());
		} catch (SQLException x) {
			throw new AmbitUserException(x);
		}
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}

	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public void setUser(AmbitUser user) {
		this.user = user;
	}
	/**
	 * Generates sql statements necessary to create tables in ambit database
	 * @param writer - a {@link BufferedWriter} to write "create tables" statements
	 */
	public void reverseEngineering(BufferedWriter writer) {
		String sql = "show tables;";
		try {
			Statement stmt = getConn().createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList tables = new ArrayList();
			while (rs.next()) {
				tables.add(rs.getString(1));
			}
			rs.close();
			for (int i=0; i < tables.size(); i++)  {
				sql = "show create table " + tables.get(i);
				rs = stmt.executeQuery(sql);
				try {
				while (rs.next()) {
					writer.write("\n");
					writer.write("-- ");
					writer.write(rs.getString(1));
					writer.write("\n");
					writer.write(rs.getString(2));
					writer.write(";\n");
				}
				} catch (IOException x) {
					x.printStackTrace();
					
				}

				rs.close();
			}
			sql = "select * from version";
			rs = stmt.executeQuery(sql);

			try {
				while (rs.next()) {
					writer.write(rs.getInt(1));
					writer.write(rs.getDate(2).toString());
					writer.write("\n");
				}				
				writer.flush();
			} catch (IOException x) {
				x.printStackTrace();
			}
			stmt.close();
			stmt = null;
		} catch (SQLException x) {
			x.printStackTrace();
			return;			
		}
	}	
	public boolean isClosed() {
		try {
			if (conn == null) return true;
			else return conn.isClosed();
		} catch (SQLException x) {
			logger.error(x);
			return false;
		}
	}
}
