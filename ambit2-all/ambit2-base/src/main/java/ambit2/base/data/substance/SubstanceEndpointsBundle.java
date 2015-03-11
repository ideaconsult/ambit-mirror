package ambit2.base.data.substance;

import java.util.UUID;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.LiteratureEntry;

public class SubstanceEndpointsBundle implements ISourceDataset {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5712705809909948536L;
	protected ILiteratureEntry reference = null;
	protected int stars = 0;
	protected String licenseURI;
	protected String rightsHolder;
	protected String maintainer;
	protected String name;
	protected int id;
	protected String userName;
	protected String description;
	protected UUID bundle_number;
	protected String status = "draft";
	public String getStatus() {
	    return status;
	}
	public void setStatus(String status) {
	    this.status = status;
	}
	public String getUserName() {
	    return userName;
	}
	public void setUserName(String userName) {
	    this.userName = userName;
	}
	public UUID getBundle_number() {
	    return bundle_number;
	}
	public void setBundle_number(UUID bundle_number) {
	    this.bundle_number = bundle_number;
	}
	public int getVersion() {
	    return version;
	}
	public void setVersion(int version) {
	    this.version = version;
	}
	protected int version;
	public String getDescription() {
	    return description;
	}
	public void setDescription(String description) {
	    this.description = description;
	}
	protected long created;
	protected long updated;
	
	public long getUpdated() {
	    return updated;
	}
	public void setUpdated(long updated) {
	    this.updated = updated;
	}
	public long getCreated() {
	    return created;
	}
	public void setCreated(long created) {
	    this.created = created;
	}
	public SubstanceEndpointsBundle(int id) {
		this("");
		setID(id);
	}
	public SubstanceEndpointsBundle() {
		this("");
	}
	public SubstanceEndpointsBundle(String name) {
		this(name,LiteratureEntry.getInstance());
	}
	/**
	 * @param name
	 */
	public SubstanceEndpointsBundle(String name, ILiteratureEntry reference) {
		super();
		setName(name);
		setReference(reference);
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}

    public String getTitle() {
    	if (reference == null) reference = LiteratureEntry.getInstance("Default");
    	return reference.getTitle();
    }
    public String getURL() {
    	if (reference == null) reference = LiteratureEntry.getInstance("Default");
    	return reference.getURL();
    }    
    public void setTitle(String title) {
    	reference = LiteratureEntry.getInstance(title,getURL());
    }
    public void setURL(String url) {
    	reference = LiteratureEntry.getInstance(getTitle(),url);
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
	public String getLicenseURI() {
		return licenseURI;
	}
	public void setLicenseURI(String license) {
		this.licenseURI = license;
	}

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
	public ILiteratureEntry getReference() {
		return reference;
	}
	public void setReference(ILiteratureEntry reference) {
		this.reference = reference;
	}
	@Override
	public String toString() {
		return name==null?getClass().getName():name;
	}
}
