package ambit2.db.update.bundle.substance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.base.relation.composition.Proportion;

public class ReadSubstanceChemicalsUnionByBundle extends ReadSubstancesByBundle {

    /**
     * 
     */
    private static final long serialVersionUID = -5710747152479311181L;

    private static String sql_chemicals = "select idsubstance,prefix,hex(uuid) as huuid,documentType,format,substance.name,publicname,\n"
	    + "content,substanceType,substance.rs_prefix,hex(substance.rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid),null as idchemical\n"
	    + "from bundle_substance join substance using(idsubstance)	where idbundle=?\n"
	    + "union(\n"
	    + "select 	null,'CHEM',hex(idchemical) as huuid,\"/compound\",\"URI\",idchemical as name,idchemical as publicname,\n"
	    + "concat(\"/compound\",idchemical),'chemical',null,null,null,null,idchemical from bundle_chemicals where idbundle=?\n"
	    + "and idchemical not in (select idchemical from substance_relation join bundle_substance using(idsubstance))\n"
	    + ")";

    public ReadSubstanceChemicalsUnionByBundle(SubstanceEndpointsBundle bundle) {
	super(bundle);
    }

    @Override
    public String getSQL() throws AmbitException {
	return sql_chemicals;
    }

    @Override
    public List<QueryParam> getParameters() throws AmbitException {
	List<QueryParam> params1 = new ArrayList<QueryParam>();
	if (getFieldname() != null) {
	    params1.add(new QueryParam<Integer>(Integer.class, getFieldname().getID()));
	    params1.add(new QueryParam<Integer>(Integer.class, getFieldname().getID()));
	    return params1;
	}
	throw new AmbitException("Unspecified bundle");
    }

    @Override
    public SubstanceRecord getObject(ResultSet rs) throws AmbitException {
	try {

	    SubstanceRecord r = super.getObject(rs);
	    if (r.getIdsubstance() <= 0) {
		r.setIdchemical(rs.getInt("idchemical"));
		r.clear();
		List<CompositionRelation> relatedStructures = new ArrayList<CompositionRelation>();
		r.setRelatedStructures(relatedStructures);
		relatedStructures.add(new CompositionRelation(r, new StructureRecord(rs.getInt("idchemical"), 1, null,
			null), new Proportion()));
	    }
	    return r;
	} catch (SQLException x) {
	    throw new AmbitException(x);
	}
    }
}
