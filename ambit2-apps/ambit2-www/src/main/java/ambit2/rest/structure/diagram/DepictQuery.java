package ambit2.rest.structure.diagram;

import java.io.Serializable;

import org.restlet.data.Form;
import org.restlet.engine.util.Base64;

import ambit2.rendering.CompoundImageTools.Mode2D;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.StructureQueryResource.QueryType;

/**
 * Depiction parameters
 * 
 * @author nina
 * 
 */
public class DepictQuery implements Serializable {
	public enum depict_type {
		all, cdk, obabel {
			@Override
			public DepictionReporter getReporter() {
				return new DepictionReporterOBabel();
			}
		},
		cactvs {
			@Override
			public DepictionReporter getReporter() {
				return new DepictionReporterCSLS();
			}
		},
		pubchem {
			@Override
			public DepictionReporter getReporter() {
				return new DepictionReporterPubChem();
			}
		},
		tautomer, reaction, standardize {
			@Override
			public DepictionReporter getReporter() {
				return new DepictionReporterStandardize();
			}
			@Override
			public DepictQuery createQuery() {
				return new DepictQueryStandardize();
			}

		};

		public DepictionReporter getReporter() {
			return new DepictionReporterCDK();
		}

		public DepictQuery createQuery() {
			return new DepictQuery(this);
		}
	}

	public DepictQuery(depict_type type) {
		this.depictType = type;
	}

	public void parseQuery(Form form) {
		try {

			try {
				setW(Integer.parseInt(form.getFirstValue("w")));
			} catch (Exception x) {
				setW(400);
			}
			try {
				setH(Integer.parseInt(form.getFirstValue("h")));
			} catch (Exception x) {
				setH(400);
			}
			try {
				setRecordType(form.getFirstValue("record_type"));
			} catch (Exception x) {
				setRecordType("2d");
			}
			try {
				setqType(QueryType.valueOf(form.getFirstValue("type")));
			} catch (Exception x) {
				setqType(QueryType.smiles);
			}
			switch (getqType()) {
			case mol: { // base64 encoded mol files
				setSmiles(form.getValuesArray(QueryResource.b64search_param));
				if (getSmiles() != null)
					for (int i = 0; i < getSmiles().length; i++)
						getSmiles()[i] = new String(Base64.decode(getSmiles()[i]));
				break;
			}
			default: {
				String[] smi = form.getValuesArray(QueryResource.search_param);
				setSmiles(smi);
				smi=getSmiles();
				if ((smi == null) || (smi.length < 1))
					setSmiles(new String[] { null });
				else
					setSmiles(new String[] { smi[0] == null ? "" : smi[0].trim()});
			}
			}

			setSmarts(form.getFirstValue("smarts"));
			setSmirks(null);
			String[] smirks_patterns = form.getValuesArray("smirks");
			for (String sm : smirks_patterns)
				if (sm != null) {
					setSmirks(sm);
					break;
				}
		} catch (Exception x) {
			x.printStackTrace();
		}

	}

	protected depict_type depictType = depict_type.cdk;

	public depict_type getDepictType() {
		return depictType;
	}

	protected Mode2D displayMode = null;

	public Mode2D getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(Mode2D displayMode) {
		this.displayMode = displayMode;
	}

	public String[] getSmiles() {
		return smiles;
	}

	public void setSmiles(String[] smiles) {
		this.smiles = smiles;
	}

	public String getSmarts() {
		return smarts;
	}

	public void setSmarts(String smarts) {
		this.smarts = smarts;
	}

	public String getSmirks() {
		return smirks;
	}

	public void setSmirks(String smirks) {
		this.smirks = smirks;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public QueryType getqType() {
		return qType;
	}

	public void setqType(QueryType qType) {
		this.qType = qType;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4119202562680877640L;
	protected String[] smiles;
	protected String smarts;
	protected String smirks;
	protected String recordType = "2d";
	protected QueryType qType = QueryType.smiles;
	protected int w = 400;
	protected int h = 200;
}
