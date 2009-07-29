package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.SourceDataset;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

public class RetrieveDatasets extends AbstractQuery<IStructureRecord,SourceDataset,EQCondition,SourceDataset>  implements IQueryRetrieval<SourceDataset>{
    public static final String select_datasets = "SELECT id_srcdataset,name,user_name,idreference,title,url FROM src_dataset join catalog_references using(idreference) %s order by name";
    public static final String select_datasets_bystruc = "SELECT id_srcdataset,name,user_name,idreference,title,url FROM struc_dataset join src_dataset using(id_srcdataset) join catalog_references using(idreference) where idstructure=? %s order by name";
    
    public static final String select_WHERE = "where name=?";
    public static final String select_AND = "and name=?";

	/**
	 * 
	 */
	private static final long serialVersionUID = -7226561858311078951L;

	public RetrieveDatasets(IStructureRecord record, SourceDataset dataset) {
		super();
		setFieldname(record);
		setValue(dataset);
	}
	public RetrieveDatasets() {
		this(null,null);
	}	
	public String getSQL() throws AmbitException {
		return 
		(getFieldname()!=null)
				?String.format(select_datasets_bystruc,(getValue()==null)?"":select_AND)
				:String.format(select_datasets,(getValue()==null)?"":select_WHERE);
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> p = new ArrayList<QueryParam>();
		if (getFieldname()!=null)
			p.add(new QueryParam<Integer>(Integer.class,getFieldname().getIdstructure()));

		if (getValue() != null)
			p.add(new QueryParam<String>(String.class,getValue().getName()));
		return p;
	}

	public SourceDataset getObject(ResultSet rs) throws AmbitException {
		try {
			
	        LiteratureEntry le = LiteratureEntry.getInstance(rs.getString(5),rs.getString(6));
	        le.setId(rs.getInt(4));
	        SourceDataset d = new SourceDataset(rs.getString(2),le);
	        d.setUsername(rs.getString(3));
	        d.setId(rs.getInt(1));
	        return d;
        } catch (SQLException x) {
        	throw new AmbitException(x);
        }
    }
	public double calculateMetric(SourceDataset object) {
		return 1;
	}
	public boolean isPrescreen() {
		return false;
	}
	@Override
	public String toString() {
		return
		(getFieldname()!=null)
		?"Datasets per compound"
		:(getValue()!=null?getValue().getName():"Datasets");
	}
}
