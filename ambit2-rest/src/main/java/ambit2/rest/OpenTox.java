package ambit2.rest;

import org.restlet.data.Reference;
import org.restlet.data.Request;

public class OpenTox {
	public static enum URI {
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
	private OpenTox() {
	}
	
}
