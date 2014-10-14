package ambit2.i5rest.convertors;

import java.io.IOException;
import java.io.Writer;
import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngine;
import org.ideaconsult.iuclidws.documentaccess.DocumentAccessFault;
import org.ideaconsult.iuclidws.documentaccess.DocumentNotFoundFault;
import org.ideaconsult.iuclidws.session.SessionEngineFault;
import org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault;
import org.ideaconsult.iuclidws.types.Types.Document;
import org.ideaconsult.iuclidws.types.Types.DocumentReferencePK;
import org.ideaconsult.iuclidws.types.Types.Session;
import org.restlet.Request;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;

public class I5DocumentReporter  extends I5Reporter<DocumentReferencePK> {
	
	public I5DocumentReporter(Request request,Session session) {
		super(request,session);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 5144043712331173973L;
		
	public Writer process(DocumentReferencePK param)
			throws AmbitException {
		try {
				login();
				DocumentAccessEngine documentAccessEngine = new DocumentAccessEngine(getSession(),getTarget());
				Document doc = documentAccessEngine.fetchDocument(param);
				if (doc != null)
					output.write(doc.getContent());
				else throw new NotFoundException();

			} catch (DocumentAccessFault x) {
				throw new AmbitException(x);
			} catch (DocumentNotFoundFault x) {
				throw new AmbitException(x);

			} catch (SessionEngineFault x) {
				throw new AmbitException(x);
			} catch (SessionEngineNotAvailableFault x) {
				throw new AmbitException(x);
			} catch (AxisFault x) {
				throw new AmbitException(x);
			} catch (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineFault x) {
				throw new AmbitException(x);
			} catch (org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngineNotAvailableFault x) {
				throw new AmbitException(x);				
			} catch (RemoteException x) {	
				throw new AmbitException(x);
			} catch (IOException x) {
				throw new AmbitException(x);				
			} finally {
				LOGGER.info("test looking for all query definition - end\n");
				try { 
					logout(); 
				} catch (Exception x) {
					LOGGER.info(x);
				}
				
			}
			
			return output;
	}

	public void setEnabled(boolean value) {
		
	}


}
