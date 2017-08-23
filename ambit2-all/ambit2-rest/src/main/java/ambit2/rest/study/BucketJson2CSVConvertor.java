package ambit2.rest.study;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import ambit2.base.data.study.Protocol;
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
	protected static ResourceBundle labels = ResourceBundle.getBundle("rest/study/bucketexport", Locale.ENGLISH,
			BucketJson2CSVConvertor.class.getClassLoader());

	public BucketJson2CSVConvertor() {
		this(new String[] { "dbtag_hss", "name_hs", "publicname_hs", "owner_name_hs", "substanceType_hs",
				"substance_uuid" },
				new String[] { "document_uuid_s", "Dispersion protocol_s", "MEDIUM_s", "MEDIUM.composition_s",
						"MEDIUM.ph_s", "MEDIUM.temperature_Celsius_s", "MEDIUM.ionic_strength_m_s", "Vial_s",
						"E.sop_reference_s", "E.method_s", "E.cell_type_s", "E.exposure_time_hour_s", "Dose_s" },
				new String[] { "s_uuid_s", "topcategory_s", "endpointcategory_s", "guidance_s", "reference_owner_s",
						"reference_year_s", "reference_s", "effectendpoint_s", "loQualifier_s", "loValue_d",
						"upQualifier_s", "upValue_d", "unit_s", "err_d", "errQualifier_s", "textValue_s" },
				new String[] { "DATA_GATHERING_INSTRUMENTS_s", "size_measurement_s", "size_measurement_type_s",
						"replicates_s", "concentration_s", "No. samples_s" });

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

	protected void printHeaders(OutputStream out, String[] headers) throws Exception {
		for (String header : headers) {
			String h = header;
			try {
				h = labels.getString(header);
			} catch (Exception x) {
				h = header.replace("_s", "").replace("_hs", "").replace("_d", "");
			}
			out.write(h.getBytes());
			out.write("\t".getBytes());
		}
	}

	protected void printValues(OutputStream out, String[] headers, JsonNode doc) throws IOException {
		for (String header : headers) {
			JsonNode dnode = doc == null ? null : doc.get(header);
			if (dnode == null)
				;
			else if (dnode instanceof ArrayNode) {
				ArrayNode aNode = (ArrayNode) dnode;
				for (int i = 0; i < aNode.size(); i++) {
					if (i > 0)
						out.write(" ".getBytes());
					out.write(aNode.get(i).asText().getBytes(StandardCharsets.UTF_8));
				}
			} else {
				String value = dnode.asText();
				if ("endpointcategory_s".equals(header))
					try {
						value = Protocol._categories.valueOf(value).toString();
					} catch (Exception x) {
						x.printStackTrace();
					}
				out.write(value.getBytes(StandardCharsets.UTF_8));
			}
			out.write("\t".getBytes());
		}
	}

	@Override
	public OutputStream process(InputStream in) throws Exception {

		File tmpfile = File.createTempFile("export", ".csv");
		tmpfile.deleteOnExit();
		try (FileOutputStream fout = new FileOutputStream(tmpfile)) {

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
					JsonNode pdoc = docuuid == null ? null : params == null ? null : params.get(docuuid);
					JsonNode cdoc = docuuid == null ? null : conditions == null ? null : conditions.get(docuuid);
					printValues(fout, headers, doc);
					printValues(fout, studyHeaders, field.getValue());
					printValues(fout, paramHeaders, pdoc);
					printValues(fout, conditionHeaders, cdoc);
					fout.write("\r\n".getBytes());
				}
			}
		}
		try (FileInputStream fin = new FileInputStream(tmpfile)) {
			printHeaders(out, headers);
			printHeaders(out, studyHeaders);
			printHeaders(out, paramHeaders);
			printHeaders(out, conditionHeaders);
			out.write("\r\n".getBytes());
			IOUtils.copy(fin, out);
		}
		tmpfile.delete();
		return out;
	}

	protected Map<String, JsonNode> lookup(ArrayNode subdocs, String type_s) {
		Map<String, JsonNode> map = new TreeMap<String, JsonNode>();
		if (subdocs != null)
			for (JsonNode doc : subdocs)
				if (type_s.equals(doc.get("type_s").asText())) {
					String docuuid = doc.get("document_uuid_s").asText();
					map.put(docuuid, doc);
				}
		return map;
	}
}
