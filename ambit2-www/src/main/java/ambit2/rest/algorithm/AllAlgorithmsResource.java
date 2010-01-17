package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.OpenTox;
import ambit2.rest.StringConvertor;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.task.CallableDescriptorCalculator;
import ambit2.rest.task.CallableQueryProcessor;
import ambit2.rest.task.CallableSimpleModelCreator;
import ambit2.rest.task.CallableWekaModelCreator;

public class AllAlgorithmsResource extends CatalogResource<Algorithm<String>> {
	public final static String algorithm = OpenTox.URI.algorithm.getURI();
	public final static String algorithmKey =  OpenTox.URI.algorithm.getKey();
	public final static String resourceID =  OpenTox.URI.algorithm.getResourceID();	
	protected static List<Algorithm<String>> algorithmList;
	

	
	private LiteratureEntry toxTreeReference = new LiteratureEntry("User input","http://toxtree.sourceforge.net");
	private Object[][] algorithms = new Object[][] {
			//id,class,name
			{"SimpleKMeans","Clustering: k-means","weka.clusterers.SimpleKMeans",null,
				new String[] {Algorithm.typeClustering,Algorithm.typeSingleTarget,Algorithm.typeLazyLearning,Algorithm.typeUnSupervised}},
			{"J48","Classification: Decision tree J48","weka.classifiers.trees.J48",null,
					new String[] {Algorithm.typeClassification,Algorithm.typeSingleTarget,Algorithm.typeEagerLearning,Algorithm.typeSupervised}},
			{"LR","Regression: Linear regression","weka.classifiers.functions.LinearRegression",null,
						new String[] {Algorithm.typeRegression,Algorithm.typeSingleTarget,Algorithm.typeEagerLearning,Algorithm.typeSupervised}},
			{"pka","pKa","ambit2.descriptors.PKASmartsDescriptor",null,new String[] {Algorithm.typeRules}},
			{"toxtreecramer","ToxTree: Cramer rules","toxTree.tree.cramer.CramerRules",null,new String[] {Algorithm.typeRules}},
			{"toxtreecramer2","ToxTree: Extended Cramer rules","cramer2.CramerRulesWithExtensions",null,new String[] {Algorithm.typeRules}},
			{"toxtreeeye","ToxTree: Eye irritation","eye.EyeIrritationRules",
				new Property[] 
				{
				new Property("MolWeight","A", toxTreeReference),
				new Property("Melting Point","°C", toxTreeReference),
				new Property("LogP","", toxTreeReference),
				new Property("Lipid Solubility","g/kg", toxTreeReference),
				new Property("Water Solubility","g/l", toxTreeReference),
				},
				new String[] {Algorithm.typeRules}
			},
			{"toxtreeskinirritation","ToxTree: Skin irritation","sicret.SicretRules",
			new Property[] 
				{
				new Property("MolWeight","A", toxTreeReference),
				new Property("Melting Point","°C", toxTreeReference),
				new Property("LogP","", toxTreeReference),
				new Property("Lipid Solubility","g/kg", toxTreeReference),
				new Property("Water Solubility","g/l", toxTreeReference),
				new Property("Vapour Pressure","Pa", toxTreeReference),
				new Property("Surface Tension","mN/m", toxTreeReference)
				},
				new String[] {Algorithm.typeRules}
			},
			{"toxtreemic","ToxTree: Structure Alerts for the in vivo micronucleus assay in rodents","mic.MICRules",null,new String[] {Algorithm.typeRules}},
			{"toxtreemichaelacceptors","ToxTree: Michael acceptors","michaelacceptors.MichaelAcceptorRules",null,new String[] {Algorithm.typeRules}},
			{"toxtreecarc","ToxTree: Benigni/Bossa rules for carcinogenicity and mutagenicity","mutant.BB_CarcMutRules",null,new String[] {Algorithm.typeRules}},
			//{"ToxTree: START biodegradation and persistence plug-in","mutant.BB_CarcMutRules",null},
			{"toxtreekroes","ToxTree: ILSI/Kroes decision tree for TTC","toxtree.plugins.kroes.Kroes1Tree",
				new Property[] {
				new Property("DailyIntake","\u00B5g/day", toxTreeReference)
			},new String[] {Algorithm.typeRules}},
	};
	
	public Representation getRepresentation(Variant variant) throws ResourceException {
		return get(variant);
	}
	
	protected void initList() {
		if (algorithmList==null) {
			algorithmList = new ArrayList<Algorithm<String>>();
			for (Object[] d : algorithms) {
				Algorithm<String> alg = new Algorithm<String>(d[1].toString());
				alg.setType((String[])d[4]);
				alg.setFormat(alg.hasType(Algorithm.typeRules)?AlgorithmFormat.JAVA_CLASS:AlgorithmFormat.WEKA);
				alg.setId(d[0].toString());
				alg.setName(d[1].toString());
				alg.setContent(d[2].toString());
				if (d[3]==null)
					alg.setInput(new Template("Empty"));
				else {
					Template predictors = new Template(String.format("Predictors-%s",d[1]));
					for (Property p : (Property[])d[3])
						predictors.add(p);
					alg.setInput(predictors);
				}					
				algorithmList.add(alg);
			}				
		}
	}

	protected Algorithm<String> find(Object key) {
		key = Reference.decode(key.toString());
		Algorithm<String> q = new Algorithm<String>();
		q.setId(key.toString());
		int index = algorithmList.indexOf(q);
		if (index <0) 
			return null;					
		else  
			return algorithmList.get(index);
	}
	@Override
	protected Iterator<Algorithm<String>> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		try {
			initList();

			Object key = getRequest().getAttributes().get(algorithmKey);
			
			if (key==null)
				return algorithmList.iterator();
			else { 
				Algorithm<String> a = find(Reference.decode(key.toString()));
				if (a == null) {
					getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
					return null;					
				} else { 
					ArrayList<Algorithm<String>> q = new ArrayList<Algorithm<String>>();
					q.add(a);
					return q.iterator();
				}
			}

		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
	}
	@Override
	public IProcessor<Iterator<Algorithm<String>>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {

		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(
					new AlgorithmHTMLReporter(getRequest(),
							getRequest().getAttributes().get(algorithmKey)==null
							),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			AlgorithmURIReporter r = new AlgorithmURIReporter(getRequest()) {
				@Override
				public void processItem(Algorithm item, Writer output) {
					super.processItem(item, output);
					try {output.write('\n');} catch (Exception x) {}
				}
			};
			return new StringConvertor(	r,MediaType.TEXT_URI_LIST);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
				) {
			return new StringConvertor(
					new AlgorithmRDFReporter(getRequest(),variant.getMediaType())
					,variant.getMediaType());					
		} else //html 	
			return new StringConvertor(
					new CatalogHTMLReporter(getRequest()),MediaType.TEXT_HTML);
		
	}
				
	protected Reference getSourceReference() throws ResourceException {
		return null;
		/*
		Form form = getRequest().getResourceRef().getQueryAsForm();
		Object datasetURI = form.getFirstValue(dataset_uri);
		if (datasetURI==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Empty %s", dataset_uri));
		return new Reference(Reference.decode(datasetURI.toString()));
		*/
	}
	@Override
	protected Reference getSourceReference(Form form,Algorithm<String> model)
			throws ResourceException {
		if (model.hasType(Algorithm.typeRules)) return null;
		Object datasetURI = OpenTox.params.dataset_uri.getFirstValue(form);
		if (datasetURI==null) 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Empty %s [%s]", OpenTox.params.dataset_uri.toString(), OpenTox.params.dataset_uri.getDescription()));
		return new Reference(Reference.decode(datasetURI.toString()));		
	}

	@Override
	protected CallableQueryProcessor createCallable(Form form,
			Algorithm<String> algorithm)
			throws ResourceException {
				
		try {
			System.out.println("Create callable");
			if (algorithm.hasType(Algorithm.typeRules))
				return new CallableSimpleModelCreator(
						form,
						getRequest().getRootRef(),
						getContext(),
						algorithm,
						new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()),
						new AlgorithmURIReporter(getRequest())
						);	
			else if (algorithm.hasType(Algorithm.typeDescriptor)) {
				try {
					CallableSimpleModelCreator mc = new CallableSimpleModelCreator(
							form,
							getRequest().getRootRef(),
							getContext(),
							algorithm,
							new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()),
							new AlgorithmURIReporter(getRequest())					
							);
					ModelQueryResults model = mc.createModel();
					return new CallableDescriptorCalculator(
							form,
							getRequest().getRootRef(),
							getContext(),
							model,
							new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()));
				} catch (Exception x) {
					throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
				}
			} else {
					
				return new CallableWekaModelCreator(
						form,
						getRequest().getRootRef(),
						getContext(),
						algorithm,
						new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()),
						new AlgorithmURIReporter(getRequest()));	
			} 
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} finally {
			//try { connection.close(); } catch (Exception x) {}
		}
		

			
	}
}
