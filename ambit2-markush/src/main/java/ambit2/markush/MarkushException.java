package ambit2.markush;

public class MarkushException extends Exception 
{
	    
	    private static final long serialVersionUID = 583453585294567201L;
	    public MarkushException(String message) {
			super(message);
		}
	    public MarkushException(String message,Exception x) {
			super(message,x);
		}    
}
