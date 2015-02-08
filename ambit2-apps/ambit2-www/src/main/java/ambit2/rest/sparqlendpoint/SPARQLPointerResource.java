package ambit2.rest.sparqlendpoint;

import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.StringConvertor;
import ambit2.rest.algorithm.CatalogResource;
import ambit2.rest.reporters.CatalogURIReporter;

public class SPARQLPointerResource extends CatalogResource<String> {
    public static final String resource = "/sparqlendpoint";
    protected List<String> pointers = new ArrayList<String>();

    public SPARQLPointerResource() {
	super();

    }

    @Override
    protected void doInit() throws ResourceException {
	super.doInit();
	getVariants().clear();
	customizeVariants(new MediaType[] { MediaType.TEXT_PLAIN, MediaType.TEXT_URI_LIST, });

    }

    @Override
    protected synchronized Iterator<String> createQuery(Context context, Request request, Response response)
	    throws ResourceException {
	if (pointers.size() == 0)
	    pointers.add(getOntologyServiceURI());
	return pointers.iterator();
    }

    @Override
    public IProcessor<Iterator<String>, Representation> createConvertor(Variant variant) throws AmbitException,
	    ResourceException {
	MediaType mime = variant.getMediaType().equals(MediaType.TEXT_URI_LIST) ? MediaType.TEXT_URI_LIST
		: MediaType.TEXT_PLAIN;
	return new StringConvertor(new CatalogURIReporter<String>(getRequest()) {
	    /**
			     * 
			     */
	    private static final long serialVersionUID = -5233206265053960743L;

	    @Override
	    public void processItem(String src, Writer output) {
		super.processItem(src, output);
		try {
		    output.write('\n');
		} catch (Exception x) {
		}
	    }

	    @Override
	    public String getURI(String item) {

		return item;
	    }

	    @Override
	    public String getURI(String ref, String item) {

		return item;
	    }
	}, mime);

    }

    public static synchronized String getOntologyServiceURI() {
	try {
	    Properties properties = new Properties();
	    InputStream in = SPARQLPointerResource.class.getClassLoader().getResourceAsStream(
		    "ambit2/rest/config/ambit2.pref");
	    properties.load(in);
	    in.close();
	    return properties.getProperty("service.ontology");
	} catch (Exception x) {
	    return null;
	}
    }
}
