package ambit2.rest.legacy;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;

import ambit2.rest.OpenTox;

/**
 * Implementation of OpenTox API {@link http
 * ://opentox.org/dev/apis/api-1.1/Algorithm}
 * 
 * @author nina
 * 
 */
@Deprecated
public class OTAlgorithm extends OTProcessingResource implements IOTAlgorithm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2661587415132661190L;

	protected OTAlgorithm(String ref, String referer) {
		super(ref, referer);
	}

	public static OTAlgorithm algorithm(String datasetURI, String referer)
			throws Exception {
		return new OTAlgorithm(datasetURI, referer);
	}

	public OTRemoteTask processAsync(OTDataset inputDataset, OTFeature feature)
			throws Exception {
		Form params = form == null ? new Form() : form;
		if (dataset_service != null) {
			params.removeAll(OpenTox.params.dataset_service.toString());
			params.add(OpenTox.params.dataset_service.toString(),
					dataset_service.toString());
		}
		params.removeAll(OpenTox.params.dataset_uri.toString());
		params.add(OpenTox.params.dataset_uri.toString(),
				inputDataset.toString());
		params.removeAll(OpenTox.params.target.toString());
		if (feature != null)
			params.add(OpenTox.params.target.toString(), feature.toString());
		return processAsync(params);
	}

	@Override
	public OTRemoteTask processAsync(OTDataset inputDataset) throws Exception {
		return processAsync(inputDataset, null);
	}

	public OTRemoteTask processAsync(Form params) throws Exception {

		return new OTRemoteTask(new Reference(getUri()),
				MediaType.TEXT_URI_LIST, params.getWebRepresentation(),
				Method.POST);
	}

	@Override
	public OTDataset process(OTDataset inputDataset) throws Exception {
		long now = System.currentTimeMillis();
		OTRemoteTask task = processAsync(inputDataset);
		task = wait(task, now);
		if (task.getError() != null)
			throw task.getError();
		return OTDataset.dataset(task.getResult());
	}

	public OTModel process(OTDataset inputDataset, OTFeature feature)
			throws Exception {
		long now = System.currentTimeMillis();
		OTRemoteTask task = processAsync(inputDataset, feature);
		task = wait(task, now);
		if (task.getError() != null)
			throw task.getError();
		return OTModel.model(task.getResult().toString(), referer);
	}

	@Override
	public OTAlgorithm withParams(Form form) throws Exception {
		return (OTAlgorithm) super.withParams(form);
	}

	@Override
	public OTAlgorithm withParams(String name, String value) throws Exception {
		return (OTAlgorithm) super.withParams(name, value);
	}

	public String report(String ontologyURI) throws Exception {
		String a = String.format("<%s>", getUri());
		String query = String.format(
				OTModel.getSparql("sparql/AlgorithmReport.sparql").toString(),
				"", a, a);

		OTOntologyService<String> ontology = new OTOntologyService<String>(
				ontologyURI);

		return ontology.report(query);
	}
}
