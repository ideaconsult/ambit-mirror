package ambit2.rest.admin;

import java.io.Writer;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.xbill.DNS.Address;

import ambit2.base.data.StringBean;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorException;
import ambit2.db.LoginInfo;
import ambit2.db.pool.DatasourceFactory;
import ambit2.db.processors.DbCreateDatabase;
import ambit2.db.reporters.QueryReporter;
import ambit2.db.version.AmbitDBVersion;
import ambit2.db.version.DBVersionQuery;
import ambit2.rest.DBConnection;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;

/**
 * Reports version of the database used. 
 * The database name is defined at compile time.
 * It it is either ambit2, or taken from .m2/settings.xml property
 * <ambit.db>ambit2</ambit.db>
 * @author nina
 * @example_get
 * <pre>
 * curl -X GET http://host:port/ambit2/admin/database
 * </pre>
 * POST creates a new database, if it doesn not exist alreadt. The dbname should match with the predefined database name.
 * User and password should be mysql credentials with create database privileges.
 * @example_post
 * <pre>
 * curl -X GET http://host:port/ambit2/admin/database -d "dbname=newdb" -d "user=uname" -d "pass=pass"
 * </pre>
 * @param <Q>
 * @param <T>
 */
public class DatabaseResource  extends QueryResource<DBVersionQuery,AmbitDBVersion> {
	public static final String resource = "database";
	
	@Override
	public IProcessor<DBVersionQuery, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		return new StringConvertor(	new QueryReporter<AmbitDBVersion,DBVersionQuery,Writer>() {
			public void open() throws ambit2.db.exceptions.DbAmbitException {}
			public void footer(Writer output, DBVersionQuery query) {}
			public Object processItem(AmbitDBVersion item) throws AmbitException {
					try {
				getOutput().write(String.format("\n%s\nVersion: %d.%d\nCreated: %s\nNote: %s\n\n",
						item.getDbname(),
						item.getMajor(),item.getMinor(),
						item.getCreated(),
						item.getComments()));
				return item;
				} catch (Exception x) { return null;}
			}
			public void header(Writer output, DBVersionQuery query) {
				try {
					InetAddress[] ipclient;
					InetAddress[] ipserver;
					String client = getRequest().getClientInfo().getAddress();
					String server = getRequest().getHostRef().getHostDomain();
					if (!Address.isDottedQuad(client)) {
						ipclient = Address.getAllByName(client);
					} else ipclient = new InetAddress[] {Address.getByAddress(client)};
					if (!Address.isDottedQuad(server)) {
						ipserver = Address.getAllByName(server);
					} else ipserver = new InetAddress[] {Address.getByAddress(server)};
					
					for (InetAddress cl : ipclient)
						for (InetAddress s : ipserver) {
							boolean ok = cl.equals(s);
							output.write(String.format("Comparing %s %s : %s\n",cl.getHostAddress(),s.getHostAddress(),Boolean.toString(ok)));

						}					
				} catch (Exception x) {
						x.printStackTrace();
				}
			}
		},MediaType.TEXT_PLAIN);
	}

	@Override
	protected DBVersionQuery createQuery(Context context, Request request, Response response)
			throws ResourceException {
		return new DBVersionQuery();
	}


	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		

		if ((entity == null) || !entity.isAvailable()) 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Expected %s dbname={databasename}",MediaType.APPLICATION_WWW_FORM));
				
		Form form = new Form(entity);
		DBConnection c = new DBConnection(getContext());
		
		if (!c.allowDBCreate())
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					"Not allowed to create the database via REST; set <ambit.db.create.allow>true</ambit.db.create.allow> into maven settings.xml file and recompile to allow.");
		
		
		if (!c.getLoginInfo().getDatabase().equals(form.getFirstValue("dbname")))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("dbname parameter doesn't match the database name",form.getFirst("dbname")));

		if (form.getFirstValue("user")==null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					"User credentials should be specified via user={} and pass={} web form parameters");
		
		try {
			if (dbExists(form.getFirstValue("dbname"),form.getFirstValue("user"),form.getFirstValue("pass")))
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					"Database already exist, if you really need to create a new one, drop it manually first.\n");
		} catch (ResourceException x) {
			throw x;
		} catch (Throwable e) {	
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,e.getMessage());
		} finally {
		
		}
		return createDB(form.getFirstValue("dbname"),form.getFirstValue("user"),form.getFirstValue("pass"));	
		
		/*
		return new StringRepresentation(String.format("Client %s<br>Server %s<br>Database %s",
				getRequest().getClientInfo().getAddress(),
				form.getFirstValue("user"),
				form.getFirstValue("pass"),
				form.getFirstValue("dbname")
				
				));
				*/
			
	}
	
	public boolean dbExists(String dbname,String user,String pass) throws Exception {
		boolean ok = false;
		ResultSet rs = null;
		Statement st = null;
		Connection c = null;
		try {
    		DBConnection dbc = new DBConnection(getContext());
    		LoginInfo li = dbc.getLoginInfo();
    		
			String uri = DatasourceFactory.getConnectionURI(
	               li.getScheme(), li.getHostname(), li.getPort(), 
	               "mysql", user,pass); 
			c = dbc.getConnection(uri);
			
			st = c.createStatement();
			rs = st.executeQuery("show databases");
			while (rs.next()) {
				if (dbname.equals(rs.getString(1))) ok = true;
			}
			
		} catch (Exception x) {
			throw x;
		} finally {
			try {if (rs != null) rs.close();} catch (Exception x) {}
			try {if (st != null) st.close();} catch (Exception x) {}
			try {if(c != null) c.close();} catch (Exception x) {}
		}
		return ok;
	}		
	
	public Representation createDB(String dbname,String user,String pass) throws ResourceException {
		//TODO refactor with Query/Update classes
		Connection c = null;
		
		DbCreateDatabase dbCreate = new DbCreateDatabase();
		try {
    		DBConnection dbc = new DBConnection(getContext());
    		LoginInfo li = dbc.getLoginInfo();
    		
			String uri = DatasourceFactory.getConnectionURI(
	               li.getScheme(), li.getHostname(), li.getPort(), 
	               "mysql", user,pass); 
			c = dbc.getConnection(uri);
    		dbCreate.setConnection(c);
    		dbCreate.open();
    		dbCreate.process(new StringBean(dbname));
			
			getResponse().setStatus(Status.SUCCESS_OK);
			
		} catch (SQLException x) {
			Context.getCurrentLogger().severe(x.getMessage());
			getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN,x,x.getMessage());			
			getResponse().setEntity(null);			
		} catch (ProcessorException x) {
			Context.getCurrentLogger().severe(x.getMessage());
			getResponse().setStatus((x.getCause() instanceof SQLException)?Status.CLIENT_ERROR_FORBIDDEN:Status.SERVER_ERROR_INTERNAL,
					x,x.getMessage());			
			getResponse().setEntity(null);			
		} catch (Exception x) {
			Context.getCurrentLogger().severe(x.getMessage());
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x,x.getMessage());			
			getResponse().setEntity(null);
		} finally {
			try {dbCreate.close();} catch (Exception x) {}
			try {if(c != null) c.close();} catch (Exception x) {}
			return new StringRepresentation(dbname);
		}
	}		
}
