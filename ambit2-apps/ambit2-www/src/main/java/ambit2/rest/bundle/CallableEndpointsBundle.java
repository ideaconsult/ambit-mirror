package ambit2.rest.bundle;

import java.sql.Connection;

import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.update.CallableDBUpdateTask;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.db.update.bundle.endpoints.AddEndpointToBundle;
import ambit2.db.update.bundle.endpoints.DeleteEndpointFromBundle;
import ambit2.rest.property.PropertyURIReporter;

public class CallableEndpointsBundle extends CallableDBUpdateTask<SubstanceProperty, Form, String> {
	protected PropertyURIReporter reporter;
	protected SubstanceEndpointsBundle bundle;
	private update_command command = update_command.add;
	
	public CallableEndpointsBundle(SubstanceEndpointsBundle item,
			PropertyURIReporter reporter,
			Method method, Form input,Connection connection, String token) {
		super(method, input, connection, token);
		this.bundle = item;
		this.reporter = reporter;
	}

	@Override
	protected SubstanceProperty getTarget(Form input) throws Exception {
		if (Method.POST.equals(method)) {
			SubstanceProperty record = new SubstanceProperty(null,null,null,"Default");
			parseForm(input, record);
			command = update_command.add; 
			return record;
		} else if (Method.PUT.equals(method)) {
			command = null;
			SubstanceProperty record = new SubstanceProperty(null,null,null,"Default");
			parseForm(input, record);
			if (command != null) return record;
			else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected IQueryUpdate<? extends Object, SubstanceProperty> createUpdate(SubstanceProperty target) throws Exception {
		if (Method.POST.equals(method)) {
			return bundle!=null?new AddEndpointToBundle(bundle,target):null; 
		}
		else if (Method.PUT.equals(method)) {
			switch (command) {
			case add: {
				return bundle!=null?new AddEndpointToBundle(bundle,target):null;  
			}
			case delete: {
				return bundle!=null?new DeleteEndpointFromBundle(bundle,target):null;
			}
			}
		}		
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected String getURI(SubstanceProperty target) throws Exception {
		return reporter.getURI(target);
	}
	protected void parseForm(Form input, SubstanceProperty property) throws ResourceException {
		try {
			//add/delete should be specified on PUT 
			command = update_command.valueOf(input.getFirstValue("command"));
		} catch (Exception x) { command = null;	}
		
		String topcategory = input.getFirstValue("topcategory");
		if (topcategory==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		String endpointcategory = input.getFirstValue("endpointcategory");
		
		String endpointhash = input.getFirstValue("endpointhash");
		
		property.setTopcategory(topcategory);
		property.setEndpointcategory(endpointcategory);
		
		
	}
}
