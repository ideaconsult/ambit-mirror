package ambit2.rest.legacy;

import net.idea.restnet.rdf.ns.BibTex;
import net.idea.restnet.rdf.ns.OT;
import net.idea.restnet.rdf.ns.OTA;
import net.idea.restnet.rdf.ns.OTEE;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * 
 * @author nina Implementation of OpenTox API {@link http
 *         ://opentox.org/dev/apis/api-1.1/Feature}
 */
@Deprecated
public class OTFeature extends OTProcessingResource {
	protected OTAlgorithm algorithm = null;
	protected boolean isNumeric = false;
	protected boolean isNominal = false;
	protected String units = "";

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public OTFeature(String ref, String referer) {
		super(ref, referer);
	}

	public OTAlgorithm getAlgorithm() {
		return algorithm;
	}

	public static OTFeature feature(String ref, String referer)
			throws Exception {
		return new OTFeature(ref, referer);
	}

	public OTFeature withFeatureService(Reference uri) throws Exception {
		this.service = uri;
		return this;
	}

	public OTFeature withAlgorithm(OTAlgorithm alg) throws Exception {
		this.algorithm = alg;
		return this;
	}

	/**
	 * Retrieve algorithms from ot:hasSource property
	 * 
	 * @return
	 * @throws Exception
	 */
	public OTFeature algorithm() throws Exception {
		if (algorithm == null) {
			QueryExecution qe = null;
			OntModel model = null;

			try {
				model = OT.createModel(OntModelSpec.OWL_DL_MEM);
				model = (OntModel) OT.createModel(model, getUri(),
						MediaType.APPLICATION_RDF_XML, referer);

				Query query = QueryFactory.create(OTModel
						.getSparql("sparql/FeatureAlgorithm.sparql"));
				qe = QueryExecutionFactory.create(query, model);
				ResultSet results = qe.execSelect();
				while (results.hasNext()) {
					QuerySolution solution = results.next();
					RDFNode var = solution.get("algorithm");
					if (var.isResource()) {
						String uri = ((Resource) var).getURI();
						// this is a hack, should verify if it is of rdf:type
						// ot:Algorithm or ot:Model
						if ((uri.indexOf("algorithm") > 0)
								|| (uri.indexOf("model") > 0))
							algorithm = OTAlgorithm.algorithm(((Resource) var)
									.getURI(),referer);
					}
				}
			} catch (Exception x) {
				throw x;
			} finally {
				try {
					qe.close();
				} catch (Exception x) {
				}
				try {
					model.close();
				} catch (Exception x) {
				}
			}
		}
		return this;
	}

	/**
	 * 
	 * @param inputDataset
	 * @param dataset_service
	 * @return
	 * @throws Exception
	 */
	@Override
	public OTRemoteTask processAsync(OTDataset inputDataset) throws Exception {
		algorithm();
		return algorithm == null ? null : algorithm.processAsync(inputDataset);
	}

	@Override
	public OTDataset process(OTDataset inputDataset) throws Exception {
		algorithm();
		return (algorithm == null) ? null : algorithm.process(inputDataset);
	}

	@Override
	public OTObject create() throws Exception {
		try {
			if (service == null)
				throw new Exception("No feature service");
			// RemoteTask task = new
			// RemoteTask(service,MediaType.APPLICATION_RDF_XML,input,Method.POST,null);
		} catch (Exception x) {

		}
		return this;
	}

	public OTFeature getPage(int page, int pageSize) throws Exception {
		return feature(OTObject.getPagedReference(getUri(), page, pageSize).toString(),referer);
	}

	public OTFeature setSameas(String newLabel) throws Exception {
		Model jenaModel = ModelFactory.createDefaultModel();

		jenaModel.setNsPrefix("ot", OT.NS);
		jenaModel.setNsPrefix("ota", OTA.NS);
		jenaModel.setNsPrefix("otee", OTEE.NS);
		jenaModel.setNsPrefix("owl", OWL.NS);
		jenaModel.setNsPrefix("dc", DC.NS);
		jenaModel.setNsPrefix("bx", BibTex.NS);
		jenaModel.setNsPrefix("xsd", XSDDatatype.XSD + "#");

		jenaModel = OT.createModel(jenaModel, getUri(),
				MediaType.APPLICATION_RDF_XML, referer);

		Resource thisFeature = jenaModel.createResource(getUri().toString());
		StmtIterator i = jenaModel.listStatements(new SimpleSelector(
				thisFeature, OWL.sameAs, (RDFNode) null));
		Statement st = null;
		while (i.hasNext()) {
			st = i.next();
			break;
		}
		i.close();
		if (st != null)
			jenaModel.remove(st);
		jenaModel.add(thisFeature, OWL.sameAs, newLabel);
		put(jenaModel);
		jenaModel.close();
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OTFeature)
			return ((OTFeature) obj).getUri().toString()
					.equals(getUri().toString());
		else
			return false;
	}

	public int hashCode() {
		int hash = 7;
		int var_code = (null == getUri() ? 0 : getUri().toString().hashCode());
		hash = 31 * hash + var_code;

		return hash;
	}
}
