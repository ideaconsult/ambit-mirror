package ambit2.rest.bundle;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ISourceDataset;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.dataset.DatasetStructuresResource;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.dataset.MetadataRDFReporter;
import ambit2.rest.dataset.MetadatasetJSONReporter;
import ambit2.rest.dataset.MetadatasetURIReporter;
import ambit2.rest.query.QueryResource;

abstract public class AbstractMetadataResource<M extends ISourceDataset> extends QueryResource<IQueryRetrieval<M>, M> {
    protected M dataset;

    public AbstractMetadataResource() {
	super();
	setHtmlbyTemplate(true);
    }

    @Override
    public String getTemplateName() {
	return "datasets.ftl";
    }

    @Override
    protected void doInit() throws ResourceException {
	super.doInit();
	customizeVariants(new MediaType[] { MediaType.TEXT_HTML, MediaType.TEXT_URI_LIST,
		ChemicalMediaType.CHEMICAL_SMILES, ChemicalMediaType.CHEMICAL_CML, ChemicalMediaType.CHEMICAL_MDLSDF,
		ChemicalMediaType.CHEMICAL_MDLMOL, ChemicalMediaType.WEKA_ARFF, ChemicalMediaType.THREECOL_ARFF,
		MediaType.APPLICATION_RDF_XML, MediaType.APPLICATION_RDF_TURTLE, MediaType.TEXT_RDF_N3,
		MediaType.TEXT_RDF_NTRIPLES, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JAVA_OBJECT });

    }

    @Override
    public RepresentationConvertor createConvertor(Variant variant) throws AmbitException, ResourceException {
	String filenamePrefix = getRequest().getResourceRef().getPath();
	if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
	    return new OutputWriterConvertor(new MetadatasetJSONReporter<IQueryRetrieval<M>, M>(getRequest()),
		    MediaType.APPLICATION_JSON);
	} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
	    return new StringConvertor(new DatasetURIReporter<IQueryRetrieval<M>, M>(getRequest()),
		    MediaType.TEXT_URI_LIST, filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_N3)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIG)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIX)) {
	    return new RDFJenaConvertor<ISourceDataset, IQueryRetrieval<ISourceDataset>>(
		    new MetadataRDFReporter<ISourceDataset, IQueryRetrieval<ISourceDataset>>(getRequest(),
			    variant.getMediaType()), variant.getMediaType(), filenamePrefix);

	} else
	    // default json
	    return new OutputWriterConvertor(new MetadatasetJSONReporter<IQueryRetrieval<M>, M>(getRequest()),
		    MediaType.APPLICATION_JSON);
    }

    @Override
    protected QueryURIReporter<M, IQueryRetrieval<M>> getURUReporter(Request baseReference) throws ResourceException {
	return new MetadatasetURIReporter<IQueryRetrieval<M>, M>(baseReference);
    }

    /**
     * PUT allowed for metadata resources only (updates the metadata
     * representation)
     */
    @Override
    protected Representation put(Representation entity, Variant variant) throws ResourceException {
	if (getRequest().getAttributes().get(DatasetStructuresResource.datasetKey) != null)
	    createNewObject(entity);
	else
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

	return getResponse().getEntity();
    }

    @Override
    public void executeUpdate(Representation entity, M entry, AbstractUpdate updateObject) throws ResourceException {
	Object id = getRequest().getAttributes().get(DatasetStructuresResource.datasetKey);
	if (id != null)
	    try {
		Integer idnum = new Integer(Reference.decode(id.toString()));
		entry.setID(idnum);
	    } catch (Exception x) {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	    }
	super.executeUpdate(entity, entry, updateObject);
    }

    /**
     * POST not allowed, use PUT for update
     */
    @Override
    protected Representation post(Representation entity, Variant variant) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
    }

    /**
     * Delete entire metadata is not allowed. It will be deleted when the
     * dataset is removed
     */
    @Override
    protected Representation delete(Variant variant) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
    }
}
