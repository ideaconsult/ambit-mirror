package ambit2.base.data.substance;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.study.Protocol;

public class SubstancePropertyCategory extends SubstanceProperty {

    public SubstancePropertyCategory(String topcategory, String endpointCategory, String name, String reference) {
	super(topcategory, endpointCategory, name, reference);
    }

    public SubstancePropertyCategory(String topcategory, String endpointCategory, String name, String units,
	    ILiteratureEntry reference) {
	super(topcategory, endpointCategory, name, units, reference);

    }

    /**
     * 
     */
    private static final long serialVersionUID = -4004185521269367373L;

    @Override
    public String getRelativeURI() {
	try {
	    //ui-matrix.js expects /property/%s/%s/%s - * is placeholder here until fixed
	    return String.format("/property/%s/%s/*", URLEncoder.encode(topcategory == null ? "TOX" : topcategory,
		    "UTF-8"), URLEncoder.encode(
		    endpointcategory == null ? Protocol._categories.UNKNOWN_TOXICITY_SECTION.name() : endpointcategory,
		    "UTF-8"));
	} catch (UnsupportedEncodingException x) {
	    return "/property";
	}
    }
}
