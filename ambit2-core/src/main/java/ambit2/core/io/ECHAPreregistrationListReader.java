package ambit2.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.processors.CASProcessor;
import ambit2.core.config.AmbitCONSTANTS;

/**
 * Reads preregistration list in XML format as in http://apps.echa.europa.eu/preregistered/prsDownload.aspx
 * @author nina
 *
 */
public class ECHAPreregistrationListReader extends
		DefaultIteratingChemObjectReader implements IRawReader<IStructureRecord> {
	protected static Logger logger = Logger.getLogger(ECHAPreregistrationListReader.class.getName());
	protected XMLStreamReader reader ;
	protected static String ECHA_URL="http://apps.echa.europa.eu/preregistered/prsDownload.aspx";
	protected static String ECHA_REFERENCE="ECHA";
	protected ArrayList<String> synonyms = new ArrayList<String>();
	protected IStructureRecord record;
	protected String tmpValue="";	
	protected CASProcessor casProcessor = new CASProcessor();
	protected Property casProperty = Property.getInstance(AmbitCONSTANTS.CASRN,
								LiteratureEntry.getInstance(ECHA_REFERENCE, ECHA_URL)); 
	protected Property ecProperty = Property.getInstance("EC",
								LiteratureEntry.getInstance(ECHA_REFERENCE, ECHA_URL));
	protected Property nameProperty = Property.getInstance(AmbitCONSTANTS.NAMES,
								LiteratureEntry.getInstance(ECHA_REFERENCE, ECHA_URL));
	protected Property registrationProperty = Property.getInstance(echa_tags.REGISTRATION_DATE.toString(),
								LiteratureEntry.getInstance(ECHA_REFERENCE, ECHA_URL));
	public enum echa_tags {
		dataroot,
		PRE_REGISTERED_SUBSTANCE,
		EC_NUMBER,
		CAS_NUMBER,
		NAME,
		REGISTRATION_DATE,
		RELATED_SUBSTANCE,
		RELATED_CAS_NUMBER,
		RELATED_EC_NUMBER,
		RELATED_NAME,
		SYNONYM,
		SYNONYM_NAME,
		SYNONYM_LANGUAGE,
		NONE
	}
	
    public ECHAPreregistrationListReader(InputStream in) throws CDKException {
    	record = new StructureRecord();
    	record.setFormat(MOL_TYPE.SDF.toString());
    	record.setContent("");
    	setReader(in);

    }	
	
    public void setReader(InputStream in) throws CDKException {
    	try {
    		XMLInputFactory factory = XMLInputFactory.newInstance();
    		factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,Boolean.TRUE);
    		reader =   factory.createXMLStreamReader(in,"UTF-8");
    		
    	} catch (Exception x) {
    		reader = null;
    		throw new CDKException(x.getMessage(),x);
    	}
    	
    }
    public void setReader(Reader in) throws CDKException {
    	try {
    		XMLInputFactory factory = XMLInputFactory.newInstance();
    		factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,Boolean.TRUE);
    		reader =   factory.createXMLStreamReader(in);
    		
    	} catch (Exception x) {
    		reader = null;
    		throw new CDKException(x.getMessage(),x);
    	}
    	
    }
	public void close() throws IOException {
		try {
			reader.close();
		} catch (XMLStreamException x) {
			throw new IOException(x.getMessage());
		}

	}

	public IResourceFormat getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasNext() {
		try {
		    while (reader.hasNext()) {
		    	int type = reader.next();
	            switch (type) {
	            case XMLStreamConstants.START_ELEMENT: {
	    			echa_tags tag = echa_tags.valueOf(reader.getName().getLocalPart());
	    			tmpValue = "";
	    			switch (tag) {
	    			case PRE_REGISTERED_SUBSTANCE: { 
	    				record.clear();
	    				synonyms.clear();
	    				break;
	    			}
	    			case NAME : {
	    				
	    			}
	    			case CAS_NUMBER: {
	    				
	    			}
	    			case EC_NUMBER: {
	    				
	    			}
	    			case REGISTRATION_DATE : {
	    				
	    			}
	    			case SYNONYM: {
	    				
	    			}
	    			case SYNONYM_NAME: {
	    				
	    			}
	    			}            	
	            	break;
	            }
	            case XMLStreamConstants.END_DOCUMENT: {
	            	return false;
	            }
	            case XMLStreamConstants.END_ELEMENT: {
	        		echa_tags tag = echa_tags.valueOf(reader.getName().getLocalPart());
	        		switch (tag) {
	        		case PRE_REGISTERED_SUBSTANCE: { 
	        			for (int i=0;i < synonyms.size();i++)
	        				record.setRecordProperty(
	        						Property.getInstance(AmbitCONSTANTS.NAMES,
	        						LiteratureEntry.getInstance(String.format("%s %s#%d",ECHA_REFERENCE,echa_tags.SYNONYM.toString(),i+1, 
	        								ECHA_URL),ECHA_URL))
	        						,synonyms.get(i));
	
	        			
	        			return true;
	        		}
	        		case NAME : {
	        			record.setRecordProperty(nameProperty,tmpValue);
	        			break;				
	        		}
	        		case EC_NUMBER: {
	        			record.setRecordProperty(ecProperty,tmpValue);
	        			break;
	        		}			
	        		case CAS_NUMBER: {
	        			try {
	        				record.setRecordProperty(casProperty,casProcessor.process(tmpValue));
	        			} catch (Exception x) {
	            			record.setRecordProperty(casProperty,tmpValue);
	        			}
	        			break;
	        		}
	        		case REGISTRATION_DATE: {
	        			record.setRecordProperty(registrationProperty,tmpValue);
	        			break;
	        		}	
	        		case SYNONYM_NAME: {
	        			synonyms.add(tmpValue);
	        			break;			
	        		}
	        		default: {
	        			tmpValue = null;
	        		}
	        		}
	        		break;
	            }
	            case XMLStreamConstants.CHARACTERS: {
	            	 
	            	String value = reader.getText();
	            	if ((value!=null) && (!"".equals(value))) {
	            		tmpValue = tmpValue+value;
	            	}

	            	break;
	            }
	            }
		    	
		    }
		} catch (XMLStreamException x) {
			logger.log(Level.SEVERE,x.getMessage(),x);

		}
		return false;
	}

	public Object next() {
		return record;
	}

	public IStructureRecord nextRecord() {
		return record;
	}

	
	public void parseDocument() throws Exception {

	}
}
