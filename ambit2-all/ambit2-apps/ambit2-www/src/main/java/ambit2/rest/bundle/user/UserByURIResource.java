package ambit2.rest.bundle.user;

import java.sql.Connection;
import java.sql.ResultSet;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.db.ReadUser;

import org.apache.poi.ss.formula.ptg.AddPtg;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.search.QueryExecutor;
import ambit2.db.update.bundle.ReadBundle;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.user.rest.ReadUserByBundleNumber;
import ambit2.user.rest.resource.SimpleUserJSONReporter;
import ambit2.user.rest.resource.UserDBResource;

/**
 * Simplified JSON for autocomplete
 * 
 * @author nina
 * 
 * @param <T>
 */
public class UserByURIResource<T> extends UserDBResource<T> {
    protected boolean addPublicGroup = false;
    @Override
    public RepresentationConvertor createJSONConvertor(Variant variant) throws AmbitException, ResourceException {
	String usersdbname = getContext().getParameters().getFirstValue(AMBITConfig.users_dbname.name());
	return new OutputWriterConvertor(new SimpleUserJSONReporter(getRequest(), usersdbname,addPublicGroup),
		MediaType.APPLICATION_JSON);
    }

    @Override
    protected ReadUser createQuery(Context context, Request request, Response response) throws ResourceException {
	if (getClientInfo() == null || getClientInfo().getUser() == null)
	    throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
	String usersdbname = getContext().getParameters().getFirstValue(AMBITConfig.users_dbname.name());
	Form form = request.getResourceRef().getQueryAsForm();
	String search_name = null;
	Object search_value = null;
	try {
	    search_name = "q";
	    search_value = form.getFirstValue(search_name);
	    if (search_value != null) {
		ReadUser query = new ReadUser();
		
		DBUser user = new DBUser();
		String s = String.format("^%s", search_value.toString());
		user.setLastname(s);
		user.setFirstname(s);
		query.setValue(user);
		addPublicGroup = true;
		return query;
	    }
	} catch (Exception x) {
	    search_value = null;
	}

	try {
	    search_name = "bundle_uri";
	    search_value = form.getFirstValue(search_name);
	    Integer idbundle = search_value == null ? null : getIdBundle(search_value, request);
	    SubstanceEndpointsBundle bundle =readBundle(idbundle);
	    ReadUserByBundleNumber query = new ReadUserByBundleNumber();
	    query.setDatabaseName(usersdbname);
	    query.setFieldname(bundle.getBundle_number().toString());
	    search_value = form.getFirstValue("mode");
	    if ("W".equals(search_value)) query.setAllowWrite(true); else query.setAllowWrite(false);
	    return query;
	} catch (Exception x) {
	    search_value = null;
	}
	throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
    }

    protected SubstanceEndpointsBundle readBundle(int idbundle) throws ResourceException {
	SubstanceEndpointsBundle dataset = new SubstanceEndpointsBundle();
	dataset.setID(idbundle);
	Connection c = null;
	ResultSet rs = null;
	QueryExecutor xx = null;
	try {
	    DBConnection dbc = new DBConnection(getContext());
	    c = dbc.getConnection();
	    ReadBundle read = new ReadBundle();
	    read.setValue(dataset);
	    xx = new QueryExecutor();
	    xx.setConnection(c);
	    rs = xx.process(read);
	    while (rs.next()) {
		dataset = read.getObject(rs);
	    }
	    return dataset;
	} catch (Exception x) {
	    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x.getMessage(), x);
	} finally {
	    try {
		rs.close();
	    } catch (Exception x) {
	    }
	    try {
		c.close();
	    } catch (Exception x) {
	    }
	    try {
		xx.close();
	    } catch (Exception x) {
	    }
	}
    }
    protected Integer getIdBundle(Object bundleURI, Request request) {
	if (bundleURI != null) {
	    Object id = OpenTox.URI.bundle.getId(bundleURI.toString(), request.getRootRef());
	    if (id != null && (id instanceof Integer))
		return (Integer) id;
	}
	return null;
    }

    @Override
    public RepresentationConvertor createConvertor(Variant variant) throws AmbitException, ResourceException {
	/*
	 * if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
	 * 
	 * return new StringConvertor(new
	 * PropertyValueReporter(),MediaType.TEXT_PLAIN);
	 * 
	 * } else
	 */
	String filenamePrefix = getRequest().getResourceRef().getPath();
	return createJSONConvertor(variant);
    }
}
