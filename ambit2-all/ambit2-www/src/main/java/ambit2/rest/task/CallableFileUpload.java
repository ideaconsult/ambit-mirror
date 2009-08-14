package ambit2.rest.task;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.fileupload.FileItem;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * Asynchronous file upload
 * @author nina
 *
 */
public abstract class CallableFileUpload implements Callable<Reference> {
	protected List<FileItem> items;
	protected String fileUploadField;
	protected long maxSize = 1000000;
	
	public CallableFileUpload(List<FileItem> items, String fileUploadField) {
		this.items = items;
		this.fileUploadField = fileUploadField;
	}
	public Reference call() throws Exception {
				try {
                    // Process only the uploaded item called "fileToUpload" and
                    // save it on disk
                    boolean found = false;
                    for (final Iterator<FileItem> it = items.iterator(); 
                    		it.hasNext()
                            && !found;) {
                        FileItem fi = it.next();
                        if (fi.getSize()>maxSize) {
                        	throw new ResourceException(new Status(Status.CLIENT_ERROR_BAD_REQUEST,String.format("File size %d > max allowed size %d",fi.getSize(),maxSize)));
                        }
                        	
                        if (fi.getFieldName().equals(fileUploadField)) {
                        	fi.getContentType();
                            found = true;
                            File file = new File(
                            		String.format("%s/%s",
                            				System.getProperty("java.io.tmpdir"),
                            				fi.getName()));
                            fi.write(file);
                            processFile(file);
                        }
                    }    

                    return createReference();
				 } catch (ResourceException x) {
					 throw x;
                 } catch (Exception e) {
                	 e.printStackTrace();
                	 throw new ResourceException(new Status(Status.SERVER_ERROR_INTERNAL,e.getMessage()));
                 } finally {
                	 
                 }

	}
	protected void processFile(File file) throws Exception { };
	public abstract Reference createReference() ;
	

	/*
FileRepresentation rep = new FileRepresentation(
        "d:\\temp\\test.txt", MediaType.TEXT_ALL, 0);
EncodeRepresentation encodedRep = new EncodeRepresentation(Encoding.GZIP,
        rep);
Client client = new Client(Protocol.HTTP);
Response response = client.put("http://localhost:8182/essai/";, encodedRep);
System.out.println("******" + response.getStatus());


	 */
}
