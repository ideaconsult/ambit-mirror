package ambit2.rest.structure.diagram;

import org.restlet.data.Form;

import ambit2.tautomers.processor.StructureStandardizer;

public class DepictQueryStandardize extends DepictQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6474835200588476235L;
	protected StructureStandardizer standardizer;

	public StructureStandardizer getStandardizer() {
		return standardizer;
	}

	public void setStandardizer(StructureStandardizer standardizer) {
		this.standardizer = standardizer;
	}

	public DepictQueryStandardize() {
		super(depict_type.standardize);
	}

	@Override
	public void parseQuery(Form form) {
		super.parseQuery(form);
		if (standardizer == null) {
			standardizer = new StructureStandardizer();
			standardizer.setImplicitHydrogens(true);
			standardizer.setClearIsotopes(true);
			standardizer.setGenerateSMILES(false);
			standardizer.setGenerateInChI(true);
		}	

		try {
			String ok = form.getFirstValue("neutralise");
			standardizer.setNeutralise("ON".equals(ok.toUpperCase()) || Boolean.parseBoolean(ok));
		} catch (Exception x) {
			standardizer.setNeutralise(false);
		}
		try {
			String ok = form.getFirstValue("tautomer");
			standardizer.setGenerateTautomers("ON".equals(ok.toUpperCase()) || Boolean.parseBoolean(ok));
					
		} catch (Exception x) {
			standardizer.setGenerateTautomers(false);
		}
		try {
			String ok = form.getFirstValue("fragment");
			standardizer.setSplitFragments("ON".equals(ok.toUpperCase()) || Boolean.parseBoolean(ok));
					
		} catch (Exception x) {
			standardizer.setSplitFragments(false);
		}		
	}
}
