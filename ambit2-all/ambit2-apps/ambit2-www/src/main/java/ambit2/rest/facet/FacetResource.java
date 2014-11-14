package ambit2.rest.facet;

import java.io.Writer;
import java.sql.Connection;
import java.util.Map;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.StructureRecord;
import ambit2.base.data.Template;
import ambit2.base.facet.IFacet;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.property.ProfileReader;
import ambit2.rest.query.QueryResource;


public abstract class FacetResource<FACET extends IFacet<String>,Q extends IQueryRetrieval<FACET>> extends	QueryResource<Q,FACET> {
	public static final String resource = "/facet";
	
	public FacetResource() {
		super();
		setHtmlbyTemplate(true);
	}
	@Override
	public String getTemplateName() {
		return "facet_body.ftl";
	}
	@Override
	public IProcessor<Q, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		/*
		if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
			
			return new StringConvertor(new PropertyValueReporter(),MediaType.TEXT_PLAIN);
			} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				return new StringConvertor(	new ReferenceURIReporter<IQueryRetrieval<ILiteratureEntry>>(getRequest()) {
					@Override
					public Object processItem(ILiteratureEntry dataset) throws AmbitException  {
						super.processItem(dataset);
						try {
							output.write('\n');
						} catch (Exception x) {}
						return null;
					}
				},MediaType.TEXT_URI_LIST);
			} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
					variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
					variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
					variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) ||
					variant.getMediaType().equals(MediaType.APPLICATION_JSON)
					) {
				return new RDFJenaConvertor<ILiteratureEntry, IQueryRetrieval<ILiteratureEntry>>(
						new ReferenceRDFReporter<IQueryRetrieval<ILiteratureEntry>>(
								getRequest(),variant.getMediaType(),getDocumentation())
						,variant.getMediaType());					
			} else 
			*/
			String filenamePrefix = getRequest().getResourceRef().getPath();
			if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
				return new OutputWriterConvertor(
						new FacetCSVReporter(getRequest()),
						MediaType.TEXT_CSV);
			} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
					String jsonpcallback = getParams().getFirstValue("jsonp");
					if (jsonpcallback==null) jsonpcallback = getParams().getFirstValue("callback");
					return new OutputWriterConvertor(createJSONReporter(getRequest(),jsonpcallback),MediaType.APPLICATION_JSON,filenamePrefix);						
			} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
				return new OutputWriterConvertor(
						createJSONReporter(getRequest(),null),
						MediaType.APPLICATION_JSON,filenamePrefix);				
			} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
					return new OutputWriterConvertor(
							new FacetURIReporter(getRequest()) {
								@Override
								public Object processItem(Object item)
										throws AmbitException {
									Object i = super.processItem(item);
									try {
										((Writer)output).write('\n');
									} catch (Exception x) {}
									return i;
								}
							},
							MediaType.TEXT_URI_LIST,filenamePrefix);				
			} else 
				return new OutputWriterConvertor(
						getHTMLReporter(getRequest()),
						MediaType.TEXT_HTML);
	}
	
	protected QueryReporter createJSONReporter(Request request, String jsonp) {
		return new FacetJSONReporter(request,jsonp);
	}
	protected FacetHTMLReporter getHTMLReporter(Request request) {
		return new FacetHTMLReporter(request);
	}

	protected Template getProperty(String[] propertyURI, int max)  throws ResourceException {
		if (propertyURI==null) return null;
		Template profile = new Template();
		Connection connection = null;
		try {

    		DBConnection dbc = new DBConnection(getContext());
    		connection = dbc.getConnection();    		
    		
    		
    		ProfileReader reader = new ProfileReader(getRequest().getRootRef(),profile,getApplication().getContext(),getToken(),
    				getRequest().getCookies(),
    				getRequest().getClientInfo()==null?null:getRequest().getClientInfo().getAgent());
    		reader.setCloseConnection(false);
    		reader.setConnection(connection);
    		for (int i=0; i < propertyURI.length;i++) {
    			reader.process(new Reference(propertyURI[i]));
    			if ((i+1) >= max) break;
    		}
    		return profile;
  
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		} finally {
			try { connection.close(); } catch (Exception x) {}
		}	

	}	
	protected IStructureRecord getStructure() {
		String uri = getParams().getFirstValue(OpenTox.params.compound_uri.toString());
		Reference base = getRequest().getRootRef();

		int index = uri==null?-1:uri.indexOf("/dataset/");
		if (index>0) {
			index = uri.indexOf("/",index+10);
			base = new Reference(uri.substring(0,index));
		}
		
		int compoundid  = -1;
		int conformerid  = -1;
		if (uri!= null) {
			Object id = OpenTox.URI.compound.getId(uri,base);
			if (id == null) {
				Object[] ids;
				ids = OpenTox.URI.conformer.getIds(uri,base);
				compoundid  = ((Integer) ids[0]).intValue();
				conformerid = ((Integer) ids[1]).intValue();
			} else  
				compoundid = ((Integer)id).intValue();
		}
		
		if ((compoundid>0) || (conformerid>0))
			return new StructureRecord(compoundid,conformerid,null,null);
		else return null;
	}
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		map.put("facet_title","Summary");
		map.put("facet_tooltip","");
		map.put("facet_group","");
		map.put("facet_subgroup","");
		map.put("facet_count","count");
	}
}