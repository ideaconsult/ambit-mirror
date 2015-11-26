package ambit2.rest.dataset;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.reporters.QueryPacketReporter;
import ambit2.rest.property.PropertyResource;

public class FastDatasetStructuresResource extends DatasetStructuresResource<IQueryRetrieval<IStructureRecord>> {
    public final static String resource = "/fastdataset";
    protected int packetSize = 50;
    protected boolean chemicals = true;

    @Override
    protected void doInit() throws ResourceException {
	super.doInit();
	try {
	    Object v = getResourceRef(getRequest()).getQueryAsForm().getFirstValue("packet");
	    if (v != null)
		packetSize = Integer.parseInt(v.toString());
	} catch (Exception x) {
	    packetSize = 50;
	}
	try {
	    Object v = getResourceRef(getRequest()).getQueryAsForm().getFirstValue("chemicals");
	    if (v != null)
		chemicals = Boolean.parseBoolean(v.toString());
	} catch (Exception x) {
	    chemicals = true;
	}
    }

    /**
     * Test setup, CSV support only
     */
    @Override
    public RepresentationConvertor createConvertor(Variant variant) throws AmbitException, ResourceException {
	/* workaround for clients not being able to set accept headers */
	if ((queryObject == null) && !(variant.getMediaType().equals(MediaType.TEXT_HTML)))
	    throw new NotFoundException();

	setTemplate(template);
	Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
	String media = acceptform.getFirstValue("accept-header");
	if (media != null) {
	    variant.setMediaType(new MediaType(media));
	}
	return new OutputWriterConvertor<IStructureRecord, IQueryRetrieval<IStructureRecord>>(new ChunkedCSVReporter(
		getTemplate(), getRequest().getRootRef().toString(), packetSize, chemicals), MediaType.TEXT_CSV);

	/*
	 * return new OutputWriterConvertor<IStructureRecord,
	 * QueryStructureByID>( new
	 * QueryPacketReporter<IQueryRetrieval<IStructureRecord>, Output> new
	 * CSVReporter
	 * (getTemplate(),groupProperties,getRequest().getRootRef().toString
	 * ()),MediaType.TEXT_CSV);
	 */
    }

    @Override
    protected String getDefaultTemplateURI(Context context, Request request, Response response) {
	Object id = request.getAttributes().get(datasetKey);
	if (id != null)
	    // return
	    // String.format("riap://application/dataset/%s%s",id,PropertyResource.featuredef);
	    return String.format("%s%s/%s%s", getRequest().getRootRef(), resource, id, PropertyResource.featuredef);
	else
	    return super.getDefaultTemplateURI(context, request, response);

    }
}

class ChunkedCSVReporter extends QueryPacketReporter<IQueryRetrieval<IStructureRecord>, Writer> {
    /**
     * 
     */
    private static final long serialVersionUID = -8593219677161780040L;
    protected String urlPrefix;
    protected Profile groupProperties;
    protected String separator = ",";
    protected int numberofHeaderLines = 1;
    protected boolean writeCompoundURI = true;

    public Profile getGroupProperties() {
	return groupProperties;
    }

    public void setGroupProperties(Profile gp) {
	this.groupProperties = gp;
    }

    protected List<Property> header = null;

    public ChunkedCSVReporter(Profile<Property> template, String urlPrefix, int chunkSize, boolean chemicalsOnly) {
	super(template, chunkSize);
	this.urlPrefix = urlPrefix;
	chunkQuery.setChemicalsOnly(chemicalsOnly);
    }

    @Override
    public void footer(Writer output, IQueryRetrieval<IStructureRecord> query) {
	try {
	    if (header == null) {
		writeHeader(output);
	    }
	    output.flush();
	} catch (Exception x) {
	    logger.log(Level.WARNING, x.getMessage(), x);
	}
	;
    };

    protected void writeHeader(Writer writer) throws IOException {
	if (header == null) {
	    header = template2Header(template, true);

	    if (numberofHeaderLines <= 0) {
		// no header
	    } else if (numberofHeaderLines == 1) {
		writer.write("Compound");
		for (Property p : header)
		    writer.write(String.format("%s\"%s %s\"", separator, p.getName() == null ? "N?A" : p.getName(),
			    p.getUnits() == null ? "" : p.getUnits()));
		writer.write("\n");
	    } else {
		writer.write("");
		for (Property p : header)
		    writer.write(String.format("%s\"%s\"", separator, p.getUrl()));
		writer.write("\n");
		writer.write("");
		for (Property p : header)
		    writer.write(String.format("%s\"%s\"", separator, p.getTitle()));

		writer.write("\n");
		writer.write("URI");
		for (Property p : header)
		    writer.write(String.format("%s\"%s\"", separator, p.getName()));
		writer.write("\n");

		writer.write("");
		for (Property p : header)
		    writer.write(String.format("%s\"%s\"", separator, p.getUnits()));
		writer.write("\n");
	    }
	}
    }

    @Override
    public Object processItem(IStructureRecord item) throws Exception {
	Writer writer = getOutput();
	try {

	    writeHeader(writer);
	    int i = 0;

	    if (writeCompoundURI) {
		writer.write(String.format("%s/compound/%d", urlPrefix, item.getIdchemical()));
		if (item.getIdstructure() > 0)
		    writer.write(String.format("/conformer/%d", item.getIdstructure()));
	    }
	    String delimiter = writeCompoundURI ? separator : "";
	    for (Property p : header) {

		Object value = item.getRecordProperty(p);
		if (p.getClazz() == Number.class)
		    writer.write(String.format("%s%s", delimiter, value == null ? "" : value));
		else if ((value != null) && (value.toString().indexOf("<html>") >= 0))
		    writer.write(String.format("%s\" \"", delimiter));
		else
		    writer.write(String.format("%s\"%s\"", delimiter,
			    value == null ? "" : value.toString().replace("\n", "").replace("\r", "")));
		i++;
		delimiter = separator;
	    }

	} catch (Exception x) {
	    logger.log(java.util.logging.Level.SEVERE, x.getMessage(), x);
	} finally {
	    try {
		writer.write('\n');
	    } catch (Exception x) {
	    }
	}
	return null;
    }

    protected List<Property> template2Header(Profile<Property> template, boolean propertiesOnly) {
	List<Property> h = new ArrayList<Property>();
	Iterator<Property> it;
	if (groupProperties != null) {
	    it = groupProperties.getProperties(true);
	    while (it.hasNext()) {
		Property t = it.next();
		h.add(t);
	    }
	}
	it = template.getProperties(true);
	while (it.hasNext()) {
	    Property t = it.next();
	    if (!propertiesOnly || (propertiesOnly && (t.getId() > 0)))
		h.add(t);
	}

	/*
	 * Collections.sort(h,new Comparator<Property>() { public int
	 * compare(Property o1, Property o2) { return
	 * o1.getOrder()-o2.getOrder(); } });
	 */
	return h;
    }
}