package ambit2.base.data.substance;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ReliabilityParams._r_flags;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class SubstanceProperty extends Property {
    protected _r_flags studyResultType;

    public _r_flags getStudyResultType() {
	return studyResultType;
    }

    public void setStudyResultType(_r_flags studyResultType) {
	this.studyResultType = studyResultType;
    }

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

    public SubstanceProperty(String topcategory, String endpointCategory, String name, String reference) {
	this(topcategory, endpointCategory, name, null, LiteratureEntry.getInstance(reference, ""));
    }

    public SubstanceProperty(String topcategory, String endpointCategory, String name, String units,
	    ILiteratureEntry reference) {
	super(name, units, reference);
	setEnabled(true);
	setTopcategory(topcategory);
	setEndpointcategory(endpointCategory);
	setLabel(String.format("http://www.opentox.org/echaEndpoints.owl#%s",
		endpointCategory == null ? "UNKNOWN_TOXICITY" : endpointcategory.replace("_SECTION", "")));
    }

    @Override
    public String getRelativeURI() {
	try {
	    String name = getName();
	    return String.format("/property/%s/%s%s%s/%s%s%s", 
		    URLEncoder.encode(topcategory == null ? "TOX"  : topcategory, "UTF-8"), 
		    URLEncoder.encode(endpointcategory == null ? Protocol._categories.UNKNOWN_TOXICITY_SECTION.name() : endpointcategory, "UTF-8"), 
		    name == null ? "" : "/", name == null ? "" : URLEncoder.encode(name, "UTF-8"),
		    identifier == null ? UUID.nameUUIDFromBytes((name + getTitle()).toString().getBytes()).toString() : identifier, 
		    (studyResultType==null?(extendedURI ? "/" : ""):("/"+ studyResultType.getIdentifier())),	    
                    extendedURI ? "/" : "",   
                    extendedURI ? URLEncoder.encode(UUID.nameUUIDFromBytes(reference.getTitle().getBytes()).toString())   : "");
	} catch (UnsupportedEncodingException x) {
	    return "/property";
	}
    }

    @Override
    public int hashCode() {
	return Objects.hashCode(topcategory, endpointcategory, getName(), identifier, getTitle());
    }

    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (o == null || getClass() != o.getClass())
	    return false;
	SubstanceProperty that = (SubstanceProperty) o;
	return Objects.equal(this.topcategory, that.topcategory)
		&& Objects.equal(this.endpointcategory, that.endpointcategory)
		&& Objects.equal(this.getName(), that.getName()) && Objects.equal(this.identifier, that.identifier)
		&& Objects.equal(this.getTitle(), that.getTitle());

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

    public String createHashedIdentifier(IParams conditions) {
	HashFunction hf = Hashing.sha1();
	StringBuilder b = new StringBuilder();
	b.append(getName() == null ? "" : getName());
	b.append(getUnits() == null ? "" : getUnits());
	b.append(conditions == null ? "" : conditions.toString());
	// System.out.println(b);
	/*
	 * % Degradation%{"Sampling time":{"unit":"d","loValue":7.0}} %
	 * Degradation%{"Sampling time":{ "unit":"d", "loValue":7}}
	 */
	HashCode hc = hf.newHasher().putString(b.toString(), Charsets.US_ASCII).hash();
	return hc.toString().toUpperCase();
    }
}