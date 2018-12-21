package ambit2.db.substance.study;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.core.io.json.SubstanceStudyParser;
import ambit2.db.search.SQLFileQueryParams;
import net.idea.modbcum.i.bucket.Bucket;
import net.idea.modbcum.i.exceptions.AmbitException;

public class SubstanceStudyFlatQuery extends SQLFileQueryParams {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5304176321951306089L;
	protected ObjectMapper dx = new ObjectMapper();
	private static String _params = "{\"params\" : {\":all\" : { \"type\" : \"boolean\", \"value\": %s}, \":s_prefix\" : { \"type\" : \"String\", \"value\": \"%s\"},\":s_uuid\" : { \"type\" : \"String\", \"value\":\"%s\"}	}}";
	private static String _params_i = "{\"params\" : {\":%s\" : { \"type\" : \"String\", \"value\":\"%s\"}	}}";

	public enum _QUERY_TYPE {
		all {
			@Override
			public String queryParams(String... params) {
				return String.format(_params, "true", "", "");
			}
		},
		bysubstance {
			@Override
			public String queryParams(String... params) {
				if (params == null || params.length < 2)
					return null;
				else
					return String.format(_params, "false", params[0], params[1].replace("-", "").toLowerCase());
			}
		},
		bystudytype {
			@Override
			public String getField() {
				return "endpointcategory";
			}

			@Override
			public String getSQL() {
				return "ambit2/db/q/substance_study_byendpointcategory.sql";
			}
		},
		bycitation {
			@Override
			public String getSQL() {
				return "ambit2/db/q/substance_study_byreference.sql";
			}

			@Override
			public String getField() {
				return "reference";
			}

		},
		byprovider {
			@Override
			public String getSQL() {
				return "ambit2/db/q/substance_study_byreferenceowner.sql";
			}

			@Override
			public String getField() {
				return "reference_owner";
			}
		},
		byinvestigation {
			@Override
			public String getField() {
				return "i_uuid";
			}

			@Override
			public String getSQL() {
				return "ambit2/db/q/substance_study_byinvestigation.sql";
			}

		},
		bysubstance_name {
			public String queryParams(String... params) {
				// expected topcategory, endpointcategory, structure identifier
				if (params == null || params.length < 3)
					return null;
				else
					try {
						return String.format(
								"{\"params\" : {\":_topcategory\" : { \"type\" : \"String\", \"value\":\"%s\"}, \":_endpointcategory\" : { \"type\" : \"String\", \"value\":\"%s\"}, \":_name1\" : { \"type\" : \"ARRAY_STRING\", \"value\":\"%s\"}, \":_name2\" : { \"type\" : \"ARRAY_STRING\", \"value\":\"%s\"}	}}",
								params[0], params[1], params[2], params[2]);
					} catch (Exception x) {
						return null;
					}
			}
			@Override
			public String getSQL() {
				return "ambit2/db/q/substance_study_bysubstance_name.sql";
			}			
		},		
		bysubstance_type {
			public String queryParams(String... params) {
				// expected topcategory, endpointcategory, structure identifier
				if (params == null || params.length < 3)
					return null;
				else
					try {
						return String.format(
								"{\"params\" : {\":_topcategory\" : { \"type\" : \"String\", \"value\":\"%s\"}, \":_endpointcategory\" : { \"type\" : \"String\", \"value\":\"%s\"}, \":_name\" : { \"type\" : \"ARRAY_STRING\", \"value\":\"%s\"}	}}",
								params[0], params[1], params[2]);
					} catch (Exception x) {
						return null;
					}
			}
			@Override
			public String getSQL() {
				return "ambit2/db/q/substance_study_bysubstance_type.sql";
			}			
		},		
		bystructure_name {
			public String queryParams(String... params) {
				// expected topcategory, endpointcategory, structure identifier
				if (params == null || params.length < 3)
					return null;
				else
					try {
						return String.format(
								"{\"params\" : {\":_topcategory\" : { \"type\" : \"String\", \"value\":\"%s\"}, \":_endpointcategory\" : { \"type\" : \"String\", \"value\":\"%s\"}, \":_name\" : { \"type\" : \"ARRAY_STRING\", \"value\":\"%s\"}	}}",
								params[0], params[1], params[2]);
					} catch (Exception x) {
						return null;
					}
			}
			@Override
			public String getSQL() {
				return "ambit2/db/q/substance_study_bystructure_name.sql";
			}			
		},
		bystructure_smiles {
			@Override
			public boolean preprocessing() {
				return true;
			}
			@Override
			public String queryParams(String... params) {
				return _QUERY_TYPE.bystructure_inchikey.queryParams(params);
			}
			@Override
			public String getSQL() {
				// TODO Auto-generated method stub
				return _QUERY_TYPE.bystructure_inchikey.getSQL();
			}
		},		
		bystructure_inchikey {
			public String queryParams(String... params) {
				// expected topcategory, endpointcategory, inchikey
				if (params == null || params.length < 3)
					return null;
				else
					try {
						return String.format(
								"{\"params\" : {\":_topcategory\" : { \"type\" : \"String\", \"value\":\"%s\"}, \":_endpointcategory\" : { \"type\" : \"String\", \"value\":\"%s\"}, \":_inchikey\" : { \"type\" : \"ARRAY_STRING\", \"value\":\"%s\"}	}}",
								params[0], params[1], params[2]);
					} catch (Exception x) {
						return null;
					}
			}
			@Override
			public String getSQL() {
				return "ambit2/db/q/substance_study_bystructure_inchi.sql";
			}
		},
		byidchemical {
			public String queryParams(String... params) {
				// expected topcategory, endpointcategory, idchemical (chemicals table)
				if (params == null || params.length < 3)
					return null;
				else
					try {
						return String.format(
								"{\"params\" : {\":_topcategory\" : { \"type\" : \"String\", \"value\":\"%s\"}, \":_endpointcategory\" : { \"type\" : \"String\", \"value\":\"%s\"}, \":_idchemicals\" : { \"type\" : \"ARRAY_INT\", \"value\":\"%s\"}	}}",
								params[0], params[1], params[2]);
					} catch (Exception x) {
						return null;
					}
			}
			@Override
			public String getSQL() {
				return "ambit2/db/q/substance_study_byidchemical.sql";
			}
		},		
		;
		public String getField() {
			return null;
		}

		public String queryParams(String... params) {
			if (params == null || params.length < 1)
				return null;
			else
				return String.format(_params_i, getField(), params[0]);
		}

		public String getSQL() {
			return "ambit2/db/q/substance_study_flat.sql";
		}
		
		public boolean preprocessing() {
			return false;
		}

	};

	public SubstanceStudyFlatQuery(SubstanceRecord record) throws IOException {
		this(_QUERY_TYPE.bysubstance);
		String[] uuid = I5Utils.splitI5UUID(record.getSubstanceUUID());
		JsonNode node = json2params(
				_QUERY_TYPE.bysubstance.queryParams(uuid[0], uuid[1].replace("-", "").toLowerCase()));
		if (node != null && node instanceof ObjectNode)
			setValue((ObjectNode) node);
	}

	public SubstanceStudyFlatQuery(Protocol papp) throws IOException {
		this(_QUERY_TYPE.bystudytype);
		if (papp.getCategory() == null)
			throw new IOException("No id");
		JsonNode node = null;
		node = json2params(_QUERY_TYPE.bystudytype.queryParams(papp.getCategory().toString()));
		if (node != null && node instanceof ObjectNode)
			setValue((ObjectNode) node);
	}

	public SubstanceStudyFlatQuery(Protocol papp, String[] search_query) throws IOException {
		this(_QUERY_TYPE.bystructure_inchikey);
	}
	public SubstanceStudyFlatQuery(_QUERY_TYPE querytype, Protocol papp, String[] search_query) throws IOException {
		this(querytype);
		if (papp.getTopCategory() == null || papp.getCategory() == null || search_query==null || search_query.length==0)
			throw new IOException("Required parameters missing");
		JsonNode node = null;
		try {
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < search_query.length; i++) {
				if (i > 0)
					b.append(",");
				b.append(search_query[i]);
			}
			node = json2params(querytype.queryParams(papp.getTopCategory().toString(),
					papp.getCategory().toString(), b.toString()));
		} catch (Exception x) {
			throw new IOException("Bad request");
		}
		if (node != null && node instanceof ObjectNode)
			setValue((ObjectNode) node);
	}

	public SubstanceStudyFlatQuery(Protocol papp, int[] idchemicals) throws IOException {
		this(_QUERY_TYPE.byidchemical);
		if (papp.getTopCategory() == null || papp.getCategory() == null || idchemicals==null || idchemicals.length==0)
			throw new IOException("Required parameters missing");
		JsonNode node = null;
		try {
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < idchemicals.length; i++) {
				if (i > 0)
					b.append(",");
				b.append(idchemicals[i]);
			}
			node = json2params(_QUERY_TYPE.byidchemical.queryParams(papp.getTopCategory().toString(),
					papp.getCategory().toString(), b.toString()));
		} catch (Exception x) {
			throw new IOException("Bad request");
		}
		if (node != null && node instanceof ObjectNode)
			setValue((ObjectNode) node);
	}
	
	public SubstanceStudyFlatQuery(ProtocolApplication papp) throws IOException {
		this(_QUERY_TYPE.byinvestigation);
		if (papp.getInvestigationUUID() == null)
			throw new IOException("No investigation");
		JsonNode node = json2params(_QUERY_TYPE.byinvestigation.queryParams(papp.getInvestigationUUID().toString()));
		if (node != null && node instanceof ObjectNode)
			setValue((ObjectNode) node);
	}

	public SubstanceStudyFlatQuery() throws IOException {
		this(_QUERY_TYPE.all);
	}

	public SubstanceStudyFlatQuery(_QUERY_TYPE queryType, String... params) throws IOException {
		this(queryType.getSQL(), queryType.queryParams(params));
	}

	public SubstanceStudyFlatQuery(_QUERY_TYPE queryType) throws IOException {
		this(queryType.getSQL(), queryType.queryParams());
	}

	public SubstanceStudyFlatQuery(String sqlResource, String json) throws IOException {
		this(sqlResource, (ObjectNode) null);
		JsonNode node = json2params(json);
		if (node != null && node instanceof ObjectNode)
			setValue((ObjectNode) node);
	}

	public SubstanceStudyFlatQuery(String sqlResource, ObjectNode params) throws IOException {
		super(sqlResource, params);
	}

	/**
	 * <pre>
	 * {
	 * 	"params": {
	 * 		":id": {
	 * 			"mandatory": false,
	 * 			"type": "Integer",
	 * 			"value": 123
	 * 		}
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param json
	 * @return
	 */
	public JsonNode json2params(String json) throws JsonProcessingException, IOException {
		if (json == null)
			return null;
		JsonNode node = dx.readTree(json);
		return node.get("params");
	}

	@Override
	public Bucket getObject(ResultSet rs) throws AmbitException {
		Bucket bucket = super.getObject(rs);
		bucket.put("s_type", "study");
		bucket.remove("_childDocuments_");

		String substanceUUID = I5Utils.getPrefixedUUID(bucket.get("s_prefix"), bucket.get("s_uuid"));
		bucket.put("s_uuid", substanceUUID);
		bucket.remove("s_prefix");

		String documentUUID = I5Utils.getPrefixedUUID(bucket.get("document_prefix"), bucket.get("document_uuid"));
		bucket.put("document_uuid", documentUUID);
		bucket.remove("document_prefix");
		List<Bucket> _childDocuments_ = new ArrayList<>();
		final String[] keys = new String[] { "params", "conditions" };
		for (String key : keys) {
			Object params = bucket.get(key);
			bucket.remove(key);
			Bucket pbucket = new Bucket();
			pbucket.setHeader(bucket.getHeader());
			pbucket.put("id", bucket.get("id") + "_" + key);
			bucket.put("s_uuid", substanceUUID);
			pbucket.put("document_uuid", documentUUID);
			pbucket.put("s_type", key);
			IParams iparams = SubstanceStudyParser.parseConditions(dx, params);
			pbucket.put(key, iparams);

			_childDocuments_.add(pbucket);
		}
		bucket.put("_childDocuments_", _childDocuments_);

		final String textValueTag = "textValue";
		Object t = bucket.get(textValueTag);
		if (t != null && t.toString().startsWith("{")) {
			IParams nonzero = SubstanceStudyParser.parseTextValueProteomics(dx, t.toString());
			bucket.put(textValueTag, nonzero);
		}

		return bucket;
	}

	@Override
	protected void setHeader(ResultSet rs, Bucket bucket) throws SQLException {

		ResultSetMetaData md = rs.getMetaData();
		String[] header = new String[md.getColumnCount() + 1];
		int[] columnTypes = new int[md.getColumnCount() + 1];

		for (int i = 0; i < md.getColumnCount(); i++) {
			header[i] = md.getColumnLabel(i + 1);
			columnTypes[i] = md.getColumnType(i + 1);
			// java.sql.Types
		}
		header[header.length - 1] = "_childDocuments_";
		columnTypes[header.length - 1] = java.sql.Types.JAVA_OBJECT;

		bucket.setColumnTypes(columnTypes);
		bucket.setHeader(header);
	}

}
