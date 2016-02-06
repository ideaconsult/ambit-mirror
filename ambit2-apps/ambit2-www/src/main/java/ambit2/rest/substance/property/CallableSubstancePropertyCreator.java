package ambit2.rest.substance.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;

import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.i.task.TaskResult;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolEffectRecord;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.core.io.json.SubstanceStudyParser;
import ambit2.core.io.study.ProtocolEffectRecord2SubstanceProperty;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.rdf.RDFPropertyIterator;

public class CallableSubstancePropertyCreator<USERID> extends CallableProtectedTask<USERID> {
    protected SubstanceProperty property;
    protected File file = null;
    protected MediaType mediaType = null;
    protected PropertyURIReporter reporter;
    protected ProtocolEffectRecord2SubstanceProperty processor = new ProtocolEffectRecord2SubstanceProperty();
    protected ObjectMapper mapper = new ObjectMapper();

    public CallableSubstancePropertyCreator(PropertyURIReporter reporter, Method method, Form form, Property item,
	    USERID token) throws ResourceException {
	super(token);
	this.reporter = reporter;
	property = parseForm(form);
    }

  /**
     * 
     * @param reporter
     * @param method
     * @param file
     * @param mediaType
     * @param item
     * @param token
     * @throws ResourceException
     */
    public CallableSubstancePropertyCreator(PropertyURIReporter reporter, Method method, File file,
	    MediaType mediaType, Property item, USERID token) throws ResourceException {
	super(token);
	this.reporter = reporter;
	this.file = file;
	this.mediaType = mediaType;
    }

    private final static String _param_protocol = "protocol";
    private final static String _param_endpointcategory = "endpointcategory";
    private final static String _param_name = "name";
    private final static String _param_units = "unit";
    private final static String _param_conditions = "conditions";

    protected SubstanceProperty parseForm(Form form) throws ResourceException {
	String topCategory = null;
	ProtocolEffectRecord<String, IParams, String> detail = new ProtocolEffectRecord<String, IParams, String>();

	String endpointcategory = form.getFirstValue(_param_endpointcategory);
	try {
	    topCategory = Protocol._categories.valueOf(endpointcategory.trim()).getTopCategory();
	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Unsupported endpoint category");
	}
	detail.setEndpoint(form.getFirstValue(_param_name));
	if (detail.getEndpoint() == null)
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Unsupported name");
	detail.setUnit(form.getFirstValue(_param_units));

	try {
	    String p_conditions = form.getFirstValue(_param_conditions);
	    if (p_conditions != null) {
		JsonNode conditions = mapper.readTree(new StringReader(p_conditions));
		if (conditions instanceof ObjectNode) {
		    detail.setConditions(SubstanceStudyParser.parseParams((ObjectNode) conditions));
		}
	    }
	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "conditions: unexpected format");
	}

	String guideline = form.getFirstValue(_param_protocol);
	if (guideline == null)
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "protocol: undefined");
	Protocol protocol = new Protocol(guideline);
	// protocol.addGuideline("Method: other: see below");
	protocol.setCategory(endpointcategory);
	protocol.setTopCategory(topCategory);
	detail.setProtocol(protocol);
	try {
	    SubstanceProperty p = processor.process(detail);
	    p.setIdentifier(p.createHashedIdentifier(detail.getConditions()));
	    return p;
	} catch (ResourceException x) {
	    throw x;
	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage(), x);
	}
    }

    @Override
    public TaskResult doCall() throws Exception {
	if (file != null) {
	    //TODO RDF iterator ignores property annotations, which will affect the URI generated
	    RDFPropertyIterator iterator = null;
	    FileInputStream in = null;
	    try {
		in = new FileInputStream(file);
		iterator = new RDFPropertyIterator(in, mediaType) {
		    @Override
		    protected Property createRecord() {
			SubstanceProperty p = new SubstanceProperty(null,null,null,null);
			p.setExtendedURI(true);
			return p;
		    }
		};
		iterator.setBaseReference(reporter.getBaseReference());
		iterator.setCloseModel(true);
		Property p = null;
		while (iterator.hasNext()) {
		    p = iterator.next();
		    break;
		}
		try {
			String category = p.getLabel().replace("http://www.opentox.org/echaEndpoints.owl#", "");
			if (!category.contains("_SECTION")) category = category + "_SECTION";
		    Protocol._categories c = Protocol._categories.valueOf(category);
		    ((SubstanceProperty)p).setTopcategory(c.getTopCategory());
		    ((SubstanceProperty)p).setEndpointcategory(c.name());
		} catch (Exception x) {
		    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Unsupported endpoint category");
		}
		IParams conditions = new Params();
		
		((SubstanceProperty)p).setIdentifier(((SubstanceProperty)p).createHashedIdentifier(conditions));
		return new TaskResult(reporter.getURI(p));
	    } catch (ResourceException x) {
		throw x;
	    } catch (Exception x) {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage(), x);
	    } finally {
		try {
		    if (in != null)
			in.close();
		} catch (Exception x) {
		}
		try {
		    if (iterator != null)
			iterator.close();
		} catch (Exception x) {
		}
	    }
	}
	return new TaskResult(reporter.getURI(property));
    }

}
