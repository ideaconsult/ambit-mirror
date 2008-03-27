/**
 * 
 */
package ambit2.ui.domain;

import ambit2.data.AmbitObjectFixedValues;
import ambit2.domain.QSARDataset;

/**
 * @author nina
 *
 */
public class ADBarPlotsType extends AmbitObjectFixedValues implements
		IADPlotsType {
	public static final String[] plotsType = {
	    "Descriptors histogram",
	    "Residuals histogrm",
	    "Domain histogram"
		}; 
	public static final int _ptDescriptorHistogram = 0;
	public static final int _ptResidualsHistogram = 1;
	public static final int _ptDomainHistogram = 2;
	protected int xindex = 0;
	protected int yindex = 1;	
	/**
	 * 
	 */
	public ADBarPlotsType() {
		super(0);
	}

	/**
	 * @param name
	 */
	public ADBarPlotsType(String name) {
		super(name);
	}

	/**
	 * @param id
	 */
	public ADBarPlotsType(int id) {
		super(id);
	}

	/**
	 * @param name
	 * @param id
	 */
	public ADBarPlotsType(String name, int id) {
		super(name, id);
	}
	public String[] predefinedvalues() {
		return plotsType;
	}	
    protected String getXname(QSARDataset ds,int index) {
        try {
        return ds.getXname(index);
        } catch (Exception x) {
            return "NA";
        }
    }	
	/* (non-Javadoc)
	 * @see ambit2.ui.domain.IADPlotsType#getXLabel(ambit2.data.domain.QSARDataset)
	 */
	public String getXLabel(QSARDataset ds) {
        switch (getId()) {
        case ADBarPlotsType._ptDescriptorHistogram : {
            return getXname(ds,getXindex());
        }
        case ADBarPlotsType._ptResidualsHistogram : {
            return "Residual";
        }
        case ADBarPlotsType._ptDomainHistogram : {
        	return getCoveragename(ds);
        }

        default: {
            return "NA";
        }
        }        

	}

	/* (non-Javadoc)
	 * @see ambit2.ui.domain.IADPlotsType#getYLabel(ambit2.data.domain.QSARDataset)
	 */
	public String getYLabel(QSARDataset ds) {
		return "Frequency";
	}

	/* (non-Javadoc)
	 * @see ambit2.ui.domain.IADPlotsType#isXDescriptor()
	 */
	public boolean isXDescriptor() {
        switch (getId()) {
        case ADBarPlotsType._ptDescriptorHistogram : {
            return true;
        }
        default: {
            return false;
        }
        }                

	}

	/* (non-Javadoc)
	 * @see ambit2.ui.domain.IADPlotsType#isYDescriptor()
	 */
	public boolean isYDescriptor() {
		return false;
	}

	/* (non-Javadoc)
	 * @see ambit2.ui.domain.IADPlotsType#getXindex()
	 */
	public int getXindex() {
		return xindex;
	}

	/* (non-Javadoc)
	 * @see ambit2.ui.domain.IADPlotsType#setXindex(int)
	 */
	public void setXindex(int xindex) {
		this.xindex = xindex;

	}

	/* (non-Javadoc)
	 * @see ambit2.ui.domain.IADPlotsType#getYindex()
	 */
	public int getYindex() {
		return yindex;
	}

	/* (non-Javadoc)
	 * @see ambit2.ui.domain.IADPlotsType#setYindex(int)
	 */
	public void setYindex(int yindex) {
		this.yindex = yindex;

	}
    protected String getCoveragename(QSARDataset ds) {
        try {
            return ds.getCoverage().getMethod().toString();
            } catch (Exception x) {
                return "Domain";
            }
    }
    public String getTitle() {
    	return "Histogram";
    }

}
