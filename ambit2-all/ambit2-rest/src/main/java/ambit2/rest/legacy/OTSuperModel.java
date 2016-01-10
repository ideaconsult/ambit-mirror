package ambit2.rest.legacy;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * OpenTox Superservice. Same as {@link OTModel}, but identifies and calculates
 * descriptors, if necessary
 * 
 * @author nina
 * 
 */
@Deprecated
public class OTSuperModel extends OTModel {

	public OTSuperModel(String ref, String referer) {
		super(ref, referer);
	}

	public static OTSuperModel model(String datasetURI, String referer)
			throws Exception {
		return new OTSuperModel(datasetURI, referer);
	}

	/*
	 * public OTDataset process(OTDataset inputDataset) throws Exception {
	 * 
	 * OTDataset results = null;
	 * 
	 * OTDataset subsetToCalculate = subsetWithoutPredictedValues(inputDataset);
	 * if (subsetToCalculate == null) { //probably remote dataset return
	 * super.predict(calculateDescriptors(inputDataset)); } try { if
	 * (!subsetToCalculate.isEmpty()) { OTDataset subset = null; try { subset =
	 * subsetToCalculate.withDatasetService(dataset_service).copy();
	 * 
	 * }catch (Exception x) { x.printStackTrace(); subset = inputDataset; }
	 * results = super.predict(calculateDescriptors(subset)); } } catch
	 * (Exception x) { //if empty check fail results =
	 * super.predict(calculateDescriptors(inputDataset)); } //else evertyhing is
	 * already calculated, return URI of this dataset with predicted features if
	 * (dataset_service.isParent(results.getUri())) return
	 * OTDataset.dataset(inputDataset
	 * .getUri()).withDatasetService(dataset_service).
	 * removeColumns().addColumns(getPredictedVariables()); else return results;
	 * }
	 */

	public OTDataset process(OTDataset inputDataset) throws Exception {

		OTDataset subsetToCalculate = subsetWithoutPredictedValues(inputDataset);
		OTDataset results = null;

		if (subsetToCalculate == null) {
			return super.predict(calculateDescriptors(inputDataset));
		} else if (!subsetToCalculate.isEmpty()) {
			OTDataset subset = null;
			try {
				subset = subsetToCalculate.withDatasetService(dataset_service)
						.copy();

			} catch (Exception x) {
				x.printStackTrace();
				subset = inputDataset;
			}
			OTDataset descriptors = calculateDescriptors(subset);
			if (descriptors == null)
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
						String.format("Descriptor calculation failed %s",
								subset));

			results = super.predict(descriptors);

			if (results == null)
				throw new ResourceException(
						Status.SERVER_ERROR_BAD_GATEWAY,
						String.format("Model prediction failed %s", descriptors));
		} else
			// else evertyhing is already calculated, return URI of this dataset
			// with predicted features
			results = inputDataset;

		if ((dataset_service != null)
				&& dataset_service.isParent(results.getUri())
				&& (getPredictedVariables() != null)
				&& (getPredictedVariables().size() > 0))
			return OTDataset.dataset(inputDataset.getUri())
					.withDatasetService(dataset_service).removeColumns()
					.addColumns(getPredictedVariables());
		else
			return results;

	}

}
