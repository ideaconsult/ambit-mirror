package ambit2.i5rest;

import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

import ambit2.i5rest.resource.DocumentResource;
import ambit2.i5rest.resource.I5QueryResource;
import ambit2.i5rest.resource.SubstanceResource;
import ambit2.rest.RESTComponent;

public class I5Application extends Application {
	protected Properties properties = null;

	//protected String connectionURI;
	protected DataSource datasource = null;

	public I5Application() {
		super();
		setName("IUCLID5 REST services");
		setDescription("REST wrapper for IUCLID");
		setOwner("Ideaconsult Ltd.");
		setAuthor("Ideaconsult Ltd.");		
		getTunnelService().setUserAgentTunnel(true);
		
	}
	protected void loadProperties()  {
		try {
		if (properties == null) {
			properties = new Properties();
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/rest/config/ambit2.pref");
			properties.load(in);
			in.close();		
		}
		} catch (Exception x) {
			properties = null;
		}
	}	

	
	@Override
	public Restlet createRoot() {
		Router router = new Router(this.getContext());

		router.attach(String.format("%s",SubstanceResource.resource,SubstanceResource.resourceKey),
				SubstanceResource.class);		
		router.attach(String.format("%s",DocumentResource.resource,DocumentResource.resourceKey),
							DocumentResource.class);
		router.attach(String.format("%s",I5QueryResource.resource,I5QueryResource.resourceKey),
				I5QueryResource.class);	
		return router;
	}
	/**
	 * Standalone, for testing mainly
	 * @param args
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception {
        
        // Create a component
        RESTComponent component = new RESTComponent(null,new Application[] {new I5Application()});
        final Server server = component.getServers().add(Protocol.HTTP, 8080);
        component.start();
   
        System.out.println("Server started on port " + server.getPort());
        System.out.println("Press key to stop server");
        System.in.read();
        System.out.println("Stopping server");
        component.stop();
        System.out.println("Server stopped");
    }
  

}