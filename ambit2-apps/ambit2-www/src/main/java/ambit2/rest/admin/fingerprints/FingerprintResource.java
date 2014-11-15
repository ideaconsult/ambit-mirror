package ambit2.rest.admin.fingerprints;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.db.update.fp.Fingerprint;
import ambit2.db.update.fp.IFingerprint;
import ambit2.db.update.fp.QueryFingerprints;
import ambit2.db.update.fp.QueryFingerprints._order;
import ambit2.descriptors.processors.BitSetGenerator.FPTable;
import ambit2.rest.query.QueryResource;

public class FingerprintResource  extends QueryResource<QueryFingerprints,IFingerprint<FPTable,String>>  {

	public final static String resource = "/fingerprint";	
	public final static String resourceKey = "fptype";
	
	protected void doInit() throws ResourceException {
		super.doInit();
		getVariants().clear();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				MediaType.TEXT_URI_LIST,
				MediaType.TEXT_CSV,
				MediaType.APPLICATION_JAVA_OBJECT,
				
		});		
	}	
	
	@Override
	public IProcessor<QueryFingerprints, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
	    if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			FingerprintURIReporter reporter = 	new FingerprintURIReporter (getRequest(),getDocumentation());
			return new OutputWriterConvertor(reporter,MediaType.TEXT_URI_LIST);
	    } else if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
	    	return new OutputWriterConvertor(new FingerprintsCSVReporter(getRequest(),getDocumentation()),MediaType.TEXT_CSV);
	    } else {
	    	return new OutputWriterConvertor(new FingerprintsHTMLReporter(getRequest(),getDocumentation()),MediaType.TEXT_HTML);
	    }
	}
	@Override
	protected QueryFingerprints createQuery(Context context, Request request, Response response)
			throws ResourceException {
		QueryFingerprints q = new QueryFingerprints();
		IFingerprint<FPTable,String> fp = new Fingerprint<FPTable, String>();
		Object fptype = request.getAttributes().get(resourceKey);
		
		try {
			if (fptype==null) fp.setType(FPTable.fp1024);
			else fp.setType(FPTable.valueOf(fptype.toString()));
		} catch (Exception x) {
			fp.setType(FPTable.fp1024);
		}
		q.setValue(fp);
		try {
			Object order = getResourceRef(request).getQueryAsForm().getFirstValue("order");
			q.setOrder(order==null?_order.frequency:_order.valueOf(order.toString()));
		} catch (Exception x) {}
		return  q;
	}	
	
}
