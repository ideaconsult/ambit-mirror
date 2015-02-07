package ambit2.base.data;

import java.util.ArrayList;

public class PropertyAnnotations extends ArrayList<PropertyAnnotation> {

    /**
	 * 
	 */
    private static final long serialVersionUID = -1776802858368833193L;

    public PropertyAnnotations() {

    }

    public String toJSON() {
	StringBuilder b = new StringBuilder();
	String acomma = "";
	for (int i=0; i < size(); i++)
	    try {
		b.append(acomma);
		get(i).toJSON(b);
		acomma = ",";
	    } catch (Exception x) {

		return null;
	    }
	return b.toString();
    }
}
