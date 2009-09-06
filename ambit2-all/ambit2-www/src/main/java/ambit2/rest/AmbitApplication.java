package ambit2.rest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.FutureTask;

import javax.sql.DataSource;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Directory;
import org.restlet.Guard;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Request;

import ambit2.base.config.Preferences;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.DatasourceFactory;
import ambit2.db.LoginInfo;
import ambit2.rest.algorithm.AlgorithmCatalogResource;
import ambit2.rest.algorithm.AlgorithmResource;
import ambit2.rest.algorithm.descriptors.AlgorithmDescriptorTypesResource;
import ambit2.rest.algorithm.quantumchemical.Build3DResource;
import ambit2.rest.algorithm.util.AlgorithmUtilTypesResource;
import ambit2.rest.algorithm.util.Name2StructureResource;
import ambit2.rest.dataset.DatasetStructuresResource;
import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.dataset.QueryDatasetResource;
import ambit2.rest.fastox.FastToxStep1;
import ambit2.rest.fastox.FastToxStep2;
import ambit2.rest.fastox.KroesInput;
import ambit2.rest.fastox.KroesStep1;
import ambit2.rest.fastox.KroesStep2;
import ambit2.rest.model.ModelResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.propertyvalue.FeatureResource;
import ambit2.rest.propertyvalue.PropertyModelResource;
import ambit2.rest.propertyvalue.PropertyTemplateResource;
import ambit2.rest.propertyvalue.PropertyValueResource;
import ambit2.rest.pubchem.PubchemResource;
import ambit2.rest.query.PropertyQueryResource;
import ambit2.rest.query.QLabelQueryResource;
import ambit2.rest.query.QueryListResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.SmartsQueryResource;
import ambit2.rest.queryresults.QueryResultsResource;
import ambit2.rest.reference.ReferenceResource;
import ambit2.rest.similarity.SimilarityResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;
import ambit2.rest.structure.diagram.AbstractDepict;
import ambit2.rest.structure.diagram.CDKDepict;
import ambit2.rest.structure.diagram.DaylightDepict;
import ambit2.rest.task.Task;
import ambit2.rest.task.TaskResource;
import ambit2.rest.template.OntologyResource;
import ambit2.rest.tuple.TuplePropertyValueResource;
import ambit2.rest.tuple.TupleResource;

/**
 * AMBIT implementation of OpenTox REST services as described in http://opentox.org/development/wiki/
 * http://opentox.org/wiki/1/Dataset
 * @author nina
 */

 /* 
 * http://www.slideshare.net/guest7d0e11/creating-a-web-of-data-with-restlet-presentation
 * http://stackoverflow.com/questions/810171/how-to-read-context-parameters-from-a-restlet
 *
 */
public class AmbitApplication extends Application {
	protected ConcurrentMap<UUID,Task<Reference>> tasks;


	public final static String dataset_structures = String.format("%s%s",DatasetsResource.datasetID,CompoundResource.compound);
	public final static String datasetID_structure = String.format("%s%s",DatasetsResource.datasetID,CompoundResource.compoundID);
	public final static String datasetID_structure_media = String.format("%s%s",DatasetsResource.datasetID,CompoundResource.compoundID_media);
	
	
	protected String connectionURI;
	protected DataSource datasource = null;
	public AmbitApplication(Context context) {
		super(context);
		tasks = new ConcurrentHashMap<UUID,Task<Reference>>();
		/*
		String tmpDir = System.getProperty("java.io.tmpdir");
        File logFile = new File(tmpDir,"ambit2-www.log");		
		System.setProperty("java.util.logging.config.file",logFile.getAbsolutePath());
		*/
		try {
			LoginInfo li = new LoginInfo();
			li.setDatabase("ambit2");
			li.setUser("guest");
			li.setPassword("guest");
			li.setPort("3306");
			if (getContext().getParameters().size()>0) {
				li.setDatabase(getContext().getParameters().getValues(Preferences.DATABASE));
				li.setUser(getContext().getParameters().getValues(Preferences.USER));
				li.setPassword(getContext().getParameters().getValues(Preferences.PASSWORD));
				li.setHostname(getContext().getParameters().getValues(Preferences.HOST));
				li.setPort(getContext().getParameters().getValues(Preferences.PORT));
			}
			
			connectionURI = DatasourceFactory.getConnectionURI(
	                li.getScheme(), li.getHostname(), li.getPort(), 
	                li.getDatabase(), li.getUser(), li.getPassword()); 
		} catch (Exception x) {

			connectionURI = null;
			
		}
		
		setStatusService(new AmbitStatusService());
		getTaskService().setEnabled(true);
	}
	
	
	
	@Override
	public Restlet createRoot() {
		Router router = new Router(this.getContext());
		router.attach("/help", AmbitResource.class);
		
		router.attach("/", AmbitResource.class);
		router.attach("", AmbitResource.class);	
		router.attach(FastToxStep1.resource, FastToxStep1.class);
		router.attach(FastToxStep2.resource, FastToxStep2.class);

		router.attach("/ttc", KroesStep1.class);
		router.attach("/ttc/step2", KroesStep2.class);
		router.attach("/ttc/input", KroesInput.class);
		
		
		router.attach(OntologyResource.resource, OntologyResource.class);
		router.attach(OntologyResource.resourceID, OntologyResource.class);

		
		Guard guard = new Guard(getContext(),ChallengeScheme.HTTP_BASIC, "AMBIT file upload") {
			@Override
			public int authenticate(Request request) {
				if (request.getMethod().equals(org.restlet.data.Method.GET)) return AUTHENTICATION_VALID;
				return super.authenticate(request);
			}
			@Override
			public boolean authorize(Request request) {
				if (request.getMethod().equals(org.restlet.data.Method.GET)) return true;
				return super.authorize(request);
			}
		};
		guard.getSecrets().put("opentox", "opentox".toCharArray());  
		guard.setNext(DatasetsResource.class);  
		
		router.attach(DatasetsResource.datasets, guard);
		
		//router.attach(DatasetsResource.datasets, DatasetsResource.class);
		router.attach(DatasetsResource.datasetID, DatasetsResource.class);
		router.attach(QueryDatasetResource.datasetName, QueryDatasetResource.class);
		router.attach(datasetID_structure, CompoundResource.class);
		router.attach(datasetID_structure_media, CompoundResource.class);
		
		
		router.attach(dataset_structures, DatasetStructuresResource.class);
		
		//router.attach("/smiles/{smiles}"+fp,SimilarityResource.class);
		//router.attach("/smiles/{smiles}"+fp_dataset,SimilarityResource.class);
		router.attach(SimilarityResource.fp+"/smiles/{smiles}",SimilarityResource.class);
		router.attach(SimilarityResource.fp_dataset+"/smiles/{smiles}",SimilarityResource.class);		
		
		//router.attach("/cas/{cas}"+fp,SimilarityResource.class);
		//router.attach("/name/{name}"+fp,SimilarityResource.class);		
		router.attach(CompoundResource.compound,CompoundResource.class);
		router.attach(CompoundResource.compoundID,CompoundResource.class);
		router.attach(CompoundResource.compoundID_media, CompoundResource.class);		
		router.attach(ConformerResource.conformers,ConformerResource.class);
		router.attach(ConformerResource.conformerID,ConformerResource.class);
		router.attach(ConformerResource.conformerID_media, ConformerResource.class);		

		router.attach(FeatureResource.CompoundFeaturedefID,FeatureResource.class);
		router.attach(FeatureResource.ConformerFeaturedefID,FeatureResource.class);
		
		router.attach(PropertyValueResource.compoundFeature,PropertyValueResource.class);
		router.attach(PropertyValueResource.compoundFeatureName,PropertyValueResource.class);
		router.attach(PropertyValueResource.conformerFeatureName,PropertyValueResource.class);

		router.attach(PropertyValueResource.FeatureNameCompound,PropertyValueResource.class);
		router.attach(PropertyValueResource.FeatureNameConformer,PropertyValueResource.class);

		router.attach(PropertyTemplateResource.compoundTemplate,PropertyTemplateResource.class);
		router.attach(PropertyTemplateResource.compoundTemplateID,PropertyTemplateResource.class);
		router.attach(PropertyTemplateResource.conformerTemplateID,PropertyTemplateResource.class);

		router.attach(PropertyTemplateResource.TemplateIDCompound,PropertyTemplateResource.class);
		router.attach(PropertyTemplateResource.TemplateIDConformer,PropertyTemplateResource.class);
		
		router.attach(PropertyModelResource.compoundModel,PropertyModelResource.class);
		router.attach(PropertyModelResource.compoundModelID,PropertyModelResource.class);
		router.attach(PropertyModelResource.conformerModelID,PropertyModelResource.class);
		
		router.attach(TupleResource.resource,TupleResource.class);
		router.attach(TuplePropertyValueResource.resourceCompoundID,TuplePropertyValueResource.class);
		router.attach(TuplePropertyValueResource.resourceConformerID,TuplePropertyValueResource.class);
		router.attach(TupleResource.resourceDataset,TupleResource.class);

		
		router.attach(ReferenceResource.referenceID,ReferenceResource.class);
		router.attach(ReferenceResource.reference,ReferenceResource.class);

		router.attach(PropertyResource.featuredef,PropertyResource.class);
		router.attach(PropertyResource.featuredefID,PropertyResource.class);
		router.attach(PropertyResource.CompoundFeaturedef,PropertyResource.class);
		router.attach(PropertyResource.ConformerFeaturedef,PropertyResource.class);
		router.attach(PropertyResource.CompoundFeaturedefID,PropertyResource.class);
		router.attach(PropertyResource.ConformerFeaturedefID,PropertyResource.class);
		
		router.attach(QueryResultsResource.resourceID,QueryResultsResource.class);
		router.attach(QueryResultsResource.resource,QueryResultsResource.class);
		
		router.attach(PubchemResource.resourceID,PubchemResource.class);
		router.attach(PubchemResource.resource,PubchemResource.class);
		
		router.attach("/depict/daylight",DaylightDepict.class);
		router.attach("/depict/cdk",CDKDepict.class);
		router.attach("/depict",AbstractDepict.class);
		router.attach("/name2structure",Name2StructureResource.class);	
		
		router.attach("/build3d/smiles/{smiles}",Build3DResource.class);	
		router.attach(PropertyQueryResource.property,PropertyQueryResource.class);
		
		router.attach(String.format("%s%s",QueryResource.query_resource,QLabelQueryResource.resource),QLabelQueryResource.class);
		router.attach(String.format("%s%s%s",DatasetsResource.datasetID,QueryResource.query_resource,QLabelQueryResource.resource),QLabelQueryResource.class);
				
		router.attach(SmartsQueryResource.smarts_resource,SmartsQueryResource.class);
		router.attach(SmartsQueryResource.smartsID,SmartsQueryResource.class);
		router.attach(SmartsQueryResource.dataset_smarts_resource,SmartsQueryResource.class);
		router.attach(SmartsQueryResource.dataset_smarts_resource_id,SmartsQueryResource.class);
		
		router.attach(QueryResource.query_resource,QueryListResource.class);
		
		router.attach(AlgorithmCatalogResource.algorithm,AlgorithmCatalogResource.class);
		router.attach(String.format("%s/%s",AlgorithmCatalogResource.algorithm,AlgorithmCatalogResource.algorithmtypes.descriptorcalculation.toString()),
						AlgorithmDescriptorTypesResource.class);
		
		router.attach(String.format("%s/%s",AlgorithmCatalogResource.algorithm,"util"),
				AlgorithmUtilTypesResource.class);
		
		for (AlgorithmUtilTypesResource.utiltypes o : AlgorithmUtilTypesResource.utiltypes.values())
			router.attach(String.format("%s/%s/%s",
					AlgorithmCatalogResource.algorithm,
					"util",
					o.toString()
					),
					AlgorithmUtilTypesResource.class);
		
		for (AlgorithmDescriptorTypesResource.descriptortypes o : AlgorithmDescriptorTypesResource.descriptortypes.values())
			router.attach(String.format("%s/%s/%s/{%s}",
					AlgorithmCatalogResource.algorithm,
					AlgorithmCatalogResource.algorithmtypes.descriptorcalculation.toString(),
					o.toString(),
					AlgorithmDescriptorTypesResource.iddescriptor),
					AlgorithmDescriptorTypesResource.class);		
		
		router.attach(String.format("%s/rules/{%s}",AlgorithmCatalogResource.algorithm,AlgorithmResource.idalgorithm),AlgorithmResource.class);
		router.attach(String.format("%s/rules",AlgorithmCatalogResource.algorithm),AlgorithmResource.class);
		
		router.attach(ModelResource.resource,ModelResource.class);
		router.attach(ModelResource.resourceID,ModelResource.class);

		
		router.attach("/"+TaskResource.resource, TaskResource.class);
		router.attach(TaskResource.resourceID, TaskResource.class);
		 
		 Directory imgDir = new Directory(getContext(), "war:///images");
		 Directory jmolDir = new Directory(getContext(), "war:///jmol");
		 Directory styleDir = new Directory(getContext(), "war:///style");

		 router.attach("/images/", imgDir);
		 router.attach("/jmol/", jmolDir);
		 router.attach("/style/", styleDir);
		 router.attach("/favicon.ico", FavIconResource.class);
		 router.attach("/favicon.png", FavIconResource.class);
		 

		return router;
	}
	
	/*
      at com.mysql.jdbc.MysqlIO.sendCommand(MysqlIO.java:1936)
        at com.mysql.jdbc.MysqlIO.sqlQueryDirect(MysqlIO.java:2060)
        at com.mysql.jdbc.ConnectionImpl.execSQL(ConnectionImpl.java:2536)
        at com.mysql.jdbc.ConnectionImpl.execSQL(ConnectionImpl.java:2465)
        at com.mysql.jdbc.StatementImpl.execute(StatementImpl.java:734)
        at org.apache.commons.dbcp.DelegatingStatement.execute(DelegatingStatement.java:264)
        at ambit2.rest.AmbitApplication.getConnection(AmbitApplication.java:251)
        at ambit2.rest.query.QueryResource.getRepresentation(QueryResource.java:49)
        at org.restlet.resource.Resource.handleGet(Resource.java:464)

	 */
	public Connection getConnection() throws AmbitException , SQLException{
		SQLException error = null;
		Connection c = null;
		for (int retry=0; retry< 3; retry++)
		try {
			c = DatasourceFactory.getDataSource(connectionURI).getConnection();
			Statement t = c.createStatement();
			ResultSet rs = t.executeQuery("SELECT 1");
			while (rs.next()) {rs.getInt(1);}
			rs.close();
			t.close();
			error= null;
			return c;
		} catch (SQLException x) {
			//TODO reinitialize the connection pool
			error = x;
			x.printStackTrace();
			//remove the connection from the pool
			try {if (c != null) c.close();} catch (Exception e) {}
		} finally {
			
		}
		if (error != null) throw error; else throw new SQLException("Can't establish connection "+connectionURI);
	}
	
	public Iterator<Task<Reference>> getTasks() {
		return tasks.values().iterator();
	}
	public synchronized Task<Reference> findTask(String id) {
		try {
		return tasks.get(UUID.fromString(id));
		} catch (Exception x) {
			System.out.println(x);
			return null;
		}
	}

	public synchronized Reference addTask(String taskName, Callable<Reference> callable, Reference baseReference) {
		if (callable == null) return null;
		FutureTask<Reference> futureTask = new FutureTask<Reference>(callable) {
			@Override
			protected void done() {
				super.done();
				//((AmbitApplication)getApplication()).getTasks().remove(this);
			}
			
			
		};		
		UUID uuid = UUID.randomUUID();
		Task<Reference> task = new Task<Reference>(futureTask);
		task.setName(taskName);
		Reference ref =	new Reference(
				String.format("%s/%s/%s", baseReference.toString(),TaskResource.resource,Reference.encode(uuid.toString())));
		task.setUri(ref);
		tasks.put(uuid,task);
		getTaskService().submit(futureTask);	
		return ref;
	}

	public void cancelTasks() {
		Iterator<Task<Reference>> i = getTasks();
		while (i.hasNext()) {
			Task<Reference> task = i.next();
			try {
			if (!task.isDone()) task.cancel(true);
			} catch (Exception x) {getLogger().warning(x.getMessage());}
		}
	}
	public void removeTasks() {
		cancelTasks();
		tasks.clear();
	}	
	/**
	 * Standalone, for testing mainly
	 * @param args
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception {
        
        // Create a component
        Component component = new Component();
        component.getServers().add(Protocol.HTTP, 8080);
       
        AmbitApplication application = new AmbitApplication(component.getContext().createChildContext());


        // Attach the application to the component and start it
        component.getDefaultHost().attach(application);
        component.start();

    }
    	
    
}
