package ambit2.rest.admin.fingerprints;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.db.update.fp.IFingerprint;
import ambit2.descriptors.processors.BitSetGenerator.FPTable;
import ambit2.rest.admin.AdminResource;

public class FingerprintURIReporter<Q extends IQueryRetrieval<IFingerprint<FPTable,String>>> extends QueryURIReporter<IFingerprint<FPTable,String>, Q>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6959244677366115189L;

	public FingerprintURIReporter(Reference baseRef) {
		super(baseRef);
	}
	public FingerprintURIReporter(Request request) {
		super(request);
	}


	@Override
	public String getURI(String ref, IFingerprint<FPTable, String> item) {
		return String.format("%s/%s%s/%s%s?search=%s",
				ref,AdminResource.resource,FingerprintResource.resource,item.getType().name(),
				StructuresByFingerprintResource.resource,
				item.getBits()
				);
	}

}
