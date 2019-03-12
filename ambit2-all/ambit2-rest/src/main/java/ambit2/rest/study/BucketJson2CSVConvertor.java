package ambit2.rest.study;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
	protected List<String> headers;
	protected List<String> paramHeaders;
	protected List<String> studyHeaders;
	protected List<String> conditionHeaders;
	protected List<String> moreHeaders;
	protected static ResourceBundle labels = ResourceBundle.getBundle("rest/study/bucketexport", Locale.ENGLISH,
			BucketJson2CSVConvertor.class.getClassLoader());

	public BucketJson2CSVConvertor() {
		this(new String[] { "dbtag_hss", "name_hs", "publicname_hs", "owner_name_hs", "substanceType_hs",
				"substance_uuid" },
				new String[] { "document_uuid_s", "Dispersion protocol_s", "MEDIUM_s", "MEDIUM.composition_s",
						"MEDIUM.ph_s", "MEDIUM.temperature_Celsius_s", "MEDIUM.ionictrength_m_s",
						"MEDIUM.ionictrength_mM_s", "MEDIUM.CO2_concentration_m", "MEDIUM.O2_concentration_%v/v",
						"Vial_s", "E.sop_reference_s", "E.method_s", "E.cell_type_s", "E.exposure_time_hour_s",
						"Dose_s" },
				new String[] { "s_uuid_s","assay_uuid_s","investigation_uuid_s", "topcategory_s", "endpointcategory_s", "guidance_s", "reference_owner_s",
						"reference_year_s", "reference_s", "effectendpoint_s", "effectendpoint_type_s","loQualifier_s", "loValue_d",
						"upQualifier_s", "upValue_d", "unit_s", "err_d", "errQualifier_s", "textValue_s" },
				new String[] { "replicate_s", "material_s" });

	}

	public BucketJson2CSVConvertor(String[] header, String[] paramHeaders, String[] studyHeaders,
			String[] conditionHeaders) {
		this.headers = Arrays.asList(header);
		this.paramHeaders = Arrays.asList(paramHeaders);
		this.studyHeaders = Arrays.asList(studyHeaders);
		this.conditionHeaders = Arrays.asList(conditionHeaders);
		this.moreHeaders = new ArrayList<String>();

	}

	public List<String> getHeaders() {
		return headers;
	}

	public void setHeaders(String[] headers) {
		this.headers = Arrays.asList(headers);
	}

	public OutputStream getOut() {
		return out;
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}

	protected void printHeaders(OutputStream out, List<String> headers) throws Exception {
		for (String header : headers) {
			String h = header;
			try {
				h = labels.getString(header);
			} catch (Exception x) {
				h = header.replace("_s", "").replace("_hs", "").replace("_d", "");
			}
			out.write(h.getBytes(StandardCharsets.UTF_8));
			out.write("\t".getBytes());
		}
	}

	protected void printValues(OutputStream out, List<String> headers, JsonNode[] docs) throws IOException {
		printValues(out, headers, docs, false);
	}

	private static final String[] _dontadd = new String[] { "effectid_hs", "id", "type_s", "topcategory_s",
			"endpointcategory_s", "guidance_s" };

	protected void printValues(OutputStream out, List<String> headers, JsonNode[] docs, boolean extend)
			throws IOException {

		if (extend)
			for (JsonNode doc : docs)
				if (doc != null) {

					Iterator<String> i = doc.fieldNames();
					while (i.hasNext()) {
						String key = i.next();
						if (Arrays.binarySearch(_dontadd, key) < 0 && !headers.contains(key)
								&& !moreHeaders.contains(key))
							moreHeaders.add(key);
					}
				}

		for (String header : headers) {
			String d = "";
			for (JsonNode doc : docs) {
				JsonNode dnode = doc == null ? null : doc.get(header);
				if (dnode == null)
					continue;
				out.write(d.getBytes());
				if (dnode instanceof ArrayNode) {
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
				d = " ";
			}
			out.write("\t".getBytes());
		}

	}

	@Override
	public OutputStream process(InputStream in) throws Exception {

		File tmpfile = File.createTempFile("export", ".csv");
		tmpfile.deleteOnExit();
		try (FileOutputStream fout = new FileOutputStream(tmpfile)) {
			try (InputStreamReader rin = new InputStreamReader(in, StandardCharsets.UTF_8)) {
				JsonNode node = dx.readTree(rin);
				ArrayNode docs = (ArrayNode) node.get("response").get("docs");
				for (JsonNode doc : docs) {
					
					Map<String, JsonNode> params = lookup((ArrayNode) doc.get("_childDocuments_"), "params","document_uuid_s");
					Map<String, JsonNode> study = lookup((ArrayNode) doc.get("_childDocuments_"), "study","id");
					Map<String, JsonNode> conditions = lookup((ArrayNode) doc.get("_childDocuments_"), "conditions","id");

					Iterator<Entry<String, JsonNode>> fields = study.entrySet().iterator();
					while (fields.hasNext()) {
						Entry<String, JsonNode> field = fields.next();
						String docuuid = field.getValue().get("document_uuid_s").asText();
						JsonNode pdoc = docuuid == null ? null : params == null ? null : params.get(docuuid);
						
						String id = field.getKey();
						JsonNode cdoc = id == null ? null : conditions == null ? null : conditions.get(id);
						printValues(fout, headers, new JsonNode[] { doc });
						printValues(fout, studyHeaders, new JsonNode[] { field.getValue() });
						printValues(fout, paramHeaders, new JsonNode[] { pdoc }, true);
						printValues(fout, conditionHeaders, new JsonNode[] { cdoc }, true);

						printValues(fout, moreHeaders, new JsonNode[] { pdoc, cdoc });
						fout.write("\r\n".getBytes());
					}
				}
			}
		}
		try (FileInputStream fin = new FileInputStream(tmpfile)) {
			printHeaders(out, headers);
			printHeaders(out, studyHeaders);
			printHeaders(out, paramHeaders);
			printHeaders(out, conditionHeaders);
			printHeaders(out, moreHeaders);
			out.write("\r\n".getBytes());
			IOUtils.copy(fin, out);
		}
		tmpfile.delete();
		return out;
	}

	protected Map<String, JsonNode> lookup(ArrayNode subdocs, String type_s, String key) {
		Map<String, JsonNode> map = new TreeMap<String, JsonNode>();
		if (subdocs != null)
			for (JsonNode doc : subdocs)
				if (type_s.equals(doc.get("type_s").asText())) {
					//docuuid is same for effect records ...
					String docuuid = doc.get(key).asText().replace("/cn", "");
					map.put(docuuid, doc);
				}
		return map;
	}
}
