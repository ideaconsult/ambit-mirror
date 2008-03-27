/*
 * Created on 2005-12-18
 *
 */
package ambit2.exceptions;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2005-12-18
 */
public class AmbitIOException extends AmbitException {
    public static String MSG_EMPTYFILE = "File is empty\t";
    public static String MSG_OPENSUCCESS = "File opened\t";
    public static String MSG_SAVESUCCESS = "File saved\t";
    public static String MSG_ERRORONOPEN = "Error while opening file\t";
    public static String MSG_ERRORONSAVE = "Error while saving file\t";
    public static String MSG_UNSUPPORTEDFORMAT = "File format not supported\t";
    
    
    
    /**
     * 
     */
    public AmbitIOException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public AmbitIOException(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    public AmbitIOException(String arg0,String filename) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    /**
     * @param arg0
     */
    public AmbitIOException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    public AmbitIOException(Throwable arg0,String filename) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    /**
     * @param arg0
     * @param arg1
     */
    public AmbitIOException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }
    public AmbitIOException(String arg0, Throwable arg1,String filename) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }
}
