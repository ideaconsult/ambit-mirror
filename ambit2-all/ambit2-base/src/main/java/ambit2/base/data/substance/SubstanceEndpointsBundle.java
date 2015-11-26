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
	protected String title;
	protected String url;
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
	protected int referenceID;

	public int getReferenceID() {
		return referenceID;
	}

	public void setReferenceID(int referenceID) {
		this.referenceID = referenceID;
	}

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
		this(null);
		setID(id);
	}

	public SubstanceEndpointsBundle() {
		this(null);
	}

	public SubstanceEndpointsBundle(String name) {
		super();
		setName(name);
	}

	/**
	 * @param name
	 */
	public SubstanceEndpointsBundle(String name, ILiteratureEntry reference) {
		this(name);
		if (reference != null) {
			setTitle(reference.getTitle());
			setURL(reference.getURL());
			setReferenceID(reference.getId());
		}
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
		return title;
	}

	public String getURL() {
		return url;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setURL(String url) {
		this.url = url;
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

	@Override
	public String toString() {
		return name == null ? getClass().getName() : name;
	}
}
