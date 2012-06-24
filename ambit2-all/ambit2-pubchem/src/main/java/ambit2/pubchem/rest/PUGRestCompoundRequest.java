package ambit2.pubchem.rest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.RawIteratingSDFReader;

public class PUGRestCompoundRequest extends PUGRestRequest<List<IStructureRecord>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7930190885253688770L;

	public PUGRestCompoundRequest(COMPOUND_DOMAIN_INPUT input) {
		this(input,COMPOUND_DOMAIN_OUTPUT.SDF);
	}
	public PUGRestCompoundRequest(COMPOUND_DOMAIN_INPUT input,COMPOUND_DOMAIN_OUTPUT output) {
		super();
		setInput(input);
		setOutput(output);
		setOperation(COMPOUND_DOMAIN_OPERATION.record);
	}
	
	@Override
	protected List<IStructureRecord> read(InputStream in) throws Exception {
		switch (output) {
		case SDF: {
			List<IStructureRecord> records = new ArrayList<IStructureRecord>();
			RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
			while (reader.hasNext()) {
				IStructureRecord record = reader.nextRecord();
				records.add((IStructureRecord)record.clone());
			}
			return records;
		}
		default: 
			throw new Exception(String.format("Unsupported format %s", output));
		}
		
	}



}
