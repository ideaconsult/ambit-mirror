package ambit2.rest.study;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import net.idea.modbcum.p.DefaultAmbitProcessor;

public class BucketJson2CSVConvertor extends DefaultAmbitProcessor<InputStream, OutputStream> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4805381027500815039L;
	protected ObjectMapper dx = new ObjectMapper();
	protected OutputStream out;
	protected String[] headers;
	protected String[] paramHeaders;
	protected String[] studyHeaders;
	protected String[] conditionHeaders;

	public BucketJson2CSVConvertor() {
		this(new String[] { "dbtag_hss", "name_hs", "publicname_hs", "owner_name_hs", "substanceType_hs",
				"substance_uuid" },
				new String[] { "document_uuid_s", "Dispersion protocol_s", "MEDIUM_s", "Vial_s" },
				new String[] { "s_uuid_s", "topcategory_s", "endpointcategory_s", "guidance_s", "effectendpoint_s",
						"reference_owner_s", "reference_year_s", "reference_s", "loQualifier_s", "loValue_d",
						"upQualifier_s", "upValue_d", "unit_s", "err_d", "errQualifier_s", "textValue_s" },
				new String[] { "DATA_GATHERING_INSTRUMENTS_s", "size_measurement_s", "size_measurement_type_s",
						"E.method_s", "E.cell_type_s", "E.exposure_time_hour_s" });
	}

	public BucketJson2CSVConvertor(String[] header, String[] paramHeaders, String[] studyHeaders,
			String[] conditionHeaders) {
		this.headers = header;
		this.paramHeaders = paramHeaders;
		this.studyHeaders = studyHeaders;
		this.conditionHeaders = conditionHeaders;
	}

	public String[] getHeaders() {
		return headers;
	}

	public void setHeaders(String[] headers) {
		this.headers = headers;
	}

	public OutputStream getOut() {
		return out;
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}

	protected void printHeaders(String[] headers) throws Exception {
		for (String header : headers) {
			out.write(header.getBytes());
			out.write("\t".getBytes());
		}
	}

	protected void printValues(String[] headers, JsonNode doc) throws IOException {
		for (String header : headers) {
			JsonNode dnode = doc == null ? null : doc.get(header);
			if (dnode == null)
				;
			else if (dnode instanceof ArrayNode) {
				ArrayNode aNode = (ArrayNode) dnode;
				for (int i = 0; i < aNode.size(); i++) {
					if (i > 0)
						out.write(" ".getBytes());
					out.write(aNode.get(i).asText().getBytes());
				}
			} else
				out.write(dnode.asText().getBytes());
			out.write("\t".getBytes());
		}
	}

	@Override
	public OutputStream process(InputStream in) throws Exception {
		printHeaders(headers);
		printHeaders(studyHeaders);
		printHeaders(paramHeaders);
		printHeaders(conditionHeaders);
		out.write("\r\n".getBytes());
		JsonNode node = dx.readTree(in);
		ArrayNode docs = (ArrayNode) node.get("response").get("docs");
		for (JsonNode doc : docs) {
			Map<String, JsonNode> params = lookup((ArrayNode) doc.get("_childDocuments_"), "params");
			Map<String, JsonNode> study = lookup((ArrayNode) doc.get("_childDocuments_"), "study");
			Map<String, JsonNode> conditions = lookup((ArrayNode) doc.get("_childDocuments_"), "conditions");

			Iterator<Entry<String, JsonNode>> fields = study.entrySet().iterator();
			while (fields.hasNext()) {
				Entry<String, JsonNode> field = fields.next();
				String docuuid = field.getKey();
				JsonNode pdoc = docuuid == null ? null : params.get(docuuid);
				JsonNode cdoc = docuuid == null ? null : conditions.get(docuuid);
				printValues(headers, doc);
				printValues(studyHeaders, field.getValue());
				printValues(paramHeaders, pdoc);
				printValues(conditionHeaders, cdoc);
				out.write("\r\n".getBytes());
			}
			/*
			 * for (JsonNode sdoc : study.values()) { JsonNode docuuid =
			 * sdoc.get("document_uuid_s"); JsonNode pdoc = docuuid == null ?
			 * null : params.get(docuuid.asText()); printValues(headers, doc);
			 * printValues(paramHeaders, pdoc); printValues(studyHeaders, sdoc);
			 * out.write("\r\n".getBytes()); }
			 */

		}
		return out;
	}

	protected Map<String, JsonNode> lookup(ArrayNode subdocs, String type_s) {
		Map<String, JsonNode> map = new TreeMap<String, JsonNode>();
		for (JsonNode doc : subdocs)
			if (type_s.equals(doc.get("type_s").asText())) {
				String docuuid = doc.get("document_uuid_s").asText();
				map.put(docuuid, doc);
			}
		return map;
	}
}
