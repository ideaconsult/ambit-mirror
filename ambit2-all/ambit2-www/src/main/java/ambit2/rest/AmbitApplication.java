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
import ambit2.rest.dataset.DatasetResource;
import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.pubchem.PubchemResource;
import ambit2.rest.query.QueryListResource;
import ambit2.rest.similarity.SimilarityResource;
import ambit2.rest.structure.StructureResource;
import ambit2.rest.structure.build3d.Build3DResource;
import ambit2.rest.structure.diagram.CDKDepict;
import ambit2.rest.structure.diagram.DaylightDepict;


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
		
		router.attach("/daylight/depict/{smiles}",DaylightDepict.class);
		router.attach("/cdk/depict/{smiles}",CDKDepict.class);	
		
		router.attach("/build3d/smiles/{smiles}",Build3DResource.class);	
		
		router.attach(query,QueryListResource.class);		

		
		 
		 
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
