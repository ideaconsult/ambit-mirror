/**
 * Created on 2005-3-23
 *
 */
package ambit.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;

import ambit.data.AmbitUser;
import ambit.exceptions.AmbitException;

/**
 * Database API<br>
 * functions to create and drop AMBIT tables
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DbAdmin extends DbConnection {
    protected String createSQLFile = "ambit/database/ambit_create.sql";
    protected static String SQL_INSERTUSER = "insert into ambituser (mysqluser,mysqlhost,usertype,regstatus) values ('lri_admin','localhost','pro','commenced');";
	/**
	 * @param database
	 */
	public DbAdmin(String database) {
		super(database);
	}

	/**
	 * @param host
	 * @param port
	 * @param database
	 * @param user
	 * @param password
	 */
	public DbAdmin(String host, String port, String database, String user,
			String password) {
		super(host, port, database, user, password);
	}

	/**
	 * @param conn
	 */
	public DbAdmin(DbConnection conn) throws AmbitException {
		super(conn);
	}
	
	public void createUsers(String newDB,AmbitUser user) throws AmbitException {
		//if (this.getUser().getUserType().equals(AmbitUser.USERTYPE_ADMIN)) 
		try {
			Statement t = createUnidirectional();
		    t.execute("USE "+newDB + ";");
		    setDatabase(newDB);
			
			if (user.getUserType().equals(AmbitUser.USERTYPE_GUEST)) 
				t.execute(
				"GRANT SELECT ON " + getDatabase() + ".* to '" + user.getName() + "'@'" +
				user.getLoginAllowedFromHost() + "' identified by '" + user.getPassword() + "'"	);
			else
				t.execute(
						"GRANT ALL PRIVILEGES ON " + getDatabase() + ".* to '" + user.getName() + "'@'" +
						user.getLoginAllowedFromHost() + "' identified by '" + user.getPassword() + "'"	);
			t.execute("insert into ambituser (mysqluser,mysqlhost,usertype,regstatus) values ('"+
			        user.getName() + "','" + 
			        user.getLoginAllowedFromHost()+ "','" +  
			        user.getUserType() + "','commenced');");			
			t.close();
			t = null;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
		//else throw new AmbitException("User not allowed to create new users!\t"+ getUser() + "\t" + getUser().getUserType());
	}		

	public void dropDatabase(String database) throws AmbitException {
		try {
			Statement t = createUnidirectional();
			t.execute("DROP DATABASE "+ database);
			t.execute("REVOKE ALL PRIVILEGES on " + database + ".* from 'lri_admin'@'localhost'");
			t.close();
			t = null;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
	public boolean databaseExist(String newDb) throws AmbitException {
	    boolean exists = false;
		try {
			Statement t = createUnidirectional();
			ResultSet rs = t.executeQuery("SHOW DATABASES;"); 
			while (rs.next()) {
			    if (rs.getString(1).equals(newDb)) {
			        System.out.println(newDb + "\texists");
			        exists = true;
			        break;
			    }
			}
			rs.close();
			t.close();
			t = null;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
		return exists;
	}	
	public int tablesExist(Hashtable tables, boolean all) throws AmbitException {
	    int exists = 0;
	    StringBuffer list = new StringBuffer();
		try {
			Statement t = createUnidirectional();
			ResultSet rs = t.executeQuery("SHOW TABLES;"); 
			while (rs.next()) {
			    String table = rs.getString(1);
			    if (tables.get(table) != null) {
			        tables.put(table,new Boolean(true));
			        exists++;
			    } else {
			        list.append(rs.getString(1));
			        list.append(",");
			    }
			}
			rs.close();
			t.close();
			t = null;
			if (all && (exists != tables.size())) {
			    Enumeration e = tables.keys();
			    StringBuffer b = new StringBuffer();
			    while (e.hasMoreElements()) {
			        Object key = e.nextElement();
			        Boolean x = (Boolean) tables.get(key);
			        if (!x.booleanValue()) {
			            b.append(key.toString());
			            b.append(',');
			        }
			    }
			    if (!b.toString().equals(""))
			    throw new AmbitException("Missing tables in database! " + b.toString());			    
			}
			if (!list.toString().equals(""))
			    throw new AmbitException("Unknown tables in database! " + list.toString());
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
		return exists;
	}		
	public void createDatabase(String newDb) throws AmbitException {
		try {

			Statement t = createUnidirectional();
			t.execute("CREATE DATABASE `"+ newDb + "`"); 
			t.execute("GRANT ALL PRIVILEGES on `" + newDb + "`.* to 'lri_admin'@'localhost' identified by 'lri' WITH GRANT OPTION");
			t.close();
			t = null;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
	public void createTables(String newDB) throws AmbitException {
	    
		//if (this.getUser().getTitle().equals(AmbitUser.USERTYPE_ADMIN)) {
		    InputStream in = this.getClass().getClassLoader().getResourceAsStream(
		            createSQLFile);
	        		//"ambit/database/ambit_create.sql");
		    if (in == null) throw new AmbitException("Can't find " + createSQLFile);
		    try {
		        getConn().setAutoCommit(true);
			    Statement t = createUnidirectional();
			    t.execute("USE "+newDB + ";");
			    setDatabase(newDB);
			    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			    String line = null;                         
			    StringBuffer table = new StringBuffer();
			    while (true) {
			    	try {
			    		line = reader.readLine();
			    		if (line == null) break;
			    		if (line.equals("")) continue;
			    		if (line.indexOf("--") == 0) continue;
			    		table.append(line);
			    		table.append("\n");
			    		if (line.indexOf(";") >= 0) {
			    			t.addBatch(table.toString());
			    			
			    			//t.execute(table.toString());
			    			logger.debug(table.toString());
			    			table = new StringBuffer();
			    		}
			    	} catch (SQLException x) {
		    			System.err.println(table.toString());

			    		throw new AmbitException(x);
			    	} catch (IOException x) {
			    		throw new AmbitException(x);
			    	}
			    }
			    reader.close();
				
			    int[] counts = t.executeBatch();
			    t.execute(SQL_INSERTUSER);
			    t.close();
			    t = null;
			    
	    	} catch (Exception x) {
	    		throw new AmbitException(x);
		    }
		//} else throw new AmbitException("User not allowed to create tables!");
	}
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("DbAdmin -R");
			System.out.println("Options\n -Rdatabase\t:reverse engineering \n -Xdatabase\t:drop tables \n -Cdatabase\t:create tables \n");
			return;
		}
		boolean reverseEngineering = false;
		boolean createTables = false;
		boolean dropTables = false;
		String database = "";
		
		for (int i = 0; i <  args.length; i++) {
			String arg = args[i];
			if (args[i].substring(0,2).equals("-R")) {
				database = args[i].substring(2);
				reverseEngineering  =true;
			} else	if (args[i].substring(0,2).equals("-X")) {
					database = args[i].substring(2);
					dropTables = true;
			} else if (args[i].substring(0,2).equals("-C")) {
				database = args[i].substring(2);
				createTables = true;
			}
		}
		if (database.equals("")) database = "ambit";
		DbAdmin dba = new DbAdmin("localhost","3306",database,"lri","cefic");
		try {
			dba.open();
			if (reverseEngineering) {
				try {
				BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ambit_reverse.sql")));	
				dba.reverseEngineering(bf);
				bf.flush();
				bf.close();
				} catch (IOException x) {
					x.printStackTrace();
				}
			} else if (createTables) { 
			} else if (dropTables) {
				
			}
			dba.close();
		} catch (AmbitException e) {
			e.printStackTrace();
		} catch (SQLException x) {
			x.printStackTrace();
		}
	}	
    public synchronized String getCreateSQLFile() {
        return createSQLFile;
    }
    public synchronized void setCreateSQLFile(String createSQLFile) {
        this.createSQLFile = createSQLFile;
    }
}
