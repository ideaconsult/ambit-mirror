package ambit2.exceptions;

/**
 * Error when calculating descriptors
 * @author nina
 *
 */
public class DescriptorCalculationException extends AmbitException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 131420638700626061L;

	public DescriptorCalculationException() {
	}

	public DescriptorCalculationException(String arg0) {
		super(arg0);
	}

	public DescriptorCalculationException(Throwable arg0) {
		super(arg0);
	}

	public DescriptorCalculationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
