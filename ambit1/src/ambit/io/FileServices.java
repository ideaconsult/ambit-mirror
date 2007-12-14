/**
 * Created on 2005-1-25
 */

package ambit.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ambit.domain.AllData;

/**
 * This is to simplify javax.jnlp.FileSaveService and javax.jnlp.FileOpenService usage <br>
 * These are used to open/save files in a sandbox JavaWebStart environment.  
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class FileServices {
	protected String fileSaveService = "javax.jnlp.FileSaveService";
	protected String fileOpenService = "javax.jnlp.FileOpenService";	
	protected Class fss = null;
	protected Class fos = null;	
	protected Class unavailableServiceException = null;
	protected Class serviceManagerC = null;
	protected Class fileContent =  null;
	
	protected Method lookup = null;
	protected Method saveFileDialog = null;
	protected Method openFileDialog = null;
	protected Method getInputStream = null;
	protected boolean initialized = false;
	/**
	 * 
	 */
	public FileServices() {
		super();
		initialized = false;
	}
	/**
	 * 
	 * @return true if the application was started from Java Web Start
	 * @see <a href="http://java.sun.com/products/javawebstart/">Java Web Start</a>
	 */
	public static boolean isJWS() {
		return (System.getProperty("jnlpx.jvm") != null); 
	}	
	/**
	 * initializes javax.jnlp services <br>
	 *
	 * @throws UnsupportedOperationException
	 */
	public void init() throws UnsupportedOperationException {
		if (initialized ) return;
		try {
			fss = Class.forName(fileSaveService);
			fos = Class.forName(fileOpenService);			
			unavailableServiceException = Class.forName("javax.jnlp.UnavailableServiceException");
			serviceManagerC = Class.forName("javax.jnlp.ServiceManager");
			lookup =  serviceManagerC.getMethod("lookup", new Class[] {String.class});
			saveFileDialog = fss.getMethod("saveFileDialog",new Class[] {String.class,String[].class,InputStream.class,String.class});
			openFileDialog = fos.getMethod("openFileDialog",new Class[] {String.class,String[].class});
			
			fileContent = Class.forName("javax.jnlp.FileContents");
			getInputStream = fileContent.getMethod("getInputStream",new Class[] {});
			initialized = true;
		} catch (ClassNotFoundException ex) {
            throw new UnsupportedOperationException("Java Webstart not available due to " + ex.getMessage());
		} catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("Java Webstart not available due to " + e.getMessage());			
		}	
		
	}
	/**
	 * File save
	 * @param filename 
	 * @param ext
	 * @param data the data to be saved
	 * @return true if successful
	 * @throws Exception
	 */
	public boolean save(String filename, String[] ext,String[] extDescription, IReadWriteStream data)  throws Exception {
	    if (fss != null) { 
	        try { 
	        	ByteArrayOutputStream out = new ByteArrayOutputStream();
	        	data.save(out);
	            InputStream in =  new ByteArrayInputStream(out.toByteArray());
	        	out.close();
	        	Object result = invoke( fileSaveService, saveFileDialog, new Object[] { 
	                    null
	                    ,ext
	                    ,in
	                    ,filename});
	            return true;
	        } catch (IOException e) { 
	            e.printStackTrace(); 
	            throw(e);
	        } 
	    }
	    return false;
	}
	/**
	 * 
	 * @param filename
	 * @param ext
	 * @param data the data to be read
	 * @param match TODO to be removed
	 * @return true if successful
	 * @throws Exception
	 */
	public boolean open(String filename, String[] ext, IReadWriteStream data,
			AllData match)  throws Exception {
	    if (fos != null) { 
	        try { 
	        	Object result = invoke( fileOpenService, openFileDialog, new Object[] { 
	                    null
	                    ,ext});
	        	 if (result != null) {
	                 InputStream in = (InputStream) getInputStream.invoke( result, new Object[] {});
	                 data.load(in);
	                 in.close();
	                 return true;
	        	 } else
	                return false;	        	
	        	
	        } catch (IOException e) { 
	            e.printStackTrace(); 
	            throw(e);
	        } 
	    } else return false;
	}

	
	 private Object invoke(String serviceName, Method method, Object[] args) throws Exception {
        Object service;
        try {
            service = lookup.invoke( null, new Object[] { serviceName });
            return method.invoke( service,  args);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getTargetException();
        } catch (Exception e) {
            if ( unavailableServiceException.isInstance( e ) ) {
                 throw new UnsupportedOperationException("The java-webstart serivce " + serviceName + " is not available!");
            }
            throw new UnsupportedOperationException(e.getMessage());
        }
    }
	
}
