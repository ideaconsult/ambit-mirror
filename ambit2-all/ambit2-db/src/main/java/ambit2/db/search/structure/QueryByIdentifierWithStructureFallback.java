package ambit2.db.search.structure;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.key.CASKey;
import ambit2.core.processors.structure.key.DSSToxCID;
import ambit2.core.processors.structure.key.DSSToxGenericSID;
import ambit2.core.processors.structure.key.DSSToxRID;
import ambit2.core.processors.structure.key.EINECSKey;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.core.processors.structure.key.InchiKey;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.PropertyNameKey;
import ambit2.core.processors.structure.key.PubchemCID;
import ambit2.core.processors.structure.key.PubchemSID;
import ambit2.core.processors.structure.key.SmilesKey;
import ambit2.db.IStoredProcStatement;
import ambit2.db.search.StringCondition;

/**
 * Calls findByProperty stored procedure
 * @author nina
 *
 */
public class QueryByIdentifierWithStructureFallback  extends AbstractStructureQuery<IStructureKey,IStructureRecord,StringCondition> 
						implements IStoredProcStatement { 
	private enum storedproc_search_mode {
		name,
		alias,
		idproperty,
		any,
		inchi,
		inchikey
	}
	/**
     * 
     */
    private static final long serialVersionUID = -2227075383236154179L;
    protected final String sql = "{call findByProperty(?,?,?,?,?,?,?,?,?)}";
    /*
   IN chemical_id INT, //if >0  only retrieves the preferred structure
   IN search_mode INT,  -- 0 : name ; 1 : alias; 2: idproperty ; 3: any ; 4: inchi ; 5: inchikey
   IN property_name VARCHAR(255),
   IN property_alias VARCHAR(255),
   IN property_id INT,
   IN query_value TEXT,  -- string value or inchi
   IN query_value_num DOUBLE,
   IN query_inchi TEXT,
   IN maxrows INT)     * 
    call findByProperty(
    		0,		3, 		
    		"http://www.opentox.org/api/1.1#CASRN","",0,
    		"testid",0,"InChI=1/C9H14N2O3/c1-4-9(5-2)6(12)10-8(14)11(3)7(9)13/h4-5H2,1-3H3,(H,10,12,14)",
    		0
    		);
    */		
	public QueryByIdentifierWithStructureFallback() {
		super();
		setFieldname(null);
		setCondition(StringCondition.getInstance("="));
	}
	
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}
	
	protected storedproc_search_mode getSearchMode() {
		IStructureKey key = getFieldname();
		if (key==null)
			return storedproc_search_mode.any;
		else if ((key instanceof InchiKey) || (key instanceof SmilesKey)) 
			return storedproc_search_mode.inchi;
		else if (key instanceof CASKey) {
			return storedproc_search_mode.any;
		} else if (key instanceof EINECSKey) {
				return storedproc_search_mode.any;		
		} else if (key instanceof DSSToxCID) {
			return storedproc_search_mode.name;		
		} else if (key instanceof DSSToxRID) {
			return storedproc_search_mode.name;		
		} else if (key instanceof DSSToxGenericSID) {
			return storedproc_search_mode.name;					
		} else if (key instanceof PubchemCID) {
			return storedproc_search_mode.name;
		} else if (key instanceof PubchemSID) {
			return storedproc_search_mode.name;
		} else if (key instanceof PropertyNameKey) {
			return storedproc_search_mode.any;			
		} else if (key instanceof PropertyKey) {
			return storedproc_search_mode.alias;
		} else return storedproc_search_mode.inchi;
	}

	protected int getSearchPropertyID() {
		return -1;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		storedproc_search_mode searchMode = getSearchMode();
		Object identifier = null;
		try {
			identifier = getFieldname()==null?null:getFieldname().process(getValue());
		} catch (Exception x) {
			identifier = null;
			searchMode = storedproc_search_mode.inchi;
		}
		Property key = getFieldname()==null?null:getFieldname().getQueryKey() instanceof Property?(Property)getFieldname().getQueryKey():null;
		
		List<QueryParam> params = new ArrayList<QueryParam>();
		//idchemical
		params.add(new QueryParam<Integer>(Integer.class, getValue().getIdchemical()));
		//searchmode
		params.add(new QueryParam<Integer>(Integer.class, searchMode.ordinal()));
		params.add(new QueryParam<String>(String.class, key==null?null:key.getName()));
		params.add(new QueryParam<String>(String.class, key==null?null:key.getLabel()));
		params.add(new QueryParam<Integer>(Integer.class, getSearchPropertyID()));
		
		params.add(new QueryParam<String>(String.class, identifier instanceof String?(String)identifier:null));
		params.add(new QueryParam<Double>(Double.class, identifier instanceof Number?((Number)identifier).doubleValue():null));
		params.add(new QueryParam<String>(String.class, getValue().getInchi()));
		params.add(new QueryParam<Integer>(Integer.class, (int)getPageSize()));
		
		return params;
	}
	
	@Override
	public void getStoredProcedureOutVars(CallableStatement statement)
			throws SQLException {
		
	}

	@Override
	protected boolean isPreferredStructure() {
		return getFieldname().useExactStructureID();
	}
}