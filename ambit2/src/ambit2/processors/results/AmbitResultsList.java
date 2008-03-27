package ambit2.processors.results;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitResult;

/**
 * 
 * A list of {@link ambit2.processors.IAmbitResult}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class AmbitResultsList extends ArrayList<IAmbitResult> implements IAmbitResult {
	protected String title;
	public AmbitResultsList() {
		super();
	}

	public AmbitResultsList(int arg0) {
		super(arg0);
	}

	public AmbitResultsList(Collection arg0) {
		super(arg0);
	}

	public void update(Object object) throws AmbitException {
		for (int i=0; i < size(); i++)
			if ((IAmbitResult) get(i) != null)
				((IAmbitResult) get(i)).update(object);

	}
	public void write(Writer writer) throws AmbitException {
		for (int i=0; i < size(); i++) 
			if ((IAmbitResult) get(i) != null)
				((IAmbitResult) get(i)).write(writer);	
		
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
		
	}



}
