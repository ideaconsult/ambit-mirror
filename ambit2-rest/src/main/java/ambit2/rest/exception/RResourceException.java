package ambit2.rest.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

/**
 * Same as {@link ResourceException} , but with additional variant to go in the message body, 
 * as specified in OpenTox error reports.
 * @author nina
 *
 */
public class RResourceException extends ResourceException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6405322729021154626L;
	protected Variant variant;
	
	   public Variant getVariant() {
		return variant;
	}

	   /**
	    * The variant
	    * @param variant
	    */
	public void setVariant(Variant variant) {
		this.variant = variant;
	}

	public RResourceException(final int code,Variant variant) {
	        super(code);
	        setVariant(variant);
	    }

	    /**
	     * Constructor.
	     * 
	     * @param code
	     *            The specification code of the encapsulated status.
	     * @param name
	     *            The name of the encapsulated status.
	     * @param description
	     *            The description of the encapsulated status.
	     * @param uri
	     *            The URI of the specification describing the method.
	     */
	    public RResourceException(final int code, final String name,
	            final String description, final String uri,Variant variant) {
	        super(code,name,description,uri);
	        setVariant(variant);
	    }

	    /**
	     * Constructor.
	     * 
	     * @param code
	     *            The specification code of the encapsulated status.
	     * @param name
	     *            The name of the encapsulated status.
	     * @param description
	     *            The description of the encapsulated status.
	     * @param uri
	     *            The URI of the specification describing the method.
	     * @param cause
	     *            The wrapped cause error or exception.
	     */
	    public RResourceException(final int code, final String name,
	            final String description, final String uri, final Throwable cause,Variant variant) {
	        super(code,name,description,uri,cause);
	        setVariant(variant);
	    }

	    /**
	     * Constructor.
	     * 
	     * @param code
	     *            The specification code of the encapsulated status.
	     * @param cause
	     *            The wrapped cause error or exception.
	     */
	    public RResourceException(final int code, final Throwable cause,Variant variant) {
	        super(code,cause);
	        setVariant(variant);
	    }

	    /**
	     * Constructor.
	     * 
	     * @param status
	     *            The status to associate.
	     */
	    public RResourceException(final Status status,Variant variant) {
	        super(status);
	        setVariant(variant);
	    }

	    /**
	     * Constructor.
	     * 
	     * @param status
	     *            The status to copy.
	     * @param description
	     *            The description of the encapsulated status.
	     */
	    public RResourceException(final Status status, final String description,Variant variant) {
	        super(status,description);
	        setVariant(variant);
	    }

	    /**
	     * Constructor.
	     * 
	     * @param status
	     *            The status to copy.
	     * @param description
	     *            The description of the encapsulated status.
	     * @param cause
	     *            The wrapped cause error or exception.
	     */
	    public RResourceException(final Status status, final String description,
	            final Throwable cause,Variant variant) {
	        super(status,description,cause);
	        setVariant(variant);
	    }

	    /**
	     * Constructor.
	     * 
	     * @param status
	     *            The status to associate.
	     * @param cause
	     *            The wrapped cause error or exception.
	     */
	    public RResourceException(final Status status, final Throwable cause,Variant variant) {
	        super(status,cause);
	        setVariant(variant);
	    }

	    /**
	     * Constructor that set the status to
	     * {@link org.restlet.data.Status#SERVER_ERROR_INTERNAL} including the
	     * related error or exception.
	     * 
	     * @param cause
	     *            The wrapped cause error or exception.
	     */
	    public RResourceException(final Throwable cause,Variant variant) {
	        super(cause);
	        setVariant(variant);
	    }
	    
	    public Representation getRepresentation() {
	    	//todo : generate RDF error report
	    	StringWriter w = new StringWriter();
	    	if (getStatus().getThrowable()!=null) 
	    		getStatus().getThrowable().printStackTrace(new PrintWriter(w));
	    	else w.append(toString());
	    	return new StringRepresentation(w.toString(),MediaType.TEXT_PLAIN);	
	    }
}
