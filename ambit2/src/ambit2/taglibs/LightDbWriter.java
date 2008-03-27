package ambit2.taglibs;

import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import ambit2.database.writers.DefaultDbWriter;
import ambit2.exceptions.AmbitException;
import ambit2.data.AmbitUser;

/**
 * TODO refactor DB writers in a more efficient way
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class LightDbWriter extends DefaultDbWriter {
	protected Document properties;
	
	public LightDbWriter(Connection connection,AmbitUser user, InputSource properties) throws AmbitException{
		super();
		this.connection = connection;
		this.user = user;
		setProperties(properties);
	}
	@Override
	protected void prepareStatement() throws SQLException {
		// TODO Auto-generated method stub

	}

	public void write(IChemObject arg0) throws CDKException {
		// TODO Auto-generated method stub

	}
	protected void setProperties(InputSource properties) throws AmbitException {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
			this.properties = builder.parse(properties);
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	

}
