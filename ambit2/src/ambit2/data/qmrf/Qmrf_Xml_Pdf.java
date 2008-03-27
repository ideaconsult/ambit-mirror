/*
Copyright (C) 2005-2007

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.data.qmrf;

import java.awt.Color;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.HyphenationAuto;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class Qmrf_Xml_Pdf extends QMRFConverter {

	protected static Color chapterColor = new Color(230,230,230);

    protected DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    protected DocumentBuilder docBuilder;
    protected DocumentBuilder nodeBuilder;
    protected Font font;
    protected Font bfont;
    protected BaseFont baseFont;
    protected String ttffont;
    
    public Qmrf_Xml_Pdf(String ttffont) {
        super();
        try {
        	this.ttffont = ttffont;
            docBuilder = docBuilderFactory.newDocumentBuilder();
            nodeBuilder = docBuilderFactory.newDocumentBuilder();
            nodeBuilder.setErrorHandler(new ErrorHandler() {
            	public void error(SAXParseException arg0) throws SAXException {
            		throw new SAXException(arg0);
            	}
            	public void fatalError(SAXParseException arg0) throws SAXException {
            		throw new SAXException(arg0);
            	}
            	public void warning(SAXParseException arg0) throws SAXException {
            	}
            });
            //PRIndirectReference pri;
            //pri.
            try {
            	
            	baseFont = BaseFont.createFont(ttffont,
            	//"c:\\windows\\fonts\\times.ttf",
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            	System.out.println(ttffont);
            } catch (Exception x) {
            	x.printStackTrace();
            	baseFont = BaseFont.createFont("c:\\windows\\fonts\\times.ttf",
                            BaseFont.IDENTITY_H, BaseFont.EMBEDDED);            	
            	System.out.println("Default font c:\\windows\\fonts\\times.ttf");
            }
            
            font = new Font(baseFont, 12);
            bfont = new Font(baseFont, 12, Font.BOLD);
        } catch (Exception x) {
            docBuilder = null;
        }

    }
    public void xml2pdf(String xml, OutputStream pdf) {
    	xml2pdf(new InputSource(new StringReader(xml)), pdf);
    }
	public void xml2pdf(InputSource xml, OutputStream pdf) {

		try {
			Document document = new Document(PageSize.A4, 80, 50, 30, 65);

			PdfWriter writer = PdfWriter.getInstance(document,pdf);
			//writer.setViewerPreferences(PdfWriter.HideMenubar| PdfWriter.HideToolbar);
			writer.setViewerPreferences(PdfWriter.PageModeUseThumbs | PdfWriter.PageModeUseOutlines);
			//PdfOutline root = writer.getDirectContent().getRootOutline();
			//new PdfOutline(root, new PDFAction("http://nina.acad.bg/qmrf"), "a bookmark");
			
			//writer.addFileAttachment(arg0, arg1, arg2, arg3)

			
            if (docBuilder == null)
                docBuilder = docBuilderFactory.newDocumentBuilder();
            docBuilder.setErrorHandler( new SimpleErrorHandler(getClass().getName()) );
			QMRFSchemaResolver resolver = new QMRFSchemaResolver("http://ambit2.acad.bg/qmrf/qmrf.dtd",null);
			resolver.setIgnoreSystemID(true);
			docBuilder.setEntityResolver(resolver);
			
			org.w3c.dom.Document doc = null;
			try {
				doc = docBuilder.parse(xml);
			} catch (Exception x) {
				document.addCreationDate();
				document.addCreator(getClass().getName());
				document.open();
				document.add(new Paragraph(new Chunk(x.getMessage())));
				document.close();
				return;
			}

			document.addCreationDate();
			document.addCreator(getClass().getName());
			document.addKeywords(replaceTags(findNodeValue("keywords", doc)));
			document.addTitle(replaceTags(findNodeValue("QSAR_title", doc)));
			
			try {
			NodeList info = doc.getElementsByTagName("QMRF");
			for (int i=0; i < info.getLength();i++)  
				document.addSubject(findAttributeValue("name", info.item(i))
						+ '.' + 
						findAttributeValue("version", info.item(i)));
			} catch (Exception x) {
				document.addSubject("QMRF");
			}
			try {
			document.addAuthor(
					listNodeAttributes(doc,
							"qmrf_authors",
							"author_ref",
							"author",
							att_author,
							new Boolean(true)
							));
			} catch (Exception x) {
				document.addAuthor(getClass().getName());				
			}
			
			document.open();
			PdfContentByte cb = writer.getDirectContent();			
			
			try {
				headerTable(document,doc);
			} catch (Exception x) {
				document.add(new Paragraph(new Chunk(x.getMessage())));
				document.close();
				return;
			}
			
			PdfOutline root = writer.getDirectContent().getRootOutline();
			
			for (int i=0; i < subchapters.length;i++)


				try {
                    int align = Paragraph.ALIGN_LEFT;
					if  (Mode.chapter == (Mode)subchapters[i][2]) {
						document.add(new Paragraph(new Chunk('\n')));
						PdfPTable table = new PdfPTable(1);

						table.setWidthPercentage(100);

						StringBuffer b = new StringBuffer();
						b.append(findAttributeValue(subchapters[i][0].toString(), xml_attribute_chapter, doc));
						b.append('.');
						b.append(findAttributeValue(subchapters[i][0].toString(), xml_attribute_name, doc));
						
						String bookmark = b.toString(); 
						
						
						Chunk title = new Chunk(bookmark);
						title.setLocalDestination(bookmark);
						title.setFont(bfont);
						
						PdfDestination destination = new PdfDestination(PdfDestination.FITH);
					    PdfOutline outline = new PdfOutline(root, destination,bookmark);
						
						Paragraph p = new Paragraph(title);
						
						PdfPCell cell = new PdfPCell(p);
						cell.setBackgroundColor(chapterColor);
						table.addCell(cell);
						document.add(table);
						float pos = writer.getVerticalPosition(false);
						if (pos < 90) document.newPage();						
					} else {
						Phrase phrase = new Phrase();
						switch ((Mode)subchapters[i][2]) {
						case title: {
							StringBuffer b = new StringBuffer();
							String cn = findAttributeValue(subchapters[i][0].toString(), xml_attribute_chapter, doc);
							if (cn == null) break;
							
							b.append(findAttributeValue(subchapters[i][0].toString(), xml_attribute_chapter, doc));
							b.append('.');
							b.append(findAttributeValue(subchapters[i][0].toString(), xml_attribute_name, doc));
							
							String subchapterBookmark = b.toString();
							b.append(':');
							
							Chunk title = new Chunk(b.toString());
							title.setLocalDestination(subchapterBookmark);
							title.setFont(bfont);
							phrase.add(title);
							
							PdfDestination destination = new PdfDestination(PdfDestination.FITBH);
						    PdfOutline outline = new PdfOutline(root, destination,subchapterBookmark);
						    
							
							break;

						}
						case text: {
							createNodePhrase(subchapters[i][0].toString(),doc,phrase,font);
                            align = Paragraph.ALIGN_JUSTIFIED;
							break;
						}
						case answer: {
							String a = findAnswer(subchapters[i][0].toString(),doc);
							if (a != null) {
								Chunk answer = new Chunk(a);
								answer.setFont(font);
								phrase.add(answer);
							}
							break;
						}
						case dataset: {
							StringBuffer b = new StringBuffer();
							b.append(findDataAvailable(subchapters[i][0].toString(),doc));
							Chunk dataset = new Chunk(b.toString());
							dataset.setFont(font);
							phrase.add(dataset);
							break;
						}
						case attachments: {
							PdfPTable table = getAttachmentsAsTable(doc,"attachment_training_data");
							if (table != null) {
								phrase.add(new Paragraph("Training set(s)"));
								phrase.add(table);
							}
							table = getAttachmentsAsTable(doc,"attachment_validation_data");
							if (table != null) {
								phrase.add(new Paragraph("Test set(s)"));
								phrase.add(table);
							}
							table = getAttachmentsAsTable(doc,"attachment_documents");
							if (table != null) {
								phrase.add(new Paragraph("Supporting information"));
								phrase.add(table);
							}
							break;
							/*
							StringBuffer b = new StringBuffer();
							b.append("Training set(s)\n");
						    b.append(listAttachments(doc,"attachment_training_data"));
						    b.append("Test set(s)\n");
						    b.append(listAttachments(doc,"attachment_validation_data"));
						    b.append("Supporting information\n");
						    b.append(listAttachments(doc,"attachment_documents"));
							Chunk attachments = new Chunk(b.toString());
							attachments.setFont(font);
							phrase.add(attachments);
							
							break;
							*/
						}
						case reference: {
							try {
								String value = listNodeAttributes(doc,subchapters[i][0].toString(),
										subchapters[i][3].toString(),
										subchapters[i][4].toString(),
										(String[])subchapters[i][5],
										(Boolean)subchapters[i][6]
										);
	
								Chunk reference = new Chunk(value);
								reference.setFont(font);
	                            align = Paragraph.ALIGN_JUSTIFIED;
								phrase.add(reference);
							} catch (Exception x) {

							}
							break;
							

						}
						}
						
						Paragraph p = new Paragraph(phrase);
                        p.setAlignment(align);
						document.add(p);
						float pos = writer.getVerticalPosition(false);
						/*
						cb.moveTo(0, pos);
						cb.lineTo(PageSize.A4.width(), pos);
						cb.stroke();
						*/
						if (pos < 90) document.newPage();
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
			document.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}
	
	protected PdfPTable getAttachmentsAsTable(org.w3c.dom.Document doc, String attachments) {
		NodeList nodes = doc.getElementsByTagName(attachments);
		if (nodes.getLength() >  0) { 
		
			float[] widths = {3f,3f};
			PdfPTable table = new PdfPTable(widths);
			table.setWidthPercentage(100);
			for (int i =0; i < nodes.getLength(); i++)
				if (nodes.item(i) instanceof org.w3c.dom.Element) {
					NodeList attachment = nodes.item(i).getChildNodes();
					if (attachment.getLength() ==  0) ;//b.append("N/A\n");
					else
						for (int j =0; j < attachment.getLength(); j++) 
							if (attachment.item(j) instanceof org.w3c.dom.Element) {
								org.w3c.dom.Element e = ((org.w3c.dom.Element)attachment.item(j));
								
								Paragraph p = new Paragraph(org.apache.commons.lang.StringEscapeUtils.unescapeXml(e.getAttribute("description")));
								PdfPCell cell = new PdfPCell(p);
								cell.setBackgroundColor(Color.white);
								table.addCell(cell);
								
								String c = org.apache.commons.lang.StringEscapeUtils.unescapeXml(e.getAttribute("url"));
								
								PdfAction action = new PdfAction(c);
								p = new Paragraph();
								p.add(new Chunk(c).setAction(action));
								cell = new PdfPCell(p);
								cell.setBackgroundColor(Color.white);
								table.addCell(cell);								

							}
				}
			return table;	
		} else return null;
			
		
	}
	public void headerTable(Document pdfdoc, org.w3c.dom.Document xmldoc) {
		try {
			int header_font_size = 10;
			Image png_left = Image.getInstance(Qmrf_Xml_Pdf.class.getClassLoader().getResource("ambit/data/qmrf/logo.png"));
			Image png_right = Image.getInstance(Qmrf_Xml_Pdf.class.getClassLoader().getResource("ambit/data/qmrf/logo.png"));
			png_left.setAlignment(Image.LEFT);
			png_right.setAlignment(Image.RIGHT);
			png_left.scalePercent(60);
			png_right.scalePercent(60);
			//png.scaleAbsolute(76, 67);
			//png.setAlignment(Image.MIDDLE);

			PdfPCell cell;

			float[] widths = {1f,5f,1f};
			float[] widths1 = {1f};
			PdfPTable table = new PdfPTable(widths);
			PdfPTable table1 = new PdfPTable(widths1);

			table.setWidthPercentage(100);

	        cell = new PdfPCell();
	        cell.setMinimumHeight(70);
            cell.addElement(new Chunk(png_left,+14,-40));
			table.addCell(cell);

			String Text ="";
			try{

					Text = findNodeValue(xml_QMRF_number, xmldoc);
					Chunk ident_title = new Chunk("QMRF identifier (ECB Inventory):");
					Chunk ident_text = new Chunk(Text);

		            Font bi_font = new Font(baseFont, header_font_size, Font.BOLDITALIC);
		            Font i_font = new Font(baseFont, header_font_size, Font.ITALIC);
		            
					ident_title.setFont(bi_font);
					ident_text.setFont(i_font);
					Paragraph p = new Paragraph();
					p.add(ident_title);
					p.add(ident_text);
					cell = new PdfPCell(p);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);

					ident_title = new Chunk("QMRF Title:");
                    Phrase textPhrase = new Phrase();
                    createNodePhrase("QSAR_title",xmldoc,textPhrase,i_font);
					ident_title.setFont(bi_font);

					p = new Paragraph();
					p.add(ident_title);
					p.add(textPhrase);
					cell = new PdfPCell(p);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					table1.addCell(cell);


					java.util.Date now = new java.util.Date();
					java.text.DateFormat df = java.text.DateFormat.getDateInstance();
				    String date = df.format(now);
					ident_title = new Chunk("Printing Date:");
					ident_text = new Chunk(date);
					ident_title.setFont(bi_font);
					ident_text.setFont(i_font);
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

			}catch (Throwable x) {
				 x.printStackTrace();

	        }

					//table.addCell(new PdfPCell(png, true));
	        cell.addElement(new Chunk(png_right,+14,-40));
					table.addCell(cell);
					pdfdoc.add(table);
					pdfdoc.add(new Paragraph(16));

		} catch (Exception x) {
			x.printStackTrace();
		}
	}
    /*
    protected String findNodeValue(String name_node, org.w3c.dom.Document source) {

        NodeList n = source.getElementsByTagName(name_node);
        if (n.getLength() ==0) return "";
        String v = n.item(0).getTextContent();
//        return org.apache.commons.lang.StringEscapeUtils.unescapeXml(replaceTags(v));
        try {
            System.out.println(v);
            org.w3c.dom.Document doc = nodeBuilder.parse(new InputSource(new StringReader(v.trim())));
            StringBuffer b = new StringBuffer();
            org.w3c.dom.Element e = doc.getDocumentElement();
            getText(e, b,font);
            return b.toString();
        } catch (Exception x) {
           // x.printStackTrace();
            return org.apache.commons.lang.StringEscapeUtils.unescapeXml(replaceTags(v));
        }
    }
    */
    protected void createNodePhrase(String name_node, org.w3c.dom.Document source, Phrase phrase, Font font) {

        NodeList n = source.getElementsByTagName(name_node);
        if (n.getLength() ==0) return;
        String v = n.item(0).getTextContent().trim();
//        return org.apache.commons.lang.StringEscapeUtils.unescapeXml(replaceTags(v));
        try {
            org.w3c.dom.Document doc = nodeBuilder.parse(new InputSource(new StringReader(v)));
            StringBuffer b = new StringBuffer();
            org.w3c.dom.Element e = doc.getDocumentElement();
            getText(e, phrase, font, ScriptMode.normal,true,0);
            return;
        } catch (Exception x) {
           // x.printStackTrace();
            Chunk chunk = new Chunk(
                    org.apache.commons.lang.StringEscapeUtils.unescapeXml(replaceTags(v)));
            chunk.setFont(font);
            phrase.add(chunk);
        }
    }

    protected int getText(org.w3c.dom.Node node, Phrase phrase, Font currentFont, ScriptMode scriptMode, boolean trim, int paragraphs) {

    	
        if (node.getNodeType() == node.ELEMENT_NODE) {
        	if ("head".equals(node.getNodeName())) return paragraphs;
        	if ("html".equals(node.getNodeName())) trim=true;
        	else if ("body".equals(node.getNodeName())) trim=true;
        	else if ("p".equals(node.getNodeName())) trim=true;
        	else trim = false;
        	//if ("p".equals(node.getNodeName())) trim=true;
//        	System.out.println(node.getNodeName() + ' ' + trim);	
        	//System.out.println(paragraphs);	
            Font f = currentFont;
            int fweight = currentFont.style();
            float fsize = currentFont.size();
            Color clr = currentFont.color();
            boolean modify = false;
            if ("b".equals(node.getNodeName())) {
                if ((currentFont.style() == Font.ITALIC) || (currentFont.style() == Font.BOLDITALIC))
                    fweight = Font.BOLDITALIC;
                else fweight = Font.BOLD;
                modify = true;
            }
            if ("i".equals(node.getNodeName())) {
                if ((currentFont.style() == Font.BOLD) || (currentFont.style() == Font.BOLDITALIC))
                    fweight = Font.BOLDITALIC;
                else fweight = Font.ITALIC;
                modify = true;
            }
            if ("sub".equals(node.getNodeName())) {
                scriptMode = ScriptMode.subscript;
            }
            if ("sup".equals(node.getNodeName())) {
                scriptMode = ScriptMode.superscript;
            }
            if ("font".equals(node.getNodeName()))  {
                String r = ((org.w3c.dom.Element) node).getAttribute("color");
                if (r != null) try {
                    clr = Hex2Color(r.substring(1));
                    modify = true;
                } catch (Exception x) {
                    clr = currentFont.color();
                }
                String z = ((org.w3c.dom.Element) node).getAttribute("size");
                if (z != null) {
                    try {
                        fsize = Integer.parseInt(z);
                        modify = true;
                    } catch (Exception x) {
                        fsize = currentFont.size();
                    }
                }
            }
            if (modify) {
                f = FontFactory.getFont(currentFont.getFamilyname(), fsize, fweight,clr);
            }
            if ("p".equals(node.getNodeName()))  {
            	if (paragraphs>0)
            		phrase.add(new Chunk('\n',f));
            	paragraphs++;

            }
            
            //f = FontFactory.getFont(currentFont.getFamilyname(),curr)
            NodeList nodes = node.getChildNodes();
            for (int i=0; i < nodes.getLength();i++)
                  paragraphs += getText(nodes.item(i), phrase, f, scriptMode,trim,paragraphs);

        } else if (node.getNodeType() == node.TEXT_NODE) {
        	
        	String value = node.getNodeValue();
        	if (trim) value=replaceNewLine(value);
        	if ("".equals(value)) return paragraphs;
        	//System.out.println(value);
             Chunk chunk = new Chunk(value,currentFont);
             HyphenationAuto autoEN = new HyphenationAuto("en", "GB", 2, 2);
             chunk.setHyphenation(autoEN);
             switch (scriptMode) {
            case superscript:
                chunk.setTextRise(currentFont.size()*0.3f);
                break;
            case subscript:
                chunk.setTextRise(-currentFont.size()*0.3f);
                break;

            default:
                break;
            }
             phrase.add(chunk);
        }
        return paragraphs;
    }

}


