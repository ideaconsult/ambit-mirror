/**
 * Created on 2005-3-21
 *
 */
package ambit2.data.literature;

import ambit2.data.qmrf.QMRFAttributes;

/**
 * A Literature entry: <br>
 * title, URL
 *   
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2008-4-20
 */
public class LiteratureEntry extends QMRFAttributes {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3089173811051934066L;
	protected static final String[] names={
		"Title","URL"};
	public LiteratureEntry() {
		this("","");
		
	}
	public LiteratureEntry(String title) {
		this(title,"");
		
	}	
	public String getURL() {
		return get(names[1]);
	}
	public void setURL(String url) {
		put(names[1],url);
	}
	public String getTitle() {
		return get(names[0]);
	}
	public void setTitle(String title) {
		put(names[0],title);
	}

	public LiteratureEntry(String title, String url) {
		setNames(names);
		setTitle(title);
		setURL(url);
	}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getTitle());
		buf.append(getURL());
		return buf.toString();
	}


}
