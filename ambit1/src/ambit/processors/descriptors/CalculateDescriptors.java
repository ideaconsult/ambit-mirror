package ambit.processors.descriptors;

import java.util.Enumeration;

import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.modeling.builder3d.TemplateHandler3D;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.IMolecularDescriptor;

import ambit.data.HydrogenAdder;
import ambit.data.IAmbitEditor;
import ambit.data.descriptors.DescriptorsHashtable;
import ambit.exceptions.AmbitException;
import ambit.log.AmbitLogger;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.Builder3DProcessor;
import ambit.processors.DefaultAmbitProcessor;
import ambit.processors.IAmbitResult;
import ambit.ui.data.descriptors.DescriptorsHashtableEditor;
/**
 * Calculates descriptors listed by {@link ambit.data.descriptors.DescriptorsHashtable}
 * to be replaced by {@link  DescriptorCalculatorProcessor}
 * @author Nina Jeliazkova
 *
 */
public class CalculateDescriptors extends DefaultAmbitProcessor {
	protected static AmbitLogger logger = new AmbitLogger(CalculateDescriptors.class);
	public static final  String ERR_UNDEFINEDDECSRIPTOR = "Descriptor not defined!";
	public static final String ERR_CALCULATION = "Error when calculating descriptor\t";
	protected DescriptorsHashtable lookup = null;
	protected boolean checkAromaticity = true;
	protected Builder3DProcessor builder = null;
	/**
	 * Creates {@link CalculateDescriptors} processor with a list of descriptors to calculate
	 * @param descriptors to be calculated
	 */
	public CalculateDescriptors(DescriptorsHashtable descriptors,TemplateHandler3D templateHandler) {
		super();
		this.lookup = descriptors;
        if (templateHandler != null)
            builder = new Builder3DProcessor(templateHandler);
	}
	/**
	 * Creates {@link CalculateDescriptors} processor with an empty list of descriptors to calculate
	 *
	 */
	public CalculateDescriptors() {
		super();
		lookup = new DescriptorsHashtable();
	}	
	public Object process(Object object) throws AmbitException {
	    if (object == null) return null;
		if (!(object instanceof IAtomContainer)) return null;
				
		try {
			IAtomContainer origin = (IAtomContainer) object;
			IAtomContainer ac = (IAtomContainer)((IAtomContainer) object).clone();
			boolean aromaticity = checkAromaticity;
			Enumeration keys = lookup.keys();
			IMolecularDescriptor descriptor = null;
			boolean done3D = false;
			HydrogenAdder ha = new HydrogenAdder();
			
			ha.addExplicitHydrogensToSatisfyValency(ac);
			if (aromaticity)
				HueckelAromaticityDetector.detectAromaticity(ac);	
			
			while (keys.hasMoreElements())  {
				Object key = keys.nextElement();
				if (key instanceof IDescriptor) {
					descriptor = (IMolecularDescriptor) key;
					if (descriptor == null) logger.error(ERR_UNDEFINEDDECSRIPTOR+descriptor); 
					else 	
					try {
											
						DescriptorValue value = descriptor.calculate(ac);
						setProperty(origin,descriptor, value);
						
						
					} catch (CDKException x) {
                        ac.setProperty(descriptor, x);
                        logger.error(ERR_CALCULATION+descriptor,x);
                        logger.debug(x);                                     
                        try {
                            if (x.getMessage().indexOf("did not have any 3D coordinates. These are required") > -1)
                            if (!done3D && (builder != null)) {
                            	Object id = ac.getProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE);
                            	
                            	System.out.print("Building 3D ...");
                            	if (id != null) System.out.println(id);
                            	else System.out.println();
                                ac = (IAtomContainer) builder.process(ac);
                                System.out.println("Building 3D DONE");
                                DescriptorValue value = descriptor.calculate(ac);
                                
                                setProperty(origin,descriptor, value);
                                done3D = true;
                            }
                        } catch (Exception xx) {
                            
                            origin.setProperty(descriptor, xx);
                            logger.error(ERR_CALCULATION+descriptor,xx);
                            logger.debug(x);                            
                        }

					}
					aromaticity = false;					
				}
			}
		} catch (Exception x) {	
			throw new AmbitException(x);
		}
		return object;
	}
	public void setProperty(IAtomContainer ac,Object key, Object value) {
		ac.setProperty(key,value);
	}
	public IAmbitResult createResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public IAmbitResult getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setResult(IAmbitResult result) {
		// TODO Auto-generated method stub

	}
	public boolean isCheckAromaticity() {
		return checkAromaticity;
	}
	public void setCheckAromaticity(boolean checkAromaticity) {
		this.checkAromaticity = checkAromaticity;
	}
	/* (non-Javadoc)
     * @see ambit.processors.IAmbitProcessor#close()
     */
    public void close() {

    }
    public IAmbitEditor getEditor() {

    	return new DescriptorsHashtableEditor(lookup);
    }
    public String toString() {
    	StringBuffer b = new StringBuffer();
    	b.append("Descriptor calculation ");
    	
    	if (lookup != null) {
    		b.append(" (");
    		b.append(lookup.size());
    		b.append(" descriptors)");
    	}	
    	return b.toString();
    }
}
