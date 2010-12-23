package ambit2.rest.task.dsl;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.rest.OpenTox;
import ambit2.rest.task.RemoteTask;
import ambit2.rest.task.dsl.interfaces.IOTDataset;

public class OTDataset extends OTObject implements IOTDataset {
	 protected enum dataset_size  {empty,nonempty,unknown};
	 protected dataset_size isEmpty = dataset_size.unknown;
	 
	 protected OTDataset(Reference ref) {
		super(ref);
	 }
	 protected OTDataset(String ref) {
		super(ref);
	}
	 public static OTDataset dataset(Reference datasetURI) throws Exception  { 
		    return new OTDataset(datasetURI);
	 }

	 public static OTDataset dataset(String datasetURI) throws Exception  { 
		    return new OTDataset(datasetURI);
	 }

	 public OTDataset copy() throws Exception  { 
		 if (dataset_service==null) throw new Exception("No dataset_service!");
		long now = System.currentTimeMillis(); 
		Form form = new Form(); 
		form.add(OpenTox.params.dataset_uri.toString(),uri.toString());
		RemoteTask task = new RemoteTask(new Reference(dataset_service),MediaType.TEXT_URI_LIST,form.getWebRepresentation(),Method.POST,authentication);
		task = wait(task,now);
		if (task.getError()!=null) throw task.getError();
		return OTDataset.dataset(task.getResult()).withDatasetService(dataset_service);
	 }
	 /**
	  * sends PUT request , adding datasets to the current one

	  */
	 public synchronized OTDataset put(OTDatasets datasets) throws Exception  { 
		    long now = System.currentTimeMillis();
		    if ((datasets==null) || (datasets.size()==0)) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,"No dataset uri to add");
			Form params = new Form();
			for (OTDataset dataset : datasets.getItems())
				params.add(OpenTox.params.dataset_uri.toString(),dataset.toString());
			if (dataset_service!=null)
				params.add(OpenTox.params.dataset_service.toString(),dataset_service.toString());
			
			RemoteTask task = null;
			if (uri==null)
				task = new RemoteTask(dataset_service,MediaType.TEXT_URI_LIST,params.getWebRepresentation(),Method.POST,authentication);
			else
				task = new RemoteTask(uri,MediaType.TEXT_URI_LIST,params.getWebRepresentation(),Method.PUT,authentication);
			task = wait(task,now);
			Reference ref = task.getResult();
			datasets.clear();
			if (ref.equals(uri)) return this; else return dataset(uri).withDatasetService(dataset_service); 
			
	 }	 


	 public OTDataset withDatasetService(Reference uri) throws Exception { 
		  this.dataset_service = uri;
		  return this; 
	 }	
	 public OTDataset withDatasetService(String uri) throws Exception { 
		  return withDatasetService(new Reference(uri));
	 }	
	 
	 public OTDataset filterByFeature(OTFeature feature,boolean withFeatures)  throws Exception  {
		 if (feature.getUri()==null) return null;
		 
		 Reference ref = new Reference(dataset_service.getParentRef()).
		 	addSegment("filter").
		 	addQueryParameter(OpenTox.params.dataset_uri.toString(), uri.toString()).
		 	addQueryParameter(OpenTox.params.condition.toString(), withFeatures?"yes":"no");
		 
		 ref.addQueryParameter(OpenTox.params.filter.toString(), feature.getUri().toString());
		 
		 return dataset(ref).withDatasetService(dataset_service);
 	 }		 
	 protected OTDataset filterByFeatures(OTFeatures features,boolean withFeatures)  throws Exception  {
		 Reference ref = new Reference(dataset_service.getParentRef()).
		 	addSegment("filter").
		 	addQueryParameter(OpenTox.params.dataset_uri.toString(), uri.toString()).
		 	addQueryParameter(OpenTox.params.condition.toString(), withFeatures?"yes":"no");
		 
		 for (OTFeature feature:features.getItems())
			 ref.addQueryParameter(OpenTox.params.filter.toString(), feature.getUri().toString());
		 
		 return dataset(ref).withDatasetService(dataset_service);
 	 }	 
	 public OTDataset filteredSubsetWithoutFeatures(OTFeatures features)  throws Exception  {
		 return filterByFeatures(features, false);
 	 }
	 public OTDataset filteredSubsetWithFeatures(OTFeatures features)  throws Exception  {
		 return filterByFeatures(features, true);
 	 }	 
	 public boolean isEmpty()  throws Exception  {
		 return isEmpty(false);
	 }
	 public boolean isEmpty(boolean cachedResult)  throws Exception  {
		 if (dataset_size.unknown.equals(isEmpty) || !cachedResult) {
			 Reference ref = uri.clone();
			 ref.addQueryParameter(OpenTox.params.page.toString(), "0");
			 ref.addQueryParameter(OpenTox.params.pagesize.toString(), "1");
			 ClientResourceWrapper client = new ClientResourceWrapper(ref);
			 Representation r = null;
			 try {
				 r = client.get(MediaType.TEXT_URI_LIST);
				 isEmpty = Status.CLIENT_ERROR_NOT_FOUND.equals(client.getStatus())?dataset_size.empty:
					 Status.SUCCESS_OK.equals(client.getStatus())?dataset_size.nonempty:dataset_size.unknown;				 
			 } catch (ResourceException x) {
				 isEmpty = Status.CLIENT_ERROR_NOT_FOUND.equals(x.getStatus())?dataset_size.empty:
					 Status.SUCCESS_OK.equals(x.getStatus())?dataset_size.nonempty:dataset_size.unknown;
			 } catch (Exception x) {
				 
			 } finally {
				 try { r.release(); } catch (Exception x) {}
				 try { client.release(); } catch (Exception x) {}
			 }
		 } 
		 return dataset_size.empty.equals(isEmpty);
	 }
	 public OTDataset removeColumns() throws Exception {
		 Form form = uri.getQueryAsForm();
		 form.removeAll(OpenTox.params.feature_uris.toString());
		 Reference ref  = new Reference(String.format("%s:%s",uri.getScheme(),uri.getHierarchicalPart()));
		 ref.setQuery(form.getQueryString());
		 return dataset(ref).withDatasetService(dataset_service);
	 }
	 public OTDataset addColumns(OTFeatures features) throws Exception {
		 Reference newuri = uri.clone();
		 for (OTFeature feature:features.getItems())
			 newuri.addQueryParameter(OpenTox.params.feature_uris.toString(), feature.getUri().toString());
		 return dataset(newuri).withDatasetService(dataset_service);
	 }
	 
	 public OTDataset addColumns(OTFeature feature) throws Exception {
		 Reference newuri = uri.clone();
		 newuri.addQueryParameter(OpenTox.params.feature_uris.toString(), feature.getUri().toString());
		 return dataset(newuri).withDatasetService(dataset_service);
	 }

	 public OTDataset getPage(int page,int pageSize) throws Exception {
		 return dataset(OTObject.getPagedReference(uri,page, pageSize));
	 }	 
	 public OTDataset getFeatures(OTFeatures features) throws Exception {
		 Reference ref = uri.clone();
		 Form form = ref.getQueryAsForm();
		 form = features.getQuery(form);
		 ref.setQuery(form.getQueryString());
		 return dataset(ref);
	 }	 
	 @Override
	public OTDataset withName(String name) {
		return (OTDataset) super.withName(name);
	}
}
