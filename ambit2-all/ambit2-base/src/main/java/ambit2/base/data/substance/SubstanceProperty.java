package ambit2.base.data.substance;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.study.Protocol;

import com.google.common.base.Objects;

public class SubstanceProperty extends Property {
	protected boolean extendedURI = false;
	public boolean isExtendedURI() {
		return extendedURI;
	}
	public void setExtendedURI(boolean extendedURI) {
		this.extendedURI = extendedURI;
	}
	protected String topcategory;
	protected String endpointcategory;
	
	protected String identifier;
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 4436949404781828869L;
	public SubstanceProperty(String topcategory,String endpointCategory,String name, String reference) {
		this(topcategory,endpointCategory,name,null,LiteratureEntry.getInstance(reference,""));
	}
	
	public SubstanceProperty(String topcategory,String endpointCategory,String name, String units, ILiteratureEntry reference) {
		super(name,units,reference);
		setEnabled(true);
		setTopcategory(topcategory);
		setEndpointcategory(endpointCategory);
		setLabel(String.format("http://www.opentox.org/echaEndpoints.owl#%s",
				endpointCategory==null?"UNKNOWN_TOXICITY":endpointcategory.replace("_SECTION", "")));
	}
	@Override
	public String getRelativeURI() {
		try {
		return String.format("/property/%s/%s%s%s/%s%s%s",
				URLEncoder.encode(topcategory==null?"TOX":topcategory,"UTF-8"),
				URLEncoder.encode(						
						endpointcategory==null?Protocol._categories.UNKNOWN_TOXICITY_SECTION.name():endpointcategory,
						"UTF-8"),
				getName()==null?"":"/",		
				URLEncoder.encode(getName(),"UTF-8"),
				identifier==null?
						UUID.nameUUIDFromBytes((getName() + getTitle()).toString().getBytes()).toString()
						:identifier,
				extendedURI?"/":"",
				extendedURI?URLEncoder.encode(UUID.nameUUIDFromBytes(reference.getTitle().getBytes()).toString()):""
						);
		} catch (UnsupportedEncodingException x) {
			return "/property";
		}
	}
	@Override
    public int hashCode() {
		return Objects.hashCode(topcategory,endpointcategory,getTitle(),getName());
    }	

	public String getTopcategory() {
		return topcategory;
	}
	public void setTopcategory(String topcategory) {
		this.topcategory = topcategory;
	}

	public String getEndpointcategory() {
		return endpointcategory;
	}
	public void setEndpointcategory(String endpointcategory) {
		this.endpointcategory = endpointcategory;
	}

}