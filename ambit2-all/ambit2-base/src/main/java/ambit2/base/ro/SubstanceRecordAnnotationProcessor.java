package ambit2.base.ro;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import net.idea.modbcum.p.DefaultAmbitProcessor;

public class SubstanceRecordAnnotationProcessor extends DefaultAmbitProcessor<SubstanceRecord, SubstanceRecord> {
	protected Properties endpoints_lookup;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5447660472718234256L;

	public SubstanceRecordAnnotationProcessor(File lookupfolder) {
		super();
		try {
			endpoints_lookup = new Properties();
			endpoints_lookup.load(new FileInputStream(new File(lookupfolder, "endpoint.properties")));
		} catch (Exception x) {
			endpoints_lookup=null;
			logger.log(Level.WARNING, lookupfolder.getAbsolutePath(), x);
		}
	}

	@Override
	public SubstanceRecord process(SubstanceRecord record) throws Exception {
		for (ProtocolApplication<Protocol, IParams, String, IParams, String> papp : record.getMeasurements()) {
			for (EffectRecord<String, IParams, String> effect : papp.getEffects())
				try {
					if (endpoints_lookup!=null)
						process(effect);
				} catch (Exception x) {

				}
		}
		return record;
	}

	protected void process(EffectRecord<String, IParams, String> effect) throws Exception {
		String[] terms = endpoints_lookup.getProperty(effect.getEndpoint().toUpperCase().replaceAll(" ", "_"))
				.split(";");
		
		for (String term : terms) {
			effect.addEndpointSynonym(term);
		}

	}
}
