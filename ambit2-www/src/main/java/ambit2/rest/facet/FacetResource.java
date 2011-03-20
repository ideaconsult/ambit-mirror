package ambit2.rest.facet;

import java.sql.Connection;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.facet.IFacet;
import ambit2.base.interfaces.IProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.DBConnection;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.property.ProfileReader;
import ambit2.rest.query.QueryResource;


public abstract class FacetResource<Q extends IQueryRetrieval<IFacet<String>>> extends	QueryResource<Q,IFacet<String>> {
	public static final String resource = "/facet";
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
				return new OutputWriterConvertor(
						new FacetHTMLReporter(getRequest()),
						MediaType.TEXT_HTML);
	}
	

	protected Template getProperty(String[] propertyURI, int max)  throws ResourceException {
		if (propertyURI==null) return null;
		Template profile = new Template();
		Connection connection = null;
		try {

    		DBConnection dbc = new DBConnection(getContext());
    		connection = dbc.getConnection(getRequest());    		
    		
    		
    		ProfileReader reader = new ProfileReader(getRequest().getRootRef(),profile);
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
}