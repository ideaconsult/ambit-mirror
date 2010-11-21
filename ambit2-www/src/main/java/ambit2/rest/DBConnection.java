package ambit2.rest;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.restlet.Context;
import org.restlet.Request;

import ambit2.base.config.Preferences;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.LoginInfo;
import ambit2.db.pool.DatasourceFactory;

public class DBConnection {
	protected static Properties properties = null;
	protected Context context;
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public DBConnection(Context context) {
		super();
		this.context = context;
	}
	protected synchronized void loadProperties()  {
		try {
		if (properties == null) {
			properties = new Properties();
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/rest/config/ambit2.pref");
			properties.load(in);
			in.close();		
		}
		} catch (Exception x) {
			properties = null;
		}
	}	
	public boolean allowDBCreate() {
		loadProperties();
		String ok = properties.getProperty("database.create");
		return (ok != null) && ok.toLowerCase().equals("true");
	}
	public String rdfWriter() {
		loadProperties();
		String rdfwriter = properties.getProperty("rdf.writer");
		return (rdfwriter==null)?"jena":rdfwriter;//jena or stax 
	}	
	public LoginInfo getLoginInfo() {
		loadProperties();
		LoginInfo li = new LoginInfo();
		String p = properties.getProperty("Database");
		li.setDatabase(p==null?"ambit2":p);
		p = properties.getProperty("Port");
		li.setPort(p==null?"3306":p);		
		p = properties.getProperty("User");
		li.setUser(p==null?"guest":p);			
		p = properties.getProperty("Password");
		li.setPassword(p==null?"guest":p);	
		
		return li;
	}
	protected String getConnectionURI() throws AmbitException {
		return getConnectionURI(null,null);
	}
	protected String getConnectionURI(Request request) throws AmbitException {
		if (request == null) return getConnectionURI(null,null);
		else if (request.getChallengeResponse()==null)
			return getConnectionURI(null,null);
		else try {
			return getConnectionURI(request.getChallengeResponse().getIdentifier(),
					new String(request.getChallengeResponse().getSecret()));
		} catch (Exception x) {
			return getConnectionURI(null,null);
		}
	}
	
	protected String getConnectionURI(String user,String password) throws AmbitException {
	
		try {
			LoginInfo li = getLoginInfo();

			if (getContext().getParameters().getFirstValue(Preferences.DATABASE)!=null)
				li.setDatabase(getContext().getParameters().getFirstValue(Preferences.DATABASE));
			if (getContext().getParameters().getFirstValue(Preferences.USER)!=null)
				li.setUser(getContext().getParameters().getFirstValue(Preferences.USER));
			if (getContext().getParameters().getFirstValue(Preferences.PASSWORD)!=null)
				li.setPassword(getContext().getParameters().getFirstValue(Preferences.PASSWORD));
			if (getContext().getParameters().getFirstValue(Preferences.HOST)!=null)
				li.setHostname(getContext().getParameters().getFirstValue(Preferences.HOST));
			if (getContext().getParameters().getFirstValue(Preferences.PORT)!=null)
				li.setPort(getContext().getParameters().getFirstValue(Preferences.PORT));
	
			
			return DatasourceFactory.getConnectionURI(
	                li.getScheme(), li.getHostname(), li.getPort(), 
	                li.getDatabase(), user==null?li.getUser():user, password==null?li.getPassword():password); 
		} catch (Exception x) {
			throw new AmbitException(x);
		} 
				
	}
	public synchronized Connection getConnection(String user, String password) throws AmbitException , SQLException{
		return getConnection(getConnectionURI(user, password));
	}
	public synchronized Connection getConnection() throws AmbitException , SQLException{
		//if (connectionURI == null)
		//	connectionURI = getConnectionURI();
		return getConnection(getConnectionURI(null));
	}

	public synchronized Connection getConnection(Request request) throws AmbitException , SQLException{
		//if (connectionURI == null)
		//	connectionURI = getConnectionURI();
		return getConnection(getConnectionURI(request));
	}
	
	public synchronized Connection getConnection(String connectionURI) throws AmbitException , SQLException{
		
		SQLException error = null;
		Connection c = null;
		
		ResultSet rs = null;
		Statement t = null;
		for (int retry=0; retry< 3; retry++)
		try {
			System.out.println("trying to getConnection "+Thread.currentThread().getName());
			c = DatasourceFactory.getDataSource(connectionURI).getConnection();
			System.out.println("got the Connection! "+Thread.currentThread().getName());
			t = c.createStatement();
			rs = t.executeQuery("SELECT 1");
			while (rs.next()) {rs.getInt(1);}
			rs.close(); rs = null;
			t.close(); t = null;
			error= null;
			return c;
		} catch (SQLException x) {
			//TODO reinitialize the connection pool
			error = x;
			Context.getCurrentLogger().severe(x.getMessage());
			//remove the connection from the pool
			try {if (c != null) c.close();} catch (Exception e) {}
		} finally {
			try { if (rs != null) rs.close();} catch (Exception x) {}
			try { if (t!= null) t.close();} catch (Exception x) {}
		}
		if (error != null) throw error; else throw new SQLException("Can't establish connection "+connectionURI);
	}
	 /*
	
	public synchronized Connection getConnection(String connectionURI) throws AmbitException , SQLException{
		
		return DatasourceFactory.getDataSource(connectionURI).getConnection();
		

	}
	*/
	
}
