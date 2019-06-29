package ambit2.groupcontribution.dataset;

import java.io.File;
import java.util.Iterator;

public class LocalPropertiesIterator implements Iterator<LocalProperties> 
{
	File file = null;
	
	public LocalPropertiesIterator(File file) throws Exception
	{
		this.file = file;
		init();
	}
	
	protected void init() throws Exception
	{
		//TODO
	}
	
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LocalProperties next() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
