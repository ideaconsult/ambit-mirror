package ambit2.rest.model.predictor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.model.ModelQueryResults;
import ambit2.model.structure.DataCoverageFingerprints;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.property.PropertyURIReporter;

public class FingerprintsPredictor extends	CoveragePredictor<IStructureRecord,List<BitSet>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5981058745327200642L;
	protected FingerprintGenerator fpgen;
	protected MoleculeReader reader = new MoleculeReader();
	protected List<BitSet> bitsets = new ArrayList<BitSet>();
	
	public FingerprintsPredictor(Reference applicationRootReference,
			ModelQueryResults model, ModelURIReporter modelReporter,
			PropertyURIReporter propertyReporter, String[] targetURI)
			throws ResourceException {
		super(applicationRootReference, model, modelReporter, propertyReporter,
				targetURI);
		fpgen = new FingerprintGenerator();
		setStructureRequired(true);
		setValuesRequired(false);
	}

	@Override
	public String getCompoundURL(IStructureRecord target) throws AmbitException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<BitSet> transform(IStructureRecord target) throws AmbitException {
		bitsets.clear();
		bitsets.add(fpgen.process(reader.process(target)));
		return bitsets;
		
	}
	
	@Override
	protected boolean isSupported(Object predictor) throws ResourceException {
		return (predictor instanceof DataCoverageFingerprints);
	}
	
	public IStructureRecord process(IStructureRecord target) throws AmbitException {
		Object value = predict(target);
		assignResults(target, value);
		return target;
	}	
	@Override
	public BufferedImage getLegend(int width, int height) throws AmbitException {
		return null;
	}
}
