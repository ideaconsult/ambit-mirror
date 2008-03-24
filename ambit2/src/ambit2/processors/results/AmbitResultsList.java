package ambit2.processors.results;

import java.io.Writer;
import java.util.ArrayList;

import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitResult;

/**
 * 
 * A list of {@link ambit.processors.IAmbitResult}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class AmbitResultsList extends ArrayList<IAmbitResult> implements IAmbitResult {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6928588073417908177L;
	protected String title;
	public AmbitResultsList() {
	}

	public AmbitResultsList(int arg0) {
		super(arg0);
	}


	public void update(Object object) throws AmbitException {
		for (int i=0; i < size(); i++) { 
			if (get(i) != null) { 
				get(i).update(object);
			}	
		}	

	}
	public void write(Writer writer) throws AmbitException {
		for (int i=0; i < size(); i++){
			if (get(i) != null) {
				get(i).write(writer);
			}
		}	
		
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
		
	}



}
