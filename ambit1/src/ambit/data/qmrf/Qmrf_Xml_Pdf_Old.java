package ambit.data.qmrf;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementTags;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.xml.TagMap;
import com.lowagie.text.xml.XmlPeer;
/**
 * Example that takes an XML file, converts it to PDF and adds all kinds of
 * extra's, such as an alternating header, a footer with page x of y, a page
 * with metadata,...
 */
public class Qmrf_Xml_Pdf_Old extends QMRFConverter {

	public String BaseFont_Url = "C:/WINDOWS/Fonts/times.ttf" ;



	/**
	 * Gets a PageEvents object.
	 * @return a new PageEvents object
	 */
	public MyPageEvents getPageEvents() {
		return new MyPageEvents();
	}
	/**
	 * Gets a Handler object.
	 * @param document  the document on which the handler operates
	 * @return a Handler object
	 */
	public MyHandler getXmlHandler(Document document, InputSource originalXML) {
		try {
			InputStream in = Qmrf_Xml_Pdf.class.getClassLoader().getResourceAsStream("ambit/data/qmrf/tagmapqmrf.xml");
			return new MyHandler(document, new QmrfMap(in,originalXML ));
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (DocumentException de) {
		}
		return null;
	}


	/**
	 * Converts a play in XML into PDF.
	 * @param args no arguments needed
	 */
	public static void main(String[] args) {

		System.out.println("QMRF");
		/*
		 * // we create a writer that listens to the document
// and directs a XML-stream to a file
PdfWriter.getInstance(documentA, new FileOutputStream("Chap0704a.pdf"));
PdfWriter.getInstance(documentB, new FileOutputStream("Chap0704b.pdf"));

// step 3: we parse the document
XmlParser.parse(documentA, "Chap0701.xml");
XmlParser.parse(documentB, "Chap0703.xml", "tagmap0703.xml");
		 */

		Qmrf_Xml_Pdf_Old x = new Qmrf_Xml_Pdf_Old();
		try {
			//this will not work , because of the second stream reading while the first is closing....
			//x.xml2pdf(new FileReader("myqmrf.xml"),new FileOutputStream("Qmrf.pdf"));
			x.xml2pdf("myqmrf.xml",new FileOutputStream("Qmrf.pdf"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void xml2pdf(Reader xml, OutputStream pdf) {
		// step 1: creation of a document-object
		Document document = new Document(PageSize.A4, 80, 50, 30, 65);

		try {
			// step 2:
			// we create a writer that listens to the document
			// and directs a XML-stream to a file
			PdfWriter writer = PdfWriter.getInstance(document,pdf);

			// create add the event handler
			MyPageEvents events = getPageEvents();
			writer.setPageEvent(events);
			events.setOriginalXML(new InputSource(xml));

			// step 3: we create a parser and set the document handler
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

			//this will not work , because of the second stream ....

			// step 4: we parse the document
			parser.parse(Qmrf_Xml_Pdf.class.getClassLoader().getResourceAsStream("ambit/data/qmrf/qmrf_pdf_template.xml"),
					getXmlHandler(document, new InputSource(xml)));

			document.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}


	public void xml2pdf(String xml, OutputStream pdf) {
		// step 1: creation of a document-object
		Document document = new Document(PageSize.A4, 80, 50, 30, 65);

		try {
			// step 2:
			// we create a writer that listens to the document
			// and directs a XML-stream to a file
			PdfWriter writer = PdfWriter.getInstance(document,pdf);

			/*
			// create add the event handler
			MyPageEvents events = getPageEvents();
			writer.setPageEvent(events);
			events.setOriginalXML(new InputSource(new StringReader(xml)));
			*/

			// step 3: we create a parser and set the document handler
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();



			// step 4: we parse the document
			parser.parse(Qmrf_Xml_Pdf.class.getClassLoader().getResourceAsStream("ambit/data/qmrf/qmrf_pdf_template.xml"),
					getXmlHandler(document, new InputSource(new StringReader(xml))));

			document.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}


	class MyPageEvents extends PdfPageEventHelper {

		protected InputSource originalXML = null;

		/** This is the contentbyte object of the writer */
		PdfContentByte cb;

		/** we will put the final number of pages in a template */
		PdfTemplate template;

		/** this is the BaseFont we are going to use for the header / footer */
		BaseFont bf = null;




		/**
		 * The first thing to do when the document is opened, is to define the BaseFont,
		 * get the Direct Content object and create the template that will hold the final
		 * number of pages.
		 * @see com.lowagie.text.pdf.PdfPageEventHelper#onOpenDocument(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
		 */
		public void onOpenDocument(PdfWriter writer, Document document) {
			try {
				 //bf = BaseFont.createFont(BaseFont_Url, BaseFont.WINANSI,	true);
				//bf =  BaseFont.createFont("c:\\windows\\fonts\\ARIALUNI.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252,
						BaseFont.NOT_EMBEDDED);
				cb = writer.getDirectContent();
				template = cb.createTemplate(50, 50);

					Image png = Image.getInstance(Qmrf_Xml_Pdf.class.getClassLoader().getResource("ambit/data/qmrf/logo.png"));
					//png.scaleAbsolute(76, 67);
					//png.setAlignment(Image.MIDDLE);

					PdfPCell cell;

					float[] widths = {1f,5f,1f};
					float[] widths1 = {1f};
					PdfPTable table = new PdfPTable(widths);
					PdfPTable table1 = new PdfPTable(widths1);

					table.setWidthPercentage(100);
					//table.addCell(new PdfPCell(png, true));
					png.scalePercent(60);
			        cell = new PdfPCell();
			        cell.setMinimumHeight(70);
		            cell.addElement(new Chunk(png,+14,-40));
					table.addCell(cell);

					String Text ="";
					try{
						DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
						DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                        docBuilder.setErrorHandler( new SimpleErrorHandler(getClass().getName()) );


						QMRFSchemaResolver resolver = new QMRFSchemaResolver("http://ambit.acad.bg/qmrf/qmrf.dtd",null);
						resolver.setIgnoreSystemID(true);
						docBuilder.setEntityResolver(resolver);

						org.w3c.dom.Document doc = docBuilder.parse (originalXML);



						Text = findNodeValue(xml_QMRF_number, doc);
						Chunk ident_title = new Chunk("QMRF identifier (JRC Inventory):");
						Chunk ident_text = new Chunk(Text);
						ident_title.setFont(new Font(Font.TIMES_ROMAN, 12,Font.BOLDITALIC));
						ident_text.setFont(new Font(Font.TIMES_ROMAN, 12,Font.ITALIC));
						Paragraph p = new Paragraph();
						p.add(ident_title);
						p.add(ident_text);
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						table1.addCell(cell);

						Text = findNodeValue("QSAR_title", doc);
						ident_title = new Chunk("QMRF Title:");
						ident_text = new Chunk(Text);
						ident_title.setFont(new Font(Font.TIMES_ROMAN, 12,Font.BOLDITALIC));
						ident_text.setFont(new Font(Font.TIMES_ROMAN, 12,Font.ITALIC));
						p = new Paragraph();
						p.add(ident_title);
						p.add(ident_text);
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						table1.addCell(cell);


						java.util.Date now = new java.util.Date();
						java.text.DateFormat df = java.text.DateFormat.getDateInstance();
					    String date = df.format(now);
						ident_title = new Chunk("Printing Date:");
						ident_text = new Chunk(date);
						ident_title.setFont(new Font(Font.TIMES_ROMAN, 12,Font.BOLDITALIC));
						ident_text.setFont(new Font(Font.TIMES_ROMAN, 12,Font.ITALIC));
						p = new Paragraph();
						p.add(ident_title);
						p.add(ident_text);

						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						table1.addCell(cell);
						p = new Paragraph("");
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						table1.addCell(cell);
						cell = new PdfPCell(table1);
						cell.setPadding(0f);
						table.addCell(cell);
				}catch (SAXParseException err) {
					        System.out.println ("** Parsing error" + ", line "
					             + err.getLineNumber () + ", uri " + err.getSystemId ());
					        System.out.println(" " + err.getMessage ());

		        }catch (SAXException e) {
					        Exception x = e.getException ();
					        ((x == null) ? e : x).printStackTrace ();

		        }catch (Throwable t) {
					        t.printStackTrace ();
		        }



						//table.addCell(new PdfPCell(png, true));
		        cell.addElement(new Chunk(png,+14,-40));
						table.addCell(cell);
						document.add(table);
						document.add(new Paragraph(16));



			} catch (DocumentException de) {
			} catch (IOException ioe) {
			}
		}


		public void onChapter(PdfWriter writer, Document document,
				float paragraphPosition, Paragraph title) {
			/*
			StringBuffer buf = new StringBuffer();
			for (Iterator i = title.getChunks().iterator(); i.hasNext();) {
				Chunk chunk = (Chunk) i.next();
				buf.append(chunk.content());
			}
			*/

		}


		public void onEndPage(PdfWriter writer, Document document) {
			/*int pageN = writer.getPageNumber();
			String text = "Page " + pageN + " of ";
			cb.beginText();
			cb.setFontAndSize(bf, 8);
			cb.setTextMatrix(280, 30);
			cb.showText(text);
			cb.endText();*/


		}

		/**
		 * Just before the document is closed, we add the final number of pages to
		 * the template.
		 * @see com.lowagie.text.pdf.PdfPageEventHelper#onCloseDocument(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
		 */
		public void onCloseDocument(PdfWriter writer, Document document) {
			template.beginText();
			template.setFontAndSize(bf, 8);
			template.showText(String.valueOf(writer.getPageNumber() - 1));
			template.endText();

		}


		public InputSource getOriginalXML() {
			return originalXML;
		}


		public void setOriginalXML(InputSource originalXML) {
			this.originalXML = originalXML;
		}


	}

	class QmrfMap extends TagMap {

		private static final long serialVersionUID = 1024517625414654121L;

		/**
		 * Constructs a TagMap based on an XML file
		 * and/or on XmlPeer objects that are added.
		 * @throws IOException
		 */
		public QmrfMap(InputStream in, InputSource originalXML) throws IOException {
			super(in);
			try {
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                docBuilder.setErrorHandler( new SimpleErrorHandler(getClass().getName()) );
				QMRFSchemaResolver resolver = new QMRFSchemaResolver("http://ambit.acad.bg/qmrf/qmrf.dtd",null);
				resolver.setIgnoreSystemID(true);
				docBuilder.setEntityResolver(resolver);


				org.w3c.dom.Document doc = docBuilder.parse (originalXML );

				XmlPeer peer = createXMLPeer(doc,"QSAR_identifier");

				peer = createXMLPeer(doc,"QSAR_General_information");
				peer = createXMLPeer(doc,"QSAR_Endpoint");
				peer = createXMLPeer(doc,"QSAR_Algorithm");
				peer = createXMLPeer(doc,"QSAR_Applicability_domain");

				peer = createXMLPeer(doc,"QSAR_Robustness");
				peer = createXMLPeer(doc,"QSAR_Predictivity");
				peer = createXMLPeer(doc,"QSAR_Interpretation");
				peer = createXMLPeer(doc,"QSAR_Miscelaneous");
				peer = createXMLPeer(doc,"QMRF_Summary");

				peer = createDateXMLPeer(doc,"PRINTING_DATE");


				for (int i=0; i < subchapters.length;i++) {
					if (subchapters[i][2] == Mode.reference)
						peer = createSubchapterXMLPeer(doc,
								subchapters[i][0].toString(),
								subchapters[i][1].toString(),
								(Mode)subchapters[i][2],
								subchapters[i][3].toString(),
								subchapters[i][4].toString(),
								(String[])subchapters[i][5],
								(Boolean)subchapters[i][6]
								                     );
					else

						peer = createSubchapterXMLPeer(doc,
								subchapters[i][0].toString(),
								subchapters[i][1].toString(),
								(Mode)subchapters[i][2]);
				};

				String value;
				//QMRF INFO
				/*
				peer = new XmlPeer(ElementTags.CHUNK, "QMRF_info_number");
				value = space+findNodeValue(xml_QMRF_number, doc);
				peer.setContent(value);
				put(peer.getAlias(), peer);

				peer = new XmlPeer(ElementTags.CHUNK, "QMRF_info_title");
				value = space+findNodeValue(xml_QSAR_title, doc);
				peer.setContent(value);
				put(peer.getAlias(), peer);
	*/
				/*
				peer = new XmlPeer(ElementTags.CHUNK, "EMAIL");
				value = space+findAttributeValue("QMRF","email", doc);
				peer.setContent(value);
				put(peer.getAlias(), peer);

				peer = new XmlPeer(ElementTags.CHUNK, "CONTACT");
				value = space+findAttributeValue("QMRF","contact", doc);
				peer.setContent(value);
				put(peer.getAlias(), peer);

				peer = new XmlPeer(ElementTags.CHUNK, "DATE");
				value = space+findAttributeValue("QMRF","date", doc);
				peer.setContent(value);
				put(peer.getAlias(), peer);

				peer = new XmlPeer(ElementTags.CHUNK, "AUTHOR");
				value = space+findAttributeValue("QMRF","author", doc);
				peer.setContent(value);
				put(peer.getAlias(), peer);

				peer = new XmlPeer(ElementTags.CHUNK, "URL");
				value = space+findAttributeValue("QMRF","url", doc);
				peer.setContent(value);
				put(peer.getAlias(), peer);
*/
				/*peer = new XmlPeer(ElementTags.CELL, "QMRF_chapter_items_cell");
				peer.addValue(ElementTags.HORIZONTALALIGN,"Middle");
				peer.addValue(ElementTags.COLSPAN,"2");
				put(peer.getAlias(), peer);
				*/




			}catch (SAXParseException err) {
		        System.out.println ("** Parsing error" + ", line "
		             + err.getLineNumber () + ", uri " + err.getSystemId ());
		        System.out.println(" " + err.getMessage ());

	        }catch (SAXException e) {
			        Exception x = e.getException ();
			        ((x == null) ? e : x).printStackTrace ();

	        }catch (Throwable t) {
	        	t.printStackTrace ();
	        }

		}

		protected XmlPeer createSubchapterXMLPeer(org.w3c.dom.Document doc, String xml_tag) {
			return createSubchapterXMLPeer(doc, xml_tag, xml_tag,Mode.title);
		}

		protected XmlPeer createSubchapterXMLPeer(org.w3c.dom.Document doc, String xml_tag, String pdf_tag, Mode mode,
				String ref_name, String catalog_name, String[] attributes, boolean line) throws Exception {
			XmlPeer peer = new XmlPeer(ElementTags.CHUNK,pdf_tag);
			peer.addValue(ElementTags.SIZE, "12");
			peer.addValue(ElementTags.FONT,BaseFont_Url);
			String value = listNodeAttributes(doc,xml_tag, ref_name, catalog_name, attributes, line);
			peer.setContent(value);
			put(peer.getAlias(), peer);
			return peer;
		}
		protected XmlPeer createSubchapterXMLPeer(org.w3c.dom.Document doc, String xml_tag, String pdf_tag, Mode mode) {
			String value;
			switch (mode) {

				case title: {
					XmlPeer peer = new XmlPeer(ElementTags.CHUNK,pdf_tag);
					peer.addValue(ElementTags.SIZE, "12");
					peer.addValue(ElementTags.FONT,BaseFont_Url);
					String sub_chapter_number = findAttributeValue(xml_tag,xml_attribute_chapter, doc);
					if (sub_chapter_number == null) return null;
					String sub_chapter_name = findAttributeValue(xml_tag,xml_attribute_name, doc);
					value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
					peer.addValue(ElementTags.STYLE," font-weight: bold");
					peer.setContent(value);
					put(peer.getAlias(), peer);
					return peer;
				}
				case text: {
					XmlPeer peer = new XmlPeer(ElementTags.PHRASE,pdf_tag);
					peer.addValue(ElementTags.SIZE, "12");
					peer.addValue(ElementTags.FONT,BaseFont_Url);

					XmlPeer chunk = new XmlPeer(ElementTags.CHUNK,"b");

					value = findNodeValue(xml_tag, doc);
					chunk.setContent(value);
					put(chunk.getAlias(), chunk);

					put(peer.getAlias(), peer);
					return peer;
				}
				case answer: {
					XmlPeer peer = new XmlPeer(ElementTags.CHUNK,pdf_tag);
					peer.addValue(ElementTags.SIZE, "12");
					peer.addValue(ElementTags.FONT,BaseFont_Url);
					peer = new XmlPeer(ElementTags.CHUNK,pdf_tag);
					peer.addValue(ElementTags.SIZE, "12");
					peer.addValue(ElementTags.FONT,BaseFont_Url);
					value = findAnswer(xml_tag,doc);
					peer.setContent(value);
					put(peer.getAlias(), peer);
					return peer;
				}
				case dataset: {
					XmlPeer peer = new XmlPeer(ElementTags.CHUNK,pdf_tag);
					peer.addValue(ElementTags.SIZE, "12");
					peer.addValue(ElementTags.FONT,BaseFont_Url);
					value = '\n' + findDataAvailable(xml_tag,doc);
					peer.setContent(value);
					put(peer.getAlias(), peer);
					return peer;
				}
				case attachments: {
					XmlPeer peer = new XmlPeer(ElementTags.CHUNK,pdf_tag);
					peer.addValue(ElementTags.SIZE, "12");
					peer.addValue(ElementTags.FONT,BaseFont_Url);
				    StringBuffer b = new StringBuffer();
				    b.append("\nTraining set(s)\n");
				    b.append(listAttachments(doc,"attachment_training_data"));
				    b.append("Test set(s)\n");
				    b.append(listAttachments(doc,"attachment_validation_data"));
				    b.append("Supporting information\n");
				    b.append(listAttachments(doc,"attachment_documents"));
				    value = b.toString();
					peer.setContent(value);
					put(peer.getAlias(), peer);
				    return peer;
				}
				default: {
					return null;
				}
			}

		}
		protected XmlPeer createXMLPeer(org.w3c.dom.Document doc, String xml_tag) {
			return createXMLPeer(doc, xml_tag, xml_tag);
		}

		protected XmlPeer createXMLPeer(org.w3c.dom.Document doc, String xml_tag, String pdf_tag) {
			XmlPeer peer = new XmlPeer(ElementTags.CHUNK, pdf_tag);
			String chapter = findAttributeValue(xml_tag,xml_attribute_chapter, doc);
			String name = findAttributeValue(xml_tag,xml_attribute_name, doc);
			String value = chapter+dot+ name;
			peer.addValue(ElementTags.SIZE, "12");
			peer.addValue(ElementTags.STYLE,"font-weight: bold");
			peer.addValue(ElementTags.FONT,BaseFont_Url);
			peer.setContent(value);
			put(peer.getAlias(), peer);

			return peer;
		}

		protected XmlPeer createDateXMLPeer(org.w3c.dom.Document doc,  String pdf_tag) {
			XmlPeer peer = new XmlPeer(ElementTags.CHUNK, pdf_tag);
			java.util.Date now = new java.util.Date();
			java.text.DateFormat df = java.text.DateFormat.getDateInstance();
		    String date = df.format(now);
			peer.addValue(ElementTags.SIZE, "12");
		    peer.addValue(ElementTags.FONT,BaseFont_Url);
			peer.setContent(df.format(now));
			put(peer.getAlias(), peer);

			return peer;
		}

	}
	/*
	public static String findNodeValue(String name_node, org.w3c.dom.Document source) {

		source.getDocumentElement();
		NodeList objCatNodes = source.getElementsByTagName(name_node);
		if(objCatNodes.getLength() == 0) return "";
		Node objNode=objCatNodes.item(0);
		NodeList objNodes = objNode.getChildNodes();
		if(objNodes.getLength() == 0) return "";

		return org.apache.commons.lang.StringEscapeUtils.unescapeXml(replaceTags(objNodes.item(0).getNodeValue()).trim());
	}

	public static String findAttributeValue(String name_attribute, Node source) {

		NamedNodeMap objAttributes =  source.getAttributes();
		Node attribute = objAttributes.getNamedItem(name_attribute);
		return org.apache.commons.lang.StringEscapeUtils.unescapeXml(replaceTags(attribute.getNodeValue()).trim());
	}

	public static ArrayList findNodeIDRef(String name_node,String ref_name, String catalog_name, org.w3c.dom.Document source) {

		source.getDocumentElement();
		ArrayList return_list = new ArrayList();
		NodeList objCatNodes = source.getElementsByTagName(name_node);

		Node objNode=objCatNodes.item(0);
		NodeList objNodes = objNode.getChildNodes();
		for(int i = 0; i < objNodes.getLength();i=i+1){

			if(objNodes.item(i).getNodeName().equals(ref_name)){
				NamedNodeMap objAttributes =  objNodes.item(i).getAttributes();
				Node attribute = objAttributes.getNamedItem("idref");
				//attribute.getNodeValue()
				NodeList objNodesAuthor = source.getElementsByTagName(catalog_name);
				for(int j = 0; j < objNodesAuthor.getLength();j=j+1){
					String id = objNodesAuthor.item(j).getAttributes().getNamedItem("id").getNodeValue();
					if(id.equals(attribute.getNodeValue())){
						//Node name = objNodesAuthor.item(j).getAttributes().getNamedItem("name");
						return_list.add(objNodesAuthor.item(j));
					}


				}

			}

		}
		return return_list;
	}



	*/




}