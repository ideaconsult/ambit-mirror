package ambit2.core.data;

/**
 * Array of srings implementing
 * {@link org.openscience.cdk.qsar.result.IDescriptorResult}.
 * 
 * @author nina
 * 
 */
public class StringArrayResult extends ArrayResult<String> {
    /**
     * 
     */
    private static final long serialVersionUID = 7274535049474222283L;

    public StringArrayResult() {
	super();
    }

    public StringArrayResult(String[] array) {
	super(array);
    }
}
