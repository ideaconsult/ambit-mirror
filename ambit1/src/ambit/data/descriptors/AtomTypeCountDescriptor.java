package ambit.data.descriptors;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;

/**
 * Enable 'dumb' atom counts as a simple similarity descriptor (total, C, N, O, P, S, F, Cl, Br, I)
 * @author nina
 *
 */
public class AtomTypeCountDescriptor implements IMolecularDescriptor {
	
	protected String atomType="C";
	
	public AtomTypeCountDescriptor() {
		super();
	}
	public AtomTypeCountDescriptor(String atomSymbol) {
		super();
		this.atomType= atomSymbol;
	}
	public DescriptorValue calculate(IAtomContainer a) throws CDKException {
		try {
			int count = 0;
			for (int i=0; i < a.getAtomCount();i++)
				if (a.getAtomAt(i).getSymbol().equals(atomType)) count++;
			return new DescriptorValue(getSpecification(),getParameterNames(),getParameters(),new IntegerResult(count));
		} catch (Exception x) {
			throw new CDKException(x.getMessage());
		}
	}

    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "element";
        return params;
    }

    public Object getParameterType(String name) {
        return "";
    }

	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "Number of atoms of certain type",
		    this.getClass().getName(),
		    "$Id: AtomTypeCountDescriptor.java,v 0.1 2007/12/13 14:59:00 Nina Jeliazkova Exp $",
            "http://ambit.sourceforge.net");
    };
    /**
     * 3 parameters : Smarts,name,hint; first two are mandatory.
     */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length==0) return;
		
        if (!(params[0] instanceof String)) 
            throw new CDKException("The first parameter must be of type String");
        try {
        	atomType = params[0].toString();
        } catch (Exception x) {
        	throw new CDKException(x.getMessage());
        }
	}
    public Object[] getParameters() {
        Object[] params = new Object[1];
        params[0] = atomType;
        return params;
    }	
    @Override
    public String toString() {
    	return atomType;
    }
    @Override
    public int hashCode() {
    	int hash = 7;
    	int var_code = (null == atomType ? 0 : atomType.hashCode());
    	hash = 31 * hash + var_code; 
	
    	return hash;
    }    
}
