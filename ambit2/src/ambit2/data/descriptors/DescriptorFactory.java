/*
 * Created on 2005-3-22
 *
 */
package ambit2.data.descriptors;

import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.APolDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.AromaticAtomsCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.AromaticBondsCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.BPolDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.HBondAcceptorCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.HBondDonorCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.LargestChainDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.PetitjeanNumberDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.RotatableBondsCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.RuleOfFiveDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.VAdjMaDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.WeightDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.ZagrebIndexDescriptor;

import ambit2.data.literature.LiteratureEntry;
import ambit2.data.literature.ReferenceFactory;
import ambit2.data.molecule.MolProperties;
import ambit2.io.IColumnTypeSelection;

/**
 * provides static functions to create several kind of descriptors 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DescriptorFactory {

	/**
	 * 
	 */
	protected DescriptorFactory() {
		super();
	}
	public static Descriptor createLogP(LiteratureEntry ref) {
		Descriptor d = new Descriptor("log_P",ref);
		d.setRemark("");
		DescriptorGroups g = new DescriptorGroups();
		g.addItem(createDescriptorPartitionGroup());
		d.setDescriptorGroups(g);
		return d;
	}
	
	public static Descriptor createEmptyDescriptor() {
		Descriptor d = new Descriptor("",
					ReferenceFactory.createEmptyReference());
		d.setRemark("");
		DescriptorGroups g = new DescriptorGroups();
		g.addItem(new DescriptorGroup(""));
		d.setDescriptorGroups(g);
		return d;
	}
	/*
	 */
	//used in Grammatica BCF model
	public static Descriptor createvIm() {
		LiteratureEntry ref = new LiteratureEntry(
				"Bonchev D.,Information Theoretic Indices for Characterization of Chemical Structures,Research Studies Press Chichester U.K.,249,1983"); 
		
		Descriptor d = new Descriptor("vIm,D deg",ref);
		d.setRemark("Mean information content of the distance degree magnitude");
		DescriptorGroups g = new DescriptorGroups();
		g.addItem(createDescriptorGroup("2D topological"));
		d.setDescriptorGroups(g);
		return d;
	}
	//used in Grammatica BCF model
	public static Descriptor createnHAcc() {
		Descriptor d = new Descriptor("nHAcc",ReferenceFactory.createEmptyReference());
		d.setRemark("Number of atoms acceptor for H-Bonds");
		DescriptorGroups g = new DescriptorGroups();
		g.addItem(createDescriptorGroup("1D - functional group"));
		d.setDescriptorGroups(g);
		return d;
	}
	//used in Grammatica BCF model
	public static Descriptor createMATS2m() {
		LiteratureEntry ref = new LiteratureEntry(
				"Moran P. A. P.,Notes on Continuous Stochastic Phenomena,Biometrika,37,pp.17-23,1950"); 
		
		Descriptor d = new Descriptor("MATS2m",ref);
		d.setRemark("Moran autocorrelation of a topological structure - lag2/weighted by atomic mass");
		DescriptorGroups g = new DescriptorGroups();
		g.addItem(createDescriptorGroup("2D - autocorrelation"));
		d.setDescriptorGroups(g);
		return d;
	}
	//used in Grammatica BCF model
	public static Descriptor createGATS2e() {

		LiteratureEntry ref = new LiteratureEntry(
				"Geary R. C.,The Contiguity Ratio and Statistical Mapping,Incorp. Statist.,pp.115-145,1954"); 

		Descriptor d = new Descriptor("GATS2e",ref);
		d.setRemark("Geary autocorrelation ± lag2/weighted by atomic Sanderson electronegativities");
		DescriptorGroups g = new DescriptorGroups();
		g.addItem(createDescriptorGroup("2D - autocorrelation"));
		d.setDescriptorGroups(g);
		return d;
	}
	//used in Grammatica BCF model
	public static Descriptor createH6p() {
	
		LiteratureEntry ref = new LiteratureEntry(
				"Consonni V.,Todeschini R.,Pavan M.,Structure/	Response Correlation and Similarity/Diversity Analysis by GETAWAY Descriptors. Part 1. Theory of the Novel 3D	Molecular Descriptors,J. Chem. Inf. Comput. Sci.,42,pp.682-692,2002"); 
		
		Descriptor d = new Descriptor("H6p",ref);
		d.setRemark("H autocorrelation ± lag 6/weighted by atomic polarizabilities");
		DescriptorGroups g = new DescriptorGroups();
		g.addItem(createDescriptorGroup("3D - GETAWAY"));
		d.setDescriptorGroups(g);
		return d;
	}		
	
	
	
	public static Descriptor createDescriptor(String name,
			String units,
			DescriptorGroups g,
			LiteratureEntry ref) {
		Descriptor d = new Descriptor(name,ref);
		d.setDescriptorGroups(g);
		d.setUnits(units);
		return d;
	}
	
	public static DescriptorGroup createDescriptorElectronicGroup() {
		return createDescriptorGroup("Electronic");
	}
	public static DescriptorGroup createDescriptorPartitionGroup() {
		return createDescriptorGroup("Partition Coefficients");
	}
	public static DescriptorGroup createDescriptorTopologicalGroup() {
		return createDescriptorGroup("Topological");
	}
	public static DescriptorGroup createDescriptorShapeGroup() {
		return createDescriptorGroup("Shape");
	}		
	public static DescriptorGroup createDescriptorIndicatorGroup() {
		return createDescriptorGroup("Indicator Variable");
	}		
	public static DescriptorGroup createDescriptorGroup(String name) {
		DescriptorGroup g = new DescriptorGroup(name);
		return g;
	}
	//Code	Compound	CAS	SMILES	Obs	Pred	Dev.	log_P	eLumo	eHomo	IL	abs dev

	public static MolProperties createDebnathSmilesFileDescriptors() {
	    MolProperties l = new MolProperties();
		LiteratureEntry ref = ReferenceFactory.createDebnathReference();
		l.addIdentifier("Code",new AmbitColumnType(IColumnTypeSelection._ctRowID));
		l.addIdentifier("Compound",new AmbitColumnType(IColumnTypeSelection._ctChemName));
		l.addIdentifier("CAS",new AmbitColumnType(IColumnTypeSelection._ctCAS));		
		l.addIdentifier("SMILES",new AmbitColumnType(IColumnTypeSelection._ctSMILES));		
	
		l.addQSAR("Obs",new AmbitColumnType(IColumnTypeSelection._ctYobserved));
		l.addQSAR("Pred",new AmbitColumnType(IColumnTypeSelection._ctYpredicted));
		l.addQSAR("Dev.",new AmbitColumnType(IColumnTypeSelection._ctYresidual));

		
		Descriptor d = new Descriptor("log_P",0,IColumnTypeSelection._ctX,ref);
		d.setOrderInModel(0);
		l.addDescriptor("log_P",d);
		
		d = new Descriptor("eLumo",1,IColumnTypeSelection._ctX,ref);
		d.setOrderInModel(1);
		l.addDescriptor("eLumo",d);
		
		d = new Descriptor("eHomo",2,IColumnTypeSelection._ctX,ref);
		d.setOrderInModel(2);
		l.addDescriptor("eHomo",d);
		
		d = new Descriptor("IL",3,IColumnTypeSelection._ctX,ref);
		d.setOrderInModel(3);
		l.addDescriptor("IL",d);
		return l;
	}
	/**
	 * Columns as defined in demo file
	 * ID,CAS,logP_estimated,logP_observed,SMILES,Chemical,Yexp.,Ypred.Mod3,Ypred.MCI,Ypred.BCFWIN,vIm,MATS2m,GATS2e,H6p,nHAcc
	 * @return {@link MolProperties}
	 */
	public static MolProperties createGrammaticaFileDescriptors() {
	    MolProperties l = new MolProperties();
		LiteratureEntry ref = ReferenceFactory.createDebnathReference();
		l.addIdentifier("ID",new AmbitColumnType(IColumnTypeSelection._ctRowID));
		l.addIdentifier("Chemical",new AmbitColumnType(IColumnTypeSelection._ctChemName));
		l.addIdentifier("CAS",new AmbitColumnType(IColumnTypeSelection._ctCAS));		
		l.addIdentifier("SMILES",new AmbitColumnType(IColumnTypeSelection._ctSMILES));		
	
		l.addQSAR("Yexp",new AmbitColumnType(IColumnTypeSelection._ctYobserved));
		l.addQSAR("Ypred.Mod3",new AmbitColumnType(IColumnTypeSelection._ctYpredicted));
		l.addQSAR("Dev.",new AmbitColumnType(IColumnTypeSelection._ctYresidual));
		l.addQSAR("logP_observed",new AmbitColumnType(IColumnTypeSelection._ctUnknown));
		l.addQSAR("Ypred.MCI",new AmbitColumnType(IColumnTypeSelection._ctUnknown));
		l.addQSAR("Ypred.BCFWIN",new AmbitColumnType(IColumnTypeSelection._ctUnknown));
		
		String[] descriptors = {"vIm","MATS2m","GATS2e","H6p","nHAcc","logP_estimated"};
		for (int i=0; i < descriptors.length;i++) {
			l.getProperties().put(descriptors[i],"1");
			l.moveToDescriptors(descriptors[i]);
		}	
		/*
		Descriptor d = new Descriptor("vIm",0,IColumnTypeSelection._ctX,ref);
		d.setOrderInModel(1);
		l.addDescriptor("vIm",d);
		
		d = new Descriptor("MATS2m",0,IColumnTypeSelection._ctX,ref);
		d.setOrderInModel(2);
		l.addDescriptor("MATS2m",d);
		
		d = new Descriptor("GATS2e",0,IColumnTypeSelection._ctX,ref);
		d.setOrderInModel(3);
		l.addDescriptor("GATS2e",d);
		
		d = new Descriptor("H6p",0,IColumnTypeSelection._ctX,ref);
		d.setOrderInModel(4);
		l.addDescriptor("H6p",d);
		
		d = new Descriptor("nHAcc",0,IColumnTypeSelection._ctX,ref);
		d.setOrderInModel(5);
		l.addDescriptor("nHAcc",d);		
		
		d = new Descriptor("logP_estimated",0,IColumnTypeSelection._ctX,ref);
		d.setOrderInModel(6);		
		l.addDescriptor("logP",d);
		*/
		
		return l;
	}
	//Code	Compound	Obs	Pred	Dev.	log_P	eLumo	eHomo	IL	abs dev

	public static MolProperties createDebnathFileDescriptors() {
	    MolProperties l = new MolProperties();
		LiteratureEntry ref = ReferenceFactory.createDebnathReference();
		l.addIdentifier("Code",new AmbitColumnType(IColumnTypeSelection._ctRowID));
		l.addIdentifier("Compound",new AmbitColumnType(IColumnTypeSelection._ctChemName));
		
		l.getProperties().put("log_P","1");
		l.getProperties().put("eHomo","1");
		l.getProperties().put("eLumo","1");
		l.getProperties().put("IL","1");
		l.moveToDescriptors("log_P");
		l.moveToDescriptors("eHomo");
		l.moveToDescriptors("eLumo");
		l.moveToDescriptors("IL");

		l.addQSAR("Obs",new AmbitColumnType(IColumnTypeSelection._ctYobserved));
		l.addQSAR("Pred",new AmbitColumnType(IColumnTypeSelection._ctYpredicted));
		l.addQSAR("Dev.",new AmbitColumnType(IColumnTypeSelection._ctYresidual));

		return l;
	}
    public static DescriptorDefinition createAmbitDescriptorFromCDKdescriptor(
    		IDescriptor cdkDescriptor, 
    		DescriptorGroups g,
    		String units,
    		String comment 
    		) {
    	LiteratureEntry ref = ReferenceFactory.createDatasetReference(
	    		cdkDescriptor.getSpecification().getImplementationVendor(),cdkDescriptor.getSpecification().getSpecificationReference());
	    DescriptorDefinition descriptor = createDescriptor
	    	(cdkDescriptor.getSpecification().getImplementationTitle(), units, g, ref);
	    descriptor.setRemark(comment);
	    return descriptor;
    }
    /*
    public static DescriptorsHashtable createDescriptorsList() {
		DescriptorsHashtable hashtable = new DescriptorsHashtable();
	    
	    DescriptorGroups gTopological = new DescriptorGroups();
	    gTopological.addItem(createDescriptorGroup("Shape"));
	    org.openscience.cdk.qsar.Descriptor cdkDescriptor = new SpherosityDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, 
		        createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"",
		        		"Spherosity index varies from zero for planar molecules, such as benzene, to one for totally spherical molecules"));
	    
		return hashtable;
    }

     */    
    
    public static DescriptorsHashtable createDescriptorsList() {
		final DescriptorsHashtable hashtable = new DescriptorsHashtable();
		
		
	    DescriptorGroups gPartition = new DescriptorGroups();
	    gPartition.addItem(DescriptorFactory.createDescriptorPartitionGroup());
	    
	    IDescriptor cdkDescriptor = new XLogPDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gPartition,"","XlogP"));
		
	    DescriptorGroups gTopological = new DescriptorGroups();
	    gTopological.addItem(DescriptorFactory.createDescriptorTopologicalGroup());
		
	    cdkDescriptor = new RotatableBondsCountDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","Number of rotatable bonds"));
	    	    
		cdkDescriptor = new ZagrebIndexDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","Zagreb index"));

        /*
		cdkDescriptor = new GravitationalIndexDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","Gravitational index"));
		*/
/*        
		cdkDescriptor = new ConnectivityOrderOneDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","Connectivity index (order 1):"));

		cdkDescriptor = new EccentricConnectivityIndexDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","The eccentric connectivity index"));

		cdkDescriptor = new LongestAliphaticChainDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","The number of atoms in the longest aliphatic chain"));

		cdkDescriptor = new LargestPiSystemDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","the number of atoms in the largest pi system"));

		cdkDescriptor = new TPSADescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","Topological polar surface area based on fragment "));
*/		
/*
		cdkDescriptor = new CarbonConnectivityOrderZeroDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","Carbon Connectivity index (order 0)"));

		cdkDescriptor = new CarbonConnectivityOrderOneDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","CarbonConnectivity index (order 1)"));

		cdkDescriptor = new ValenceCarbonConnectivityOrderZeroDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","Atomic valence connectivity index (order 0)"));

		cdkDescriptor = new ValenceCarbonConnectivityOrderOneDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","Atomic valence connectivity index (order 1)"));
		
		cdkDescriptor = new ValenceConnectivityOrderOneDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","Atomic valence connectivity index (order 1)"));
*/
		cdkDescriptor = new VAdjMaDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","Vertex adjacency information (magnitude)"));

		cdkDescriptor = new LargestChainDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","The number of atoms in the largest chain"));

		cdkDescriptor = new APolDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","Sum of the atomic polarizabilities (including implicit hydrogens)"));
		
		cdkDescriptor = new HBondAcceptorCountDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","The number of hydrogen bond acceptors"));

		cdkDescriptor = new HBondDonorCountDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","The number of hydrogen bond donors"));

		cdkDescriptor = new PetitjeanNumberDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","the eccentricity of a vertex corresponds to the distance from that vertex to the most remote vertex in the graph"));
		
		cdkDescriptor = new BPolDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","Sum of the absolute value of the difference between atomic polarizabilities of all bonded atoms in the molecule"));
		
	
		/*
		cdkDescriptor = new WienerNumbersDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","Wiener number"));
		//cdkDescriptor = new KappaShapeIndicesDescriptor();
		//hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","Kappa shape index"));
		*/
		
		cdkDescriptor = new AromaticAtomsCountDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","Number of aromatic atoms"));			
		cdkDescriptor = new AromaticBondsCountDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gTopological,"","Number of aromatic bonds"));			
		
		cdkDescriptor = new RuleOfFiveDescriptor();
		
	    DescriptorGroups g = new DescriptorGroups();
	    gTopological.addItem(new DescriptorGroup("Composite descriptors"));
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor,  g,"","Lipinski rule of five"));
		
		DescriptorGroups gSize = new DescriptorGroups();
		gSize.addItem(DescriptorFactory.createDescriptorShapeGroup());
		cdkDescriptor = new PlanarityDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gSize,"Angstrom","Planarity - zero means planar molecule"));

		cdkDescriptor = new SpherosityDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gSize,"","Spherosity"));
		
		cdkDescriptor = new MaximumDiameterDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gSize,"Angstrom","Maximum diameter - the diameter of mimimal sphere circumscribing the molecule"));		
		
		cdkDescriptor = new CrossSectionalDiameterDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gSize,"Angstrom","Effective cross sectional diameter - the diameter of minimal cyllinder circumscribing the molecule"));
		
		cdkDescriptor = new WeightDescriptor();
		hashtable.addDescriptorPair(cdkDescriptor, createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, gSize,"","Molecular weight"));

		/*
		try {
			final DescriptorGroups gr = new DescriptorGroups();
			gr.addItem(new DescriptorGroup("Functional groups"));
	    	final LiteratureEntry ref = ReferenceFactory.createDatasetReference("Functional group","http://ambit2.acad.bg/downloads/AmbitDb/html/funcgroups.xml");
	    	
			FuncGroupsDescriptorFactory.getDescriptors(new IProcessDescriptor<FunctionalGroupDescriptor>() {
				public void process(FunctionalGroupDescriptor descriptor) throws Exception {
					
					Object[] o = descriptor.getParameters();
				    DescriptorDefinition key = createDescriptor(o[1].toString(), "", gr, ref);
				    //o[0] smarts o[1] name o[2] hint 
				    key.setRemark(o[1] + " Smarts: "+o[0] +" Explanation: "+o[2]);
					hashtable.addDescriptorPair(descriptor, key);

				}
			});
		} catch (Exception x) {
			x.printStackTrace();
		}
		*/
		return hashtable;
    }
    
}
