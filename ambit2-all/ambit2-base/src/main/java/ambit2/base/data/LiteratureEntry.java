/**
 * Created on 2005-3-21
 *
 */
package ambit2.base.data;

import ambit2.base.data.substance.SubstanceEndpointsBundle;




/**
 * A Literature entry: <br>
 * title, URL
 *   
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2008-4-20
 */
public class LiteratureEntry extends AmbitBean implements ILiteratureEntry{
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3089173811051934066L;
	public final static String p_title="title";
	public final static String p_url="URL";
	protected String title;
	protected String URL;
    public void setURL(String uRL) {
		URL = uRL;
	}
	protected int id = -1;
    protected boolean editable;
    protected _type type = _type.BibtexEntry;
    public _type getType() {
		return type;
	}
	public void setType(_type type) {
		this.type = type;
	}
	protected static String EINECS_name = "EINECS";
    protected static String IUPAC_name = "IUPAC name";
    protected static String Public_name = "Public name";
    protected static String Trade_name = "Trade name";
    protected static String OWNER_name = "Owner name";
    protected static String CAS_num = "CAS Registry Number";
    protected static String IUCLID5_UUID = "IUCLID5 UUID";
    protected static String DX = "DX";
    protected static String Default_name = "Default";
    protected static String AMBIT_uri = "http://ambit.sourceforge.net";
    protected static String EINECS_uri = "http://ec.europa.eu/environment/chemicals/exist_subst/einecs.htm";
    protected static String PROPERTIES_DX_uri = String.format("%s//descriptors.owl#%s",AMBIT_uri,DX);
/*
	private static java.util.concurrent.CopyOnWriteArrayList<LiteratureEntry> references = 
		new CopyOnWriteArrayList<LiteratureEntry>();
		*/

	public static synchronized LiteratureEntry getInstance() {
		return getInstance(Default_name,AMBIT_uri);
	}	
	public static synchronized LiteratureEntry getInstance(String name) {
		return getInstance(name,AMBIT_uri);
	}
	public static synchronized LiteratureEntry getCASReference() {
		return getInstance(CAS_num,AMBIT_uri);
	}
	public static synchronized LiteratureEntry getIUPACReference() {
		return getInstance(IUPAC_name,AMBIT_uri);
	}
	public static synchronized LiteratureEntry getTradeNameReference() {
		return getInstance(Trade_name,AMBIT_uri);
	}
	public static synchronized LiteratureEntry getI5UUIDReference() {
		return getInstance(IUCLID5_UUID,AMBIT_uri);
	}
	public static synchronized LiteratureEntry getOwnerNameReference() {
		return getInstance(OWNER_name,AMBIT_uri);
	}	
	public static synchronized LiteratureEntry getDXReference() {
		return getDXReference(DX);
	}
	public static synchronized LiteratureEntry getDXReference(String dx) {
		LiteratureEntry entry = getInstance(dx,PROPERTIES_DX_uri);
		entry.setType(_type.Dataset);
		return entry;
	}	
	
	public static synchronized LiteratureEntry getEINECSReference() {
		return getInstance(EINECS_name,EINECS_uri);
	}
	public static synchronized LiteratureEntry getBundleReference(SubstanceEndpointsBundle bundle) {
		LiteratureEntry et = new LiteratureEntry(String.format("/bundle/%d",bundle.getID()),String.format("/bundle/%d", bundle.getID()));
		et.setType(_type.Dataset);
		return et;
	}
	public static synchronized LiteratureEntry getInstance(String name,String url) {
		return getInstance(name,url,-1);
	}
	public static synchronized LiteratureEntry getInstance(String name,String url, int id) {
		LiteratureEntry et = new LiteratureEntry(name,url);
		et.setId(id);
		return et;
	}	    
	public String getTitle() {
		return title;
	}

	public String getURL() {
		return URL;
	}
	/**
	 * We need separate instances for web services
	 * @param title
	 * @param url
	 */
	public LiteratureEntry(String title, String url) {
		this.title = title==null?"NA":((title.length()>255)?title.substring(1,255):title);
		this.URL = url==null?null:(url.length()>255)?url.substring(1,255):url;
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
    @Override
    public boolean equals(Object obj) {
    	if (obj instanceof LiteratureEntry)
    		return ((LiteratureEntry)obj).getTitle().equals(getTitle());
    	else return false;
    }
    public int hashCode() {
    	int hash = 7;
    	int var_code = (null == getName() ? 0 : getTitle().hashCode());
    	hash = 31 * hash + var_code; 

    	return hash;
    }	    


}
