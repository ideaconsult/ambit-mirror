package ambit2.rest;

import java.util.HashMap;
import java.util.Map;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.routing.Template;

import ambit2.base.data.study.ModelParams;

public class OpenTox {
	public static enum URI {
		query,
		missingValues,
		task,
		algorithm,
		model,
		dataset {
			@Override
			public String getKey() {
				return "dataset_id";
			}
			@Override
			public Object getId(String uri, Template template) {
				Map<String, Object> vars = new HashMap<String, Object>();
				try {
					template.parse(uri, vars);
					return Integer.parseInt(vars.get(getKey()).toString()); 
				} catch (Exception x) { 
					return vars.get(getKey()).toString(); 
				}
			}
		},
		bundle {
			@Override
			public String getKey() {
				return "idbundle";
			}
			@Override
			public Object getId(String uri, Template template) {
				Map<String, Object> vars = new HashMap<String, Object>();
				try {
					template.parse(uri, vars);
					return Integer.parseInt(vars.get(getKey()).toString()); 
				} catch (Exception x) { 
					return vars.get(getKey()).toString(); 
				}
			}
		},		
		feature,
		bookmark {
			@Override
			public String getResourceID() {
				return String.format("%s/{creator}/entries/{%s}",getURI(),getKey());
			}	
		},
		reference,
		compound ,
		conformer {
			@Override
			public String getResourceID() {
				return String.format("%s%s/{%s}",OpenTox.URI.compound.getResourceID(),getURI(),getKey());
			}
			@Override
			public Object[] getIds(String uri,Reference baseReference)  {
				return getIds(uri,getTemplate(baseReference));
			}
			@Override
			public Object[] getIds(String uri,Template template)  {
				Map<String, Object> vars = new HashMap<String, Object>();
				Object[] ids = new Object[2];
				try { 
					template.parse(uri, vars);
					try {
					ids[0] = Integer.parseInt(vars.get(OpenTox.URI.compound.getKey()).toString()); } 
					catch (Exception x) { ids[0] = null;};
				
					try { ids[1] = Integer.parseInt(vars.get(OpenTox.URI.conformer.getKey()).toString()); } 
					catch (Exception x) { ids[1] = null;};
				} catch (Exception x) {};
				return ids;
			}			
		},
		qmap,
		substance,
		substanceowner,
		composition,
		study,
		effect;
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
		public Template getTemplate(Reference baseReference) {
			return new Template(String.format("%s%s",baseReference==null?"":baseReference,getResourceID()));
		}
		
		public Object getId(String uri, Reference baseReference) {
			return getId(uri, getTemplate(baseReference));
		}
		
		public Object getId(String uri,Template template)  {
			Map<String, Object> vars = new HashMap<String, Object>();
			
			try {
				template.parse(uri, vars);
				return Integer.parseInt(vars.get(getKey()).toString()); 
			} catch (Exception x) { return null; }
		}

		public Object[] getIds(String uri,Reference baseReference)  {
			return null;
		}
		
		public Object[] getIds(String uri,Template template)  { return null; }
	};
	
	public enum params  {
		filter {
			@Override
			public String getDescription() {
				return "Filtered URI(s)";
			}
		},		
		condition {
			@Override
			public String getDescription() {
				return "Filtered URI(s) condition";
			}
		},			
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
		sameas {

			@Override
			public String getDescription() {
				return "sameas";
			}
		},		
		model_uri {
			@Override
			public String getDescription() {
				return "Model URI";
			}
		},		
		algorithm_uri {
			@Override
			public String getDescription() {
				return "Algorithm URI";
			}
		},		
		compound_uri {
			public boolean isMandatory() {
				return false;
			}
	
			public String getDescription() {
				return "Compound URI";
			}			
		},			
		compound_uris {
			public boolean isMandatory() {
				return false;
			}
			@Override
			public String toString() {
				return String.format("%s[]",super.toString());
			}			
			public String getDescription() {
				return "Compound URIs";
			}			
		},	
		bundle_uri {
			public boolean isMandatory() {
				return false;
			}
	
			public String getDescription() {
				return "Bundle URI";
			}			
		},		
		substance_uri {
			public boolean isMandatory() {
				return false;
			}
	
			public String getDescription() {
				return "Substance URI";
			}			
		},			
		delay {
			public boolean isMandatory() {
				return false;
			}
			public String getDescription() {
				return "Delay,ms for /algorithm/mockup";
			}			
		},		
		error {
			public boolean isMandatory() {
				return false;
			}
			public String getDescription() {
				return "Error to be thrown by /algorithm/mockup";
			}			
		},			
		dataset_uri {
			public boolean isMandatory() {
				return true;
			}
			public String getDescription() {
				return "Dataset URI to be used as input to model generation (Algorithm POST) or prediction (Model POST)";
			}			
		},
		target {
			public boolean isMandatory() {
				return false;
			}			
			public String getDescription() {
				return "Feature URI, target variable for model generation of classification and regression models";
			}	
			@Override
			public String toString() {
				return "prediction_feature";
			}
		},
		parameters {
			public boolean isMandatory() {
				return false;
			}			
			public String getDescription() {
				return "Algorithm parameters";
			}			
		},		
		source_uri {
			public String getDescription() {
				return "either use ?source_uri=URI, or POST with text/uri-list or RDF representation of the object to be created";
			}
		},
		dataset_service {
			public String getDescription() {
				return "Destination dataset service, used by Algorithms and Models to store results";
			}
		},
		//superbuilder_param 
		feature_calculation {
			@Override
			public boolean isMandatory() {
				return false;
			}
			public String getDescription() {
				return "Feature calculation algorithms, used by superservice for model creation";
			}
		},
		feature_selection {
			@Override
			public boolean isMandatory() {
				return false;
			}
			public String getDescription() {
				return "Feature selection algorithms, used by superservice for model creation";
			}			
		},
		model_learning {
			@Override
			public boolean isMandatory() {
				return false;
			}	
			public String getDescription() {
				return "Model learning algorithms, used by superservice for model creation";
			}			
		},
		applicability_domain {
			@Override
			public boolean isMandatory() {
				return false;
			}
			public String getDescription() {
				return "Applicability domain algorithms, used by superservice for model creation";
			}	
		},
		validation_service {
			@Override
			public boolean isMandatory() {
				return false;
			}
			public String getDescription() {
				return "Validation service, used by superservice for model creation";
			}				
		},
		confidenceOf {
			@Override
			public boolean isMandatory() {
				return false;
			}
			public String getDescription() {
				return "ConfidenceOf";
			}				
		},
		page {
			public String getDescription() {
				return "Page";
			}
		},
		pagesize {
			public String getDescription() {
				return "Page size";
			}
		},
		qmap_uri {
			public boolean isMandatory() {
				return true;
			}
			public String getDescription() {
				return "QMap URI";
			}			
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
		public ModelParams getModelParams(Form form) {
			ModelParams p = new ModelParams(form.getFirstValue(toString()));
			return p;
		}

	};
	
	
	private OpenTox() {
	}
	
	
	
}
