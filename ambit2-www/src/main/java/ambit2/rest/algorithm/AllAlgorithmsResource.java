package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

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
import ambit2.db.processors.FP1024Writer.FPTable;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.OpenTox;
import ambit2.rest.StringConvertor;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.model.predictor.DescriptorPredictor;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.task.CallableDescriptorCalculator;
import ambit2.rest.task.CallableFingerprintsModelCreator;
import ambit2.rest.task.CallableMockup;
import ambit2.rest.task.CallableNumericalModelCreator;
import ambit2.rest.task.CallablePOST;
import ambit2.rest.task.CallableSimpleModelCreator;
import ambit2.rest.task.CallableWekaModelCreator;
import ambit2.rest.task.dbpreprocessing.CallableFingerprintsCalculator;

public class AllAlgorithmsResource extends CatalogResource<Algorithm<String>> {
	public final static String algorithm = OpenTox.URI.algorithm.getURI();
	public final static String algorithmKey =  OpenTox.URI.algorithm.getKey();
	public final static String resourceID =  OpenTox.URI.algorithm.getResourceID();	
	protected static List<Algorithm<String>> algorithmList;


	
	private LiteratureEntry toxTreeReference = new LiteratureEntry("User input","http://toxtree.sourceforge.net");
	private Object[][] algorithms = new Object[][] {
			//id,class,name
			
			{"SimpleKMeans","Clustering: k-means","weka.clusterers.SimpleKMeans",null,
				new String[] {Algorithm.typeClustering,Algorithm.typeSingleTarget,Algorithm.typeLazyLearning,Algorithm.typeUnSupervised},
				null,Algorithm.requires.property},
			{"J48","Classification: Decision tree J48","weka.classifiers.trees.J48",null,
					new String[] {Algorithm.typeClassification,Algorithm.typeSingleTarget,Algorithm.typeEagerLearning,Algorithm.typeSupervised},null,Algorithm.requires.property},
			{"LR","Regression: Linear regression","weka.classifiers.functions.LinearRegression",null,
						new String[] {Algorithm.typeRegression,Algorithm.typeSingleTarget,Algorithm.typeEagerLearning,Algorithm.typeSupervised},null,Algorithm.requires.property},
			{"pka","pKa","ambit2.descriptors.PKASmartsDescriptor",null,new String[] {Algorithm.typeRules},"http://www.opentox.org/echaEndpoints.owl#Dissociation_constant_pKa",Algorithm.requires.structure},
			{"toxtreecramer","ToxTree: Cramer rules","toxTree.tree.cramer.CramerRules",null,new String[] {Algorithm.typeRules},"http://www.opentox.org/echaEndpoints.owl#HumanHealthEffects",Algorithm.requires.structure},
			{"toxtreecramer2","ToxTree: Extended Cramer rules","cramer2.CramerRulesWithExtensions",null,new String[] {Algorithm.typeRules},"http://www.opentox.org/echaEndpoints.owl#HumanHealthEffects",Algorithm.requires.structure},
			{"toxtreeeye","ToxTree: Eye irritation","eye.EyeIrritationRules",
				new Property[] 
				{
				new Property("MolWeight","A", toxTreeReference),
				new Property("Melting Point","°C", toxTreeReference),
				new Property("LogP","", toxTreeReference),
				new Property("Lipid Solubility","g/kg", toxTreeReference),
				new Property("Water Solubility","g/l", toxTreeReference),
				},
				new String[] {Algorithm.typeRules},
				"http://www.opentox.org/echaEndpoints.owl#Eye_irritation_corrosion",
				Algorithm.requires.structure
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
				new String[] {Algorithm.typeRules},
				"http://www.opentox.org/echaEndpoints.owl#SkinIrritationCorrosion",
				Algorithm.requires.structure
			},
			{"toxtreemic","ToxTree: Structure Alerts for the in vivo micronucleus assay in rodents","mic.MICRules",null,new String[] {Algorithm.typeRules},
				"http://www.opentox.org/echaEndpoints.owl#Endpoints",Algorithm.requires.structure},
			{"toxtreeskinsens","ToxTree: Skin sensitisation alerts (M. Cronin)","toxtree.plugins.skinsensitisation.SkinSensitisationPlugin",null,new String[] {Algorithm.typeRules},
				"http://www.opentox.org/echaEndpoints.owl#SkinSensitisation"
				,Algorithm.requires.structure
				},
			{"toxtreesmartcyp","SmartCYP: Cytochrome P450-Mediated Drug Metabolism","toxtree.plugins.smartcyp.SMARTCYPPlugin",null,new String[] {Algorithm.typeRules},
					"http://www.opentox.org/echaEndpoints.owl#Protein-binding"
					,Algorithm.requires.structure
					},				
			{"toxtreemichaelacceptors","ToxTree: Michael acceptors","michaelacceptors.MichaelAcceptorRules",null,new String[] {Algorithm.typeRules},
					"http://www.opentox.org/echaEndpoints.owl#SkinSensitisation"
					,Algorithm.requires.structure
					},
			{"toxtreecarc","ToxTree: Benigni/Bossa rules for carcinogenicity and mutagenicity","mutant.BB_CarcMutRules",null,new String[] {Algorithm.typeRules},
				"http://www.opentox.org/echaEndpoints.owl#Carcinogenicity",Algorithm.requires.structure},
			//{"ToxTree: START biodegradation and persistence plug-in","mutant.BB_CarcMutRules",null},
			{"toxtreebiodeg","START biodegradation and persistence plug-in","com.molecularnetworks.start.BiodgeradationRules",null,new String[] {Algorithm.typeRules},"http://www.opentox.org/echaEndpoints.owl#PersistenceBiodegradation",Algorithm.requires.structure},				
			
			{"toxtreekroes","ToxTree: ILSI/Kroes decision tree for TTC","toxtree.plugins.kroes.Kroes1Tree",
				new Property[] {
				new Property("DailyIntake","\u00B5g/day", toxTreeReference)
			},new String[] {Algorithm.typeRules}, "http://www.opentox.org/echaEndpoints.owl#HumanHealthEffects",Algorithm.requires.structure},
			
			/** Descriptors */
			{"org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor","XLogP","org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor",null,new String[] {Algorithm.typeDescriptor},"http://www.opentox.org/echaEndpoints.owl#Octanol-water_partition_coefficient_Kow",Algorithm.requires.structure},
			{"ambit2.descriptors.MolecularWeight","MolecularWeight","ambit2.descriptors.MolecularWeight",null,new String[] {Algorithm.typeDescriptor},"http://www.opentox.org/echaEndpoints.owl#MolecularWeight",Algorithm.requires.structure},
			{"org.openscience.cdk.qsar.descriptors.molecular.RuleOfFiveDescriptor","Lipinski Rule of Five","org.openscience.cdk.qsar.descriptors.molecular.RuleOfFiveDescriptor",null,new String[] {Algorithm.typeDescriptor}, "http://www.opentox.org/echaEndpoints.owl#HumanHealthEffects",Algorithm.requires.structure},
			{"org.openscience.cdk.qsar.descriptors.molecular.WHIMDescriptor","WHIM descriptors","org.openscience.cdk.qsar.descriptors.molecular.WHIMDescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},
			{"org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor","TPSA descriptor","org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},
			
			{"org.openscience.cdk.qsar.descriptors.molecular.CPSADescriptor","CPSA descriptor","org.openscience.cdk.qsar.descriptors.molecular.CPSADescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},
			{"org.openscience.cdk.qsar.descriptors.molecular.AromaticAtomsCountDescriptor","Number of aromatic atoms","org.openscience.cdk.qsar.descriptors.molecular.AromaticAtomsCountDescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},
			{"org.openscience.cdk.qsar.descriptors.molecular.AromaticBondsCountDescriptor","Number of aromatic bonds","org.openscience.cdk.qsar.descriptors.molecular.AromaticBondsCountDescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},	
			
			{"org.openscience.cdk.qsar.descriptors.molecular.BondCountDescriptor","Number of bonds","org.openscience.cdk.qsar.descriptors.molecular.BondCountDescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},
			{"org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor","Number of atoms","org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},
			{"org.openscience.cdk.qsar.descriptors.molecular.RotatableBondsCountDescriptor","Number of rotatable bonds","org.openscience.cdk.qsar.descriptors.molecular.RotatableBondsCountDescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},
			
			{"org.openscience.cdk.qsar.descriptors.molecular.ChiChainDescriptor","Chi chain descriptor","org.openscience.cdk.qsar.descriptors.molecular.ChiChainDescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},
			{"org.openscience.cdk.qsar.descriptors.molecular.ChiClusterDescriptor","Chi cluster descriptor","org.openscience.cdk.qsar.descriptors.molecular.ChiClusterDescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},			
			{"org.openscience.cdk.qsar.descriptors.molecular.ChiPathClusterDescriptor","Chi path descriptor","org.openscience.cdk.qsar.descriptors.molecular.ChiPathClusterDescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},
			{"org.openscience.cdk.qsar.descriptors.molecular.ChiPathDescriptor","Chi path descriptor","org.openscience.cdk.qsar.descriptors.molecular.ChiPathDescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},
			
			{"org.openscience.cdk.qsar.descriptors.molecular.LargestChainDescriptor","Largest chain","org.openscience.cdk.qsar.descriptors.molecular.LargestChainDescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},
			{"org.openscience.cdk.qsar.descriptors.molecular.LargestPiSystemDescriptor","Largest Pi system","org.openscience.cdk.qsar.descriptors.molecular.LargestPiSystemDescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},

			{"org.openscience.cdk.qsar.descriptors.molecular.HBondAcceptorCountDescriptor","Hydrogen Bond acceptors","org.openscience.cdk.qsar.descriptors.molecular.HBondAcceptorCountDescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},
			{"org.openscience.cdk.qsar.descriptors.molecular.HBondDonorCountDescriptor","Hydrogen Bond donors","org.openscience.cdk.qsar.descriptors.molecular.HBondDonorCountDescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},
			
			{"org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor","BCUT descriptors","org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},
			{"org.openscience.cdk.qsar.descriptors.molecular.ZagrebIndexDescriptor","ZagrebIndex","org.openscience.cdk.qsar.descriptors.molecular.ZagrebIndexDescriptor",null,new String[] {Algorithm.typeDescriptor},null,Algorithm.requires.structure},
			
			
			
			
			
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
			{"pcaRanges","Applicability domain: PCA ranges","ambit2.model.numeric.DataCoverageDescriptors",null,new String[] {Algorithm.typeAppDomain},null,Algorithm.requires.property},
			{"distanceEuclidean","Applicability domain: Euclidean distance","ambit2.model.numeric.distance.DataCoverageDistanceEuclidean",null,new String[] {Algorithm.typeAppDomain},null,Algorithm.requires.property},
			{"distanceCityBlock","Applicability domain: Cityblock distance","ambit2.model.numeric.distance.DataCoverageDistanceCityBlock",null,new String[] {Algorithm.typeAppDomain},null,Algorithm.requires.property},
			{"distanceMahalanobis","Applicability domain: Mahalanobis distance","ambit2.model.numeric.distance.DataCoverageDistanceMahalanobis",null,new String[] {Algorithm.typeAppDomain},null,Algorithm.requires.property},			
			{"nparamdensity","Applicability domain: nonparametric density estimation","ambit2.model.numeric.DataCoverageDensity",null,new String[] {Algorithm.typeAppDomain},null,Algorithm.requires.property},
			{"leverage","Applicability domain: Leverage","ambit2.model.numeric.DataCoverageLeverage",null,new String[] {Algorithm.typeAppDomain},null,Algorithm.requires.property},
			{"fptanimoto","Applicability domain: Fingerprints, Tanimoto distance to a consensus fingerprints","ambit2.model.structure.DataCoverageFingerprintsTanimoto",null,new String[] {Algorithm.typeAppDomain},null,Algorithm.requires.structure},
			{"fpmissingfragments","Applicability domain: Fingerprints, Missing fragments","ambit2.model.structure.DataCoverageFingeprintsMissingFragments",null,new String[] {Algorithm.typeAppDomain},null,Algorithm.requires.structure},
			
			{"fingerprints","Generate fingerprints","fp1024",null,new String[] {Algorithm.typeFingerprints},null,Algorithm.requires.structure},
			{"structurequality","Structure quality workflow","fp1024_struc",null,new String[] {Algorithm.typeFingerprints},null,Algorithm.requires.structure},
			{"struckeys","Generate structure keys","sk1024",null,new String[] {Algorithm.typeFingerprints},null,Algorithm.requires.structure},
			{"smartsprop","Generate SMARTS accelerator data","smarts_accelerator",null,new String[] {Algorithm.typeFingerprints},null,Algorithm.requires.structure},
			
			{"superservice","Calls a remote service",null,null,new String[] {Algorithm.typeSuperService},null,null},
			{"mockup","Sleeps for 'delay' milliseconds, returns 'dataset_uri' or 'model_uri', specified on input. For testing purposes",null,null,new String[] {Algorithm.typeMockup},null,null}
			
			
			
	};
	
	public Representation getRepresentation(Variant variant) throws ResourceException {
		return get(variant);
	}
	
	protected void initList() {
		if (algorithmList==null) {
			algorithmList = new ArrayList<Algorithm<String>>();
			for (Object[] d : algorithms) {
				System.out.println(d);
				Algorithm<String> alg = new Algorithm<String>(d[1].toString());
				alg.setType((String[])d[4]);
				alg.setFormat(alg.hasType(Algorithm.typeRules)||alg.hasType(Algorithm.typeFingerprints)?AlgorithmFormat.JAVA_CLASS:
						alg.hasType(Algorithm.typeAppDomain)?AlgorithmFormat.COVERAGE_SERIALIZED:AlgorithmFormat.WEKA);
				alg.setId(d[0].toString());
				alg.setName(d[1].toString());
				alg.setContent(d[2]==null?null:d[2].toString());
				alg.setEndpoint(d[5]==null?null:d[5].toString());
				if (d[3]==null)
					alg.setInput(new Template("Empty"));
				else {
					Template predictors = new Template(String.format("Predictors-%s",d[1]));
					for (Property p : (Property[])d[3])
						predictors.add(p);
					alg.setInput(predictors);
				}					
				alg.setRequirement((Algorithm.requires)d[6]);
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
		if (model.hasType(Algorithm.typeFingerprints)) return null;
		if (model.hasType(Algorithm.typeMockup)) return null;
		if (model.hasType(Algorithm.typeSuperService)) return null;
		Object datasetURI = OpenTox.params.dataset_uri.getFirstValue(form);
		if (datasetURI==null) 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Empty %s [%s]", OpenTox.params.dataset_uri.toString(), OpenTox.params.dataset_uri.getDescription()));
		return new Reference(Reference.decode(datasetURI.toString()));		
	}

	@Override
	protected Callable<Reference> createCallable(Form form,
			Algorithm<String> algorithm)
			throws ResourceException {
				
		try {
			if (algorithm.hasType(Algorithm.typeSuperService))  {
				return new CallablePOST(form);			
			} else if (algorithm.hasType(Algorithm.typeMockup))  {
				return new CallableMockup(form);
			} else if (algorithm.hasType(Algorithm.typeRules))
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
				} catch (ResourceException x) {
					throw x;
				} catch (Exception x) {
					throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
				}
			} else if (algorithm.hasType(Algorithm.typeAppDomain)) {				
				switch (algorithm.getRequirement()) {
				case structure: {
					return new CallableFingerprintsModelCreator(
							form,
							getRequest().getRootRef(),
							getContext(),
							algorithm,
							new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()),
							new AlgorithmURIReporter(getRequest()));						
				}
				case property: {
					return new CallableNumericalModelCreator(
							form,
							getRequest().getRootRef(),
							getContext(),
							algorithm,
							new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()),
							new AlgorithmURIReporter(getRequest()));					
				}		
				default: {
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,algorithm.toString());
				}
				}
			} else if (algorithm.hasType(Algorithm.typeFingerprints)) {				
					return new CallableFingerprintsCalculator(
							form,
							getRequest().getRootRef(),
							getContext(),
							algorithm);						
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
