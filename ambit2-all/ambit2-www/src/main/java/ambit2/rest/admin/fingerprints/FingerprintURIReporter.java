package ambit2.rest.admin.fingerprints;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.fp.IFingerprint;
import ambit2.descriptors.processors.BitSetGenerator.FPTable;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.admin.AdminResource;

public class FingerprintURIReporter<Q extends IQueryRetrieval<IFingerprint<FPTable,String>>> extends QueryURIReporter<IFingerprint<FPTable,String>, Q>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6959244677366115189L;

	public FingerprintURIReporter(Reference baseRef,ResourceDoc doc) {
		super(baseRef,doc);
	}
	public FingerprintURIReporter(Request request,ResourceDoc doc) {
		super(request,doc);
	}


	@Override
	public String getURI(String ref, IFingerprint<FPTable, String> item) {
		return String.format("%s/%s%s/%s%s?search=%s%s",
				ref,AdminResource.resource,FingerprintResource.resource,item.getType().name(),
				StructuresByFingerprintResource.resource,
				item.getBits(),
				getDelimiter());
	}

}
