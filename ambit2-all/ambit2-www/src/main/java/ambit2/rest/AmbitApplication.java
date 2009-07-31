package ambit2.rest;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.Protocol;

import ambit2.base.config.Preferences;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.DatasourceFactory;
import ambit2.db.LoginInfo;
import ambit2.rest.dataset.DatasetStructuresResource;
import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.dataset.QueryDatasetResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.propertyvalue.PropertyValueResource;
import ambit2.rest.pubchem.PubchemResource;
import ambit2.rest.query.PropertyQueryResource;
import ambit2.rest.query.QueryListResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.SmartsQueryResource;
import ambit2.rest.reference.ReferenceResource;
import ambit2.rest.similarity.SimilarityResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;
import ambit2.rest.structure.build3d.Build3DResource;
import ambit2.rest.structure.diagram.CDKDepict;
import ambit2.rest.structure.diagram.DaylightDepict;

/**
 * http://opentox.org/wiki/1/Dataset
 * @author nina
 *
 */
public class AmbitApplication extends Application {
	
	

	
	public final static String dataset_structures = String.format("%s%s",DatasetsResource.datasetID,CompoundResource.compound);
	
	public final static String datasetID_structure = String.format("%s%s",DatasetsResource.datasetID,CompoundResource.compoundID);
	public final static String datasetID_structure_media = String.format("%s%s",DatasetsResource.datasetID,CompoundResource.compoundID_media);

	public final static String property =  QueryResource.query_resource + "/property/{condition}" + "/{value}";
	
	
	protected String connectionURI;
	protected DataSource datasource = null;
	public AmbitApplication(Context context) {
		super(context);
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
	}
	
	
	@Override
	public Restlet createRoot() {
		Router router = new Router(this.getContext());
		router.attach("/", AmbitResource.class);
		router.attach("", AmbitResource.class);
		
		router.attach(DatasetsResource.datasets, DatasetsResource.class);
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
		router.attach(CompoundResource.compoundID,CompoundResource.class);
		router.attach(CompoundResource.compoundID_media, CompoundResource.class);		
		
		router.attach(ConformerResource.conformers,ConformerResource.class);
		router.attach(ConformerResource.conformerID,ConformerResource.class);
		router.attach(ConformerResource.conformerID_media, ConformerResource.class);		

		router.attach(PropertyValueResource.compoundFeatureName,PropertyValueResource.class);
		router.attach(PropertyValueResource.conformerFeatureName,PropertyValueResource.class);
		
		router.attach(ReferenceResource.referenceID,ReferenceResource.class);
		router.attach(ReferenceResource.reference,ReferenceResource.class);

		router.attach(PropertyResource.featuredef,PropertyResource.class);
		router.attach(PropertyResource.featuredefID,PropertyResource.class);
		
		router.attach("/pubchem/query/{term}",PubchemResource.class);
		
		router.attach("/daylight/depict/{smiles}",DaylightDepict.class);
		router.attach("/cdk/depict/{smiles}",CDKDepict.class);	
		
		router.attach("/build3d/smiles/{smiles}",Build3DResource.class);	
		router.attach(property,PropertyQueryResource.class);
		router.attach(SmartsQueryResource.smarts_resource,SmartsQueryResource.class);
		router.attach(SmartsQueryResource.dataset_smarts_resource,SmartsQueryResource.class);
		
		router.attach(QueryResource.query_resource,QueryListResource.class);		
		 
		return router;
	}
	
	public Connection getConnection() throws AmbitException , SQLException{
		for (int retry=0; retry< 2; retry++)
		try {
			Connection c = DatasourceFactory.getDataSource(connectionURI).getConnection();
			Statement t = c.createStatement();
			t.execute("SELECT 1");
			t.close();
			return c;
		} catch (SQLException x) {
			if (retry >= 2)
				throw x;
		} finally {
			
		}
		throw new SQLException("Can't establish connection!");
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
       
        AmbitApplication application = new AmbitApplication(component.getContext());

        // Attach the application to the component and start it
        component.getDefaultHost().attach(application);
        component.start();

    }
    	
}
