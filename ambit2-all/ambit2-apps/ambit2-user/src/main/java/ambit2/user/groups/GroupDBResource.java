package ambit2.user.groups;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.db.convertors.QueryHTMLReporter;
import net.idea.restnet.db.convertors.RDFJenaConvertor;
import net.idea.restnet.groups.IDBGroup;
import net.idea.restnet.groups.db.ReadGroup;
import net.idea.restnet.groups.resource.GroupJSONReporter;
import net.idea.restnet.groups.resource.GroupQueryURIReporter;
import net.idea.restnet.groups.resource.GroupRDFReporter;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.rdf.FactoryTaskConvertorRDF;
import net.idea.restnet.user.DBUser;
import net.toxbank.client.io.rdf.TOXBANK;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.user.rest.resource.AmbitDBQueryResource;
import ambit2.user.rest.resource.UserDBResource;

/**
 * Protocol resource
 * @author nina
 *
 * @param <Q>
 */
public abstract class GroupDBResource<G extends IDBGroup>	extends AmbitDBQueryResource<ReadGroup<G>,G> {
	public static final String resourceKey = "key";
	
	protected boolean singleItem = false;
	protected boolean editable = true;

	public GroupDBResource() {
		super();
		setHtmlbyTemplate(true);
	}
	@Override
	public boolean isHtmlbyTemplate() {
		return freeMarkerSupport.isHtmlbyTemplate();
	}
	@Override
	public String getTemplateName() {
		return singleItem?"organisation_body.ftl":"organisation_body.ftl";
	}
	
	
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				return new StringConvertor(	
						new GroupQueryURIReporter(getRequest())
						,MediaType.TEXT_URI_LIST,filenamePrefix);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			return new OutputWriterConvertor(
					new GroupJSONReporter<IQueryRetrieval<IDBGroup>>(getRequest()),
					MediaType.APPLICATION_JSON);					
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
					variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
					variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
					variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) 
					) {
				return new RDFJenaConvertor<IDBGroup, IQueryRetrieval<IDBGroup>>(
						new GroupRDFReporter<IQueryRetrieval<IDBGroup>>(
								getRequest(),variant.getMediaType(),getDocumentation())
						,variant.getMediaType(),filenamePrefix) {
					/**
						     * 
						     */
						    private static final long serialVersionUID = -9057507646017674637L;

					@Override
					protected String getDefaultNameSpace() {
						return TOXBANK.URI;
					}					
				};
		} else //html shoud be rendered by the Fremarker template, not by this convertor
			return new OutputWriterConvertor(
					new GroupJSONReporter<IQueryRetrieval<IDBGroup>>(getRequest()),
					MediaType.APPLICATION_JSON);	
	}
	@Override
	protected QueryHTMLReporter createHTMLReporter(boolean headless) throws ResourceException {
		return null;
	}
	
	public abstract ReadGroup<G> createGroupQuery(Integer key,String search, String groupName);
	public abstract String getGroupBackLink();
	public abstract String getGroupTitle();

	protected String getObjectURI(Form queryForm) throws ResourceException {
		return null;		
	}
	@Override
	protected ReadGroup<G> createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Form form = request.getResourceRef().getQueryAsForm();
		Object search = null;
		try {
			search = form.getFirstValue("search").toString();
			if ((search != null) && !"".equals(search)) search = String.format("^%s", search);
		} catch (Exception x) {
			search = null;
		}	
		Object groupname = null;
		try {
			groupname = form.getFirstValue("groupname").toString();
			if ((groupname != null) && !"".equals(groupname)) groupname = String.format("^%s", groupname);
		} catch (Exception x) {
			groupname = null;
		}			
		try {
			String n = form.getFirstValue("new");
			editable = n==null?false:Boolean.parseBoolean(n);
		} catch (Exception x) {
			editable = false;
		}
		Object key = request.getAttributes().get(resourceKey);
		Object userKey = request.getAttributes().get(UserDBResource.resourceKey);		
		ReadGroup<G> query = null;
		try {
			if (key==null) {
//				query.setFieldname(search.toString());
				query = createGroupQuery(null,search==null?null:search.toString(), groupname==null?null:groupname.toString());
			}			
			else {
				if (key.toString().startsWith("G")) {
					singleItem = true;
					query = createGroupQuery(new Integer(Reference.decode(key.toString().substring(1))),null, null);
				} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
			
			if ((userKey!=null) && userKey.toString().startsWith("U")) try {
				DBUser user = new DBUser(new Integer(Reference.decode(userKey.toString().substring(1))));
				query.setFieldname(user);
			} catch (Exception x) {}
			
			return query;
		}catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Invalid id %d",key),
					x
					);
		}
	} 

	@Override
	protected QueryURIReporter<G, ReadGroup<G>> getURIReporter(
			Request baseReference) throws ResourceException {
		return new GroupQueryURIReporter(getRequest());
	}

	@Override
	protected boolean isAllowedMediaType(MediaType mediaType)
			throws ResourceException {
		return MediaType.MULTIPART_FORM_DATA.equals(mediaType);
	}

	@Override
	protected ReadGroup<G> createUpdateQuery(Method method, Context context,
			Request request, Response response) throws ResourceException {
		Object key = request.getAttributes().get(resourceKey);
		if (Method.POST.equals(method)) {
			if (key==null) return null;//post allowed only on /protocol level, not on /protocol/id
		} else {
			if (key!=null) return super.createUpdateQuery(method, context, request, response);
		}
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);		
	}
	@Override
	protected FactoryTaskConvertor getFactoryTaskConvertor(ITaskStorage storage)
			throws ResourceException {
		return new FactoryTaskConvertorRDF(storage);
	}


}
