package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.Iterator;
import java.util.logging.Level;

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
import ambit2.rest.task.weka.CallableWekaModelCreator;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.ChemicalMediaType;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.i.task.ICallableTask;

public class AllAlgorithmsResource extends AlgorithmListResource {

	public AllAlgorithmsResource() {
		super();
		setHtmlbyTemplate(true);
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
				|| variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
				|| variant.getMediaType().equals(ChemicalMediaType.APPLICATION_JSONLD)) {
			return new StringConvertor(new AlgorithmRDFReporter(getRequest(), variant.getMediaType()),
					variant.getMediaType(), filenamePrefix);
		} else {
			AlgorithmJSONReporter r = new AlgorithmJSONReporter(getRequest());
			return new StringConvertor(r, MediaType.APPLICATION_JSON);
		}

	}

	@Override
	protected ICallableTask createCallable(Form form, Algorithm<String> algorithm,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> modelReporter, AlgorithmURIReporter algReporter,
			Object token) throws ResourceException {
		try {
			if (algorithm.hasType(AlgorithmType.Expert)) {
				String userName = "guest";
				try {
					userName = getRequest().getClientInfo().getUser().getIdentifier();
				} catch (Exception x) {
					userName = "guest";
				}
				Object datasetURI = OpenTox.params.dataset_uri.getFirstValue(form);
				return new CallableSimpleModelCreator(form, getContext(), algorithm, false,
						new ExpertModelBuilder(datasetURI == null ? null : datasetURI.toString(), userName,
								getRequest().getRootRef(), modelReporter, algReporter,
								getRequest().getResourceRef().toString()),
						token, getRequest().getResourceRef().toString(), getClientInfo());

			} else if (algorithm.hasType(AlgorithmType.SuperService)) {
				return new CallablePOST(form, getRequest().getRootRef(), token,
						getRequest().getResourceRef().toString());

			} else if (algorithm.hasType(AlgorithmType.SuperBuilder)) {
				return new CallableBuilder(form, getRequest().getRootRef(), token,
						getRequest().getResourceRef().toString());

			} else if (algorithm.hasType(AlgorithmType.Mockup)) {
				return new CallableMockup(form, token);
			} else if (algorithm.hasType(AlgorithmType.ExternalModels))
				return new CallableSimpleModelCreator(form, getRequest().getRootRef(), getContext(), algorithm,
						modelReporter, algReporter, false, token, getRequest().getResourceRef().toString(),
						getClientInfo());
			else if (algorithm.hasType(AlgorithmType.Rules))
				return new CallableSimpleModelCreator(form, getRequest().getRootRef(), getContext(), algorithm,
						modelReporter, algReporter, false, token, getRequest().getResourceRef().toString(),
						getClientInfo());
			else if (algorithm.hasType(AlgorithmType.Finder)) {
				return new CallableFinder(form, getRequest().getRootRef(), getContext(), algorithm, token,
						getRequest().getResourceRef().toString(), getClientInfo());

			} else if (algorithm.hasType(AlgorithmType.Structure)) {
				return new CallableSimpleModelCreator(form, getContext(), algorithm, false,
						new OptimizerModelBuilder(getRequest().getRootRef(), form, modelReporter, algReporter, false,
								getRequest().getResourceRef().toString()),
						token, getRequest().getResourceRef().toString(), getClientInfo());
			} else if (algorithm.hasType(AlgorithmType.Structure2D)) {

				try {
					CallableSimpleModelCreator modelCreator = new CallableSimpleModelCreator(form, getContext(),
							algorithm, false,
							new Structure2DModelBuilder(getRequest().getRootRef(), modelReporter, algReporter, false,
									getRequest().getResourceRef().toString()),
							token, getRequest().getResourceRef().toString(), getClientInfo());
					TaskResult modelRef = modelCreator.call();
					ModelQueryResults model = modelCreator.getModel();
					Structure2DProcessor predictor = new Structure2DProcessor(getRequest().getRootRef(), model,
							new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()),
							new PropertyURIReporter(getRequest()), null);

					return new CallableStructureOptimizer(form, getRequest().getRootRef(), getContext(),
							(Structure2DProcessor) predictor, token, getRequest().getResourceRef().toString(),
							getClientInfo());
				} catch (ResourceException x) {
					throw x;
				} catch (Exception x) {
					getLogger().log(Level.WARNING, x.getMessage(), x);
					throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x.getMessage(), x);
				}

			} else if (algorithm.hasType(AlgorithmType.TautomerGenerator)) {
				return new CallableSimpleModelCreator(form, getContext(), algorithm, false,
						new TautomersModelBuilder(getRequest().getRootRef(), modelReporter, algReporter, false,
								getRequest().getResourceRef().toString()),
						token, getRequest().getResourceRef().toString(), getClientInfo());
			} else if (algorithm.hasType(AlgorithmType.PreferredStructure)) {
				return new CallableFixPreferredStructure(form, getRequest().getRootRef(), getContext(), null, token,
						getRequest().getResourceRef().toString(), getClientInfo());
			} else if (algorithm.hasType(AlgorithmType.DescriptorCalculation)) {
				try {
					CallableSimpleModelCreator modelCreator = new CallableSimpleModelCreator(form,
							getRequest().getRootRef(), getContext(), algorithm, modelReporter, algReporter, true, token,
							getRequest().getResourceRef().toString(), getClientInfo());
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
						// this is wrong, the parameters should be stored in the
						// model already (and in the database!)
						model.setParameters(b.toString().split("\t"));
					}

					DescriptorPredictor predictor = new DescriptorPredictor(getRequest().getRootRef(), model,
							modelReporter, new PropertyURIReporter(getRequest()), null);
					return new CallableDescriptorCalculator(form, getRequest().getRootRef(), getContext(), predictor,
							token, getRequest().getResourceRef().toString(), getClientInfo());
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
							algorithm, modelReporter, algReporter, token, getRequest().getResourceRef().toString(),
							getClientInfo());
				}
				case property: {
					return new CallableNumericalModelCreator(form, getRequest().getRootRef(), getContext(), algorithm,
							modelReporter, algReporter, token, getRequest().getResourceRef().toString(),
							getClientInfo());
				}
				default: {
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, algorithm.toString());
				}
				}
			} else if (algorithm.hasType(AlgorithmType.Fingerprints.toString())) {
				return new CallableFingerprintsCalculator(form, getRequest().getRootRef(), getContext(), algorithm,
						token, getRequest().getResourceRef().toString(), getClientInfo());
				/*
				 * } else if
				 * (AlgorithmFormat.WAFFLES_JSON.equals(algorithm.getFormat()))
				 * { return new CallableWafflesModelCreator(form,
				 * getRequest().getRootRef(), getContext(), algorithm,
				 * modelReporter, algReporter,
				 * token,getRequest().getResourceRef().toString());
				 */
			} else {

				return new CallableWekaModelCreator(form, getRequest().getRootRef(), getContext(), algorithm,
						modelReporter, algReporter, token, getRequest().getResourceRef().toString(), getClientInfo());
			}
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} finally {
			// try { connection.close(); } catch (Exception x) {}
		}

	}

}
