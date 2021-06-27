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

	private static final String QUERY_PARAM_GENERIC = "?";
	private static final String QUERY_PARAM_UUID = "UNHEX(REPLACE(?, '-', ''))";
	private static final String QUERY_PARAM_TS = "COALESCE(?, CURRENT_TIMESTAMP)";

	private static final String[] create_sql_bundle = {
		String.join(" ",
			"INSERT INTO bundle_substance_protocolapplication (",
			String.join(", ",
				"idbundle",
				"document_prefix",
				"document_uuid",
				"topcategory",
				"endpointcategory",
				"endpoint",
				"guidance",
				"substance_prefix",
				"substance_uuid",
				"params",
				"interpretation_result",
				"interpretation_criteria",
				"reference",
				"reference_year",
				"reference_owner",
				"reliability",
				"isRobustStudy",
				"isUsedforClassification",
				"isUsedforMSDS",
				"purposeFlag",
				"studyResultType",
				"investigation_uuid",
				"assay_uuid",
				"updated"
			),
			") VALUES (",
			String.join(", ",
				QUERY_PARAM_GENERIC,  // idbundle
				QUERY_PARAM_GENERIC,  // document_prefix
				QUERY_PARAM_UUID,     // document_uuid
				QUERY_PARAM_GENERIC,  // topcategory
				QUERY_PARAM_GENERIC,  // endpointcategory
				QUERY_PARAM_GENERIC,  // endpoint
				QUERY_PARAM_GENERIC,  // guidance
				QUERY_PARAM_GENERIC,  // substance_prefix
				QUERY_PARAM_UUID,     // substance_uuid
				QUERY_PARAM_GENERIC,  // params
				QUERY_PARAM_GENERIC,  // interpretation_result
				QUERY_PARAM_GENERIC,  // interpretation_criteria
				QUERY_PARAM_GENERIC,  // reference
				QUERY_PARAM_GENERIC,  // reference_year
				QUERY_PARAM_GENERIC,  // reference_owner
				QUERY_PARAM_GENERIC,  // reliability
				QUERY_PARAM_GENERIC,  // isRobustStudy
				QUERY_PARAM_GENERIC,  // isUsedforClassification
				QUERY_PARAM_GENERIC,  // isUsedforMSDS
				QUERY_PARAM_GENERIC,  // purposeFlag
				QUERY_PARAM_GENERIC,  // studyResultType
				QUERY_PARAM_UUID,     // investigation_uuid
				QUERY_PARAM_UUID,     // assay_uuid
				QUERY_PARAM_TS        // updated
			),
			") ON DUPLICATE KEY UPDATE",
			String.join(", ",
				"substance_prefix = VALUES(substance_prefix)",
				"substance_uuid = VALUES(substance_uuid)",
				"topcategory = VALUES(topcategory)",
				"endpointcategory = VALUES(endpointcategory)",
				"endpoint = VALUES(endpoint)",
				"guidance = VALUES(guidance)",
				"params = VALUES(params)",
				"interpretation_result = VALUES(interpretation_result)",
				"interpretation_criteria = VALUES(interpretation_criteria)",
				"reference = VALUES(reference)",
				"reference_year = VALUES(reference_year)",
				"reference_owner = VALUES(reference_owner)",
				"reliability = VALUES(reliability)",
				"isRobustStudy = VALUES(isRobustStudy)",
				"isUsedforClassification = VALUES(isUsedforClassification)",
				"isUsedforMSDS = VALUES(isUsedforMSDS)",
				"purposeFlag = VALUES(purposeFlag)",
				"studyResultType = VALUES(studyResultType)",
				"investigation_uuid = VALUES(investigation_uuid)",
				"assay_uuid = VALUES(assay_uuid)"
			)
		)
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
