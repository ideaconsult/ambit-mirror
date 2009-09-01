package org.ideaconsult.iuclid5client.test;

import junit.framework.TestCase;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.ClientServiceException;
import org.ideaconsult.iuclidws.documentaccess.DocumentAccessEngine;
import org.ideaconsult.iuclidws.session.SessionEngineFault;
import org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault;
import org.ideaconsult.iuclidws.types.Types.Document;
import org.ideaconsult.iuclidws.types.Types.DocumentReferencePK;
import org.ideaconsult.iuclidws.types.Types.DocumentType;
import org.ideaconsult.iuclidws.types.Types.DocumentTypeType;

/**
 * The <code>DocumentAccessEngineTest</code> is used to demonstrate and test the
 * DocumentAccessEngine Web Service
 * 
 */
public class DocumentAccessEngineTest extends TestCase {

	private final static Logger LOGGER = Logger.getLogger(DocumentAccessEngineTest.class);

	private static DocumentReferencePK documentReferencePK;
	private static Document document;
	private static DocumentAccessEngine documentAccessEngine;

	public DocumentAccessEngineTest() throws AxisFault, SessionEngineNotAvailableFault, SessionEngineFault,
			ClientServiceException {
		super(DocumentAccessEngineTest.class.getSimpleName());
		documentAccessEngine = new DocumentAccessEngine();
	}

	/**
	 * Test case to demonstrate how you could create document
	 * 
	 * @throws Exception
	 */
	public void testCreateDocument() throws Exception {
		LOGGER.info("test create document - start");
		try {
			DocumentType documentType = new DocumentType();
			DocumentTypeType documentTypeType = new DocumentTypeType("LegalEntity", true);
			documentType.setType(documentTypeType);
			document = documentAccessEngine.createDocument(documentType);

			assertNotNull(document);
			documentReferencePK = new DocumentReferencePK();
			documentReferencePK.setUniqueKey(document.getDocumentReference().getUniqueKey());
			StringBuilder sb = new StringBuilder("Created document reference: \n");

			sb.append("   document type: ");
			sb.append(document.getDocumentReference().getDocType().getValue());
			sb.append("\n");
			sb.append("   document unique key: ");
			sb.append(document.getDocumentReference().getUniqueKey().toString());
			sb.append("\n");
			sb.append("   last modification date: ");
			sb.append(document.getDocumentReference().getLastModificationDate().getTime().toString());
			sb.append("\n");
			LOGGER.info(sb);
		} finally {
			LOGGER.info("test create document - end");
		}
	}

	/**
	 * Test case to demonstrate how you could commit document
	 * 
	 * @throws Exception
	 */
	public void testCommitDocument() throws Exception {
		LOGGER.info("test commit document - start");
		try {
			assertNotNull(document);
			assertTrue(documentAccessEngine.commitDocument(document));
		} finally {
			LOGGER.info("test commit document - end");
		}
	}

	/**
	 * Test case to demonstrate how you could check document exist
	 * 
	 * @throws Exception
	 */
	public void testExistDocument() throws Exception {
		LOGGER.info("test exist document - start");
		try {
			assertNotNull(documentReferencePK);
			assertTrue(documentAccessEngine.existsDocument(documentReferencePK));
		} finally {
			LOGGER.info("test exist document - end");
		}
	}

	/**
	 * Test case to demonstrate how you could delete document
	 * 
	 * @throws Exception
	 */
	public void testDeleteDocument() throws Exception {
		LOGGER.info("test delete document - start");
		try {
			assertNotNull(documentReferencePK);
			assertTrue(documentAccessEngine.deleteDocument(documentReferencePK));
		} finally {
			LOGGER.info("test delete document - end");
		}
	}

	/**
	 * Test case to demonstrate how you could check document not exist
	 * 
	 * @throws Exception
	 */
	public void testNotExistDocument() throws Exception {
		LOGGER.info("test not exist document - start");
		try {
			assertNotNull(documentReferencePK);
			assertFalse(documentAccessEngine.existsDocument(documentReferencePK));
		} finally {
			LOGGER.info("test not exist document - end");
		}
	}
}
