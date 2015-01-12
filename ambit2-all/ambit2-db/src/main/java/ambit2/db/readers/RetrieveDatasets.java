package ambit2.db.readers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.StringCondition;
import ambit2.db.update.dataset.ReadDataset;

public class RetrieveDatasets extends AbstractQuery<IStructureRecord,SourceDataset,StringCondition,SourceDataset>  implements IQueryRetrieval<SourceDataset>{
    
    public static final String select_datasets_bystruc = "SELECT id_srcdataset,name,src_dataset.user_name,idreference,title,url,licenseURI,rightsHolder,stars,maintainer FROM structure join  struc_dataset using(idstructure) join src_dataset using(id_srcdataset) join catalog_references using(idreference) where %s order by name";
    
    public static final String select_WHERE = "where name %s ?";
    public static final String select_AND = " name %s ?";
    public static final String select_structure = " idstructure = ? ";
    public static final String select_chemical = " idchemical = ? ";
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
		String and = "";
		if (getFieldname()!= null) {
			StringBuilder b = new StringBuilder();
			if (getFieldname().getIdchemical()>0) {b.append(select_chemical); and = " and "; }
			if (getFieldname().getIdstructure()>0) {b.append(and); b.append(select_structure); and = " and ";  }
			if (getValue()!=null) {b.append(and); b.append(String.format(select_AND,getCondition().getSQL())); }
			return String.format(select_datasets_bystruc,b.toString());
		}
		else return 
			String.format(ReadDataset.select_datasets,(getValue()==null)?"":String.format(select_WHERE,getCondition().getSQL()));
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> p = new ArrayList<QueryParam>();
		if (getFieldname()!=null) {
			if (getFieldname().getIdchemical()>0)
				p.add(new QueryParam<Integer>(Integer.class,getFieldname().getIdchemical()));
			if (getFieldname().getIdstructure()>0)
				p.add(new QueryParam<Integer>(Integer.class,getFieldname().getIdstructure()));
		}
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
