package ambit2.rest;

import java.util.ArrayList;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.routing.Filter;

/**
 * CORS
 * @author nina
 *
 */
public class OriginFilter extends Filter {
	protected List<String> allowedOrigins = null;
	public List<String> getAllowedOrigins() {
		return allowedOrigins;
	}
	public OriginFilter(Context context, String allowedOrigins) { 
	      super(context); 
	      String[] origins = allowedOrigins==null?new String[]{}:allowedOrigins.split(" ");
	      this.allowedOrigins = new ArrayList<String>();
	      for (String origin : origins) this.allowedOrigins.add(origin.trim());
	}
	public OriginFilter(Context context, List<String> allowedOrigins) { 
	      super(context); 
	      this.allowedOrigins = allowedOrigins;
	   } 

	   @Override 
	   protected int beforeHandle(Request request, Response response) { 
	      if(Method.OPTIONS.equals(request.getMethod())) { 
	         Form requestHeaders = (Form) request.getAttributes().get("org.restlet.http.headers"); 
	         String origin = requestHeaders.getFirstValue("Origin", true); 
	         boolean all = getAllowedOrigins().contains("*");
	         if(all || getAllowedOrigins().contains(origin)) { 
	            Form responseHeaders = (Form) response.getAttributes().get("org.restlet.http.headers"); 
	            if (responseHeaders == null) { 
	                responseHeaders = new Form(); 
	                response.getAttributes().put("org.restlet.http.headers", responseHeaders); 
	            } 
	            responseHeaders.add("Access-Control-Allow-Origin", origin!=null?origin:(all?"*":origin)); 
	            responseHeaders.add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"); 
	            responseHeaders.add("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept" );
	            responseHeaders.add("Access-Control-Allow-Credentials", "true"); 
	            responseHeaders.add("Access-Control-Max-Age", "60"); 
	            response.setEntity(new EmptyRepresentation()); 
	            return SKIP; 
	         } 
	      } 

	      return super.beforeHandle(request, response); 
	   } 

	   @Override 
	   protected void afterHandle(Request request, Response response) { 
	      if(!Method.OPTIONS.equals(request.getMethod())) { 
	         Form requestHeaders = (Form) request.getAttributes().get("org.restlet.http.headers"); 
	         String origin = requestHeaders.getFirstValue("Origin", true); 
	         boolean all = getAllowedOrigins().contains("*");
	         if(all || getAllowedOrigins().contains(origin)) { 
	            Form responseHeaders = (Form) response.getAttributes().get("org.restlet.http.headers"); 
	            if (responseHeaders == null) { 
	                responseHeaders = new Form(); 
	                response.getAttributes().put("org.restlet.http.headers", responseHeaders); 
	            } 
	            responseHeaders.add("Access-Control-Allow-Origin", origin!=null?origin:(all?"*":origin)); 
	            responseHeaders.add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"); 
	            responseHeaders.add("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept" );
	            responseHeaders.add("Access-Control-Allow-Credentials", "true"); 
	            responseHeaders.add("Access-Control-Max-Age", "60"); 
	         } 
	      } 
	      super.afterHandle(request, response); 
	   } 
}
