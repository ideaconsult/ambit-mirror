package ambit2.notation;

public class NotationElement 
{
	public static enum NotationElementType {
		INTEGER, DOUBLE, STRING
	}
	
	public String name = null;
	public String info = null;
	public boolean flagActive = true; 
	public int order = -1;
	public NotationElementType elementType = NotationElementType.STRING; 
	//public Object dataObj = null;
}
