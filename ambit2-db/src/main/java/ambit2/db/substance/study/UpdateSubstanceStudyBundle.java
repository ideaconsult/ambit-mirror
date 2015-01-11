package ambit2.db.substance.study;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.substance.SubstanceEndpointsBundle;

/**
 * Write studies into bundle_substance_protocolapplication
 * @author nina
 *
 */
public class UpdateSubstanceStudyBundle extends UpdateSubstanceStudy {
	protected SubstanceEndpointsBundle bundle;
	public SubstanceEndpointsBundle getBundle() {
		return bundle;
	}
	public void setBundle(SubstanceEndpointsBundle bundle) {
		this.bundle = bundle;
	}
	private static final String[] create_sql_bundle = {
		"INSERT INTO bundle_substance_protocolapplication (idbundle,document_prefix,document_uuid,topcategory,endpointcategory,endpoint,guidance," +
		"substance_prefix,substance_uuid,params,interpretation_result,interpretation_criteria,reference,reference_year,reference_owner," +
		"reliability,isRobustStudy,isUsedforClassification,isUsedforMSDS,purposeFlag,studyResultType)\n" +
		"values(?,?,unhex(replace(?,'-','')),?,?,?,?,?,unhex(replace(?,'-','')),?,?,?,?,?,?,?,?,?,?,?,?) on duplicate key update\n"+
		"substance_prefix=values(substance_prefix),substance_uuid=values(substance_uuid),topcategory=values(topcategory),\n"+
		"endpointcategory=values(endpointcategory),endpoint=values(endpoint),guidance=values(guidance),params=values(params)," +
		"interpretation_result=values(interpretation_result),interpretation_criteria=values(interpretation_criteria)," +
		"reference=values(reference),reference_year=values(reference_year),reference_owner=values(reference_owner),reliability=values(reliability)," +
		"isRobustStudy=values(isRobustStudy),isUsedforClassification=values(isUsedforClassification)," +
		"isUsedforClassification=values(isUsedforClassification),purposeFlag=values(purposeFlag),studyResultType=values(studyResultType)" 
	};
	public UpdateSubstanceStudyBundle(SubstanceEndpointsBundle bundle,String substanceuuid,
			ProtocolApplication<Protocol, IParams, String, IParams, String> papp) {
		super(substanceuuid, papp);
		setBundle(bundle);
	}

	@Override
	public String[] getSQL() throws AmbitException {
		check();
		return create_sql_bundle;
	}
	@Override
	protected void check() throws AmbitException {
		if (bundle==null || bundle.getID()<=0) throw new AmbitException("Bundle undefined");
		super.check();
	}
	
	@Override
	public void addParameters(int index, List<QueryParam> params1)
			throws AmbitException {
		params1.add(new QueryParam<Integer>(Integer.class, getBundle().getID()));
		super.addParameters(index, params1);
	}
}
