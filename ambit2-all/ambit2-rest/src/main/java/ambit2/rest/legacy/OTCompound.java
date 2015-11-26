package ambit2.rest.legacy;

import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.List;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * Implementation of OpenTox API {@link http://opentox.org/dev/apis/api-1.1/structure}
 * @author nina
 *
 */
@Deprecated
public class OTCompound extends OTObject {
	protected Hashtable<String, String> identifiers = null;
	public Hashtable<String, String> getIdentifiers() {
		return identifiers;
	}
	public void setIdentifiers(Hashtable<String, String> identifiers) {
		this.identifiers = identifiers;
	}
	public enum _titles { //from OpenTox.owl
		Compound,
		CASRN {
			@Override
			public String getTitle() {
				return "CASRN";
			}
		},
		EINECS,
		IUPACName {
			@Override
			public String getTitle() {
				return "IUPAC name";
			}
		},
		ChemicalName {
			@Override
			public String getTitle() {
				return "Chemical Name";
			}
		},
		SMILES,
		InChI_std {
			@Override
			public String getTitle() {
				return "Standard InChI";
			}
		},
		InChIKey_std {
			@Override
			public String getTitle() {
				return "Standard InChI key";
			}
		},
		REACHRegistrationDate {
			@Override
			public String getTitle() {
				return "REACH registration date";
			}
		};
		public String getTitle() {
			return name();
		}
		@Override
		public String toString() {
			return getTitle();
		}
	}	
	 /**
	 * 
	 */
	private static final long serialVersionUID = -979314615579855647L;

	public OTCompound(Reference ref) {
			super(ref);
		 }
	public OTCompound(String ref) {
			super(ref);
	}
		 public static OTCompound compound(Reference uri) throws Exception  { 
			    return new OTCompound(uri);
		 }

		 public static OTCompound compound(String uri) throws Exception  { 
			    return new OTCompound(uri);
		 }

		public static OTCompound createFromName(String name,String compoundService)  throws Exception  {
			Form form = new Form();
			form.add("identifier", name);
			OTRemoteTask task = new OTRemoteTask(new Reference(compoundService),MediaType.TEXT_URI_LIST,form.getWebRepresentation(),Method.POST);
			Thread.sleep(200);
			while (!task.isDone()) {
				task.poll();
				Thread.sleep(800);
				Thread.yield();
			}
			if (task.isERROR())
				throw task.getError();
			else if (task.getResult().toString().indexOf("/compound/")>0) 
		    	return compound(task.getResult());
		    else throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,name);
		    
		}

		 /*
    protected Hashtable<String, String> find(String queryService, String value) throws Exception {
		URL url = new URL(String.format("http://apps.ideaconsult.net:8080/ambit2/query/compound/%s/all?max=1",
						URLEncoder.encode(value,"UTF-8")));
		return read(url);
	}
		 
		 */
	public Hashtable<String, String> readIdentifiers(String queryService) throws Exception {
				String url = String.format(queryService,URLEncoder.encode(getUri().toString(),"UTF-8"));
				identifiers = read(url);
				return identifiers;
	}
	public Hashtable<String, String> read(String url) throws Exception {
		  return read(url,new Hashtable<String, String>());
	}
	public Hashtable<String, String> read(String url, Hashtable<String, String> nameValues) throws Exception {
		final Hashtable<String, String> v = nameValues==null?new Hashtable<String, String>():nameValues;
		CSVFeatureValuesIterator i = new CSVFeatureValuesIterator(url) {
			@Override
			public Object transformRawValues(List header, List values) {
				for (int i=0; i < header.size();i++) 
					v.put(header.get(i).toString().replace("http://www.opentox.org/api/1.1#", ""),
							values.get(i).toString());
				return values;
			}
		};
		try {
			while (i.hasNext())
				i.next();
		} finally {
			i.close();
		}
		return v;
	}		 
	

}
