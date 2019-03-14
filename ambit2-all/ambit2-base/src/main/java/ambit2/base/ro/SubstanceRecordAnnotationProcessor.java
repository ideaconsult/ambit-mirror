package ambit2.base.ro;

import java.io.File;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;

public class SubstanceRecordAnnotationProcessor extends AbstractAnnotator<SubstanceRecord> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5447660472718234256L;

	public SubstanceRecordAnnotationProcessor(File lookupfolder, boolean fulllinks) {
		super(lookupfolder,fulllinks);
	}

	@Override
	public SubstanceRecord process(SubstanceRecord record) throws Exception {
		
		for (ProtocolApplication<Protocol, IParams, String, IParams, String> papp : record.getMeasurements()) {
			annotate(papp);
			for (EffectRecord<String, IParams, String> effect : papp.getEffects())
				try {
					if (endpoints_lookup!=null)
						annotate(effect);
				} catch (Exception x) {

				}
		}
		return record;
	}
	

}
