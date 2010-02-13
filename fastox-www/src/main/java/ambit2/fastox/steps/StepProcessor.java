package ambit2.fastox.steps;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.restlet.data.Form;
import org.restlet.representation.Representation;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;

public class StepProcessor extends DefaultAmbitProcessor<Representation, Form> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1318642062244171641L;
	
	public Form process(Representation entity) throws AmbitException {
		return new Form(entity);
	}
	
	public static String readUriList(InputStream in) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) { return line; }
			
		} catch (Exception x) {
			
		} finally {
			try {in.close(); } catch (Exception x) {}
		}
		return null;
	}	
}
