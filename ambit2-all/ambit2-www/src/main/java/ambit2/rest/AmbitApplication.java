package ambit2.rest;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.Protocol;

import ambit2.core.config.Preferences;
import ambit2.core.exceptions.AmbitException;
import ambit2.core.pubchem.EntrezSearchProcessor;
import ambit2.db.DatasourceFactory;
import ambit2.db.LoginInfo;
import ambit2.rest.dataset.DatasetResource;
import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.pubchem.PubchemResource;
import ambit2.rest.query.QueryListResource;
import ambit2.rest.similarity.SimilarityResource;
import ambit2.rest.structure.StructureResource;


public class AmbitApplication extends Application {
	public final static String datasets = "/dataset";		
	public final static String dataset = datasets+"/{dataset_id}";
	public final static String query = "/query";	
	public final static String similarity = query + "/similarity/method";		
	public final static String fp_dataset = similarity + "/fp1024/distance/tanimoto/{threshold}" + dataset;
	public final static String tanimoto = similarity + "/fp1024/distance/tanimoto";
	public final static String fp =  tanimoto + "/{threshold}";
	
	protected String connectionURI;
	protected DataSource datasource = null;
	public AmbitApplication(Context context) {
		super(context);
		LoginInfo li = new LoginInfo();
		li.setDatabase("ambit2");
		li.setUser("guest");
		li.setPassword("guest");
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
	
		EntrezSearchProcessor entrezQuery = new EntrezSearchProcessor();
	}
	
	
	@Override
	public Restlet createRoot() {
		Router router = new Router(this.getContext());
		router.attach("/", AmbitResource.class);
		
		router.attach(datasets, DatasetsResource.class);
		router.attach(dataset, DatasetResource.class);
		//router.attach("/smiles/{smiles}"+fp,SimilarityResource.class);
		//router.attach("/smiles/{smiles}"+fp_dataset,SimilarityResource.class);
		router.attach(fp+"/smiles/{smiles}",SimilarityResource.class);
		router.attach(fp_dataset+"/smiles/{smiles}",SimilarityResource.class);		
		
		//router.attach("/cas/{cas}"+fp,SimilarityResource.class);
		//router.attach("/name/{name}"+fp,SimilarityResource.class);		
		router.attach("/structure/{idstructure}",StructureResource.class);
		router.attach("/pubchem/query/{term}",PubchemResource.class);			
		
		router.attach(query,QueryListResource.class);		

		
		 
		 
		return router;
	}
	
	public Connection getConnection() throws AmbitException , SQLException{
		return DatasourceFactory.getDataSource(connectionURI).getConnection();
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
