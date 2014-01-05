package ambit2.loom.descriptors;

import java.io.InputStream;
import java.util.Properties;

import net.idea.ops.cli.OPSClient;
import net.sf.jniinchi.INCHI_RET;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.smiles.FixBondOrdersTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.data.Property;
import ambit2.core.processors.structure.InchiProcessor;

public class OPSCountsByCompound implements IMolecularDescriptor {
	protected OPSClient cli;
	protected transient FixBondOrdersTool fbt = new FixBondOrdersTool();
	protected InchiProcessor processor;
	protected static final String[] names = new String[] {
		"OPSPathwayCount",
		"OPSPharmacologyCount"
	};



	@Override
	public String[] getParameterNames() {
		return null;
	}

	@Override
	public Object getParameterType(String name) {
		return null;
	}

	@Override
	public void setParameters(Object[] params) throws CDKException {
	}

	@Override
	public Object[] getParameters() {
		return null;
	}

	@Override
	public String[] getDescriptorNames() {
		return names;
	}

	@Override
	public DescriptorValue calculate(IAtomContainer molecule) {
		try {
			Object inchikey = molecule.getProperty(Property.opentox_InChIKey_std);
			if (inchikey==null) {
				if (processor==null) processor = new InchiProcessor();
				
				boolean kekulize = false;
				for (IBond bond:molecule.bonds()) if (bond.getFlag(CDKConstants.ISAROMATIC)) {
					kekulize = true; break;
				}
				IAtomContainer kekulized = (IAtomContainer) molecule;
				if (kekulize) try {
					//inchi can't process aromatic bonds...
					kekulized = (IMolecule) molecule.clone();
					//and kekulizer needs atom typing
					AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(kekulized); 
					kekulized = fbt.kekuliseAromaticRings((IMolecule)kekulized);
					for (IBond bond:kekulized.bonds()) bond.setFlag(CDKConstants.ISAROMATIC, false); 	
				} catch (Exception x) { 
					//logger.log(Level.FINE,x.getMessage(),x);; 
				}
				
				InChIGenerator gen = processor.process(kekulized);
				INCHI_RET ret = gen.getReturnStatus();
				if (ret.equals(INCHI_RET.OKAY) || ret.equals(INCHI_RET.WARNING) ) 
					inchikey = gen.getInchiKey();
			}	
			if (inchikey!=null) {
				if (cli==null) cli = createOPSClient();
				 int[] counts = cli.getCountsbyCompound(inchikey.toString());
				 IntegerArrayResult result = new IntegerArrayResult();
				 result.add(counts[0]);
				 result.add(counts[1]);
			     return new DescriptorValue(getSpecification(), getParameterNames(), 
			                getParameters(),result,getDescriptorNames());		        
			}
			throw new Exception("No InChI key");
		} catch (Exception x) {
	        return new DescriptorValue(getSpecification(), getParameterNames(), 
	                getParameters(),null,getDescriptorNames(),x);  	
		}
		
	}

	protected OPSClient createOPSClient() throws Exception {
		InputStream in = null;
		try {
			in = getClass().getClassLoader().getResourceAsStream("ambit2/loom/ops.properties");
			Properties properties = new Properties();
			properties.load(in);
			MyOPSClient cli = new MyOPSClient(null);
			cli.setProperties(properties);
			return cli;
		} catch (Exception x) {
			throw x;
		} finally {
			try {in.close();} catch (Exception x) {}
		}
	}
	@Override
	public IDescriptorResult getDescriptorResultType() {
		return new IntegerArrayResult(2);
	}

	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            	String.format("http://ambit.sf.net/api/#%s","OPSCountsByCompound"),
    		    this.getClass().getName(),
    		    "$Id: OPSCountsByCompound,v 1.0 2013/12/12 8:19:00 Nina Jeliazkova Exp $",
                "http:///ambit.sourceforge.net");
	}
}
