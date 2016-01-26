package ambit2.db.update.bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.update.bundle.UpdateBundle._published_status;

public class ReadBundleVersion extends ReadBundle {

    /**
     * 
     */
    private static final long serialVersionUID = -988961563705675581L;
    public static final String select_datasets = "SELECT idbundle as id_srcdataset,name,user_name,idreference,title,url,licenseURI,rightsHolder,stars,maintainer,created,description,hex(bundle_number) bn,version,user_name,published_status,updated FROM bundle join catalog_references using(idreference) where bundle_number in (select bundle_number from bundle where idbundle=?)\n";

    public ReadBundleVersion(Set<_published_status> status) {
		super(status);
	}
    
    public List<QueryParam> getParameters() throws AmbitException {
	if (getValue() == null)
	    return null;
	List<QueryParam> params = new ArrayList<QueryParam>();
	if (getValue() != null && getValue().getID() > 0)
	    params.add(new QueryParam<Integer>(Integer.class, getValue().getID()));
	else
	    throw new AmbitException("Undefined bundle");
	return params;
    }

    public String getSQL() throws AmbitException {
	return select_datasets;
    }
}
