package ambit2.db.readers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.SourceDataset;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;
import ambit2.db.update.dataset.ReadDataset;

public class RetrieveDatasets extends AbstractQuery<IStructureRecord,SourceDataset,StringCondition,SourceDataset>  implements IQueryRetrieval<SourceDataset>{
    
    public static final String select_datasets_bystruc = "SELECT id_srcdataset,name,user_name,idreference,title,url FROM struc_dataset join src_dataset using(id_srcdataset) join catalog_references using(idreference) where idstructure=? %s order by name";
    
    public static final String select_WHERE = "where name %s ?";
    public static final String select_AND = "and name %s ?";
    protected ReadDataset readDataset = new ReadDataset();
	/**
	 * 
	 */
	private static final long serialVersionUID = -7226561858311078951L;

	public RetrieveDatasets(IStructureRecord record, SourceDataset dataset) {
		super();
		setFieldname(record);
		setValue(dataset);
		setCondition(StringCondition.getInstance("="));
	}
	public RetrieveDatasets() {
		this(null,null);
	}	
	public String getSQL() throws AmbitException {
		return 
		(getFieldname()!=null)
				?String.format(select_datasets_bystruc,(getValue()==null)?"":String.format(select_AND,getCondition().getSQL()))
				:String.format(ReadDataset.select_datasets,(getValue()==null)?"":String.format(select_WHERE,getCondition().getSQL()));
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
		return readDataset.getObject(rs);
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
