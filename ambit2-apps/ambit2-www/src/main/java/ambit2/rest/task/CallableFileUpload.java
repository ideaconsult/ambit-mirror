package ambit2.rest.task;

import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.apache.commons.fileupload.FileItem;
import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * Asynchronous file upload. TODO - configure accepted formats
 * 
 * @author nina
 * 
 */
public abstract class CallableFileUpload implements Callable<Reference> {
    public static final String field_files = "files[]";
    public static final String field_config = "jsonconfig";
    protected List<FileItem> items;
    protected String[] filesUploadField;

    protected long maxSize = 5000000000L;

    public CallableFileUpload(List<FileItem> items, String[] fileUploadField) {
	this.items = items;
	this.filesUploadField = fileUploadField;

    }

    public Reference call() throws Exception {
	try {
	    // Process only the uploaded item called "fileToUpload" and
	    // save it on disk
	    boolean found = false;
	    Hashtable<String, String> properties = new Hashtable<String, String>();

	    for (final Iterator<FileItem> it = items.iterator(); it.hasNext();) {
		// && !found;) {
		FileItem fi = it.next();
		if (fi.isFormField()) {

		    if ((fi.getFieldName() != null) && (fi.getString() != null))
			properties.put(fi.getFieldName(), fi.getString());
		    continue;
		}
		if (fi.getSize() > maxSize) {
		    throw new ResourceException(new Status(Status.CLIENT_ERROR_BAD_REQUEST, String.format(
			    "File size %d > max allowed size %d", fi.getSize(), maxSize)));
		}
		if (Arrays.binarySearch(filesUploadField, fi.getFieldName())>=0) {
		    if (fi.getSize() == 0)
			throw new ResourceException(new Status(Status.CLIENT_ERROR_BAD_REQUEST, "Empty file!"));
		    File file = null;
		    String description;
		    if (fi.getName() == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "File name can't be empty!");
		    else {
			// stupid File class ...
			int lastIndex = fi.getName().lastIndexOf("\\");
			if (lastIndex < 0)
			    lastIndex = fi.getName().lastIndexOf("/");
			int extIndex = fi.getName().lastIndexOf(".");
			description = extIndex <= 0 ? fi.getName() : fi.getName().substring(0, extIndex);
			description = stripFileName(description);
			String ext = extIndex > 0 ? fi.getName().substring(extIndex) : "";
			file = new File(String.format("%s/www_%s%s", System.getProperty("java.io.tmpdir"), UUID
				.randomUUID().toString(), ext));
			file.deleteOnExit();
		    }
		    fi.write(file);

		    processFile(fi.getFieldName(), fi.getName(), file, description);
		}
	    }
	    processProperties(properties);
	    return createReference();
	} catch (ResourceException x) {
	    throw x;
	} catch (java.io.FileNotFoundException e) {
	    Context.getCurrentLogger().severe(e.getMessage());
	    throw new ResourceException(new Status(Status.CLIENT_ERROR_BAD_REQUEST, e));
	} catch (Exception e) {
	    Context.getCurrentLogger().severe(e.getMessage());
	    throw new ResourceException(new Status(Status.SERVER_ERROR_INTERNAL, e.getMessage()));
	} finally {

	}

    }

    public static String stripFileName(String fileName) {
	int lastIndex = fileName.lastIndexOf("\\");
	if (lastIndex < 0)
	    lastIndex = fileName.lastIndexOf("/");
	return lastIndex > 0 ? fileName.substring(lastIndex + 1) : fileName;

    }

    protected void processFile(String fieldname, File file, String description) throws Exception {
    };

    protected void processFile(String fieldName, String originalname, File file, String description) throws Exception {
	processFile(fieldName, file, description);
    };

    protected void processProperties(Hashtable<String, String> properties) throws Exception {
    };

    public abstract Reference createReference();

}
