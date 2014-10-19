package ambit2.pubchem.rest;

import java.net.MalformedURLException;
import java.net.URL;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.search.chemidplus.AbstractSearchRequest;

public abstract class PUGRestRequest<R> extends AbstractSearchRequest<R> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4821305383980518514L;
	public static final String PUGREST_URL = "http://pubchem.ncbi.nlm.nih.gov/rest/pug/";
	public static final String PUGREST_COMPOUND_URL = String.format("%scompound/", PUGREST_URL);

	public enum COMPOUND_DOMAIN_INPUT {
		cid,
		sid,
		name,
		smiles,
		inchi,
		inchikey,
		substructure,
		listkey;
		public String getRepresentation(String query) {
			return String.format("%s/%s",name(),query);
		}
	};
	public enum COMPOUND_DOMAIN_OPERATION {
		record,
		property,
		synonyms,
		sids,
		cids,
		aids,
		assaysummary,
		classification;
		public String getOperation() {
			return name();
		}
	};	
	public enum COMPOUND_DOMAIN_OUTPUT {
		XML,
		ASNT,
		ASNB {
			@Override
			public String getMediaType() {
				return "application/ber-encoded";
			}				
		},
		JSON  {
			@Override
			public String getMediaType() {
				return "application/json";
			}			
		},
		JSONP  {
			@Override
			public String getMediaType() {
				return "application/javascript";
			}
		},
		callback,
		SDF  {
			@Override
			public String getMediaType() {
				return "chemical/x-mdl-sdfile";
			}
		},
		CSV  {
			@Override
			public String getMediaType() {
				return "text/csv";
			}
		},
		PNG  {
			@Override
			public String getMediaType() {
				return "image/png";
			}
		};
		
		public String getRepresentation() {
			return name();
		}
		public String getMediaType() {
			return "application/xml";
		}
	};		
	protected COMPOUND_DOMAIN_INPUT input = COMPOUND_DOMAIN_INPUT.smiles;
	public COMPOUND_DOMAIN_INPUT getInput() {
		return input;
	}
	public void setInput(COMPOUND_DOMAIN_INPUT input) {
		this.input = input;
	}
	public COMPOUND_DOMAIN_OPERATION getOperation() {
		return operation;
	}
	public void setOperation(COMPOUND_DOMAIN_OPERATION operation) {
		this.operation = operation;
	}
	public COMPOUND_DOMAIN_OUTPUT getOutput() {
		return output;
	}
	public void setOutput(COMPOUND_DOMAIN_OUTPUT output) {
		this.output = output;
	}

	protected COMPOUND_DOMAIN_OPERATION operation = null;
	protected COMPOUND_DOMAIN_OUTPUT output;
	

	
	protected String getOptions() { return "";}
	
	public R process(String query) throws AmbitException {
		try {

			return get(new URL(
				String.format("%s%s%s%s/%s%s", 
							PUGREST_COMPOUND_URL, 
							getInput().getRepresentation(query),
							getOperation()==null?"":"/",
							getOperation()==null?"":getOperation(),
							getOutput(),getOptions())));
		} catch (MalformedURLException x) {
			x.printStackTrace();
			throw new AmbitException(x);
		}
	}
		
	@Override
	public String toString() {
		return PUGREST_COMPOUND_URL;
	}
}