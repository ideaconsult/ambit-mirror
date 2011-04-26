package ambit2.rest.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.core.data.model.AlgorithmType;

public class AlgorithmsPile {
	private static final LiteratureEntry toxTreeReference = new LiteratureEntry("User input","http://toxtree.sourceforge.net");
	private static final Object[][] algorithms = new Object[][] {
			//id,class,name
			
			{"SimpleKMeans","Clustering: k-means","weka.clusterers.SimpleKMeans",null,
				new String[] {AlgorithmType.Clustering.toString(),AlgorithmType.SingleTarget.toString(),AlgorithmType.LazyLearning.toString(),AlgorithmType.UnSupervised.toString()},
				null,Algorithm.requires.property},
			{"J48","Classification: Decision tree J48","weka.classifiers.trees.J48",null,
					new String[] {AlgorithmType.Classification.toString(),AlgorithmType.SingleTarget.toString(),AlgorithmType.EagerLearning.toString(),AlgorithmType.Supervised.toString()},null,Algorithm.requires.property},
			{"LR","Regression: Linear regression","weka.classifiers.functions.LinearRegression",null,
						new String[] {AlgorithmType.Regression.toString(),AlgorithmType.SingleTarget.toString(),AlgorithmType.EagerLearning.toString(),AlgorithmType.Supervised.toString()},null,Algorithm.requires.property},
/*
			{"PCA","Principal Component Analysis","weka.attributeSelection.PrincipalComponents",null,
							new String[] {AlgorithmType.FeatureSelection,AlgorithmType.MultipleTarget,AlgorithmType.EagerLearning.toString(),AlgorithmType.UnSupervised.toString()},null,Algorithm.requires.property},
*/						
			{"pka","pKa","ambit2.descriptors.PKASmartsDescriptor",null,new String[] {AlgorithmType.Rules.toString()},"http://www.opentox.org/echaEndpoints.owl#Dissociation_constant_pKa",Algorithm.requires.structure,"http://ambit.sourceforge.net/descriptors.owl#pkaSMARTS"},
			
			{"categories","OECD Categories","ambit2.descriptors.FunctionalGroupDescriptor",null,new String[] {AlgorithmType.Rules.toString()},"http://www.opentox.org/echaEndpoints.owl#Endpoints",Algorithm.requires.structure,"http://ambit.sourceforge.net/descriptors.owl#categories"},
			
			
			{"toxtreecramer","ToxTree: Cramer rules","toxTree.tree.cramer.CramerRules",null,new String[] {AlgorithmType.Rules.toString()},"http://www.opentox.org/echaEndpoints.owl#HumanHealthEffects",Algorithm.requires.structure,"http://ambit.sourceforge.net/descriptors.owl#toxtreecramer"},
			{"toxtreecramer2","ToxTree: Extended Cramer rules","cramer2.CramerRulesWithExtensions",null,new String[] {AlgorithmType.Rules.toString()},"http://www.opentox.org/echaEndpoints.owl#HumanHealthEffects",Algorithm.requires.structure},
			{"toxtreeverhaar","ToxTree: Verhaar scheme for predicting toxicity mode of action","verhaar.VerhaarScheme",null,new String[] {AlgorithmType.Rules.toString()},"http://www.opentox.org/echaEndpoints.owl#Acute_toxicity_to_fish_lethality",Algorithm.requires.structure},
			{"toxtreeeye","ToxTree: Eye irritation","eye.EyeIrritationRules",
				new Property[] 
				{
				new Property("MolWeight","A", toxTreeReference),
				new Property("Melting Point","\u2103C", toxTreeReference),
				new Property("LogP","", toxTreeReference),
				new Property("Lipid Solubility","g/kg", toxTreeReference),
				new Property("Water Solubility","g/l", toxTreeReference),
				},
				new String[] {AlgorithmType.Rules.toString()},
				"http://www.opentox.org/echaEndpoints.owl#Eye_irritation_corrosion",
				Algorithm.requires.structure
			},
			{"toxtreeskinirritation","ToxTree: Skin irritation","sicret.SicretRules",
			new Property[] 
				{
				new Property("MolWeight","A", toxTreeReference),
				new Property("Melting Point","\u2103C", toxTreeReference),
				new Property("LogP","", toxTreeReference),
				new Property("Lipid Solubility","g/kg", toxTreeReference),
				new Property("Water Solubility","g/l", toxTreeReference),
				new Property("Vapour Pressure","Pa", toxTreeReference),
				new Property("Surface Tension","mN/m", toxTreeReference)
				},
				new String[] {AlgorithmType.Rules.toString()},
				"http://www.opentox.org/echaEndpoints.owl#SkinIrritationCorrosion",
				Algorithm.requires.structure
			},
			{"toxtreemic","ToxTree: Structure Alerts for the in vivo micronucleus assay in rodents","mic.MICRules",null,new String[] {AlgorithmType.Rules.toString()},
				"http://www.opentox.org/echaEndpoints.owl#Endpoints",Algorithm.requires.structure},
			{"toxtreeskinsens","ToxTree: Skin sensitisation alerts (M. Cronin)","toxtree.plugins.skinsensitisation.SkinSensitisationPlugin",null,new String[] {AlgorithmType.Rules.toString()},
				"http://www.opentox.org/echaEndpoints.owl#SkinSensitisation"
				,Algorithm.requires.structure
				},
			{"toxtreesmartcyp","SmartCYP: Cytochrome P450-Mediated Drug Metabolism","toxtree.plugins.smartcyp.SMARTCYPPlugin",null,new String[] {AlgorithmType.Rules.toString()},
					"http://www.opentox.org/echaEndpoints.owl#Protein-binding"
					,Algorithm.requires.structure
					},				
			{"toxtreemichaelacceptors","ToxTree: Michael acceptors","michaelacceptors.MichaelAcceptorRules",null,new String[] {AlgorithmType.Rules.toString()},
					"http://www.opentox.org/echaEndpoints.owl#SkinSensitisation"
					,Algorithm.requires.structure
					},
			{"toxtreecarc","ToxTree: Benigni/Bossa rules for carcinogenicity and mutagenicity","mutant.BB_CarcMutRules",null,new String[] {AlgorithmType.Rules.toString()},
				"http://www.opentox.org/echaEndpoints.owl#Carcinogenicity",Algorithm.requires.structure},
			//{"ToxTree: START biodegradation and persistence plug-in","mutant.BB_CarcMutRules",null},
			{"toxtreebiodeg","START biodegradation and persistence plug-in","com.molecularnetworks.start.BiodgeradationRules",null,new String[] {AlgorithmType.Rules.toString()},"http://www.opentox.org/echaEndpoints.owl#PersistenceBiodegradation",Algorithm.requires.structure},				
			
			{"toxtreekroes","ToxTree: ILSI/Kroes decision tree for TTC","toxtree.plugins.kroes.Kroes1Tree",
				new Property[] {
				new Property("DailyIntake","\u00B5g/day", toxTreeReference)
			},new String[] {AlgorithmType.Rules.toString()}, "http://www.opentox.org/echaEndpoints.owl#HumanHealthEffects",Algorithm.requires.structure},
			
			/** Descriptors */
			{"org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor","XLogP","org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"http://www.opentox.org/echaEndpoints.owl#Octanol-water_partition_coefficient_Kow",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#xlogP"},
			{"org.openscience.cdk.qsar.descriptors.molecular.ALOGPDescriptor","ALogP","org.openscience.cdk.qsar.descriptors.molecular.ALOGPDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"http://www.opentox.org/echaEndpoints.owl#Octanol-water_partition_coefficient_Kow",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#ALOGP"},
			{"org.openscience.cdk.qsar.descriptors.molecular.MannholdLogPDescriptor","Prediction of logP based on the number of carbon and hetero atoms","org.openscience.cdk.qsar.descriptors.molecular.MannholdLogPDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"http://www.opentox.org/echaEndpoints.owl#Octanol-water_partition_coefficient_Kow",Algorithm.requires.structure},
	
			{"ambit2.descriptors.MolecularWeight","MolecularWeight","ambit2.descriptors.MolecularWeight",null,new String[] {AlgorithmType.Descriptor.toString()},"http://www.opentox.org/echaEndpoints.owl#MolecularWeight",Algorithm.requires.structure,"http://ambit.sourceforge.net/descriptors.owl#MolecularWeight"},
			{"org.openscience.cdk.qsar.descriptors.molecular.RuleOfFiveDescriptor","Lipinski Rule of Five","org.openscience.cdk.qsar.descriptors.molecular.RuleOfFiveDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()}, "http://www.opentox.org/echaEndpoints.owl#HumanHealthEffects",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#lipinskifailures"},
			{"org.openscience.cdk.qsar.descriptors.molecular.WHIMDescriptor","WHIM descriptors","org.openscience.cdk.qsar.descriptors.molecular.WHIMDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#WHIM"},
			{"org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor","TPSA descriptor","org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#tpsa"},
			
			{"org.openscience.cdk.qsar.descriptors.molecular.CPSADescriptor","CPSA descriptor","org.openscience.cdk.qsar.descriptors.molecular.CPSADescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#CPSA"},
			{"org.openscience.cdk.qsar.descriptors.molecular.AromaticAtomsCountDescriptor","Number of aromatic atoms","org.openscience.cdk.qsar.descriptors.molecular.AromaticAtomsCountDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#aromaticAtomsCount"},
			{"org.openscience.cdk.qsar.descriptors.molecular.AromaticBondsCountDescriptor","Number of aromatic bonds","org.openscience.cdk.qsar.descriptors.molecular.AromaticBondsCountDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#aromaticBondsCount"},	
			
			{"org.openscience.cdk.qsar.descriptors.molecular.BondCountDescriptor","Number of bonds","org.openscience.cdk.qsar.descriptors.molecular.BondCountDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#bondCount"},
			{"org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor","Number of atoms","org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#atomCount"},
			{"org.openscience.cdk.qsar.descriptors.molecular.RotatableBondsCountDescriptor","Number of rotatable bonds","org.openscience.cdk.qsar.descriptors.molecular.RotatableBondsCountDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#rotatableBondsCount"},
			
			{"org.openscience.cdk.qsar.descriptors.molecular.ChiChainDescriptor","Chi chain descriptor","org.openscience.cdk.qsar.descriptors.molecular.ChiChainDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#chiChain"},
			{"org.openscience.cdk.qsar.descriptors.molecular.ChiClusterDescriptor","Chi cluster descriptor","org.openscience.cdk.qsar.descriptors.molecular.ChiClusterDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#chiCluster"},			
			{"org.openscience.cdk.qsar.descriptors.molecular.ChiPathClusterDescriptor","Chi path cluster descriptor","org.openscience.cdk.qsar.descriptors.molecular.ChiPathClusterDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#chiPathCluster"},
			{"org.openscience.cdk.qsar.descriptors.molecular.ChiPathDescriptor","Chi path descriptor","org.openscience.cdk.qsar.descriptors.molecular.ChiPathDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#chiPath"},
			
			{"org.openscience.cdk.qsar.descriptors.molecular.LargestChainDescriptor","Largest chain","org.openscience.cdk.qsar.descriptors.molecular.LargestChainDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#largestChain"},
			{"org.openscience.cdk.qsar.descriptors.molecular.LargestPiSystemDescriptor","Largest Pi system","org.openscience.cdk.qsar.descriptors.molecular.LargestPiSystemDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#largestPiSystem"},

			{"org.openscience.cdk.qsar.descriptors.molecular.HBondAcceptorCountDescriptor","Hydrogen Bond acceptors","org.openscience.cdk.qsar.descriptors.molecular.HBondAcceptorCountDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#hBondacceptors"},
			{"org.openscience.cdk.qsar.descriptors.molecular.HBondDonorCountDescriptor","Hydrogen Bond donors","org.openscience.cdk.qsar.descriptors.molecular.HBondDonorCountDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#hBondDonors"},
			
			{"org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor","BCUT descriptors","org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#BCUT"},
			{"org.openscience.cdk.qsar.descriptors.molecular.ZagrebIndexDescriptor","ZagrebIndex","org.openscience.cdk.qsar.descriptors.molecular.ZagrebIndexDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#zagrebIndex"},
			
			
			{"org.openscience.cdk.qsar.descriptors.molecular.APolDescriptor","Atomic polarizabilities","org.openscience.cdk.qsar.descriptors.molecular.APolDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#apol"},
			{"org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorCharge","ATS autocorrelation descriptor charge","org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorCharge",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#autoCorrelationCharge"},
			{"org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorMass","ATS autocorrelation descriptor mass","org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorMass",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#autoCorrelationMass"},
			{"org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorPolarizability","ATS autocorrelation descriptor polarizability","org.openscience.cdk.qsar.descriptors.molecular.AutocorrelationDescriptorPolarizability",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#autoCorrelationPolarizability"},

			{"org.openscience.cdk.qsar.descriptors.molecular.BPolDescriptor","Difference between atomic polarizabilities of all bonded atoms","org.openscience.cdk.qsar.descriptors.molecular.BPolDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#bpol"},
			
			{"org.openscience.cdk.qsar.descriptors.molecular.CarbonTypesDescriptor","Topological descriptor characterizing the carbon connectivity","org.openscience.cdk.qsar.descriptors.molecular.CarbonTypesDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#carbonTypes"},
			{"org.openscience.cdk.qsar.descriptors.molecular.EccentricConnectivityIndexDescriptor","A topological descriptor combining distance and adjacency information","org.openscience.cdk.qsar.descriptors.molecular.EccentricConnectivityIndexDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#eccentricConnectivityIndex"},

			{"org.openscience.cdk.qsar.descriptors.molecular.FragmentComplexityDescriptor","Fragment complexity","org.openscience.cdk.qsar.descriptors.molecular.FragmentComplexityDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure},
			{"org.openscience.cdk.qsar.descriptors.molecular.GravitationalIndexDescriptor","Characterizing the mass distribution of the molecule","org.openscience.cdk.qsar.descriptors.molecular.GravitationalIndexDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#gravitationalIndex"},
			
			{"org.openscience.cdk.qsar.descriptors.molecular.IPMolecularDescriptor","Ionization potential of a molecule","org.openscience.cdk.qsar.descriptors.molecular.IPMolecularDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#ip"},
			{"org.openscience.cdk.qsar.descriptors.molecular.KappaShapeIndicesDescriptor","Kier and Hall kappa molecular shape indices","org.openscience.cdk.qsar.descriptors.molecular.KappaShapeIndicesDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure},
			{"org.openscience.cdk.qsar.descriptors.molecular.KierHallSmartsDescriptor","A fragment count descriptor that uses e-state fragments","org.openscience.cdk.qsar.descriptors.molecular.KierHallSmartsDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#kierHallSmarts"},
			
			{"org.openscience.cdk.qsar.descriptors.molecular.LengthOverBreadthDescriptor","Evaluates length over breadth descriptors","org.openscience.cdk.qsar.descriptors.molecular.LengthOverBreadthDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#lengthOverBreadth"},
			{"org.openscience.cdk.qsar.descriptors.molecular.LongestAliphaticChainDescriptor","Number of atoms in the longest aliphatic chain","org.openscience.cdk.qsar.descriptors.molecular.LongestAliphaticChainDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#longestAliphaticChain"},

			{"org.openscience.cdk.qsar.descriptors.molecular.MDEDescriptor","Molecular Distance Edge","org.openscience.cdk.qsar.descriptors.molecular.MDEDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#mde"},
			{"org.openscience.cdk.qsar.descriptors.molecular.MomentOfInertiaDescriptor","Moment of inertia and radius of gyration","org.openscience.cdk.qsar.descriptors.molecular.MomentOfInertiaDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#momentOfInertia"},
			{"org.openscience.cdk.qsar.descriptors.molecular.PetitjeanNumberDescriptor","The graph diameter D is defined as the largest vertex eccentricity in the graph","org.openscience.cdk.qsar.descriptors.molecular.PetitjeanNumberDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#petitjeanNumber"},
			{"org.openscience.cdk.qsar.descriptors.molecular.PetitjeanShapeIndexDescriptor","Petitjean shape indices","org.openscience.cdk.qsar.descriptors.molecular.PetitjeanShapeIndexDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#petitjeanShapeIndex"},
			
			{"org.openscience.cdk.qsar.descriptors.molecular.VAdjMaDescriptor","Vertex adjacency information (magnitude)","org.openscience.cdk.qsar.descriptors.molecular.VAdjMaDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#vAdjMa"},
			{"org.openscience.cdk.qsar.descriptors.molecular.WienerNumbersDescriptor","Wiener Path number and Wiener Polarity Number","org.openscience.cdk.qsar.descriptors.molecular.WienerNumbersDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#wienerNumbers"},
			
			{"ambit2.mopac.MopacOriginalStructure","MOPAC descriptors (Energy, EHOMO, ELUMO,etc.)","ambit2.mopac.MopacOriginalStructure",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure,"http://ambit.sourceforge.net/descriptors.owl#MOPAC"},
			
			{"ambit2.some.DescriptorSOMEShell","Site Of Metabolism Estimator (SOME) Bioinformatics (2009) 25(10):1251-1258.","ambit2.some.DescriptorSOMEShell",null,new String[] {AlgorithmType.Descriptor.toString()},"",Algorithm.requires.structure},

			{"ambit2.descriptors.InChI","InChI 1.03","ambit2.descriptors.InChI",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure},
			
			{"ambit2.descriptors.AtomTypeVerifierDescriptor","AtomTypes verifier","ambit2.descriptors.AtomTypeVerifierDescriptor",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure},
			{"ambit2.descriptors.KekulizationVerifier","Kekulization verifier","ambit2.descriptors.KekulizationVerifier",null,new String[] {AlgorithmType.Descriptor.toString()},null,Algorithm.requires.structure},
		
			{"ambit2.mopac.MopacShell","MOPAC: optimizes 3D structure","ambit2.mopac.MopacShell",null,new String[] {AlgorithmType.Structure.toString()},"",Algorithm.requires.structure},			

			{"finder","Find","finder",null,new String[] {AlgorithmType.Finder.toString()},"",Algorithm.requires.structure},
			

			{"pcaRanges","Applicability domain: PCA ranges","ambit2.model.numeric.DataCoverageDescriptors",null,new String[] {AlgorithmType.AppDomain.toString()},null,Algorithm.requires.property},
			{"distanceEuclidean","Applicability domain: Euclidean distance","ambit2.model.numeric.distance.DataCoverageDistanceEuclidean",null,new String[] {AlgorithmType.AppDomain.toString()},null,Algorithm.requires.property},
			{"distanceCityBlock","Applicability domain: Cityblock distance","ambit2.model.numeric.distance.DataCoverageDistanceCityBlock",null,new String[] {AlgorithmType.AppDomain.toString()},null,Algorithm.requires.property},
			{"distanceMahalanobis","Applicability domain: Mahalanobis distance","ambit2.model.numeric.distance.DataCoverageDistanceMahalanobis",null,new String[] {AlgorithmType.AppDomain.toString()},null,Algorithm.requires.property},			
			{"nparamdensity","Applicability domain: nonparametric density estimation","ambit2.model.numeric.DataCoverageDensity",null,new String[] {AlgorithmType.AppDomain.toString()},null,Algorithm.requires.property},
			{"leverage","Applicability domain: Leverage","ambit2.model.numeric.DataCoverageLeverage",null,new String[] {AlgorithmType.AppDomain.toString()},null,Algorithm.requires.property},
			{"fptanimoto","Applicability domain: Fingerprints, Tanimoto distance to a consensus fingerprints","ambit2.model.structure.DataCoverageFingerprintsTanimoto",null,new String[] {AlgorithmType.AppDomain.toString()},null,Algorithm.requires.structure},
			{"fpmissingfragments","Applicability domain: Fingerprints, Missing fragments","ambit2.model.structure.DataCoverageFingeprintsMissingFragments",null,new String[] {AlgorithmType.AppDomain.toString()},null,Algorithm.requires.structure},
			
			{"fingerprints","Generate fingerprints","fp1024",null,new String[] {AlgorithmType.Fingerprints.toString()},null,Algorithm.requires.structure},
			{"atomenvironments","Generate atomenvironments","atomenvironments",null,new String[] {AlgorithmType.Fingerprints.toString()},null,Algorithm.requires.structure},
			//{"structurequality","Structure quality workflow","fp1024_struc",null,new String[] {AlgorithmType.Fingerprints.toString()},null,Algorithm.requires.structure},
			{"struckeys","Generate structure keys","sk1024",null,new String[] {AlgorithmType.Fingerprints.toString()},null,Algorithm.requires.structure},
			{"smartsprop","Generate SMARTS accelerator data","smarts_accelerator",null,new String[] {AlgorithmType.Fingerprints.toString()},null,Algorithm.requires.structure},
			
			{"mcss","Find maximum common substructures of a dataset","mcss",null,new String[] {AlgorithmType.SMSD.toString()},null,Algorithm.requires.structure},
			
			{"superservice","Calls a remote service",null,null,new String[] {AlgorithmType.SuperService.toString()},null,null},
			{"superbuilder","Builds a model with all dependencies",null,null,new String[] {AlgorithmType.SuperBuilder.toString()},null,null},
			{"mockup","Sleeps for 'delay' milliseconds, returns 'dataset_uri' or 'model_uri', specified on input. For testing purposes",null,null,new String[] {AlgorithmType.Mockup.toString()},null,null},
			
			{"expert","Human experts input","expert",null,new String[] {AlgorithmType.Expert.toString()},null,Algorithm.requires.structure}
			
			
			
	};
	
	public static synchronized List<Algorithm<String>> createList() {
		List<Algorithm<String>> algorithmList = new ArrayList<Algorithm<String>>();
		for (Object[] d : algorithms) {
			System.out.println(d);
			Algorithm<String> alg = new Algorithm<String>(d[1].toString());
			alg.setType((String[])d[4]);
			alg.setFormat(
					alg.hasType(AlgorithmType.Expert)?AlgorithmFormat.WWW_FORM:
					alg.hasType(AlgorithmType.SMSD)?AlgorithmFormat.WWW_FORM:
					alg.hasType(AlgorithmType.Structure.toString())?AlgorithmFormat.MOPAC:
					alg.hasType(AlgorithmType.Rules.toString())||alg.hasType(AlgorithmType.Fingerprints.toString())?AlgorithmFormat.JAVA_CLASS:
					alg.hasType(AlgorithmType.AppDomain.toString())?AlgorithmFormat.COVERAGE_SERIALIZED:
					AlgorithmFormat.WEKA);
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
			alg.setImplementationOf(d.length>=8?d[7].toString():null);
			algorithmList.add(alg);
		}		
		return algorithmList;
	}
	
	public static synchronized Iterator<Algorithm<String>> createIterator(List<Algorithm<String>> algorithms, String type, String search) {
		return new FilteredIterator(algorithms.iterator(),type,search);
	}
}

class FilteredIterator implements Iterator<Algorithm<String>> {
	protected Iterator<Algorithm<String>> algorithms;
	protected Algorithm<String> record;
	protected String type;
	protected String search;
	
	public FilteredIterator(Iterator<Algorithm<String>> algorithms,String type,String search) {
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
