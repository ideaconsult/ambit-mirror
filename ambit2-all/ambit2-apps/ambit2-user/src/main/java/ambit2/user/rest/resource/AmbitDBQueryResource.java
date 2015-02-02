package ambit2.user.rest.resource;

import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.p.UpdateExecutor;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.QueryResource;
import net.idea.restnet.db.convertors.QueryHTMLReporter;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskApplication;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.rdf.FactoryTaskConvertorRDF;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;

public abstract class AmbitDBQueryResource<Q extends IQueryRetrieval<T>,T extends Serializable> extends QueryResource<Q,T>{
	
	public static final MediaType GPML_WIKIPATHWAYS = new MediaType("application/wikipath+xml");
		
	public AmbitDBQueryResource() {
		super();
		
	}

	@Override
	public String getConfigFile() {
		return "ambit2/rest/config/config.prop";
	}
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
	}

	
	@Override
	protected FactoryTaskConvertor getFactoryTaskConvertor(ITaskStorage storage)
			throws ResourceException {
		return new FactoryTaskConvertorRDF(storage,getHTMLBeauty());
	}
	@Deprecated
	protected QueryHTMLReporter createHTMLReporter(boolean headless) throws ResourceException {
		return null;
	}
	
	@Override
	protected ITask<Reference, Object> addTask(
			ICallableTask callable,
			T item,
			Reference reference) throws ResourceException {

			return ((ITaskApplication)getApplication()).addTask(
				String.format("%s %s %s",
						callable.toString(),
						getItemName(item),
						reference==null?"":" "),									
				callable,
				getRequest().getRootRef(),
				getToken());		
		
	}

	protected Map<String, Object> getMap(Variant variant) throws ResourceException {
		   Map<String, Object> map = new HashMap<String, Object>();

		   
	        try {
	        	map.put(AMBITConfig.ambit_version_short.name(),((IFreeMarkerApplication)getApplication()).getVersionShort());
		    	map.put(AMBITConfig.ambit_version_long.name(),((IFreeMarkerApplication)getApplication()).getVersionLong());
		    	map.put(AMBITConfig.googleAnalytics.name(),((IFreeMarkerApplication)getApplication()).getGACode());
		    	map.put(AMBITConfig.menu_profile.name(),((IFreeMarkerApplication)getApplication()).getProfile());
	        } catch (Exception x) {}
		    
			map.put(AMBITDBRoles.ambit_admin.name(), Boolean.FALSE);
			map.put(AMBITDBRoles.ambit_datasetmgr.name(), Boolean.FALSE);
			if (getClientInfo()!=null) {
				if (getClientInfo().getUser()!=null)
					map.put("username", getClientInfo().getUser().getIdentifier());
				if (getClientInfo().getRoles()!=null) {
					if (DBRoles.isAdmin(getClientInfo().getRoles()))
						map.put(AMBITDBRoles.ambit_admin.name(),Boolean.TRUE);
					if (DBRoles.isDatasetManager(getClientInfo().getRoles()))
						map.put(AMBITDBRoles.ambit_datasetmgr.name(), Boolean.TRUE);
					if (DBRoles.isUser(getClientInfo().getRoles()))
						map.put(AMBITDBRoles.ambit_user.name(), Boolean.TRUE);	
				}
			}

			map.put(AMBITConfig.creator.name(),"IdeaConsult Ltd.");
		    map.put(AMBITConfig.ambit_root.name(),getRequest().getRootRef().toString());
		    getRequest().getResourceRef().addQueryParameter("media",MediaType.APPLICATION_JSON.toString());
		    map.put(AMBITConfig.ambit_request.name(),getRequest().getResourceRef().toString());
		        
		        //remove paging
		        Form query = getRequest().getResourceRef().getQueryAsForm();
		        //query.removeAll("page");query.removeAll("pagesize");query.removeAll("max");
		        query.removeAll("media");
		        Reference r = cleanedResourceRef(getRequest().getResourceRef());
		        r.setQuery(query.getQueryString());
		        map.put(AMBITConfig.ambit_request.name(),r.toString()) ;
		        if (query.size()>0)
		        	map.put(AMBITConfig.ambit_query.name(),query.getQueryString()) ;
		        //json
		        query.removeAll("media");query.add("media", MediaType.APPLICATION_JSON.toString());
		        r.setQuery(query.getQueryString());
		        map.put(AMBITConfig.ambit_request_json.name(),r.toString());
		        //csv
		        query.removeAll("media");query.add("media", MediaType.TEXT_CSV.toString());
		        r.setQuery(query.getQueryString());
		        map.put(AMBITConfig.ambit_request_csv.name(),r.toString());
		        return map;
	}
	
	@Override
	protected Representation getHTMLByTemplate(Variant variant) throws ResourceException {
		getHTMLBeauty();
        return toRepresentation(getMap(variant), getTemplateName(), MediaType.TEXT_PLAIN);
	}	
	
	protected void execUpdate(Object object, IQueryUpdate query) throws ResourceException { 
		Connection conn = null;
		UpdateExecutor x = null;
		try {
			DBConnection dbc = new DBConnection(getApplication().getContext(),getConfigFile());
			conn = dbc.getConnection();
			x = new UpdateExecutor();
			x.setConnection(conn);
			x.process(query);
		} catch (Exception e) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,e.getMessage(),e);
		} finally {
			try { if (conn != null) conn.close(); } catch (Exception xx) {}
			try { if (x !=null) x.close(); } catch (Exception xx) {}
		}			
	}

	public String getDefaultUsersDB() {
		String usersdbname = getContext().getParameters().getFirstValue(AMBITConfig.users_dbname.name());
		return (usersdbname==null)?"ambit_users":usersdbname;
	}
	
	protected String getItemName(T item) {
		return item==null?"":item.toString();
	}
}
