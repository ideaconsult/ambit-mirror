package ambit2.base.data.study;

import java.util.Hashtable;

import com.google.common.base.Objects;

/**
 * Corresponds to Klimish codes
 * @author nina
 *
 * @param <VALUE>
 */

public class ReliabilityParams<VALUE> extends Params<VALUE> {

	public enum _r_flags {
	    experimentalresult {
		@Override
		public String toString() {
		    return "experimental result";
		}
		@Override
		public String getCode() {
		    return "1895";
		}
		@Override
		public String getIdentifier() {
		    return "E";
		}
		@Override
		public boolean isPredicted() {
		    return false;
		}
	    },
	    experimentalstudyplanned {
		@Override
		public String toString() {
		    return "experimental study planned";
		}
		@Override
		public String getCode() {
		    return "1896";
		}
		@Override
		public String getIdentifier() {
		    return "Planned";
		}
		@Override
		public boolean isPredicted() {
		    return false;
		}
	    },
	    estimatedbycalculation {
		@Override
		public String toString() {
		    return "estimated by calculation";
		}
		@Override
		public String getCode() {
		    return "1885";
		}
		@Override
		public String getIdentifier() {
		    return "Calculated";
		}
	    },
	    readacrossbasedongroupingofsubstancescategoryapproach {
		@Override
		public String toString() {
		    return "read-across based on grouping of substances (category approach)";
		}
		@Override
		public String getCode() {
		    return "2303";
		}
		@Override
		public String getIdentifier() {
		    return "Read-across(category)";
		}
	    },
	    readacrossfromsupportingsubstancestructuralanalogueorsurrogate {
		@Override
		public String toString() {
		    return "read-across from supporting substance (structural analogue or surrogate)";
		}
		@Override
		public String getCode() {
		    return "2304";
		}
		@Override
		public String getIdentifier() {
		    return "Read-across";
		}
	    },
	    QSAR {
		@Override
		public String toString() {
		    return "(Q)SAR";
		}
		@Override
		public String getCode() {
		    return "14";
		}
		@Override
		public String getIdentifier() {
		    return "QSAR";
		}		
	    },
	    other {
		@Override
		public String toString() {
		    return "other";
		}
		@Override
		public String getCode() {
		    return "1342";
		}
		@Override
		public String getIdentifier() {
		    return name();
		}
	    },
	    nodata {
		@Override
		public String toString() {
		    return "no data";
		}
		@Override
		public String getCode() {
		    return "1173";
		}
	    },
	    NOTSPECIFIED {
		@Override
		public String toString() {
		    return "NOT_SPECIFIED";
		}
		@Override
		public String getCode() {
		    return "";
		}
	    },
	    unsupported {
		@Override
		public String toString() {
		    return "unsupported";
		}
		@Override
		public String getCode() {
		    return null;
		}
	    };	 	    
	    public boolean isPredicted() {
		return true;
	    }
	    public abstract String getCode();
	    public String getIdentifier() {
		return name();
	    }
	}
	    
	private static Hashtable<String, String> phrasegroup_Z05 = phrasegroup_Z05();
	private static Hashtable<String, String> phrasegroup_Z05() {
	    	Hashtable<String, String> p = new Hashtable<String,String>();
	    	for (_r_flags f : _r_flags.values()) 
	    		if (f.getCode()!=null)
	    	    p.put(f.getCode(),f.toString());
	    	return p;
	    }

	@Override
    public int hashCode() {
		return Objects.hashCode(getValue(),getIsRobustStudy(),getIsUsedforClassification(),getIsUsedforMSDS(),getPurposeFlag(),getStudyResultType());
    }	
	
	public VALUE getId() {
		return get(_FIELDS_RELIABILITY.r_id.name());
	}

	public void setId(VALUE id) {
		put(_FIELDS_RELIABILITY.r_id.name(),id);
	}

	public VALUE getValue() {
		return get(_FIELDS_RELIABILITY.r_value.name());
	}

	public void setValue(VALUE value) {
		put(_FIELDS_RELIABILITY.r_value.name(),value);
	}

	public VALUE getIsRobustStudy() {
		return get(_FIELDS_RELIABILITY.r_isRobustStudy.name());
	}

	public void setIsRobustStudy(VALUE isRobustStudy) {
		put(_FIELDS_RELIABILITY.r_isRobustStudy.name(),isRobustStudy);
	}

	public VALUE getIsUsedforClassification() {
		return get(_FIELDS_RELIABILITY.r_isUsedforClassification.name());
	}

	public void setIsUsedforClassification(VALUE isUsedforClassification) {
		put(_FIELDS_RELIABILITY.r_isUsedforClassification.name(),isUsedforClassification);
	}

	public VALUE getIsUsedforMSDS() {
		return get(_FIELDS_RELIABILITY.r_isUsedforMSDS.name());
	}

	public void setIsUsedforMSDS(VALUE isUsedforMSDS) {
		put(_FIELDS_RELIABILITY.r_isUsedforMSDS.name(),isUsedforMSDS);
	}

	public VALUE getPurposeFlag() {
		return get(_FIELDS_RELIABILITY.r_purposeFlag.name());
	}

	public void setPurposeFlag(VALUE purposeFlag) {
		put(_FIELDS_RELIABILITY.r_purposeFlag.name(),purposeFlag);
	}

	public VALUE getStudyResultType() {
		return get(_FIELDS_RELIABILITY.r_studyResultType.name());
	}

	public void setStudyResultType(VALUE studyResultType) {
	        Object value = phrasegroup_Z05.get(studyResultType);
		put(_FIELDS_RELIABILITY.r_studyResultType.name(),value==null?studyResultType:(VALUE)value);
	}


	public ReliabilityParams() {
	}
}
