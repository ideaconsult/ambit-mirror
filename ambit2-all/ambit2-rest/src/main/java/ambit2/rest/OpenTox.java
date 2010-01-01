package ambit2.rest;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Reference;

public class OpenTox {
	public static enum URI {
		task,
		algorithm,
		model,
		dataset {
			@Override
			public String getKey() {
				return "dataset_id";
			}
		},
		feature,
		reference,
		compound,
		conformer {
			@Override
			public String getResourceID() {
				return String.format("%s%s/{%s}",OpenTox.URI.compound.getResourceID(),getURI(),getKey());
			}
		};
		public String getURI() {
			return String.format("/%s",toString());
		}
		public String getKey() {
			return String.format("id%s",toString());
		}		
		public String getResourceID() {
			return String.format("%s/{%s}",getURI(),getKey());
		}			
		public String getValue(Request request) throws Exception {
			Object o = request.getAttributes().get(getKey());
			return o==null?null:Reference.decode(o.toString());
		}
		public int getIntValue(Request request) throws Exception {
			return Integer.parseInt(getValue(request));
		}		
	};
	
	public enum params  {
		feature_uris {
			@Override
			public String toString() {
				return String.format("%s[]",super.toString());
			}
			@Override
			public String getDescription() {
				return "Feature URI(s)";
			}
		},
		dataset_uri {
			public boolean isMandatory() {
				return true;
			}
			public String getDescription() {
				return "Dataset URI to be used as input to model generation (ALgorithm POST) or prediction (Model POST)";
			}			
		},
		target {
			public boolean isMandatory() {
				return false;
			}			
			public String getDescription() {
				return "Feature URI, target variable for model generation of classification and regression models";
			}			
		},
		source_uri {
		}; 
		public boolean isMandatory() {
			return true;
		}
		public String getDescription() {
			return "either use ?source_uri=URI, or POST with text/uri-list or RDF representation of the object to be created";
		}
		public Object getFirstValue(Form form) {
			return form.getFirstValue(toString());
		}
		public String[] getValuesArray(Form form) {
			return form.getValuesArray(toString());
		}
	};
	private OpenTox() {
	}
	
}
