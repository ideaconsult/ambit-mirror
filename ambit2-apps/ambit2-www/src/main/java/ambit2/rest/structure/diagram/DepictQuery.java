package ambit2.rest.structure.diagram;

import java.io.Serializable;

import ambit2.rendering.CompoundImageTools.Mode2D;
import ambit2.rest.query.StructureQueryResource.QueryType;

/**
 * Depiction parameters
 * 
 * @author nina
 * 
 */
public class DepictQuery implements Serializable {
	public enum depict_type {
		all,
		cdk, 
		obabel {
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
		tautomer, 
		reaction
		;

		public DepictionReporter getReporter() {
			return new DepictionReporterCDK();
		}

	}

	public DepictQuery(depict_type type) {
		this.depictType = type;
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
