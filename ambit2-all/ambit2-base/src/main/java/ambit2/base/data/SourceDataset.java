/**
 * Created on 2005-3-28
 *
 */
package ambit2.base.data;




/**
 * A dataset
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2008-4-20
 */
public class SourceDataset extends AmbitBean implements Comparable<SourceDataset>, ISourceDataset{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6881106188176017447L;
	protected ILiteratureEntry reference = null;
	protected int stars = 0;
	protected String licenseURI;
	public String getLicenseURI() {
		return licenseURI;
	}
	public void setLicenseURI(String license) {
		this.licenseURI = license;
	}
	
	protected String rightsHolder;
	protected String maintainer;

	public String getMaintainer() {
		return maintainer;
	}
	public void setMaintainer(String maintainer) {
		this.maintainer = maintainer;
	}
	@Override
	public String getrightsHolder() {
		return rightsHolder;
	}
	@Override
	public void setrightsHolder(String uri) {
		this.rightsHolder = uri;
		
	}
	protected int id;
    protected String name;
    protected String userName;
	protected static final String[] names={
		"Name","User name"};	
	/**
	 * 
	 */
	public SourceDataset() {
		this("");
	}
	public SourceDataset(String name) {
		this(name,LiteratureEntry.getInstance());
	}
	/**
	 * @param name
	 */
	public SourceDataset(String name, ILiteratureEntry reference) {
		super();
		setName(name);
		setReference(reference);
	}

	public ILiteratureEntry getReference() {
		return reference;
	}
	public void setReference(ILiteratureEntry reference) {
		this.reference = reference;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setUsername(String name) {
		this.userName = name;
	}	
	public String getName() {
		return name;
	}
	public String getUsername() {
		return userName;
	}
	/* (non-Javadoc)
     * @see ambit2.data.AmbitObject#toString()
     */
    public String toString() {
        return getName()==null?Integer.toString(getId()):getName();
    }
    public int getId() {
        return id;
    }
    public boolean hasID() {
        return id > 0;
    }
    public void setId(int id) {
        this.id = id;
        
    }
    public int compareTo(SourceDataset o) {
        return getName().compareTo(o.getName());
    }
    @Override
    public boolean equals(Object o) {
    	if (o instanceof SourceDataset) 
    		return compareTo((SourceDataset)o)==0;
    	return false;
    }
    public String getTitle() {
    	if (reference == null) reference = LiteratureEntry.getInstance(getTitle(),getURL());
    	return reference.getTitle();
    }
    public String getURL() {
    	if (reference == null) reference = LiteratureEntry.getInstance(getTitle());
    	return reference.getURL();
    }    
    public void setTitle(String title) {
    	reference = LiteratureEntry.getInstance(title,getURL());
    }
    public void setURL(String url) {
    	reference = LiteratureEntry.getInstance(getTitle(),url);
    }
    //new interface
    @Override
    public int getID() {
    	return getId();
    }
    @Override
    public void setID(int id) {
    	setId(id);
    }
    @Override
    public String getSource() {
    	return getTitle();
    }
    @Override
    public void setSource(String name) {
    	setTitle(name);
    	
    }
    public int getStars() {
    	return stars;
    }
    public void setStars(int rating) {
    	this.stars = rating;
    }
}
