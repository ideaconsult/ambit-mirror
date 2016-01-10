package ambit2.rest.legacy;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * Convenience class for processing OpenTox resources (e.g. {@link OTAlgorithm}
 * and {@link OTModel}
 * 
 * @author nina
 * 
 */
@Deprecated
public class OTProcessingResource extends OTObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3483706848787877937L;
	protected String referer;

	public OTProcessingResource(String ref, String referer) {
		super(ref);
		this.referer = referer;
	}

	public OTProcessingResource() {
		super((Reference) null);
	}

	public OTRemoteTask processAsync(OTDataset inputDataset) throws Exception {
		throw new Exception("Not implemented");
	}

	public OTDataset process(OTDataset inputDataset) throws Exception {
		throw new Exception("Not implemented");
	}

	protected OTProcessingResource tasksCompleted(OTRemoteTaskPool pool)
			throws Exception {
		long now = System.currentTimeMillis();
		while (pool.poll() > 0) {
			Thread.sleep(pollInterval);
			Thread.yield();
			if ((System.currentTimeMillis() - now) > pollTimeout)
				break;
		}
		return this;
	}

	protected OTDatasets taskResults(OTRemoteTaskPool pool, OTDatasets datasets)
			throws Exception {

		for (OTRemoteTask task : pool.getTasks()) {
			if (task.getError() != null)
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
						String.format("%s %s", task.getUrl(), task.getError()
								.getMessage()));
			if (pool.size() > 1) {
				datasets = (datasets == null) ? OTDatasets.datasets(null,
						referer) : datasets;
				datasets.add(OTDataset.dataset(task.getResult()));
			} else {
				datasets = (datasets == null) ? OTDatasets.datasets(null,
						referer) : datasets;
				datasets.add(OTDataset.dataset(task.getResult()));
			}
		}
		return datasets;
	}

}
