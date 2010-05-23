package ambit2.descriptors;

import net.sf.jniinchi.INCHI_RET;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.base.data.Property;
import ambit2.core.data.StringArrayResult;
import ambit2.core.processors.structure.InchiProcessor;

public class InChI implements IMolecularDescriptor {
	protected InchiProcessor processor;
	protected static final String[] names = new String[] {
			Property.opentox_InChI_std,
			Property.opentox_InChIAuxInfo_std,
			Property.opentox_InChIKey_std
			//Property.opentox_SMILES
	};
	public InChI() throws CDKException {
		super();
		processor = new InchiProcessor();
	}
	public DescriptorValue calculate(IAtomContainer mol) {
		try {
			InChIGenerator gen = processor.process(mol);
			INCHI_RET ret = gen.getReturnStatus();
			if (ret.equals(INCHI_RET.OKAY) || ret.equals(INCHI_RET.WARNING) ) {
		        StringArrayResult value = new StringArrayResult(new String[] {
		        		gen.getInchi(),
		        		gen.getAuxInfo(),
		        		gen.getInchiKey()
		        });
		        return new DescriptorValue(getSpecification(), getParameterNames(), 
		                getParameters(), value,getDescriptorNames()); 	
			} else {
				 return new DescriptorValue(getSpecification(), getParameterNames(), 
			                getParameters(),null,getDescriptorNames(),
			                new Exception(String.format("[%s] %s",gen.getReturnStatus(),gen.getMessage()))); 
			}
	        
		
		} catch (Exception x) {
	        return new DescriptorValue(getSpecification(), getParameterNames(), 
	                getParameters(),null,getDescriptorNames(),x);  
		}
	}

	public IDescriptorResult getDescriptorResultType() {
		return new StringArrayResult();
	}

	public String[] getDescriptorNames() {
		return names;
	}

	public String[] getParameterNames() {
		return null;
	}

	public Object getParameterType(String arg0) {
		return null;
	}

	public Object[] getParameters() {
		return null;
	}

	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            	String.format("http://www.opentox.org/api/1.1#%s","InChI"),
    		    this.getClass().getName(),
    		    "$Id: StructureIdentifiersGenerator.java,v 1.0 2010/05/23 9:01:00 Nina Jeliazkova Exp $",
                "http:///ambit.sourceforge.net");
	}

	public void setParameters(Object[] arg0) throws CDKException {
		
	}

}
