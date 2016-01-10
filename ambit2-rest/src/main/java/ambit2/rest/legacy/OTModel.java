package ambit2.rest.legacy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.idea.restnet.c.task.ClientResourceWrapper;
import net.idea.restnet.rdf.ns.OT;
import net.idea.restnet.rdf.ns.OT.OTProperty;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.rest.OpenTox;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Implementation of OpenTox API {@link http
 * ://opentox.org/dev/apis/api-1.1/Model}
 * 
 * @author nina
 * 
 */
@Deprecated
public class OTModel extends OTProcessingResource implements IOTModel {
	protected String endpointName;
	protected boolean supportsPredictedVarURI = true;

	public boolean isSupportsPredictedVarURI() {
		return supportsPredictedVarURI;
	}

	public void setSupportsPredictedVarURI(boolean supportsPredictedVarURI) {
		this.supportsPredictedVarURI = supportsPredictedVarURI;
	}

	public String getEndpointName() {
		return endpointName;
	}

	public void setEndpointName(String endpointName) {
		this.endpointName = endpointName;
	}

	protected String endpointURI;

	public String getEndpointURI() {
		return endpointURI;
	}

	public void setEndpointURI(String endpointURI) {
		this.endpointURI = endpointURI;
	}

	protected OTFeatures independentVariables;
	protected String creator;
	protected OTDataset trainingData;
	protected OTAlgorithm algorithm;

	protected OTFeatures dependentVariables;

	public OTFeatures getIndependentVariables() {
		return independentVariables;
	}

	public OTFeatures getDependentVariables() {
		return dependentVariables;
	}

	public OTFeatures getPredictedVariables() {
		return predictedVariables;
	}

	protected OTFeatures predictedVariables;

	public OTModel(String ref,String referer) {
		super(ref,referer);
	}

	public static OTModel model(String datasetURI,String referer) throws Exception {
		return new OTModel(datasetURI,referer);
	}

	public OTObject withCreator(String name) {
		this.creator = name;
		return this;
	}

	public String getCreator() {
		return creator;
	}

	public OTObject withAlgorithm(String url) {

		try {
			this.algorithm = url == null ? null : OTAlgorithm.algorithm(url,referer);
		} catch (Exception x) {
		}
		return this;
	}

	public OTAlgorithm getAlgorithm() {
		return algorithm;
	}

	public OTObject withTrainingData(String url) {

		try {
			this.trainingData = url == null ? null : OTDataset.dataset(url);
		} catch (Exception x) {
		}
		return this;
	}

	public OTDataset getTrainingDataset() {
		return trainingData;
	}

	public OTModel load() throws Exception {
		return load(new OTProperty[] { OTProperty.independentVariables,
				OTProperty.predictedVariables },referer);
	}

	/**
	 * Reads RDF and initializes variables
	 * 
	 * @return
	 * @throws Exception
	 */
	public OTModel load(OTProperty[] varTypes, String referer) throws Exception {
		OTFeatures vars = null;
		String sparqlName = "sparql/ModelIndependentVariables.sparql";

		OntModel model = null;
		try {
			model = OT.createModel(OntModelSpec.OWL_DL_MEM);
			model = (OntModel) OT.createModel(model, new Reference(getUri()),
					MediaType.APPLICATION_RDF_XML, referer);
			for (OTProperty varType : varTypes) {
				switch (varType) {
				case independentVariables: {
					if (independentVariables == null)
						independentVariables = OTFeatures.features((String)null,referer);
					vars = independentVariables;
					break;
				}
				case dependentVariables: {
					if (dependentVariables == null)
						dependentVariables = OTFeatures.features((String)null,referer);
					vars = dependentVariables;
					sparqlName = "sparql/ModelDependentVariables.sparql";
					break;
				}
				case predictedVariables: {
					if (predictedVariables == null)
						predictedVariables = OTFeatures.features((String)null,referer);
					vars = predictedVariables;
					sparqlName = "sparql/ModelPredictedVariables.sparql";
					break;
				}
				case model: {
					sparqlName = "sparql/Model.sparql";
					QueryExecution qe = null;
					try {

						Query query = QueryFactory.create(String.format(OTModel
								.getSparql(sparqlName).toString(), getUri(),
								getUri(), getUri(), getUri(), getUri()));
						qe = QueryExecutionFactory.create(query, model);
						ResultSet results = qe.execSelect();

						while (results.hasNext()) {
							QuerySolution solution = results.next();
							RDFNode var = solution.get("title");
							if (var != null)
								withName(var.isLiteral() ? ((Literal) var)
										.getString() : ((Resource) var)
										.getURI());

							var = solution.get("creator");
							if (var != null)
								withCreator(var.isLiteral() ? ((Literal) var)
										.getString() : ((Resource) var)
										.getURI());

							var = solution.get(OTProperty.trainingDataset
									.toString());
							if (var != null)
								withTrainingData(var.isLiteral() ? ((Literal) var)
										.getString() : ((Resource) var)
										.getURI());

							var = solution.get("algorithm");
							if (var != null)
								withAlgorithm(var.isLiteral() ? ((Literal) var)
										.getString() : ((Resource) var)
										.getURI());

						}
						this.forbidden = false;
					} catch (Exception x) {
						throw x;
					} finally {
						try {
							qe.close();
						} catch (Exception x) {
						}

					}
					return this;
				}
				default:
					throw new ResourceException(
							Status.CLIENT_ERROR_BAD_REQUEST, String.format(
									"Not supported", varType));
				}
				vars.clear();
				vars.withDatasetService(dataset_service);

				QueryExecution qe = null;
				try {

					Query query = QueryFactory.create(String.format(OTModel
							.getSparql(sparqlName).toString(), getUri(),
							getUri()));
					qe = QueryExecutionFactory.create(query, model);
					ResultSet results = qe.execSelect();

					while (results.hasNext()) {
						QuerySolution solution = results.next();
						Resource var = solution.getResource("vars");

						vars.add(OTFeature.feature(var.getURI(),referer));
					}
				} catch (Exception x) {
					throw x;
				} finally {
					try {
						qe.close();
					} catch (Exception x) {
					}

				}
			}
		} catch (ResourceException x) {
			if (Status.CLIENT_ERROR_FORBIDDEN.equals(x.getStatus()))
				this.forbidden = true;
			throw x;
		} catch (Exception x) {
			throw x;
		} finally {
			try {
				model.close();
			} catch (Exception x) {
			}
		}

		return this;
	}

	public OTDataset calculateDescriptors(OTDataset inputDataset)
			throws Exception {
		if (independentVariables == null)
			load();
		return calculateDescriptors(independentVariables, inputDataset);
	}

	public OTDataset calculateDescriptors(OTFeatures features,
			OTDataset inputDataset) throws Exception {
		// System.out.println("descriptors" + getClass().getName() +
		// inputDataset);

		if (features.size() > 0) {
			OTRemoteTaskPool pool = new OTRemoteTaskPool();
			OTAlgorithms algorithms = OTAlgorithms.algorithms(null,referer);
			algorithms.withDatasetService(dataset_service);

			OTDatasets datasets = OTDatasets.datasets(null,referer);
			datasets.withDatasetService(dataset_service);

			Exception stop = null;

			for (OTFeature feature : features.getItems()) {
				if ((feature != null)
						&& (feature.algorithm().getAlgorithm() != null)) {
					try {
						OTDataset subset = inputDataset.filterByFeature(
								feature, false);
						if (subset != null) {
							if (subset.isEmpty(false)) { // nothing to calculate
								OTDataset newdataset = OTDataset
										.dataset(inputDataset.getUri())
										.withDatasetService(dataset_service)
										.addColumns(feature);
								datasets.add(newdataset);
							} else { // run descriptors on the subset
								OTDataset newDataset = subset.copy();
								OTRemoteTask task = feature.getAlgorithm()
										.processAsync(newDataset);
								pool.add(task);
							}
						} else { // subset == null, run descriptors on entire
									// dataset
							// System.out.println(feature);
							// System.out.println(feature.getAlgorithm());
							OTRemoteTask task = feature.getAlgorithm()
									.processAsync(inputDataset);
							// System.out.println(task);
							if (!task.isERROR())
								pool.add(task);
							else {
								stop = task.getError();
								task.getError().printStackTrace();
								break;
							}
						}

					} catch (Exception x) {
						if (stop == null) {
							// x.printStackTrace();
							OTRemoteTask task = feature.getAlgorithm()
									.processAsync(inputDataset);
							pool.add(task);
						} else
							break;
					}
				}
			}
			if (pool.size() > 0)
				datasets = tasksCompleted(pool).taskResults(pool, datasets);

			if (stop != null)
				throw stop;
			// ok, all datasets in place, merge into one to submit for model
			// calculation
			if (datasets.size() > 0)
				return datasets.merge();

		}
		return inputDataset;
	}

	public OTDataset subsetWithoutPredictedValues(OTDataset inputDataset)
			throws Exception {
		if (predictedVariables == null)
			load();
		return inputDataset
				.filteredSubsetWithoutFeatures(getPredictedVariables());
	}

	@Override
	public OTDataset process(OTDataset inputDataset) throws Exception {
		// System.out.println(getClass().getName() + inputDataset);
		OTDataset subsetToCalculate = subsetWithoutPredictedValues(inputDataset);
		// System.out.println(getClass().getName() + subsetToCalculate);
		OTDataset result = null;
		if (!subsetToCalculate.isEmpty()) {
			OTDataset newdataset = subsetToCalculate.withDatasetService(
					dataset_service).copy();
			result = predict(newdataset);
		}
		if ((getPredictedVariables() == null)
				|| getPredictedVariables().size() == 0)
			return result;
		return OTDataset.dataset(inputDataset.uri)
				.withDatasetService(dataset_service).removeColumns()
				.addColumns(getPredictedVariables());
	}

	/*
	 * public OTDataset process(OTDataset inputDataset) throws Exception {
	 * 
	 * OTDataset subsetToCalculate = subsetWithoutPredictedValues(inputDataset);
	 * if (subsetToCalculate == null) { //probably remote dataset return
	 * predict(inputDataset); } else { //ours
	 * 
	 * if (!subsetToCalculate.isEmpty()) { OTDataset newdataset =
	 * subsetToCalculate.withDatasetService(dataset_service).copy();
	 * predict(newdataset); } if
	 * (dataset_service.getParentRef().isParent(getUri())) //ours return
	 * OTDataset
	 * .dataset(inputDataset.getUri()).withDatasetService(dataset_service).
	 * removeColumns
	 * ().addColumns(OTFeature.feature(String.format("%s/predicted",getUri())));
	 * else return
	 * OTDataset.dataset(inputDataset.getUri()).withDatasetService(dataset_service
	 * ). removeColumns().addColumns(getPredictedVariables()); }
	 * 
	 * }
	 */

	public OTDataset predict(OTDataset subsetToCalculate) throws Exception {
		// System.out.println(getClass().getName() + subsetToCalculate);
		long now = System.currentTimeMillis();
		OTRemoteTask task = processAsync(subsetToCalculate);
		wait(task, now);
		if (task.getError() != null)
			throw task.getError();
		if ((task.getResult() == null)
				|| !task.getResult().toString().startsWith("http"))
			throw new Exception(String.format(
					"Error when running task at %s Bad Result: %s",
					task.getUrl(), task.getResult()));
		return OTDataset.dataset(task.getResult()).withDatasetService(
				dataset_service);

	}

	@Override
	public OTRemoteTask processAsync(OTDataset inputDataset) throws Exception {
		if (inputDataset == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					"No input dataset");
		Form params = form == null ? new Form() : form;
		if (dataset_service != null) {
			params.removeAll(OpenTox.params.dataset_service.toString());
			params.add(OpenTox.params.dataset_service.toString(),
					dataset_service.toString());
		}
		params.removeAll(OpenTox.params.model_uri.toString());
		params.removeAll(OpenTox.params.dataset_uri.toString());
		params.add(OpenTox.params.dataset_uri.toString(),
				inputDataset.toString());
		return new OTRemoteTask(new Reference(getUri()),
				MediaType.TEXT_URI_LIST, params.getWebRepresentation(),
				Method.POST);

	}

	public OTModel withDatasetService(Reference uri) throws Exception {
		this.dataset_service = uri;
		return this;
	}

	public OTModel withDatasetService(String uri) throws Exception {
		return withDatasetService(new Reference(uri));
	}

	public static String getSparql(String name) throws Exception {
		InputStream in = OTModel.class.getClassLoader().getResourceAsStream(
				name);
		StringBuilder sparqlQuery = new StringBuilder();
		String line;

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in, "UTF-8"));
			while ((line = reader.readLine()) != null) {
				sparqlQuery.append(line).append("\n");
			}
		} finally {
			in.close();
		}
		return sparqlQuery.toString();
	}

	@Override
	public OTModel withParams(Form form) throws Exception {
		return (OTModel) super.withParams(form);
	}

	@Override
	public OTModel withParams(String name, String value) throws Exception {
		return (OTModel) super.withParams(name, value);
	}

	public String report(String ontologyURI) throws Exception {
		String a = String.format("<%s>", getUri());
		String query = String.format(
				OTModel.getSparql("sparql/ModelReport.sparql").toString(), "",
				a, a);

		OTOntologyService<String> ontology = new OTOntologyService<String>(
				ontologyURI);

		StringBuilder b = new StringBuilder().append(ontology.report(query));
		ClientResourceWrapper client = new ClientResourceWrapper(getUri());
		Representation r = null;
		try {
			r = client.get(MediaType.TEXT_PLAIN);
			b.append(r.getText().replace("\n", "<br>"));
		} catch (Exception x) {

		} finally {
			try {
				r.release();
			} catch (Exception x) {
			}
			try {
				client.release();
			} catch (Exception x) {
			}
		}
		return b.toString();
	}
}
