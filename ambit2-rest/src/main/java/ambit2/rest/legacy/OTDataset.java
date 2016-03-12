package ambit2.rest.legacy;

import java.io.File;

import net.idea.restnet.c.ChemicalMediaType;
import net.idea.restnet.c.task.ClientResourceWrapper;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.rest.OpenTox;

/**
 * Implementation of OpenTox API {@link http
 * ://opentox.org/dev/apis/api-1.1/dataset}
 * 
 * @author nina
 * 
 */
@Deprecated
public class OTDataset extends OTObject implements IOTDataset {
	protected enum dataset_size {
		empty, nonempty, unknown
	};

	protected dataset_size isEmpty = dataset_size.unknown;
	protected String license;

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSeeAlso() {
		return seeAlso;
	}

	public void setSeeAlso(String seeAlso) {
		this.seeAlso = seeAlso;
	}

	protected String source;
	protected String seeAlso;

	public OTDataset(Reference ref) {
		super(ref);
	}

	public OTDataset(String ref) {
		super(ref);
	}

	public static OTDataset dataset(Reference datasetURI) throws Exception {
		return new OTDataset(datasetURI);
	}

	public static OTDataset dataset(String datasetURI) throws Exception {
		return new OTDataset(datasetURI);
	}

	public static OTDataset createDataset(Representation entity,
			String dataset_service) throws ResourceException {
		OTRemoteTaskPool pool = new OTRemoteTaskPool();
		try {

			OTRemoteTask task = new OTRemoteTask(
					new Reference(dataset_service), MediaType.TEXT_URI_LIST,
					entity, org.restlet.data.Method.POST);

			pool.add(task);
			pool.run();
			if (Status.SUCCESS_OK.equals(task.getStatus())) {
				return OTDataset.dataset(task.getResult().toString())
						.withDatasetService(dataset_service);
			} else if (Status.CLIENT_ERROR_NOT_FOUND.equals(task.getStatus())) {
				throw new ResourceException(task.getStatus(),
						"Please use the \"Browse...\" button to select a file to upload!");
			} else
				throw new ResourceException(task.getStatus(),
						task.getResult() == null ? task.getStatus()
								.getDescription() : task.getResult().toString());

		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
					x.getMessage());
		} finally {
			pool.clear();
		}
	}

	public static OTDataset createDataset(File file, String mimeType,
			String dataset_service) throws ResourceException {
		try {
			MediaType media;
			if (mimeType == null) {
				media = ChemicalMediaType.guessMediaByExtension(file.getName()
						.toLowerCase());
			} else
				media = new MediaType(mimeType);
			if (media == null)
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
						"Unsuported file format");

			return createDataset(new FileRepresentation(file, media),
					dataset_service);
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,
					x.getMessage());
		}
	}

	public OTDataset copy() throws Exception {
		if (dataset_service == null)
			throw new Exception("No dataset_service!");
		long now = System.currentTimeMillis();
		Form form = new Form();
		form.add(OpenTox.params.dataset_uri.toString(), getUri().toString());
		OTRemoteTask task = new OTRemoteTask(new Reference(dataset_service),
				MediaType.TEXT_URI_LIST, form.getWebRepresentation(),
				Method.POST);
		task = wait(task, now);
		if (task.getError() != null)
			throw task.getError();
		return OTDataset.dataset(task.getResult()).withDatasetService(
				dataset_service);
	}

	/**
	 * sends PUT request , adding datasets to the current one
	 */
	public synchronized OTDataset put(OTDatasets datasets) throws Exception {
		long now = System.currentTimeMillis();
		if ((datasets == null) || (datasets.size() == 0))
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,
					"No dataset uri to add");
		Form params = new Form();
		for (OTDataset dataset : datasets.getItems())
			params.add(OpenTox.params.dataset_uri.toString(),
					dataset.toString());
		if (dataset_service != null)
			params.add(OpenTox.params.dataset_service.toString(),
					dataset_service.toString());

		OTRemoteTask task = null;
		if (getUri() == null)
			task = new OTRemoteTask(dataset_service, MediaType.TEXT_URI_LIST,
					params.getWebRepresentation(), Method.POST);
		else
			task = new OTRemoteTask(getUri(), MediaType.TEXT_URI_LIST,
					params.getWebRepresentation(), Method.PUT);
		task = wait(task, now);
		if (task.getError() != null)
			throw task.getError();
		Reference ref = task.getResult();
		datasets.clear();
		if (ref.equals(getUri()))
			return this;
		else
			return dataset(getUri()).withDatasetService(dataset_service);

	}

	public OTDataset withDatasetService(Reference uri) throws Exception {
		this.dataset_service = uri;
		return this;
	}

	public OTDataset withDatasetService(String uri) throws Exception {
		return withDatasetService(new Reference(uri));
	}

	public OTDataset filterByFeature(OTFeature feature, boolean withFeatures)
			throws Exception {
		if (feature.getUri() == null)
			return null;
		if (dataset_service == null)
			return null;

		Reference ref = new Reference(dataset_service.getParentRef());

		if (!ref.isParent(getUri()))
			return null;
		if (!ref.isParent(feature.getUri()))
			return null;

		ref = new Reference(ref)
				.addSegment("filter")
				.addQueryParameter(OpenTox.params.dataset_uri.toString(),
						uri.toString())
				.addQueryParameter(OpenTox.params.condition.toString(),
						withFeatures ? "yes" : "no");

		ref.addQueryParameter(OpenTox.params.filter.toString(), feature
				.getUri().toString());

		return dataset(ref).withDatasetService(dataset_service);
	}

	protected OTDataset filterByFeatures(OTFeatures features,
			boolean withFeatures) throws Exception {
		Reference ref = new Reference(dataset_service.getParentRef());

		if (!ref.isParent(getUri()))
			return null;

		ref = new Reference(ref)
				.addSegment("filter")
				.addQueryParameter(OpenTox.params.dataset_uri.toString(),
						uri.toString())
				.addQueryParameter(OpenTox.params.condition.toString(),
						withFeatures ? "yes" : "no");

		for (OTFeature feature : features.getItems()) {
			if (!ref.isParent(feature.getUri()))
				return null;
			ref.addQueryParameter(OpenTox.params.filter.toString(), feature
					.getUri().toString());
		}

		return dataset(ref).withDatasetService(dataset_service);
	}

	/*
	 * public OTDataset filterByFeature(OTFeature feature,boolean withFeatures)
	 * throws Exception { if (feature.getUri()==null) return null;
	 * 
	 * Reference ref = new Reference(dataset_service.getParentRef()).
	 * addSegment("filter").
	 * addQueryParameter(OpenTox.params.dataset_uri.toString(),
	 * getUri().toString()).
	 * addQueryParameter(OpenTox.params.condition.toString(),
	 * withFeatures?"yes":"no");
	 * 
	 * ref.addQueryParameter(OpenTox.params.filter.toString(),
	 * feature.getUri().toString());
	 * 
	 * return dataset(ref).withDatasetService(dataset_service); } protected
	 * OTDataset filterByFeatures(OTFeatures features,boolean withFeatures)
	 * throws Exception { if (dataset_service==null) return null;
	 * 
	 * for (OTFeature feature:features.getItems()) if
	 * (!dataset_service.getParentRef().isParent(feature.getUri())) return null;
	 * 
	 * Reference ref = new Reference(dataset_service.getParentRef()).
	 * addSegment("filter").
	 * addQueryParameter(OpenTox.params.dataset_uri.toString(),
	 * getUri().toString()).
	 * addQueryParameter(OpenTox.params.condition.toString(),
	 * withFeatures?"yes":"no");
	 * 
	 * for (OTFeature feature:features.getItems())
	 * ref.addQueryParameter(OpenTox.params.filter.toString(),
	 * feature.getUri().toString());
	 * 
	 * return dataset(ref).withDatasetService(dataset_service); }
	 */
	public OTDataset filteredSubsetWithoutFeatures(OTFeatures features)
			throws Exception {
		if ((features == null) || (features.size() == 0))
			return this;
		return filterByFeatures(features, false);
	}

	public OTDataset filteredSubsetWithFeatures(OTFeatures features)
			throws Exception {
		return filterByFeatures(features, true);
	}

	public boolean isEmpty() throws Exception {
		return isEmpty(false);
	}

	public boolean isEmpty(boolean cachedResult) throws Exception {
		if (dataset_size.unknown.equals(isEmpty) || !cachedResult) {
			Reference ref = getUri().clone();
			ref.addQueryParameter(OpenTox.params.page.toString(), "0");
			ref.addQueryParameter(OpenTox.params.pagesize.toString(), "1");
			ClientResourceWrapper client = new ClientResourceWrapper(ref);
			Representation r = null;
			try {
				r = client.get(MediaType.TEXT_URI_LIST);
				isEmpty = Status.CLIENT_ERROR_NOT_FOUND.equals(client
						.getStatus()) ? dataset_size.empty : Status.SUCCESS_OK
						.equals(client.getStatus()) ? dataset_size.nonempty
						: dataset_size.unknown;
			} catch (ResourceException x) {
				isEmpty = Status.CLIENT_ERROR_NOT_FOUND.equals(x.getStatus()) ? dataset_size.empty
						: Status.SUCCESS_OK.equals(x.getStatus()) ? dataset_size.nonempty
								: dataset_size.unknown;
				if (isEmpty.equals(dataset_size.unknown))
					throw x;
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
		}
		return dataset_size.empty.equals(isEmpty);
	}

	public OTDataset removeColumns() throws Exception {
		Form form = getUri().getQueryAsForm();
		form.removeAll(OpenTox.params.feature_uris.toString());
		Reference ref = new Reference(String.format("%s:%s", getUri()
				.getScheme(), getUri().getHierarchicalPart()));
		ref.setQuery(form.getQueryString());
		return dataset(ref).withDatasetService(dataset_service);
	}

	/*
	 * public OTDataset addColumns(OTFeatures features) throws Exception { if
	 * ((features == null) || (features.getItems()==null)) return this;
	 * Reference newuri = getUri().clone(); String d =
	 * newuri.getQuery()==null?"?":"&"; StringBuilder b = new StringBuilder();
	 * b.append(newuri.toString()); int count = 0; for (OTFeature
	 * feature:features.getItems()) { if (!feature.isSelected()) continue;
	 * b.append(d); b.append(String.format("%s=%s",
	 * OpenTox.params.feature_uris.toString
	 * (),Reference.encode(feature.getUri().toString()))); d="&"; count++; } if
	 * (count==0) return this; //[] in feature_uris[] is encoded
	 * //newuri.addQueryParameter(OpenTox.params.feature_uris.toString(),
	 * feature.getUri().toString()); return
	 * dataset(b.toString()).withDatasetService(dataset_service); }
	 */
	public OTDataset addColumns(OTFeatures features) throws Exception {
		Reference newuri = uri.clone();
		for (OTFeature feature : features.getItems())
			newuri.addQueryParameter(OpenTox.params.feature_uris.toString(),
					feature.getUri().toString());
		return dataset(newuri).withDatasetService(dataset_service);
	}

	public OTDataset addColumns(OTFeature feature) throws Exception {
		Reference newuri = getUri().clone();
		newuri.addQueryParameter(OpenTox.params.feature_uris.toString(),
				feature.getUri().toString());
		return dataset(newuri).withDatasetService(dataset_service);
	}

	public OTDataset getPage(int page, int pageSize) throws Exception {
		return dataset(OTObject.getPagedReference(uri, page, pageSize));
	}

	public OTDataset getFeatures(OTFeatures features) throws Exception {
		Reference ref = getUri().clone();
		Form form = ref.getQueryAsForm();
		form = features.getQuery(form);
		ref.setQuery(form.getQueryString());
		return dataset(ref);
	}

	public OTFeatures retrieveFeatures(OTFeatures features, String referer)
			throws Exception {
		if ((getUri() == null) || ("".equals(getUri())))
			throw new Exception("No URI");
		Reference ref = getUri().clone();
		if (features == null)
			features = OTFeatures.features(null, referer);

		if (getUri().getQuery() != null) {

			Form form = ref.getQueryAsForm();
			String[] array = form.getValuesArray(OpenTox.params.feature_uris
					.toString());
			if ((array == null) || (array.length == 0))
				features.read(String.format("%s/feature", getUri()));
			else
				for (int i = 0; i < array.length; i++) {
					features.read(array[i]);
				}

		} else
			features.read(String.format("%s/feature", getUri()));

		return features;
	}

	@Override
	public OTDataset withName(String name) {
		return (OTDataset) super.withName(name);
	}
}
