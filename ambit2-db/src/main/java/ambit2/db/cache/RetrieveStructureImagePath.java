package ambit2.db.cache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

/**
 * Cached images
 * @author nina
 *
 */
public class RetrieveStructureImagePath extends RetrieveStructure  {
	/**
	 * 
	 */
	protected static String s_text = "text";
	protected static String s_idquery = "idquery";	
	public static final String IMAGES= "IMAGES";
	public static final String IMAGE_PATH = "image/png";
	private static final long serialVersionUID = -2535919053818523334L;
	public static final String sqlField=
		"select structure.idstructure,idchemical,uncompress(structure) as ustructure,format,type_structure,atomproperties,text,idquery from structure\n"+
		"join query_results using(idchemical,idstructure)\n"+
		"join `query` using(idquery)\n"+
		"join sessions using(idsessions)\n"+
		"where\n"+
		"sessions.title=\"image/png\" and\n"+
		"query.name=? and\n"+
		"structure.%s =?\n"+
		"union\n"+
		"SELECT idstructure,idchemical,uncompress(structure) as ustructure,format,type_structure,atomproperties,null,null FROM structure\n"+
		"where\n"+
		"structure.%s =?\n"+
		"order by type_structure desc";
	//TODO order by requires filesort
	protected String queryName = null;
	public String getQueryName() {
		return queryName;
	}
	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
	public RetrieveStructureImagePath() {
		this(null);
		
	}
	public RetrieveStructureImagePath(IStructureRecord record) {
		super();
		setValue(record);
		setCondition(EQCondition.getInstance());
	}
	public String getSQL() throws AmbitException {
		return String.format(sqlField,getFieldname()?"idchemical":"idstructure",getFieldname()?"idchemical":"idstructure");
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class,getQueryName()));
		params.add(new QueryParam<Integer>(Integer.class, getFieldname()?getValue().getIdchemical():getValue().getIdstructure()));
		params.add(new QueryParam<Integer>(Integer.class, getFieldname()?getValue().getIdchemical():getValue().getIdstructure()));
		return params;		
	}	

    public IStructureRecord getObject(ResultSet rs) throws AmbitException {
        try {
            IStructureRecord r = super.getObject(rs);
            try {
                r.setProperty(Property.getInstance(IMAGE_PATH,IMAGE_PATH), rs.getString(s_text));
            } catch (SQLException x){
                r.removeProperty(Property.getInstance(IMAGE_PATH,IMAGE_PATH));
            }
            try {
            	String t = rs.getString(5);
            	for (STRUC_TYPE type : STRUC_TYPE.values()) if (type.toString().equals(t)) {
            		r.setType(type);
            		break;
            	}
            } catch (Exception x) {
            	r.setType(STRUC_TYPE.NA);
            }            
            return r;
        } catch (AmbitException x){
        	throw x;
        } catch (Exception x){
        	throw new AmbitException(x);
        }    	

    }
}
