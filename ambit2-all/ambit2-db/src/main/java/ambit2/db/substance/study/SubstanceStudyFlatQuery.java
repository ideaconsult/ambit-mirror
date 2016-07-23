package ambit2.db.substance.study;

import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.bucket.Bucket;
import net.idea.modbcum.i.exceptions.AmbitException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Value;
import ambit2.core.io.json.SubstanceStudyParser;
import ambit2.db.search.SQLFileQueryParams;

public class SubstanceStudyFlatQuery extends SQLFileQueryParams {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5304176321951306089L;
	protected ObjectMapper dx = new ObjectMapper();

	private static String _params = "{\"params\" : {\":all\" : { \"type\" : \"boolean\", \"value\": %s}, \":s_prefix\" : { \"type\" : \"String\", \"value\": \"%s\"},\":s_uuid\" : { \"type\" : \"String\", \"value\":\"%s\"}	}}";

	public SubstanceStudyFlatQuery(SubstanceRecord record) throws IOException {
		this((ObjectNode) null);
		String[] uuid = I5Utils.splitI5UUID(record.getSubstanceUUID());
		JsonNode node = json2params(String.format(_params, "false", uuid[0],
				uuid[1].replace("-", "").toLowerCase()));
		if (node != null && node instanceof ObjectNode)
			setValue((ObjectNode) node);
	}

	public SubstanceStudyFlatQuery() throws IOException {
		this(String.format(_params, "true", "", ""));
	}

	public SubstanceStudyFlatQuery(String json) throws IOException {
		this((ObjectNode) null);
		JsonNode node = json2params(json);
		if (node != null && node instanceof ObjectNode)
			setValue((ObjectNode) node);
	}

	public SubstanceStudyFlatQuery(ObjectNode params) throws IOException {
		super("ambit2/db/q/substance_study_flat.sql", params);
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
	public JsonNode json2params(String json) throws JsonProcessingException,
			IOException {
		JsonNode node = dx.readTree(json);
		return node.get("params");
	}

	@Override
	public Bucket getObject(ResultSet rs) throws AmbitException {
		Bucket bucket = super.getObject(rs);
		bucket.put("s_type", "study");
		bucket.remove("_childDocuments_");

		String substanceUUID = I5Utils.getPrefixedUUID(bucket.get("s_prefix"),
				bucket.get("s_uuid"));
		bucket.put("s_uuid", substanceUUID);
		bucket.remove("s_prefix");

		String documentUUID = I5Utils.getPrefixedUUID(
				bucket.get("document_prefix"), bucket.get("document_uuid"));
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
			IParams nonzero = SubstanceStudyParser.parseTextValueProteomics(dx,
					t.toString());
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
