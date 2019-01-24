package ambit2.structure2name.components;

import java.util.ArrayList;
import java.util.List;

public class CyclicComponent extends AbstractComponent 
{
	public List<Integer> ringNumbers = new ArrayList<Integer>();
	
	@Override
	public CompType getType() {
		return CompType.CYCLIC_COMPONENT;
	}
}