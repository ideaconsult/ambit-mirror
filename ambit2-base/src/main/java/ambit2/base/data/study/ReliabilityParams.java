package ambit2.base.data.study;

import com.google.common.base.Objects;

/**
 * Corresponds to Klimish codes
 * @author nina
 *
 * @param <VALUE>
 */

public class ReliabilityParams<VALUE> extends Params<VALUE> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
		put(_FIELDS_RELIABILITY.r_studyResultType.name(),studyResultType);
	}


	public ReliabilityParams() {
	}
}
