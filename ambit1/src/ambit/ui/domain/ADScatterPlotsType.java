/**
 * <b>Filename</b> ADPlotsType.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-7-20
 * <b>Project</b> ambit
 */
package ambit.ui.domain;

import ambit.data.AmbitObjectFixedValues;
import ambit.domain.QSARDataset;

/**
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-7-20
 */
public class ADScatterPlotsType extends AmbitObjectFixedValues implements IADPlotsType {
	public static final String[] plotsType = {
	    "Descriptors Scatter plot",
	    "Observed-Predicted plot",
	    "Predicted-Residuals plot",
	    "Descriptor-Residuals plot",
	    "Descriptor-Predicted plot",
	    "Descriptor-Observed plot",	    
	    "Descriptor-Domain plot",	    
	    "Domain-Residuals plot",
	    "Domain-Predicted plot",
	    "Domain-Observed plot",		
	    "Patterson plot (Domain-Predicted)",		
	    "Patterson plot (Domain-Observed)",
	    "Domain plot"
		}; 
	public static final int _ptDescriptorScatter = 0;
	public static final int _ptObservedPredicted = 1;
	public static final int _ptPredictedResidual = 2;
	public static final int _ptDescriptorResidual = 3;
	public static final int _ptDescriptorPredicted = 4;	
	public static final int _ptDescriptorObserved = 5;	
	public static final int _ptDescriptorDomain = 6;	
	public static final int _ptDomainResidual = 7;
	public static final int _ptDomainPredicted = 8;	
	public static final int _ptDomainObserved = 9;	
	public static final int _ptDomainPredictedDplot = 10;
	public static final int _ptDomainObservedDplot = 11;	
	public static final int _ptDomain = 12;
	protected int xindex = 0;
	protected int yindex = 1;
    /**
     * 
     */
    public ADScatterPlotsType() {
        super(0);
    }

    /**
     * @param name
     */
    public ADScatterPlotsType(String name) {
        super(name);
    }

    /**
     * @param id
     */
    public ADScatterPlotsType(int id) {
        super(id);
    }

    /**
     * @param name
     * @param id
     */
    public ADScatterPlotsType(String name, int id) {
        super(name, id);
    }
    
	public String[] predefinedvalues() {
		return plotsType;
	}		
    public int getXindex() {
        return xindex;
    }
    public void setXindex(int xindex) {
        this.xindex = xindex;
    }
    public int getYindex() {
        return yindex;
    }
    public void setYindex(int yindex) {
        this.yindex = yindex;
    }
    protected String getXname(QSARDataset ds,int index) {
        try {
        return ds.getXname(index);
        } catch (Exception x) {
            return "NA";
        }
    }
    protected String getCoveragename(QSARDataset ds) {
        try {
            return ds.getCoverage().getMethod().toString();
            } catch (Exception x) {
                return "Domain";
            }
    }
    public String getXLabel(QSARDataset ds) {
        switch (getId()) {
        case ADScatterPlotsType._ptDescriptorScatter : {
            return getXname(ds,getXindex());
        }
        case ADScatterPlotsType._ptDescriptorResidual : {
            return getXname(ds,getXindex());
        }
        case ADScatterPlotsType._ptDescriptorPredicted : {
            return getXname(ds,getXindex());
        }
        case ADScatterPlotsType._ptDescriptorObserved : {
            return getXname(ds,getXindex());
        }
        case ADScatterPlotsType._ptDescriptorDomain : {
            return getXname(ds,getXindex());
        }        
                
        case ADScatterPlotsType._ptObservedPredicted : {
            return "Observed";
        }
        case ADScatterPlotsType._ptPredictedResidual : {
            return "Predicted";
        }
        case ADScatterPlotsType._ptDomainResidual : {
        	return getCoveragename(ds);
        }
        case ADScatterPlotsType._ptDomainPredicted : {
        	return getCoveragename(ds);
        }
        case ADScatterPlotsType._ptDomainObserved : {
        	return getCoveragename(ds);
        }                        
        
        case ADScatterPlotsType._ptDomainPredictedDplot : {
        	return "delta "+getCoveragename(ds);
        }        
        case ADScatterPlotsType._ptDomainObservedDplot : {
        	return "delta "+getCoveragename(ds);
        }        
                
        case ADScatterPlotsType._ptDomain : {
            return ds.getXname(getXindex());       
        }
        default: {
            return "NA";
        }
        }        
    }
    public String getYLabel(QSARDataset ds) {
        switch (getId()) {
        case ADScatterPlotsType._ptDescriptorScatter : {
            return getXname(ds,getYindex());
        }
        case ADScatterPlotsType._ptDescriptorResidual : {
            return "Residual";
        }
        case ADScatterPlotsType._ptDescriptorPredicted : {
            return "Predicted";
        }
        case ADScatterPlotsType._ptDescriptorObserved : {
            return "Observed";
        }
        case ADScatterPlotsType._ptDescriptorDomain : {
        	return getCoveragename(ds);        	
        }        
        
        case ADScatterPlotsType._ptObservedPredicted : {
            return "Predicted";
        }
        case ADScatterPlotsType._ptPredictedResidual : {
            return "Residual";
        }
        case ADScatterPlotsType._ptDomainResidual : {
            return "Residual";
        }
        case ADScatterPlotsType._ptDomainPredicted : {
            return "Predicted";
            
        }
        case ADScatterPlotsType._ptDomainObserved : {
            return "Observed";
        }                        
        
        case ADScatterPlotsType._ptDomainPredictedDplot : {
            return "delta Predicted";
        }
        case ADScatterPlotsType._ptDomainObservedDplot : {
            return "delta Observed";
        }                        
        case ADScatterPlotsType._ptDomain : {
            return ds.getXname(getYindex());       
        }
        default: {
            return "NA";
        }
        }                
    }
    public boolean isXDescriptor() {
        switch (getId()) {
        case ADScatterPlotsType._ptDescriptorScatter : {
            return true;
        }
        case ADScatterPlotsType._ptDescriptorResidual : {
            return true;
        }
        case ADScatterPlotsType._ptDescriptorPredicted : {
            return true;
        }
        case ADScatterPlotsType._ptDescriptorObserved : {
            return true;
        }
        
        case ADScatterPlotsType._ptDescriptorDomain : {
            return true;
        }               
                       
        case ADScatterPlotsType._ptDomain : {
            return true;       
        }
        default: {
            return false;
        }
        }                
    }
    public boolean isYDescriptor() {
        switch (getId()) {
        case ADScatterPlotsType._ptDescriptorScatter : {
            return true;
        }
        case ADScatterPlotsType._ptDomain : {
            return true;       
        }
        default: {
            return false;
        }
        }                
    }
    public String getTitle() {
    	return "Scatter plot";
    }
}

