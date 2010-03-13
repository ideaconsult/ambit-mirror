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
import ambit2.rest.model.builder.SimpleModelBuilder;
import ambit2.rest.model.predictor.DescriptorPredictor;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.task.CallableDescriptorCalculator;
import ambit2.rest.task.CallableNumericalModelCreator;
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
			
			/** Descriptors */
			{"org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor","XLogP","org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor",null,new String[] {Algorithm.typeDescriptor}},
			{"ambit2.descriptors.MolecularWeight","MolecularWeight","ambit2.descriptors.MolecularWeight",null,new String[] {Algorithm.typeDescriptor}},
			{"org.openscience.cdk.qsar.descriptors.molecular.RuleOfFiveDescriptor","Lipinski Rule of Five","org.openscience.cdk.qsar.descriptors.molecular.RuleOfFiveDescriptor",null,new String[] {Algorithm.typeDescriptor}},
			{"org.openscience.cdk.qsar.descriptors.molecular.WHIMDescriptor","WHIM descriptors","org.openscience.cdk.qsar.descriptors.molecular.WHIMDescriptor",null,new String[] {Algorithm.typeDescriptor}},
			{"org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor","TPSA descriptor","org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor",null,new String[] {Algorithm.typeDescriptor}},
			
			{"org.openscience.cdk.qsar.descriptors.molecular.CPSADescriptor","CPSA descriptor","org.openscience.cdk.qsar.descriptors.molecular.CPSADescriptor",null,new String[] {Algorithm.typeDescriptor}},
			{"org.openscience.cdk.qsar.descriptors.molecular.AromaticAtomsCountDescriptor","Number of aromatic atoms","org.openscience.cdk.qsar.descriptors.molecular.AromaticAtomsCountDescriptor",null,new String[] {Algorithm.typeDescriptor}},
			{"org.openscience.cdk.qsar.descriptors.molecular.AromaticBondsCountDescriptor","Number of aromatic bonds","org.openscience.cdk.qsar.descriptors.molecular.AromaticBondsCountDescriptor",null,new String[] {Algorithm.typeDescriptor}},			{"tpsa","TPSA descriptor","org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor",null,new String[] {Algorithm.typeDescriptor}},
			{"org.openscience.cdk.qsar.descriptors.molecular.BondCountDescriptor","Number of bonds","org.openscience.cdk.qsar.descriptors.molecular.BondCountDescriptor",null,new String[] {Algorithm.typeDescriptor}},
			{"org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor","Number of atoms","org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor",null,new String[] {Algorithm.typeDescriptor}},
			{"org.openscience.cdk.qsar.descriptors.molecular.RotatableBondsCountDescriptor","Number of rotatable bonds","org.openscience.cdk.qsar.descriptors.molecular.RotatableBondsCountDescriptor",null,new String[] {Algorithm.typeDescriptor}},
			
			{"org.openscience.cdk.qsar.descriptors.molecular.ChiChainDescriptor","Chi chain descriptor","org.openscience.cdk.qsar.descriptors.molecular.ChiChainDescriptor",null,new String[] {Algorithm.typeDescriptor}},
			{"org.openscience.cdk.qsar.descriptors.molecular.ChiClusterDescriptor","Chi cluster descriptor","org.openscience.cdk.qsar.descriptors.molecular.ChiClusterDescriptor",null,new String[] {Algorithm.typeDescriptor}},			
			{"org.openscience.cdk.qsar.descriptors.molecular.ChiPathClusterDescriptor","Chi path descriptor","org.openscience.cdk.qsar.descriptors.molecular.ChiPathClusterDescriptor",null,new String[] {Algorithm.typeDescriptor}},
			{"org.openscience.cdk.qsar.descriptors.molecular.ChiPathDescriptor","Chi path descriptor","org.openscience.cdk.qsar.descriptors.molecular.ChiPathDescriptor",null,new String[] {Algorithm.typeDescriptor}},
			
			{"org.openscience.cdk.qsar.descriptors.molecular.LargestChainDescriptor","Largest chain","org.openscience.cdk.qsar.descriptors.molecular.LargestChainDescriptor",null,new String[] {Algorithm.typeDescriptor}},
			{"org.openscience.cdk.qsar.descriptors.molecular.LargestPiSystemDescriptor","Largest Pi system","org.openscience.cdk.qsar.descriptors.molecular.LargestPiSystemDescriptor",null,new String[] {Algorithm.typeDescriptor}},

			{"org.openscience.cdk.qsar.descriptors.molecular.HBondAcceptorCountDescriptor","Largest chain","org.openscience.cdk.qsar.descriptors.molecular.HBondAcceptorCountDescriptor",null,new String[] {Algorithm.typeDescriptor}},
			{"org.openscience.cdk.qsar.descriptors.molecular.HBondDonorCountDescriptor","Largest Pi system","org.openscience.cdk.qsar.descriptors.molecular.HBondDonorCountDescriptor",null,new String[] {Algorithm.typeDescriptor}},
			
			{"org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor","BCUT descriptors","org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor",null,new String[] {Algorithm.typeDescriptor}},
			{"org.openscience.cdk.qsar.descriptors.molecular.ZagrebIndexDescriptor","ZagrebIndex","org.openscience.cdk.qsar.descriptors.molecular.ZagrebIndexDescriptor",null,new String[] {Algorithm.typeDescriptor}},			
			
/*
 * ;org.openscience.cdk.qsar.descriptors.molecular.ALOGPDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.APolDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.AminoAcidCountDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.AromaticAtomsCountDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.AromaticBondsCountDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorCharge
;org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorMass
;org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorPolarizability
;org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.BPolDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.BondCountDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.CPSADescriptor
;org.openscience.cdk.qsar.descriptors.molecular.CarbonTypesDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.ChiChainDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.ChiClusterDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.ChiPathClusterDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.ChiPathDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.EccentricConnectivityIndexDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.FragmentComplexityDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.GravitationalIndexDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.HBondAcceptorCountDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.HBondDonorCountDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.IPMolecularDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.KappaShapeIndicesDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.LargestChainDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.LargestPiSystemDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.LengthOverBreadthDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.LongestAliphaticChainDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.MDEDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.MomentOfInertiaDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.PetitjeanNumberDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.PetitjeanShapeIndexDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.RotatableBondsCountDescriptor
org.openscience.cdk.qsar.descriptors.molecular.RuleOfFiveDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor
;org.openscience.cdk.qsar.descriptors.molecular.VAdjMaDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.WHIMDescriptor
ambit2.descriptors.MolecularWeight
;org.openscience.cdk.qsar.descriptors.molecular.WienerNumbersDescriptor
org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor
;org.openscience.cdk.qsar.descriptors.molecular.ZagrebIndexDescriptor
 */
			{"pcaRanges","Applicability domain: PCA ranges","ambit2.model.numeric.DataCoverageDescriptors",null,new String[] {Algorithm.typeAppDomain}},
			{"distanceEuclidean","Applicability domain: Euclidean distance","ambit2.model.numeric.distance.DataCoverageDistanceEuclidean",null,new String[] {Algorithm.typeAppDomain}},
			{"distanceCityBlock","Applicability domain: Cityblock distance","ambit2.model.numeric.distance.DataCoverageDistanceCityBlock",null,new String[] {Algorithm.typeAppDomain}},
			{"distanceMahalanobis","Applicability domain: Mahalanobis distance","ambit2.model.numeric.distance.DataCoverageDistanceMahalanobis",null,new String[] {Algorithm.typeAppDomain}},			
			{"nparamdensity","Applicability domain: nonparametric density estimation","ambit2.model.numeric.DataCoverageDensity",null,new String[] {Algorithm.typeAppDomain}},
			{"leverage","Applicability domain: Leverage","ambit2.model.numeric.DataCoverageLeverage",null,new String[] {Algorithm.typeAppDomain}}
			
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
				alg.setFormat(alg.hasType(Algorithm.typeRules)?AlgorithmFormat.JAVA_CLASS:
						alg.hasType(Algorithm.typeAppDomain)?AlgorithmFormat.COVERAGE_SERIALIZED:AlgorithmFormat.WEKA);
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
						new AlgorithmURIReporter(getRequest()),
						false
						);	
			else if (algorithm.hasType(Algorithm.typeDescriptor)) {
				try {
					CallableSimpleModelCreator modelCreator = new CallableSimpleModelCreator(
							form,
							getRequest().getRootRef(),
							getContext(),
							algorithm,
							new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()),
							new AlgorithmURIReporter(getRequest()),
							true
							);	
					Reference modelRef = modelCreator.call();
					ModelQueryResults model = modelCreator.getModel();
					
					DescriptorPredictor predictor = new DescriptorPredictor(
							getRequest().getRootRef(),
							model,
							new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()),
							new PropertyURIReporter(getRequest()),
							null
							);
					return
					new CallableDescriptorCalculator(
							form,
							getRequest().getRootRef(),
							getContext(),
							predictor
							);					
					/*
					SimpleModelBuilder builder = new SimpleModelBuilder(
							getRequest().getRootRef(),
							new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()),
							new AlgorithmURIReporter(getRequest())					
							);
					ModelQueryResults model = builder.process(algorithm);
					
					DescriptorPredictor predictor = new DescriptorPredictor(
							getRequest().getRootRef(),
							model,
							new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()),
							new PropertyURIReporter(getRequest()),
							null
								);
					return new CallableDescriptorCalculator(
							form,
							getRequest().getRootRef(),
							getContext(),
							predictor
							);
							*/
				} catch (ResourceException x) {
					throw x;
				} catch (Exception x) {
					throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
				}
			} else if (algorithm.hasType(Algorithm.typeAppDomain)) {				
				
				return new CallableNumericalModelCreator(
						form,
						getRequest().getRootRef(),
						getContext(),
						algorithm,
						new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()),
						new AlgorithmURIReporter(getRequest()));					
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
