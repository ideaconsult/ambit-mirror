package ambit2.db.update.dataset;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;

/**
 * Retrieve {@link SourceDataset} by id
 * @author nina
 *
 */
public class ReadDataset extends AbstractReadDataset<String>{

	public static final String select_datasets = "SELECT id_srcdataset,name,user_name,idreference,title,url,licenseURI,rightsHolder,stars FROM src_dataset join catalog_references using(idreference) %s order by stars desc\n";
	/**
	 * 
	 */
	private static final long serialVersionUID = -5560670663328542819L;


	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null) return null;	
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getValue().getId()>0)
			params.add(new QueryParam<Integer>(Integer.class,getValue().getId()));
		else
			params.add(new QueryParam<String>(String.class,getValue().getName()));
		return params;
	}

	public String getSQL() throws AmbitException {
		return String.format(select_datasets,
				getValue()==null?
					"":
					getValue().getId()>0?"where id_srcdataset=?":"where name=?");
	}


	@Override
	public String toString() {
		return (getValue()!=null?getValue().getId()>0
				?String.format("/dataset/%d",getValue().getId())
				:String.format("/dataset/%s",getValue().getName())
				:"Datasets");
	}

}
