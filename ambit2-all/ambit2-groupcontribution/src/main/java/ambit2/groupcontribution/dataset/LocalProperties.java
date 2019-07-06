package ambit2.groupcontribution.dataset;

import java.util.Map;


public class LocalProperties 
{
	public static enum Type {
		ATOM, BOND, ATOM_PAIR
	}
	
	public Type type = Type.ATOM;
	public Map<Object, double[]> properties = null;
}
