package ambit2.db.substance.study;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.substance.SubstanceEndpointsBundle;

public class UpdateEffectRecordsBundle extends UpdateEffectRecords {

	protected SubstanceEndpointsBundle bundle;
	public SubstanceEndpointsBundle getBundle() {
		return bundle;
	}
	public void setBundle(SubstanceEndpointsBundle bundle) {
		this.bundle = bundle;
	}
	
	private static final String[] create_sql_bundle = {
		"INSERT INTO bundle_substance_experiment (idbundle,document_prefix,document_uuid,endpoint,conditions,unit,loQualifier,loValue,upQualifier,upValue,textValue,errQualifier,err,endpointhash,topcategory,endpointcategory,substance_prefix,substance_uuid)\n"+
		"values(?,?,unhex(replace(?,'-','')),?,?,?,?,?,?,?,?,?,?,unhex(sha1(concat(ifnull(?,''),ifnull(?,''),ifnull(?,'')))),?,?,?,unhex(replace(?,'-','')))"
	};
	
	public String[] getSQL() throws AmbitException {
		check();
		return create_sql_bundle;
	}
	
	public UpdateEffectRecordsBundle(SubstanceEndpointsBundle bundle,String substanceUUID,
			ProtocolApplication<Protocol, Params, String, Params, String> papp,
			EffectRecord effect) {
		super(substanceUUID, papp, effect);
		setBundle(bundle);
	}
	
	@Override
	public void addParameters(int index, List<QueryParam> params1)
			throws AmbitException {
		params1.add(new QueryParam<Integer>(Integer.class, getBundle().getID()));
		super.addParameters(index, params1);
	}
	
	@Override
	protected void check() throws AmbitException {
		if (bundle==null || bundle.getID()<=0) throw new AmbitException("Bundle undefined");
		super.check();
	}


}
