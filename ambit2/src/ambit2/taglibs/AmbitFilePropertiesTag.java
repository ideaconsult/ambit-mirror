package ambit2.taglibs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.io.FileInputState;
import ambit2.io.IteratingFilesWithHeaderReader;

public class AmbitFilePropertiesTag extends AmbitFileTag {
	protected String  params ="params";
	protected int recordsToRead =1;
	
	public String getRecordsToRead() {
		return Integer.toString(recordsToRead);
	}

	public void setRecordsToRead(String recordsToRead) {
		try {
			this.recordsToRead = Integer.parseInt(recordsToRead);
		} catch (Exception x) {
			this.recordsToRead = 1;
		}
	}

	@Override
	public void doTag() throws JspException, IOException {
		super.doTag();
		try {
			File file = getFile();
			IIteratingChemObjectReader reader = FileInputState.getReader(
					new FileInputStream(file), file.getName());
			System.out.println("reader");
			if (reader instanceof IteratingFilesWithHeaderReader) 
				this.recordsToRead = 1;
			Hashtable<String, String> p = new Hashtable<String, String>();
			int i = 0;
			while (reader.hasNext()) {
				i++;
				Object o = reader.next();
				if ((o != null) && (o instanceof IChemObject)) {
					Map h = ((IChemObject)o).getProperties();
					if (h != null) {
						Iterator keys = h.keySet().iterator();
						while (keys.hasNext()) {
							Object key = keys.next();
							p.put(key.toString(), key.toString());
						}
					}
				}
				if (i>= recordsToRead) break;
			}
			reader.close();
			JspContext pageContext = getJspContext();
			pageContext.setAttribute(params, p);
		} catch (Exception x) {
			x.printStackTrace();
			throw new JspException(x);
		}
	}


	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}


}
