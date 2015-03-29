package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.i.task.ICallableTask;

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
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.core.data.model.AlgorithmType;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.core.data.model.Parameter;
import ambit2.rest.OpenTox;
import ambit2.rest.StringConvertor;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.model.builder.ExpertModelBuilder;
import ambit2.rest.model.predictor.DescriptorPredictor;
import ambit2.rest.model.predictor.Structure2DProcessor;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.task.CallableBuilder;
import ambit2.rest.task.CallableDescriptorCalculator;
import ambit2.rest.task.CallableFingerprintsModelCreator;
import ambit2.rest.task.CallableMockup;
import ambit2.rest.task.CallableNumericalModelCreator;
import ambit2.rest.task.CallablePOST;
import ambit2.rest.task.CallableSimpleModelCreator;
import ambit2.rest.task.CallableStructureOptimizer;
import ambit2.rest.task.OptimizerModelBuilder;
import ambit2.rest.task.Structure2DModelBuilder;
import ambit2.rest.task.TaskResult;
import ambit2.rest.task.dbpreprocessing.CallableFinder;
import ambit2.rest.task.dbpreprocessing.CallableFingerprintsCalculator;
import ambit2.rest.task.dbpreprocessing.CallableFixPreferredStructure;
import ambit2.rest.task.tautomers.TautomersModelBuilder;
import ambit2.rest.task.waffles.CallableWafflesModelCreator;
import ambit2.rest.task.weka.CallableWekaModelCreator;

public class AllAlgorithmsResource extends CatalogResource<Algorithm<String>> {
	public final static String algorithm = OpenTox.URI.algorithm.getURI();
	public final static String algorithmKey = OpenTox.URI.algorithm.getKey();

	public enum _param {
		level0, level1, level2
	}

	public final static String resourceID = OpenTox.URI.algorithm.getResourceID();
	protected static List<Algorithm<String>> algorithmList;
	protected AlgorithmsPile algorithmsPile = new AlgorithmsPile();

	public AllAlgorithmsResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
	public String getTemplateName() {
		return "algorithm.ftl";
	}

	protected synchronized void initList() {
		if (algorithmList == null) {
			algorithmList = new ArrayList<Algorithm<String>>();
			algorithmList = AlgorithmsPile.createList();
		}
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

			Object key = getRequest().getAttributes().get(algorithmKey);

			if (key == null) {
				Object type = getResourceRef(getRequest()).getQueryAsForm().getFirstValue("type");
				Object search = getResourceRef(getRequest()).getQueryAsForm().getFirstValue("search");
				if ((type != null) || (search != null))
					return AlgorithmsPile.createIterator(algorithmList, type == null ? null : type.toString(),
							search == null ? null : search.toString());
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
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			AlgorithmJSONReporter r = new AlgorithmJSONReporter(getRequest());
			return new StringConvertor(r, MediaType.APPLICATION_JSON);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			Form params = getResourceRef(getRequest()).getQueryAsForm();
			String jsonpcallback = params.getFirstValue("jsonp");
			if (jsonpcallback == null)
				jsonpcallback = params.getFirstValue("callback");
			AlgorithmJSONReporter r = new AlgorithmJSONReporter(getRequest(), jsonpcallback);
			return new StringConvertor(r, MediaType.APPLICATION_JAVASCRIPT);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML)
				|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE)
				|| variant.getMediaType().equals(MediaType.TEXT_RDF_N3)
				|| variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)) {
			return new StringConvertor(new AlgorithmRDFReporter(getRequest(), variant.getMediaType()),
					variant.getMediaType(), filenamePrefix);
		} else
			// html
			return new StringConvertor(new CatalogHTMLReporter(getRequest()), MediaType.TEXT_HTML);

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

		String token = getToken();
		try {
			if (algorithm.hasType(AlgorithmType.Expert)) {
				String userName = "guest";
				try {
					userName = getRequest().getClientInfo().getUser().getIdentifier();
				} catch (Exception x) {
					userName = "guest";
				}
				Object datasetURI = OpenTox.params.dataset_uri.getFirstValue(form);
				return new CallableSimpleModelCreator(form, getContext(), algorithm, false, new ExpertModelBuilder(
						datasetURI == null ? null : datasetURI.toString(), userName, getRequest().getRootRef(),
						modelReporter, algReporter), token);

			} else if (algorithm.hasType(AlgorithmType.SuperService)) {
				return new CallablePOST<String>(form, getRequest().getRootRef(), token);

			} else if (algorithm.hasType(AlgorithmType.SuperBuilder)) {
				return new CallableBuilder<String>(form, getRequest().getRootRef(), token);

			} else if (algorithm.hasType(AlgorithmType.Mockup)) {
				return new CallableMockup<String>(form, token);
			} else if (algorithm.hasType(AlgorithmType.Rules))
				return new CallableSimpleModelCreator(form, getRequest().getRootRef(), getContext(), algorithm,
						modelReporter, algReporter, false, token);
			else if (algorithm.hasType(AlgorithmType.Finder)) {
				return new CallableFinder(form, getRequest().getRootRef(), getContext(), algorithm, token);

			} else if (algorithm.hasType(AlgorithmType.Structure)) {
				return new CallableSimpleModelCreator(form, getContext(), algorithm, false, new OptimizerModelBuilder(
						getRequest().getRootRef(), form, modelReporter, algReporter, false), token);
			} else if (algorithm.hasType(AlgorithmType.Structure2D)) {

				try {
					CallableSimpleModelCreator modelCreator = new CallableSimpleModelCreator(form, getContext(),
							algorithm, false, new Structure2DModelBuilder(getRequest().getRootRef(), modelReporter,
									algReporter, false), token);
					TaskResult modelRef = modelCreator.call();
					ModelQueryResults model = modelCreator.getModel();
					Structure2DProcessor predictor = new Structure2DProcessor(getRequest().getRootRef(), model,
							new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()),
							new PropertyURIReporter(getRequest()), null);

					return new CallableStructureOptimizer(form, getRequest().getRootRef(), getContext(),
							(Structure2DProcessor) predictor, token);
				} catch (ResourceException x) {
					throw x;
				} catch (Exception x) {
					getLogger().log(Level.WARNING, x.getMessage(), x);
					throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x.getMessage(), x);
				}

			} else if (algorithm.hasType(AlgorithmType.TautomerGenerator)) {
				return new CallableSimpleModelCreator(form, getContext(), algorithm, false, new TautomersModelBuilder(
						getRequest().getRootRef(), modelReporter, algReporter, false), token);
			} else if (algorithm.hasType(AlgorithmType.PreferredStructure)) {
				return new CallableFixPreferredStructure(form, getRequest().getRootRef(), getContext(), null, token);
			} else if (algorithm.hasType(AlgorithmType.DescriptorCalculation)) {
				try {
					CallableSimpleModelCreator modelCreator = new CallableSimpleModelCreator(form, getRequest()
							.getRootRef(), getContext(), algorithm, modelReporter, algReporter, true, token);
					TaskResult modelRef = modelCreator.call();
					ModelQueryResults model = modelCreator.getModel();

					/**
					 * Filtering params, specifying to calculate only some of
					 * the values Do not change the outcome and threfore the
					 * same model URI is generated e.g.
					 * /algorithm/ambit2.dragon.DescriptorDragonShell/MW
					 */
					//
					
					if (algorithm.getParameters() != null) {
						StringBuilder b = new StringBuilder();
						for (Parameter prm : algorithm.getParameters())
							// b.append(String.format("-%s\t'%s'\t",
							// prm.getName(),prm.getValue()));
							b.append(String.format("-%s\t'%s'\t", prm.getName(), prm.getValue()));
						//this is wrong, the parameters should be stored in the model already (and in the database!)
						model.setParameters(b.toString().split("\t"));
					}

					DescriptorPredictor predictor = new DescriptorPredictor(getRequest().getRootRef(), model,
							modelReporter, new PropertyURIReporter(getRequest()), null);
					return new CallableDescriptorCalculator(form, getRequest().getRootRef(), getContext(), predictor,
							token);
				} catch (ResourceException x) {
					throw x;
				} catch (Exception x) {
					getLogger().log(Level.WARNING, x.getMessage(), x);
					throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x.getMessage(), x);
				}
			} else if (algorithm.hasType(AlgorithmType.AppDomain)) {
				switch (algorithm.getRequirement()) {
				case structure: {
					return new CallableFingerprintsModelCreator(form, getRequest().getRootRef(), getContext(),
							algorithm, modelReporter, algReporter, token);
				}
				case property: {
					return new CallableNumericalModelCreator(form, getRequest().getRootRef(), getContext(), algorithm,
							modelReporter, algReporter, token);
				}
				default: {
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, algorithm.toString());
				}
				}
			} else if (algorithm.hasType(AlgorithmType.Fingerprints.toString())) {
				return new CallableFingerprintsCalculator(form, getRequest().getRootRef(), getContext(), algorithm,
						token);
			} else if (AlgorithmFormat.WAFFLES_JSON.equals(algorithm.getFormat())) {
				return new CallableWafflesModelCreator(form, getRequest().getRootRef(), getContext(), algorithm,
						modelReporter, algReporter, token);
			} else {

				return new CallableWekaModelCreator(form, getRequest().getRootRef(), getContext(), algorithm,
						modelReporter, algReporter, token);
			}
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} finally {
			// try { connection.close(); } catch (Exception x) {}
		}

	}

	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request, IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		Object taskid = getRequest().getAttributes().get(algorithmKey);
		if (taskid != null)
			map.put("algid", taskid.toString());
	}
}
