package ambit2.rest.legacy;

/**
 * Convenience class for processing OpenTox resources (e.g. {@link OTAlgorithm}
 * and {@link OTModel}
 * 
 * @author nina
 * 
 * @param <T>
 */
@Deprecated
public abstract class OTProcessingContainers<T extends OTProcessingResource>
		extends OTContainers<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2923678513191494188L;

	public OTProcessingContainers(String uri, String referer) {
		super(uri, referer);
	}

	@Override
	public OTDataset process(OTDataset inputDataset) throws Exception {
		return process(inputDataset, false);
	}

	public OTDataset process(OTDataset inputDataset, boolean copyAlways)
			throws Exception {

		if (size() == 0)
			return inputDataset;

		OTDataset dataset_copy = !copyAlways && (size() == 1) ? inputDataset
				: inputDataset.withDatasetService(dataset_service).copy();

		OTRemoteTaskPool pool = new OTRemoteTaskPool();
		for (T item : getItems())
			if (item.isSelected()) {
				OTRemoteTask task = item.processAsync(dataset_copy);
				if (task != null)
					pool.add(task);
			}
		OTDatasets datasets = tasksCompleted(pool).taskResults(pool, null);
		if (!copyAlways && (datasets.size() == 1))
			for (OTDataset dataset : datasets.getItems())
				return dataset;
		return dataset_copy.put(datasets);

	}

}
