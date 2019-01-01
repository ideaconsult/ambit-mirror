package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.AlgorithmType;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.core.data.model.Parameter;
import ambit2.rest.OpenTox;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.task.CallableMockup;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.i.task.ICallableTask;

public class AlgorithmListResource extends CatalogResource<Algorithm<String>> {

	public enum _param {
		level0, level1, level2
	}

	public final static String resourceID = OpenTox.URI.algorithm.getResourceID();
	protected static List<Algorithm<String>> algorithmList;
	protected AlgorithmsPile algorithmsPile;

	public AlgorithmListResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
	public String getTemplateName() {
		return "algorithm.ftl";
	}

	protected synchronized void initList() {
		if (algorithmList == null) {
			AlgorithmsPile ap = new AlgorithmsPile();
			algorithmList = ap.createList(ap.fromJSON());
		}
	}

	protected Iterator<Algorithm<String>> getAlgorithmIterator(AlgorithmsPile algobucket, Object type, Object search) {
		return AlgorithmsPile.createIterator(algorithmList, type == null ? null : type.toString(),
				search == null ? null : search.toString());
	}

	protected Algorithm<String> find(Object key) {
		key = Reference.decode(key.toString());
		Algorithm<String> q = new Algorithm<String>();
		q.setId(key.toString());
		int index = algorithmList.indexOf(q);
		if (index < 0)
			return null;
		else
			return algorithmList.get(index);
	}

	@Override
	protected Iterator<Algorithm<String>> createQuery(Context context, Request request, Response response)
			throws ResourceException {
		try {
			initList();

			Object key = getRequest().getAttributes().get(MLResources.algorithmKey);

			if (key == null) {
				Object type = getResourceRef(getRequest()).getQueryAsForm().getFirstValue("type");
				Object search = getResourceRef(getRequest()).getQueryAsForm().getFirstValue("search");
				if ((type != null) || (search != null))
					return getAlgorithmIterator(algorithmsPile, type, search);
				else
					return algorithmList.iterator();
			} else {
				Algorithm<String> a = find(Reference.decode(key.toString()));
				if (a == null) {
					getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
					return null;
				} else {
					for (_param p : _param.values()) {
						key = getRequest().getAttributes().get(p.name());
						if (key != null)
							a.addParameter(new Parameter<String>(p.name(), key.toString()));
					}
					;
					ArrayList<Algorithm<String>> q = new ArrayList<Algorithm<String>>();
					q.add(a);
					return q.iterator();
				}
			}

		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage(), x);
		}
	}

	@Override
	public IProcessor<Iterator<Algorithm<String>>, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		String filenamePrefix = getRequest().getResourceRef().getPath();

		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			AlgorithmURIReporter r = new AlgorithmURIReporter(getRequest()) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 6533510120881493518L;

				@Override
				public void processItem(Algorithm item, Writer output) {
					super.processItem(item, output);
					try {
						output.write('\n');
					} catch (Exception x) {
					}
				}
			};
			return new StringConvertor(r, MediaType.TEXT_URI_LIST);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			Form params = getResourceRef(getRequest()).getQueryAsForm();
			String jsonpcallback = params.getFirstValue("jsonp");
			if (jsonpcallback == null)
				jsonpcallback = params.getFirstValue("callback");
			AlgorithmJSONReporter r = createAlgorithmJSONReporter(getRequest(), jsonpcallback);
			return new StringConvertor(r, MediaType.APPLICATION_JAVASCRIPT);
		} else {
			AlgorithmJSONReporter r = createAlgorithmJSONReporter(getRequest(), null);
			return new StringConvertor(r, MediaType.APPLICATION_JSON);
		}

	}

	protected AlgorithmJSONReporter createAlgorithmJSONReporter(Request request, String jsonpcallback) {
		return new AlgorithmJSONReporter(request, jsonpcallback);
	}

	protected Reference getSourceReference() throws ResourceException {
		return null;
		/*
		 * Form form = getRequest().getResourceRef().getQueryAsForm(); Object
		 * datasetURI = form.getFirstValue(dataset_uri); if (datasetURI==null)
		 * throw new
		 * ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format
		 * ("Empty %s", dataset_uri)); return new
		 * Reference(Reference.decode(datasetURI.toString()));
		 */
	}

	@Override
	protected Reference getSourceReference(Form form, Algorithm<String> model) throws ResourceException {
		if (model.hasType(AlgorithmType.Rules))
			return null;
		if (model.hasType(AlgorithmType.ExternalModels))
			return null;
		if (model.hasType(AlgorithmType.Fingerprints))
			return null;
		if (model.hasType(AlgorithmType.Mockup))
			return null;
		if (model.hasType(AlgorithmType.SuperService))
			return null;
		if (model.hasType(AlgorithmType.SuperBuilder))
			return null;
		if (model.hasType(AlgorithmType.Structure))
			return null;
		if (model.hasType(AlgorithmType.Structure2D))
			return null;
		if (model.hasType(AlgorithmType.TautomerGenerator))
			return null;
		Object datasetURI = OpenTox.params.dataset_uri.getFirstValue(form);
		if (datasetURI == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, String.format("Empty %s [%s]",
					OpenTox.params.dataset_uri.toString(), OpenTox.params.dataset_uri.getDescription()));
		return new Reference(Reference.decode(datasetURI.toString().trim()));
	}

	@Override
	protected ICallableTask createCallable(Form form, Algorithm<String> algorithm) throws ResourceException {

		if (form.getFirstValue(OpenTox.params.dataset_service.toString()) == null)
			form.add(OpenTox.params.dataset_service.toString(),
					String.format("%s/%s", getRequest().getRootRef(), OpenTox.URI.dataset.toString()));

		ModelURIReporter<IQueryRetrieval<ModelQueryResults>> modelReporter = new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(
				getRequest());
		AlgorithmURIReporter algReporter = new AlgorithmURIReporter(getRequest());

		Object token = getToken();
		try {
			return createCallable(form, algorithm, modelReporter, algReporter, token);
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} finally {
			// try { connection.close(); } catch (Exception x) {}
		}

	}

	protected ICallableTask createCallable(Form form, Algorithm<String> algorithm,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> modelReporter, AlgorithmURIReporter algReporter,
			Object token) throws ResourceException {
		if (algorithm.hasType(AlgorithmType.Mockup)) {
			return new CallableMockup(form, token);
		} else
			throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}

	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request, IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		Object taskid = getRequest().getAttributes().get(MLResources.algorithmKey);
		if (taskid != null)
			map.put("algid", taskid.toString());
	}
}
