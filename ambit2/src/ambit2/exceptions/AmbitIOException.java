/*
 * Created on 2005-12-18
 *
 */
package ambit2.exceptions;

/**
 * IO Exception
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2005-12-18
 */
public class AmbitIOException extends AmbitException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1062271543737500261L;

    
    protected String filename="";
    

	/**
     * 
     */
    public AmbitIOException() {
    }

    /**
     * @param arg0
     */
    public AmbitIOException(String arg0) {
        super(arg0);
    }
    public AmbitIOException(String arg0,String filename) {
        super(arg0);
        setFilename(filename);
    }
    /**
     * @param arg0
     */
    public AmbitIOException(Throwable arg0) {
        super(arg0);
    }
    public AmbitIOException(Throwable arg0,String filename) {
        super(arg0);
        setFilename(filename);
    }
    /**
     * @param arg0
     * @param arg1
     */
    public AmbitIOException(String arg0, Throwable arg1) {
        this(arg0, arg1,"");
    }
    public AmbitIOException(String arg0, Throwable arg1,String filename) {
        super(arg0, arg1);
        setFilename(filename);
    }
    public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
    
}
