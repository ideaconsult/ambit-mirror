/**
 * Created on 2005-3-21
 *
 */
package ambit2.core.data;


/**
 * A Literature entry: <br>
 * title, URL
 *   
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2008-4-20
 */
public class LiteratureEntry extends AmbitBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3089173811051934066L;
	public final static String p_title="title";
	public final static String p_url="URL";
	protected String title;
	protected String URL;
    protected int id = -1;
    protected boolean editable;
	public LiteratureEntry() {
		this("","");
		
	}
	public LiteratureEntry(String title) {
		this(title,"");
		
	}	


	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String url) {
		URL = url;
	}
	public LiteratureEntry(String title, String url) {
		setTitle(title);
		setURL(url);
	}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getTitle());
		buf.append(getURL());
		return buf.toString();
	}
    public int getId() {
        return id;
    }
    public String getName() {
        return getTitle();
    }
    public boolean hasID() {
        return id > 0;
    }
    public void setId(int id) {
        this.id = id;
        
    }


}
