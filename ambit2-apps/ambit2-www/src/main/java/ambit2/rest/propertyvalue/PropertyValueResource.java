package ambit2.rest.propertyvalue;

import java.io.Serializable;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.RetrieveFieldPropertyValue;
import ambit2.db.search.AbstractQuery;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

public class PropertyValueResource<T extends Serializable> extends
		QueryResource<IQueryRetrieval<T>, T> {
	public static final String featureKey = "/feature_value";
	public static final String compoundFeature = String.format("%s%s",
			CompoundResource.compoundID, featureKey);

	public static final String FeatureNameConformer = String.format(
			"%s/{name}%s", featureKey, ConformerResource.conformerID);
	public static final String FeatureNameCompound = String.format(
			"%s/{name}%s", featureKey, CompoundResource.compoundID);

	@Override
	protected void doInit() throws ResourceException {
		try {
			super.doInit();
		} catch (ResourceException x) {
			queryObject = null;
			error = x;
		}
		customizeVariants(new MediaType[] { MediaType.TEXT_HTML,
				MediaType.TEXT_URI_LIST, MediaType.TEXT_PLAIN,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE, MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JAVA_OBJECT });

	}

	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {

			return new StringConvertor(new PropertyValueReporter(),
					MediaType.TEXT_PLAIN, filenamePrefix);

		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(getURUReporter(getRequest()),
					MediaType.TEXT_URI_LIST);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML)
				|| variant.getMediaType().equals(
						MediaType.APPLICATION_RDF_TURTLE)
				|| variant.getMediaType().equals(MediaType.TEXT_RDF_N3)
				|| variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
				|| variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			return new RDFJenaConvertor<T, IQueryRetrieval<T>>(
					new PropertyValueRDFReporter<T>(getRequest(),
							variant.getMediaType()), variant.getMediaType(),
					filenamePrefix);
		} else
			return new StringConvertor(new PropertyValueReporter(),
					MediaType.TEXT_PLAIN, filenamePrefix);

	}

	@Override
	protected QueryURIReporter<T, IQueryRetrieval<T>> getURUReporter(
			Request baseReference) throws ResourceException {
		PropertyValueURIReporter reporter = new PropertyValueURIReporter<T, IQueryRetrieval<T>>(
				baseReference);
		if (queryObject instanceof AbstractQuery) {
			if (((AbstractQuery) queryObject).getValue() instanceof IStructureRecord)
				reporter.setRecord((IStructureRecord) ((AbstractQuery) queryObject)
						.getValue());
		}
		return reporter;

	}

	@Override
	protected IQueryRetrieval<T> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		RetrieveFieldPropertyValue field = new RetrieveFieldPropertyValue();
		field.setSearchByAlias(true);

		IStructureRecord record = new StructureRecord();
		try {
			record.setIdchemical(Integer.parseInt(Reference.decode(request
					.getAttributes().get(CompoundResource.idcompound)
					.toString())));
		} catch (NumberFormatException x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Invalid resource id %d", request
							.getAttributes().get(CompoundResource.idcompound)),
					x);
		}
		try {
			record.setIdstructure(Integer.parseInt(Reference.decode(request
					.getAttributes().get(ConformerResource.idconformer)
					.toString())));
			field.setChemicalsOnly(false);

		} catch (Exception x) {
			field.setChemicalsOnly(true);
		} finally {
			field.setValue(record);
		}
		try {
			field.setFieldname(null);
			Object name = request.getAttributes().get("name");
			if (name != null) {
				name = Reference.decode(name.toString());
				field.setFieldname(Property.getInstance(name.toString(),
						LiteratureEntry.getInstance()));
			}
		} catch (Exception x) {
			field.setFieldname(null);
		}
		return (IQueryRetrieval) field;
	}

}
