package ambit2.db.update.bundle;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.dataset.AbstractReadDataset;

/**
 * Retrieve {@link SourceDataset} by id
 * 
 * @author nina
 * 
 */
public class ReadBundle extends AbstractReadDataset<String, SubstanceEndpointsBundle> {

    public static final String select_datasets = "SELECT idbundle as id_srcdataset,name,user_name,idreference,title,url,licenseURI,rightsHolder,stars,maintainer,created,description FROM bundle join catalog_references using(idreference) %s order by stars desc\n";
    /**
	 * 
	 */
    private static final long serialVersionUID = -5560670663328542819L;

    @Override
    protected SubstanceEndpointsBundle createObject(String title, LiteratureEntry le, String username) {
	SubstanceEndpointsBundle d = new SubstanceEndpointsBundle(title, le);
	d.setUsername(username);
	return d;
    }

    public List<QueryParam> getParameters() throws AmbitException {
	if (getValue() == null)
	    return null;
	List<QueryParam> params = new ArrayList<QueryParam>();
	if (getValue().getID() > 0)
	    params.add(new QueryParam<Integer>(Integer.class, getValue().getID()));
	else if (getValue().getName() != null)
	    params.add(new QueryParam<String>(String.class, getValue().getName()));
	else
	    throw new AmbitException("Undefined bundle");
	return params;
    }

    public String getSQL() throws AmbitException {
	return String.format(select_datasets, getValue() == null ? "" : getValue().getID() > 0 ? "where idbundle=?"
		: "where name=?");
    }

    @Override
    public String toString() {
	return (getValue() != null ? getValue().getID() > 0 ? String.format("/bundle/%d", getValue().getID()) : String
		.format("/bundle/%s", getValue().getName()) : "Bundle");
    }

    @Override
    public SubstanceEndpointsBundle getObject(ResultSet rs) throws AmbitException {
	SubstanceEndpointsBundle bundle = super.getObject(rs);
	try {
	    bundle.setCreated(rs.getTimestamp("created").getTime());
	} catch (Exception x) {
	}
	try {
	    bundle.setDescription(rs.getString("description"));
	} catch (Exception x) {
	}
	return bundle;
    }

}
