package ambit2.db.update.bundle.chemicals;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.db.search.EQCondition;
import ambit2.db.search.structure.AbstractStructureQuery;

public class ReadChemicalsByBundle  extends AbstractStructureQuery<SubstanceEndpointsBundle,StructureRecord,EQCondition> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3139508348246513746L;
	/**
	 * 
	 */
	private static String sql = 
		"select idbundle,idchemical,smiles,formula,inchi,inchikey,tag,remarks from chemicals join bundle_chemicals using(idchemical) where idbundle=?";
	
	protected boolean enableFeatures = false;
	public boolean isEnableFeatures() {
		return enableFeatures;
	}
	public void setEnableFeatures(boolean enableFeatures) {
		this.enableFeatures = enableFeatures;
	}
	public ReadChemicalsByBundle() {
		super();
	}
	public ReadChemicalsByBundle(SubstanceEndpointsBundle bundle) {
		super();
		setFieldname(bundle);
	}
	
	
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		if (getFieldname()!=null) {
			params1.add(new QueryParam<Integer>(Integer.class, getFieldname().getID()));
			return params1;
		}
		throw new AmbitException("Unspecified bundle");
	}
	@Override
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			StructureRecord record = new StructureRecord();
			record.setIdchemical(rs.getInt(2));
			record.setIdstructure(-1);
			record.setType(STRUC_TYPE.D2noH);
			record.setFormat(MOL_TYPE.INC.name());
			record.setContent(rs.getString("inchi"));			
			record.setUsePreferedStructure(true);
			record.setInchi(rs.getString("inchi"));
			record.setInchiKey(rs.getString("inchikey"));
			record.setSmiles(rs.getString("smiles"));
			record.setFormula(rs.getString("formula"));
			if (enableFeatures) {
				LiteratureEntry reference = LiteratureEntry.getBundleReference(fieldname);			
				String value = rs.getString("remarks");
				if (value!=null)  {
					Property tag = new Property("tag",reference);
					tag.setEnabled(true);
					record.setProperty(tag,rs.getString("tag"));
				}
				
				value = rs.getString("remarks");
				if (value !=null) {
					Property remarks = new Property("remarks",reference);
					remarks.setEnabled(true);
					record.setProperty(remarks,rs.getString("remarks"));
				}	
			}
			return record;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}



}
