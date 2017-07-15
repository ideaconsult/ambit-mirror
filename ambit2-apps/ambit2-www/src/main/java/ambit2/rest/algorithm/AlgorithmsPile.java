package ambit2.rest.algorithm;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.json.JSONUtils;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.core.data.model.AlgorithmType;
import ambit2.rest.property.PropertyJSONReporter;

public class AlgorithmsPile {

	private static final Object[][] algorithms = fromJSON();

	public static Object[][] getAlgorithms() {
		return algorithms;
	}

	public static synchronized List<Algorithm<String>> createList() {
		List<Algorithm<String>> algorithmList = new ArrayList<Algorithm<String>>();
		for (Object[] d : algorithms) {
			Algorithm<String> alg = new Algorithm<String>(d[1].toString());
			alg.setType((String[]) d[4]);
			alg.setFormat(alg.hasType(AlgorithmType.Expert) ? AlgorithmFormat.WWW_FORM
					: alg.hasType(AlgorithmType.SMSD) ? AlgorithmFormat.WWW_FORM
							: alg.hasType(AlgorithmType.Structure2D.toString()) ? AlgorithmFormat.Structure2D
									: alg.hasType(AlgorithmType.Structure.toString()) ? AlgorithmFormat.MOPAC
											: alg.hasType(AlgorithmType.Rules.toString())
													|| alg.hasType(AlgorithmType.Fingerprints.toString())
															? AlgorithmFormat.JAVA_CLASS
															: alg.hasType(AlgorithmType.AppDomain.toString())
																	? AlgorithmFormat.COVERAGE_SERIALIZED
																	: (d[2] != null) && d[2].toString()
																			.startsWith("ambit2.waffles.")
																					? AlgorithmFormat.WAFFLES_JSON
																					: AlgorithmFormat.WEKA);

			alg.setId(d[0].toString());
			alg.setName(d[1].toString());
			alg.setContent(d[2] == null ? null : d[2].toString());
			alg.setEndpoint(d[5] == null ? null : d[5].toString());
			if (d[3] == null)
				alg.setInput(new Template("Empty"));
			else {
				Template predictors = new Template(String.format("Predictors-%s", d[1]));
				for (Property p : (Property[]) d[3])
					predictors.add(p);
				alg.setInput(predictors);
			}
			alg.setRequirement((Algorithm.requires) d[6]);
			alg.setImplementationOf(d.length >= 8 ? d[7].toString() : null);
			algorithmList.add(alg);
		}
		return algorithmList;
	}

	public static synchronized Iterator<Algorithm<String>> createIterator(List<Algorithm<String>> algorithms,
			String type, String search) {
		return new FilteredIterator(algorithms.iterator(), type, search);
	}

	private static final String _headers = "id\tname\tclass\tinputparam\ttype\tendpoint\trequires\timplementationof";

	public static Object[][] fromJSON() {
		try (InputStream in = AlgorithmsPile.class.getClassLoader()
				.getResourceAsStream("ambit2/rest/config/algorithmspile.json")) {
			return fromJSON(in);
		} catch (Exception x) {
			Logger.getAnonymousLogger().log(Level.SEVERE, x.getMessage());
			return new Object[][] {};
		}
	}

	/**
	 * <pre>
	"id": "SimpleKMeans",
	"name": "Clustering: k-means",
	"class": "weka.clusterers.SimpleKMeans",
	"inputparam": null,
	"type": [
	"http://www.opentox.org/algorithmTypes.owl#Clustering",
	"http://www.opentox.org/algorithmTypes.owl#SingleTarget",
	"http://www.opentox.org/algorithmTypes.owl#LazyLearning",
	"http://www.opentox.org/algorithmTypes.owl#UnSupervised"
	],
	"endpoint": null,
	"requires": "property"
	 * </pre>
	 * 
	 * @param in
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static Object[][] fromJSON(InputStream in) throws JsonProcessingException, IOException {
		ObjectMapper m = new ObjectMapper();
		ArrayNode nodes = (ArrayNode) m.readTree(in);
		String[] headers = _headers.split("\t");
		Object[][] algs = new Object[nodes.size()][];
		for (int i = 0; i < nodes.size(); i++)
			try {
				JsonNode impl = nodes.get(i).get("implementationof");
				algs[i] = impl == null ? new Object[headers.length - 1] : new Object[headers.length];

				for (int c = 0; c < algs[i].length; c++) {
					JsonNode node = nodes.get(i).get(headers[c]);
					if (node == null || node instanceof NullNode)
						algs[i][c] = null;
					else if ("requires".equals(headers[c]))
						try {
							algs[i][c] = Algorithm.requires.valueOf(node.asText(null));
						} catch (Exception x) {
						}
					else if ("inputparam".equals(headers[c])) {
						Iterator<Map.Entry<String, JsonNode>> pi = node.fields();
						Property[] subitems = new Property[node.size()];
						int n = 0;
						while (pi.hasNext()) {
							Map.Entry<String, JsonNode> p = pi.next();
							String creator = p.getValue().get("creator").asText();
							ILiteratureEntry ref = LiteratureEntry.getInstance("User input", creator);
							String name = p.getValue().get("title").asText();
							String units = p.getValue().get("units").asText();
							Property property = new Property(name, units, ref);
							subitems[n] = property;
							n++;
						}
						algs[i][c] = subitems;

					} else if (node instanceof ArrayNode) {
						ArrayNode anode = (ArrayNode) node;
						Object[] subitems = new String[anode.size()];
						for (int a = 0; a < anode.size(); a++) {
							if ("type".equals(headers[c]))
								try {
									String atype = anode.get(a).asText()
											.replaceAll("http://www.opentox.org/algorithmTypes.owl#", "");
									subitems[a] = AlgorithmType.valueOf(atype).toString();
								} catch (Exception x) {
									Logger.getGlobal().log(Level.WARNING, x.getMessage(), x);
								}
							else
								subitems[a] = anode.get(a).asText();
						}
						algs[i][c] = subitems;

					} else
						algs[i][c] = node == null ? null : node.asText(null);
				}
			} catch (Exception x) {
				Logger.getGlobal().log(Level.SEVERE, x.getMessage(), x);
			}
		return algs;

	}

	public static String toJSON() {
		return toJSON(algorithms);
	}

	public static String toJSON(Object[][] algorithms) {

		StringBuilder b = new StringBuilder();
		String[] headers = _headers.split("\t");
		b.append("[");
		String s1 = "";
		for (Object[] oo : algorithms) {
			int cols = 0;
			b.append(s1);
			b.append("{");
			s1 = ",";
			String s2 = "";
			for (int c = 0; c < oo.length; c++) {
				Object col = oo[c];
				cols++;
				b.append(s2);
				s2 = ",";
				b.append(JSONUtils.jsonQuote(headers[c]));
				b.append(":");

				if (col == null) {
					b.append("null");
				} else if ("inputparam".equals(headers[c])) {
					Object[] a = (Object[]) col;
					b.append("{");
					PropertyJSONReporter pr = new PropertyJSONReporter(null);
					StringWriter w = new StringWriter();
					try {
						pr.setOutput(w);
						for (Object subitem : a)
							if (subitem instanceof Property)
								pr.processItem((Property) subitem);
						b.append(w);
					} catch (Exception x) {
						Logger.getGlobal().log(Level.SEVERE, x.getMessage(), x);
					}
					b.append("}");
				} else if (col.getClass().isArray()) {
					Object[] a = (Object[]) col;
					String d = "[";
					for (Object subitem : a) {
						b.append(d);
						b.append(JSONUtils.jsonQuote(subitem.toString()));
						d = ",";
					}
					b.append("]");

				} else
					b.append(JSONUtils.jsonQuote(col.toString()));

			}
			b.append("}\n");
		}
		b.append("]");
		return b.toString();
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();

		b.append(_headers);
		b.append("\n");
		for (Object[] oo : algorithms) {
			int cols = 0;
			for (Object alg : oo) {
				cols++;
				if (alg == null)
					b.append("");
				else if (alg.getClass().isArray()) {
					Object[] a = (Object[]) alg;
					String d = "[";
					for (Object subitem : a) {
						b.append(d);
						b.append(subitem.toString());
						d = ",";
					}
					b.append("]");
				} else
					b.append(alg.toString());
				b.append("\t");
			}
			System.out.println(cols);
			b.append("\n");
		}
		return b.toString();
	}
}

class FilteredIterator implements Iterator<Algorithm<String>> {
	protected Iterator<Algorithm<String>> algorithms;
	protected Algorithm<String> record;
	protected String type;
	protected String search;

	public FilteredIterator(Iterator<Algorithm<String>> algorithms, String type, String search) {
		this.algorithms = algorithms;
		try {
			this.type = AlgorithmType.valueOf(type).toString();
		} catch (Exception x) {
			this.type = type;
		}
		this.search = search;
		record = null;
	}

	@Override
	public boolean hasNext() {
		while (algorithms.hasNext()) {
			Algorithm<String> alg = algorithms.next();
			if ((type != null) && (search != null)) {
				if (alg.hasType(type) && alg.getName().startsWith(search)) {
					record = alg;
					return true;
				}
			} else if (type != null) {
				if (alg.hasType(type)) {
					record = alg;
					return true;
				}
			} else if (search != null) {
				if (alg.getName().startsWith(search)) {
					record = alg;
					return true;
				}
			}

		}
		record = null;
		return false;
	}

	@Override
	public Algorithm<String> next() {
		return record;
	}

	@Override
	public void remove() {

	}

}
