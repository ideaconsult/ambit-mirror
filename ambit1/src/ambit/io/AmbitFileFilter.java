/**
 * <b>Filename</b> toxTreeFileFilter.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-8-4
 * <b>Project</b> toxTree
 */
package ambit.io;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Customized file filter
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-8-4
 */
public class AmbitFileFilter extends FileFilter {
    protected String extension = ".sdf";
    protected String description = "SDF file";
    public AmbitFileFilter() {
        super();
	}    
    /**
     * 
     */
    public AmbitFileFilter(String ext, String description) {
        super();
        this.extension = ext.toLowerCase();
        this.description = description;
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    public boolean accept(File f) {
		if (f.isDirectory()) return true;
		else return (f.getName().toLowerCase().endsWith(extension));
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        return description;
    }
	public static String getSuffix(String fname) {
	    String suffix = null;
	    int i = fname.lastIndexOf('.');

	    if(i > 0 && i < fname.length() - 1)
	      suffix = fname.substring(i).toLowerCase();

	    return suffix;
	  }
	
	  public static String getSuffix(File f) {
	    return getSuffix(f.getPath());
	  }
	public File changeExtension(File file) {
	    String suffix = null;
	    String fname= file.getAbsolutePath(); 
	    int i = fname.lastIndexOf('.');

	    if(i > 0 && i < fname.length() - 1)
	    	if (fname.substring(i).toLowerCase().equals(extension)) return file;
	    
	    return new File(fname+extension);
	}
}

