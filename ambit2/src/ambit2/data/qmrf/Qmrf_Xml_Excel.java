package ambit2.data.qmrf;


import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



public class Qmrf_Xml_Excel extends QMRFConverter {
	protected static byte[] color_even_sections = {(byte)204,(byte)255,(byte)204};
	protected static byte[] color_odd_sections = {(byte)255,(byte)255,(byte)153};
	protected String font_name="Arial";
	protected short column_chapters = 0;
	protected short column_sections = 1;
	protected short column_help = 2;
	protected short column_content = 3;
	

	//public void xml2excel(InputStream  xml, OutputStream excel) {
	public void xml2excel(InputSource source, OutputStream excel) throws Exception {


			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet_readme = wb.createSheet("README");
		    HSSFSheet sheet_qmrf = wb.createSheet("QMRF");
		    
		   // HSSFSheet sheet_endpoint = wb.createSheet("Look-up endpoint classification");
		    
		    wb.setSelectedTab((short)1);
		    String chapter = "";
			String name = "";
			String value = "";
			String sub_chapter_number = "";
			String sub_chapter_name = "";
			String sub_chapter_text= "";


		    HSSFPalette palette = wb.getCustomPalette();
	        palette.setColorAtIndex(HSSFColor.YELLOW.index, 
	        		color_odd_sections[0],color_odd_sections[1],color_odd_sections[2]
	        );	    
		    palette.setColorAtIndex(HSSFColor.GREEN.index,
		    		color_even_sections[0],color_even_sections[1],color_even_sections[2]
		    );

		    
//			bold text
		    HSSFFont font = wb.createFont();
		    font.setFontHeightInPoints((short)10);
		    font.setFontName(font_name);
		    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		    //italic text
		    HSSFFont font1 = wb.createFont();
		    font1.setFontName(font_name);
		    font1.setFontHeightInPoints((short)9);
		    font1.setItalic(true);

		    //dynamic text
		    HSSFFont dynamic_text = wb.createFont();
		    dynamic_text.setFontName(font_name);
		    dynamic_text.setFontHeightInPoints((short)9);

		    //dynamic text
		    HSSFFont help_text = wb.createFont();
		    help_text.setFontName(font_name);
		    help_text.setFontHeightInPoints((short)8);


		    //header row
		    HSSFCellStyle cellStyle = wb.createCellStyle();
	        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
	        cellStyle.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
	        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
	        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	        cellStyle.setWrapText( true );
	        cellStyle.setFont(font);
	        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP );



	        //yellow cells chapter
	        HSSFCellStyle cellStyle_chapter =chapterStyle(wb,font,true,true,HSSFColor.YELLOW.index); 
	        	//createChapterCellStyle(wb, font);

	        //yellow cells subchapter
	        HSSFCellStyle cellStyle1 = subchapterStyle(wb,font,false,false,HSSFColor.YELLOW.index);

	        //yellow cells subchapter first
	        HSSFCellStyle cellStyle_yellow_subchapter_first = subchapterStyle(wb,font,true,false,HSSFColor.YELLOW.index);

	        //green cells chapter
	        HSSFCellStyle green_cellStyle_chapter = chapterStyle(wb,font,true,true,HSSFColor.GREEN.index);
	        
	        //green cells chapter last
	        HSSFCellStyle green_cellStyle_chapter_last = chapterStyle(wb,font,true,true,HSSFColor.GREEN.index);

	        //green cells subchapter
	        HSSFCellStyle green_cellStyle_subchapter = subchapterStyle(wb,font,false,false,HSSFColor.GREEN.index);
	        
	        //green cells subchapter first row
	        HSSFCellStyle green_cellStyle_subchapter_first = subchapterStyle(wb,font,true,false,HSSFColor.GREEN.index);

	        
	        //green cells subchapter last
	        HSSFCellStyle green_cellStyle_subchapter_last = subchapterStyle(wb,font,false,true,HSSFColor.GREEN.index);
	        
	        //yellow cells  dynamic text
	        HSSFCellStyle cellStyle_dynamic_text = textStyle(wb, dynamic_text,false, false,HSSFColor.YELLOW.index);

	        //yellow cells  dynamic text first
	        HSSFCellStyle cellStyle_dynamic_text_first = textStyle(wb, dynamic_text,true, false, HSSFColor.YELLOW.index);

	        wb.getCustomPalette().setColorAtIndex(HSSFColor.ROSE.index,
	        		color_even_sections[0],color_even_sections[1],color_even_sections[2]
	        );

	        //green cells  dynamic text
	        HSSFCellStyle cellStyle_dynamic_text_green = textStyle(wb, dynamic_text,false, false,HSSFColor.ROSE.index);

	        //green cells  dynamic text
	        HSSFCellStyle cellStyle_dynamic_text_green_first = textStyle(wb, dynamic_text,true, false, HSSFColor.ROSE.index);

	        //green cells  dynamic text
	        HSSFCellStyle cellStyle_dynamic_text_green_last =  textStyle(wb, dynamic_text,false, true, HSSFColor.ROSE.index);

	        //yellow cells  help text
	        HSSFCellStyle cellStyle_help_text = helpStyle(wb, help_text,false, false, HSSFColor.YELLOW.index);

	        //yellow cells  help text first cell
	        HSSFCellStyle cellStyle_yellow_help_text_first = helpStyle(wb, help_text,true, false, HSSFColor.YELLOW.index);
	        
	        //green cells  help text
	        HSSFCellStyle cellStyle_help_text_green = helpStyle(wb, help_text,false, false, HSSFColor.ROSE.index);

	        //green cells  help text first cell
	        HSSFCellStyle cellStyle_help_text_green_first = helpStyle(wb, help_text,true, false, HSSFColor.ROSE.index);

	        //green cells  help text last cell
	        HSSFCellStyle cellStyle_help_text_green_last = helpStyle(wb, help_text,false, true, HSSFColor.ROSE.index);


	        short row_number = 0;
	       //row1
	        HSSFRow row = sheet_qmrf.createRow(row_number); row_number++;

		    row.createCell(column_chapters).setCellValue("QMRF CHAPTER TITLES");
		    row.getCell(column_chapters).setCellStyle(cellStyle);
		    row.createCell(column_sections).setCellValue("SECTION TITLES");
		    row.getCell(column_sections).setCellStyle(cellStyle);
		    row.createCell(column_help).setCellValue("Help on the content");
		    row.getCell(column_help).setCellStyle(cellStyle);


		    HSSFRichTextString bold_string = new HSSFRichTextString("MODEL DESCRIPTION \n (Fill in this column)");
		    bold_string.applyFont(0, 17,font);
		    bold_string.applyFont(18,bold_string.length(),font1);

		    row.createCell(column_content).setCellValue(bold_string);
		    row.getCell(column_content).setCellStyle(cellStyle);


 		    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			QMRFSchemaResolver resolver = new QMRFSchemaResolver("http://ambit2.acad.bg/qmrf/qmrf.dtd",null);
			resolver.setIgnoreSystemID(true);
			docBuilder.setEntityResolver(resolver);

			org.w3c.dom.Document doc = docBuilder.parse(source);

			createReadmeSheet(wb,sheet_readme,doc);
			//createEndpointsSheet(wb,sheet_endpoint,doc);
			//row2
		    HSSFRow row2 = sheet_qmrf.createRow(row_number); 
		    chapter = findAttributeValue("QSAR_identifier",xml_attribute_chapter, doc);
			name = findAttributeValue("QSAR_identifier",xml_attribute_name, doc);
			value = chapter+dot+ name;
		    row2.createCell(column_chapters).setCellValue(value);
		    row2.getCell(column_chapters).setCellStyle(cellStyle_chapter);

		    sheet_qmrf.addMergedRegion(new Region(row_number,(short)0,row_number+3,(short)0));


		    row2.createCell(column_sections);
		    row2.getCell(column_sections).setCellStyle(cellStyle_yellow_subchapter_first);

		    row2.createCell(column_help);
		    row2.getCell(column_help).setCellStyle(cellStyle_yellow_help_text_first);
		    row2.createCell(column_content);
		    row2.getCell(column_content).setCellStyle(cellStyle_dynamic_text_first);

		    row_number++;




			//row3
		    sub_chapter_number = findAttributeValue(xml_QSAR_title,xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue(xml_QSAR_title,xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			HSSFRow row3 = sheet_qmrf.createRow(row_number); row_number++;
		    row3.createCell((short)1).setCellValue(value);
		    row3.getCell(column_sections).setCellStyle(cellStyle1);

		    value = findAttributeValue(xml_QSAR_title,xml_attribute_help,doc);
		    row3.createCell(column_help).setCellValue(value);
		    row3.getCell(column_help).setCellStyle(cellStyle_help_text);


		    value = findNodeValue(xml_QSAR_title,doc);
		    row3.createCell(column_content).setCellValue(value);
		    row3.getCell(column_content).setCellStyle(cellStyle_dynamic_text);


		    //row4
		    HSSFRow row4 = sheet_qmrf.createRow(row_number); row_number++; 
		    sub_chapter_number = findAttributeValue(xml_QSAR_models,xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue(xml_QSAR_models,xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
		    row4.createCell(column_sections).setCellValue(value);
		    row4.getCell(column_sections).setCellStyle(cellStyle1);


		    value = findAttributeValue(xml_QSAR_models,xml_attribute_help,doc);
		    row4.createCell(column_help).setCellValue(value);
		    row4.getCell(column_help).setCellStyle(cellStyle_help_text);


		    value = findNodeValue(xml_QSAR_models,doc);
		    row4.createCell(column_content).setCellValue(value);
		    row4.getCell(column_content).setCellStyle(cellStyle_dynamic_text);


		    //row5
		    HSSFRow row5 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("QSAR_software",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("QSAR_software",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
		    row5.createCell(column_sections).setCellValue(value);
		    row5.getCell(column_sections).setCellStyle(cellStyle1);

		    value = findAttributeValue("QSAR_software",xml_attribute_help,doc);
		    row5.createCell(column_help).setCellValue(value);
		    row5.getCell(column_help).setCellStyle(cellStyle_help_text);

		    HSSFCell cell = row5.createCell(column_content);
		    cell.setCellStyle(cellStyle_dynamic_text);
		    cell.setCellValue(listNodeAttributes(doc,"QSAR_software","software_ref", "software", att_software,false));



		    //row6
		    HSSFRow row6 = sheet_qmrf.createRow(row_number); 
		    chapter = findAttributeValue("QSAR_General_information",xml_attribute_chapter, doc);
			name = findAttributeValue("QSAR_General_information",xml_attribute_name, doc);
			value = chapter+dot+ name;


		    row6.createCell(column_chapters).setCellValue(value);
		    row6.getCell(column_chapters).setCellStyle(green_cellStyle_chapter);

		    row6.createCell(column_sections);
		    row6.getCell(column_sections).setCellStyle(green_cellStyle_subchapter_first);
		    row6.createCell(column_help);
		    row6.getCell(column_help).setCellStyle(cellStyle_help_text_green_first);
		    row6.createCell(column_content);
		    row6.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green_first);
		    sheet_qmrf.addMergedRegion(new Region(row_number,column_chapters,row_number+9,column_chapters));

		    row_number++;


		    //row7
		    HSSFRow row7 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("qmrf_date",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("qmrf_date",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
		    row7.createCell(column_sections).setCellValue(value);
		    row7.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("qmrf_date",xml_attribute_help,doc);
		    row7.createCell(column_help).setCellValue(value);
		    row7.getCell(column_help).setCellStyle(cellStyle_help_text_green);


		    value = findNodeValue("qmrf_date",doc);
		    row7.createCell(column_content).setCellValue(value);
		    row7.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row8
		    HSSFRow row8 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("qmrf_authors",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("qmrf_authors",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
		    row8.createCell(column_sections).setCellValue(value);
		    row8.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("qmrf_authors",xml_attribute_help,doc);
		    row8.createCell(column_help).setCellValue(value);
		    row8.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    value = listNodeAttributes(doc,"qmrf_authors","author_ref", "author", att_author,false);
		    /*
		    StringBuffer b = new StringBuffer();
		    for (Iterator it = qmrf_authors_list.iterator(); it.hasNext(); ) {
		    	   Node qmrf_authors = (Node)it.next();
		    	   b.append(listNodeAttributes(qmrf_authors, att_author));
		    }

		    //value = findNodeValue("qmrf_authors",doc);
		     * */
		     
		    row8.createCell(column_content).setCellValue(value);
		    row8.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row9
		    HSSFRow row9 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("qmrf_date_revision",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("qmrf_date_revision",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row9.createCell(column_sections).setCellValue(value);
			row9.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("qmrf_date_revision",xml_attribute_help,doc);
		    row9.createCell(column_help).setCellValue(value);
		    row9.getCell(column_help).setCellStyle(cellStyle_help_text_green);


		    value = findNodeValue("qmrf_date_revision",doc);
		    row9.createCell(column_content).setCellValue(value);
		    row9.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row10
		    HSSFRow row10 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("qmrf_revision",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("qmrf_revision",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row10.createCell(column_sections).setCellValue(value);
			row10.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("qmrf_revision",xml_attribute_help,doc);
		    row10.createCell(column_help).setCellValue(value);
		    row10.getCell(column_help).setCellStyle(cellStyle_help_text_green);


		    value = findNodeValue("qmrf_revision",doc);
		    row10.createCell(column_content).setCellValue(value);
		    row10.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row11
		    HSSFRow row11 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("model_authors",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("model_authors",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row11.createCell(column_sections).setCellValue(value);
			row11.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("model_authors",xml_attribute_help,doc);
		    row11.createCell(column_help).setCellValue(value);
		    row11.getCell(column_help).setCellStyle(cellStyle_help_text_green);


		    value = listNodeAttributes(doc,"model_authors","author_ref", "author", att_author,false);
		    row11.createCell(column_content).setCellValue(value);
		    row11.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row12
		    HSSFRow row12 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("model_date",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("model_date",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row12.createCell(column_sections).setCellValue(value);
			row12.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("model_date",xml_attribute_help,doc);
		    row12.createCell(column_help).setCellValue(value);
		    row12.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    value = findNodeValue("model_date",doc);
		    row12.createCell(column_content).setCellValue(value);
		    row12.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row13
		    HSSFRow row13 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("references",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("references",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row13.createCell(column_sections).setCellValue(value);
			row13.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("references",xml_attribute_help,doc);
		    row13.createCell(column_help).setCellValue(value);
		    row13.getCell(column_help).setCellStyle(cellStyle_help_text_green);


		    /*
		    value = "";
		    String prefix = "";
		    ArrayList publication_list = findNodeIDRef("references","publication_ref", "publication", doc);
	        ArrayList bold_array = new ArrayList();
		    String pub_string  = null;
	        HSSFRichTextString bold_string_pub = null;
		    int num = 1;

		    Qmrf_Xml_Excel.BoldProperty bp =null;

		    for (Iterator it = publication_list.iterator(); it.hasNext(); ) {
		    	   Node publication = (Node)it.next();


		    	   if(findAttributeValue("title",publication).trim().length() > 0 ){
		    	   prefix = "["+num+"]";
		    	   pub_string = prefix +  findAttributeValue("title",publication);
		    	   //bold_string_pub = new HSSFRichTextString(pub_string);
		    	   //bold_string_pub.applyFont(0, prefix.length(),font);
		    	   bp = new Qmrf_Xml_Excel().new BoldProperty();
		    	   bp.StartValue = value.length();
		    	   bp.EndValue = bp.StartValue + prefix.length();
		    	   bold_array.add(bp);
		    	   value += pub_string;
		   	       value += "\n";
		    	   }
		    	   num++;

		    }


		   // value = findNodeValue("references",doc);
		    bold_string_pub = new HSSFRichTextString(value);
		    for (Iterator it = bold_array.iterator(); it.hasNext(); ) {
		    	   Qmrf_Xml_Excel.BoldProperty bold_border = (Qmrf_Xml_Excel.BoldProperty)it.next();
		    	   bold_string_pub.applyFont(bold_border.StartValue, bold_border.EndValue,font);
		    }
		    //row13.createCell(column_content).setCellValue(value);

		     */
		    
	 	    row13.createCell(column_content).setCellValue(listNodeAttributes(doc,"references","publication_ref", "publication", att_reference,true));
		    row13.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row14
		    HSSFRow row14 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("info_availability",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("info_availability",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row14.createCell(column_sections).setCellValue(value);
			row14.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("info_availability",xml_attribute_help,doc);
		    row14.createCell(column_help).setCellValue(value);
		    row14.getCell(column_help).setCellStyle(cellStyle_help_text_green);


		    value = findNodeValue("info_availability",doc);
		    row14.createCell(column_content).setCellValue(value);
		    row14.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row15
		    HSSFRow row15 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("related_models",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("related_models",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row15.createCell(column_sections).setCellValue(value);
			row15.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("related_models",xml_attribute_help,doc);
		    row15.createCell(column_help).setCellValue(value);
		    row15.getCell(column_help).setCellStyle(cellStyle_help_text_green);


		    value = findNodeValue("related_models",doc);
		    row15.createCell(column_content).setCellValue(value);
		    row15.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row16
		    HSSFRow row16 = sheet_qmrf.createRow(row_number); 
		    chapter = findAttributeValue("QSAR_Endpoint",xml_attribute_chapter, doc);
			name = findAttributeValue("QSAR_Endpoint",xml_attribute_name, doc);
			value = chapter+dot+ name;
		    row16.createCell(column_chapters).setCellValue(value);
		    row16.getCell(column_chapters).setCellStyle(cellStyle_chapter);

		    sheet_qmrf.addMergedRegion(new Region(row_number,(short)0,row_number+7,(short)0));


		    row16.createCell(column_sections);
		    row16.getCell(column_sections).setCellStyle(cellStyle_yellow_subchapter_first);

		    row16.createCell(column_help);
		    row16.getCell(column_help).setCellStyle(cellStyle_yellow_help_text_first);
		    row16.createCell(column_content);
		    row16.getCell(column_content).setCellStyle(cellStyle_dynamic_text_first);
		    row_number++;
		    
		    
		    //row17
		    HSSFRow row17 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("model_species",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("model_species",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row17.createCell(column_sections).setCellValue(value);
			row17.getCell(column_sections).setCellStyle(cellStyle1);


		    value = findAttributeValue("model_species",xml_attribute_help,doc);
		    row17.createCell(column_help).setCellValue(value);
		    row17.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findNodeValue("model_species",doc);
		    row17.createCell(column_content).setCellValue(value);
		    row17.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row18
		    HSSFRow row18 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("model_endpoint",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("model_endpoint",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row18.createCell(column_sections).setCellValue(value);
			row18.getCell(column_sections).setCellStyle(cellStyle1);


		    value = findAttributeValue("model_endpoint",xml_attribute_help,doc);
		    row18.createCell(column_help).setCellValue(value);
		    row18.getCell(column_help).setCellStyle(cellStyle_help_text);

		    /*
		    value = "";
		    ArrayList model_endpoints_list = findNodeIDRef("model_endpoint","endpoint_ref", "endpoint", doc);
		    for (Iterator it = model_endpoints_list.iterator(); it.hasNext(); ) {
			   Node qmrf_endpoints = (Node)it.next();
			   if(findAttributeValue(xml_attribute_name,qmrf_endpoints).trim().length() > 0 ){
			   value += findAttributeValue(xml_attribute_name,qmrf_endpoints);
			       value += "\n";
			   }
		    }
		    */
		    
		    cell = row18.createCell(column_content);
		    cell.setCellValue(listNodeAttributes(doc,"model_endpoint","endpoint_ref", "endpoint", att_endpoint,true));
		    row18.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row19
		    HSSFRow row19 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("endpoint_comments",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("endpoint_comments",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row19.createCell(column_sections).setCellValue(value);
			row19.getCell(column_sections).setCellStyle(cellStyle1);

		    value = findAttributeValue("endpoint_comments",xml_attribute_help,doc);
		    row19.createCell(column_help).setCellValue(value);
		    row19.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findNodeValue("endpoint_comments",doc);
		    row19.createCell(column_content).setCellValue(value);
		    row19.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row20
		    HSSFRow row20 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("endpoint_units",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("endpoint_units",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row20.createCell(column_sections).setCellValue(value);
			row20.getCell(column_sections).setCellStyle(cellStyle1);


		    value = findAttributeValue("endpoint_units",xml_attribute_help,doc);
		    row20.createCell(column_help).setCellValue(value);
		    row20.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findNodeValue("endpoint_units",doc);
		    row20.createCell(column_content).setCellValue(value);
		    row20.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row21
		    HSSFRow row21 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("endpoint_variable",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("endpoint_variable",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row21.createCell(column_sections).setCellValue(value);
			row21.getCell(column_sections).setCellStyle(cellStyle1);


		    value = findAttributeValue("endpoint_variable",xml_attribute_help,doc);
		    row21.createCell(column_help).setCellValue(value);
		    row21.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findNodeValue("endpoint_variable",doc);
		    row21.createCell(column_content).setCellValue(value);
		    row21.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row22
		    HSSFRow row22 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("endpoint_protocol",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("endpoint_protocol",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row22.createCell(column_sections).setCellValue(value);
			row22.getCell(column_sections).setCellStyle(cellStyle1);

		    value = findAttributeValue("endpoint_protocol",xml_attribute_help,doc);
		    row22.createCell(column_help).setCellValue(value);
		    row22.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findNodeValue("endpoint_protocol",doc);
		    row22.createCell(column_content).setCellValue(value);
		    row22.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row23
		    HSSFRow row23 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("endpoint_data_quality",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("endpoint_data_quality",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row23.createCell(column_sections).setCellValue(value);
			row23.getCell(column_sections).setCellStyle(cellStyle1);


		    value = findAttributeValue("endpoint_data_quality",xml_attribute_help,doc);
		    row23.createCell(column_help).setCellValue(value);
		    row23.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findNodeValue("endpoint_data_quality",doc);
		    row23.createCell(column_content).setCellValue(value);
		    row23.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row24
		    HSSFRow row24 = sheet_qmrf.createRow(row_number); 
		    chapter = findAttributeValue("QSAR_Algorithm",xml_attribute_chapter, doc);
			name = findAttributeValue("QSAR_Algorithm",xml_attribute_name, doc);
			value = chapter+dot+ name;


		    row24.createCell(column_chapters).setCellValue(value);
		    row24.getCell(column_chapters).setCellStyle(green_cellStyle_chapter);

		    row24.createCell(column_sections);
		    row24.getCell(column_sections).setCellStyle(green_cellStyle_subchapter_first);
		    row24.createCell(column_help);
		    row24.getCell(column_help).setCellStyle(cellStyle_help_text_green_first);
		    row24.createCell(column_content);
		    row24.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green_first);

		    int h = 8;
		    String section_removed_1_2 = findAttributeValue("algorithm_comments",xml_attribute_chapter, doc);
		    if (section_removed_1_2==null) h = 7;
		    

		    sheet_qmrf.addMergedRegion(new Region(row_number,column_chapters,row_number+h,column_chapters));
		    row_number++;

		    //row25
		    HSSFRow row25 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("algorithm_type",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("algorithm_type",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row25.createCell(column_sections).setCellValue(value);
			row25.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("algorithm_type",xml_attribute_help,doc);
		    row25.createCell(column_help).setCellValue(value);
		    row25.getCell(column_help).setCellStyle(cellStyle_help_text_green);


		    value = findNodeValue("algorithm_type",doc);
		    row25.createCell(column_content).setCellValue(value);
		    row25.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row26
		    HSSFRow row26 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("algorithm_explicit",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("algorithm_explicit",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row26.createCell(column_sections).setCellValue(value);
			row26.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("algorithm_explicit",xml_attribute_help,doc);
		    row26.createCell(column_help).setCellValue(value);
		    row26.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    /*
		    //value = findNodeValue("algorithm_explicit",doc);
		    value = "";
		    ArrayList algorithm_list = findNodeIDRef("algorithm_explicit","algorithm_ref", "algorithm", doc);
		    for (Iterator it = algorithm_list.iterator(); it.hasNext(); ) {
			   Node qmrf_algorithms = (Node)it.next();
			   if(findAttributeValue("definition",qmrf_algorithms).trim().length() > 0 ){
			   value += findAttributeValue("definition",qmrf_algorithms);
			       value += "\n";
			   }
			   if(findAttributeValue("description",qmrf_algorithms).trim().length() > 0 ){
			       value += findAttributeValue("description",qmrf_algorithms);
			       value += "\n";
			   }

		    }
		    */
		    
		    cell = row26.createCell(column_content);
		    cell.setCellValue(listNodeAttributes(doc,"algorithm_explicit","algorithm_ref", "algorithm", att_algorithm,false));
		    row26.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row27 - removed in 1.2
		     
		    sub_chapter_number = findAttributeValue("algorithm_comments",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("algorithm_comments",xml_attribute_name, doc);
			if ((sub_chapter_number!=null) && (sub_chapter_name!=null)) {
				HSSFRow row27 = sheet_qmrf.createRow(row_number);
				row_number++;
				value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
				row27.createCell(column_sections).setCellValue(value);
				row27.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
			    value = findAttributeValue("algorithm_comments",xml_attribute_help,doc);
			    row27.createCell(column_help).setCellValue(value);
			    row27.getCell(column_help).setCellStyle(cellStyle_help_text_green);
	
	
			    value = findNodeValue("algorithm_comments",doc);
			    row27.createCell(column_content).setCellValue(value);
			    row27.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);
			}
		    //row28
		    HSSFRow row28 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("algorithms_descriptors",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("algorithms_descriptors",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row28.createCell(column_sections).setCellValue(value);
			row28.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("algorithms_descriptors",xml_attribute_help,doc);
		    row28.createCell(column_help).setCellValue(value);
		    row28.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    /*
		    //value = findNodeValue("algorithms_descriptors",doc);
		    value = "";
		    ArrayList descriptor_list = findNodeIDRef("algorithms_descriptors","descriptor_ref", "descriptor", doc);
		    bold_array = new ArrayList();
		    for (Iterator it = descriptor_list.iterator(); it.hasNext(); ) {
			   Node qmrf_descriptors = (Node)it.next();
			   bp = new Qmrf_Xml_Excel().new BoldProperty();
	    	   bp.StartValue = value.length();

			   if(findAttributeValue(xml_attribute_name,qmrf_descriptors).trim().length() > 0 ){
			   value += findAttributeValue(xml_attribute_name,qmrf_descriptors);

			   }
			   if(findAttributeValue("units",qmrf_descriptors).trim().length() > 0 ){
				   value += ",";
				   value += findAttributeValue("units",qmrf_descriptors);

			   }
			   bp.EndValue = bp.StartValue + (value.length() - bp.StartValue);
	    	   bold_array.add(bp);
			   if(findAttributeValue("description",qmrf_descriptors).trim().length() > 0 ){
				   value += space;
			       value += findAttributeValue("description",qmrf_descriptors);
			       value += "\n";
			   }
		    }

		    //row28.createCell(column_content).setCellValue(value);
		    bold_string_pub = new HSSFRichTextString(value);
		    for (Iterator it = bold_array.iterator(); it.hasNext(); ) {
		    	   Qmrf_Xml_Excel.BoldProperty bold_border = (Qmrf_Xml_Excel.BoldProperty)it.next();
		    	   bold_string_pub.applyFont(bold_border.StartValue, bold_border.EndValue,font);
	        }
	        */
		    
		    cell = row28.createCell(column_content);
		    cell.setCellValue(listNodeAttributes(doc,"algorithms_descriptors","descriptor_ref", "descriptor", att_descriptor,true));
		    cell.setCellStyle(cellStyle_dynamic_text_green);

		    //row29
		    HSSFRow row29= sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("descriptors_selection",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("descriptors_selection",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row29.createCell(column_sections).setCellValue(value);
			row29.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("descriptors_selection",xml_attribute_help,doc);
		    row29.createCell(column_help).setCellValue(value);
		    row29.getCell(column_help).setCellStyle(cellStyle_help_text_green);


		    value = findNodeValue("descriptors_selection",doc);
		    row29.createCell(column_content).setCellValue(value);
		    row29.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row30
		    HSSFRow row30= sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("descriptors_generation",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("descriptors_generation",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row30.createCell(column_sections).setCellValue(value);
			row30.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("descriptors_generation",xml_attribute_help,doc);
		    row30.createCell(column_help).setCellValue(value);
		    row30.getCell(column_help).setCellStyle(cellStyle_help_text_green);


		    value = findNodeValue("descriptors_generation",doc);
		    row30.createCell(column_content).setCellValue(value);
		    row30.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row31
		    HSSFRow row31= sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("descriptors_generation_software",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("descriptors_generation_software",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row31.createCell(column_sections).setCellValue(value);
			row31.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("descriptors_generation_software",xml_attribute_help,doc);
		    row31.createCell(column_help).setCellValue(value);
		    row31.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    

		    cell = row31.createCell(column_content);
		    cell.setCellValue(listNodeAttributes(doc,"descriptors_generation_software","software_ref", "software", att_software,false));
		    row31.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row32
		    HSSFRow row32= sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("descriptors_chemicals_ratio",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("descriptors_chemicals_ratio",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row32.createCell(column_sections).setCellValue(value);
			row32.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("descriptors_chemicals_ratio",xml_attribute_help,doc);
		    row32.createCell(column_help).setCellValue(value);
		    row32.getCell(column_help).setCellStyle(cellStyle_help_text_green);


		    value = findNodeValue("descriptors_chemicals_ratio",doc);
		    row32.createCell(column_content).setCellValue(value);
		    row32.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row33
		    HSSFRow row33 = sheet_qmrf.createRow(row_number); 
		    chapter = findAttributeValue("QSAR_Applicability_domain",xml_attribute_chapter, doc);
			name = findAttributeValue("QSAR_Applicability_domain",xml_attribute_name, doc);
			value = chapter+dot+ name;
			row33.createCell(column_chapters).setCellValue(value);
			row33.getCell(column_chapters).setCellStyle(cellStyle_chapter);

		    sheet_qmrf.addMergedRegion(new Region(row_number,column_chapters,row_number+4,column_chapters));




		    row33.createCell(column_sections);
		    row33.getCell(column_sections).setCellStyle(cellStyle_yellow_subchapter_first);

		    row33.createCell(column_help);
		    row33.getCell(column_help).setCellStyle(cellStyle_yellow_help_text_first);
		    row33.createCell(column_content);
		    row33.getCell(column_content).setCellStyle(cellStyle_dynamic_text_first);
		    row_number++;

		    //row34
		    HSSFRow row34 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("app_domain_description",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("app_domain_description",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row34.createCell(column_sections).setCellValue(value);
			row34.getCell(column_sections).setCellStyle(cellStyle1);

		    value = findAttributeValue("app_domain_description",xml_attribute_help,doc);
		    row34.createCell(column_help).setCellValue(value);
		    row34.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findNodeValue("app_domain_description",doc);
		    row34.createCell(column_content).setCellValue(value);
		    row34.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row35
		    HSSFRow row35 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("app_domain_method",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("app_domain_method",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row35.createCell(column_sections).setCellValue(value);
			row35.getCell(column_sections).setCellStyle(cellStyle1);


		    value = findAttributeValue("app_domain_method",xml_attribute_help,doc);
		    row35.createCell(column_help).setCellValue(value);
		    row35.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findNodeValue("app_domain_method",doc);
		    row35.createCell(column_content).setCellValue(value);
		    row35.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row36
		    HSSFRow row36 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("app_domain_software",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("app_domain_software",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row36.createCell(column_sections).setCellValue(value);
			row36.getCell(column_sections).setCellStyle(cellStyle1);


		    value = findAttributeValue("app_domain_software",xml_attribute_help,doc);
		    row36.createCell(column_help).setCellValue(value);
		    row36.getCell(column_help).setCellStyle(cellStyle_help_text);

		    cell = row36.createCell(column_content);
		    cell.setCellValue(listNodeAttributes(doc,"app_domain_software","software_ref", "software", att_software,false));
		    row36.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row37
		    HSSFRow row37 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("applicability_limits",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("applicability_limits",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row37.createCell(column_sections).setCellValue(value);
			row37.getCell(column_sections).setCellStyle(cellStyle1);


		    value = findAttributeValue("applicability_limits",xml_attribute_help,doc);
		    row37.createCell(column_help).setCellValue(value);
		    row37.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findNodeValue("applicability_limits",doc);
		    row37.createCell(column_content).setCellValue(value);
		    row37.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row38
		    HSSFRow row38 = sheet_qmrf.createRow(row_number); 
		    chapter = findAttributeValue("QSAR_Robustness",xml_attribute_chapter, doc);
			name = findAttributeValue("QSAR_Robustness",xml_attribute_name, doc);
			value = chapter+dot+ name;


			row38.createCell(column_chapters).setCellValue(value);
			row38.getCell(column_chapters).setCellStyle(green_cellStyle_chapter);

			row38.createCell(column_sections);
			row38.getCell(column_sections).setCellStyle(green_cellStyle_subchapter_first);
			row38.createCell(column_help);
			row38.getCell(column_help).setCellStyle(cellStyle_help_text_green_first);
			row38.createCell(column_content);
			row38.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green_first);

			sheet_qmrf.addMergedRegion(new Region(row_number,(short)0,row_number+10,(short)0));
			row_number++;


		    //row39
		    HSSFRow row39= sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("training_set_availability",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("training_set_availability",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row39.createCell(column_sections).setCellValue(value);
			row39.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("training_set_availability",xml_attribute_help,doc);
		    row39.createCell(column_help).setCellValue(value);
		    row39.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    
		    value = findAnswer("training_set_availability",doc);
		    row39.createCell(column_content).setCellValue(value); 
		    row39.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row40
		    HSSFRow row40= sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("training_set_data",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("training_set_data",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row40.createCell(column_sections).setCellValue(value);
			row40.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("training_set_data",xml_attribute_help,doc);
		    row40.createCell(column_help).setCellValue(value);
		    row40.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    
		    value = findDataAvailable("training_set_data",doc);
		    row40.createCell(column_content).setCellValue(value);
		    row40.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row41
		    HSSFRow row41= sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("training_set_descriptors",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("training_set_descriptors",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row41.createCell(column_sections).setCellValue(value);
			row41.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("training_set_descriptors",xml_attribute_help,doc);
		    row41.createCell(column_help).setCellValue(value);
		    row41.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    value = findAnswer("training_set_descriptors",doc);
		    row41.createCell(column_content).setCellValue(value);
		    row41.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row42
		    HSSFRow row42 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("dependent_var_availability",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("dependent_var_availability",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row42.createCell(column_sections).setCellValue(value);
			row42.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("dependent_var_availability",xml_attribute_help,doc);
		    row42.createCell(column_help).setCellValue(value);
		    row42.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    value = findAnswer("dependent_var_availability",doc);
		    row42.createCell(column_content).setCellValue(value);
		    row42.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row43
		    HSSFRow row43 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("other_info",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("other_info",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row43.createCell(column_sections).setCellValue(value);
			row43.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("other_info",xml_attribute_help,doc);
		    row43.createCell(column_help).setCellValue(value);
		    row43.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    value = findNodeValue("other_info",doc);
		    row43.createCell(column_content).setCellValue(value);
		    row43.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row44
		    HSSFRow row44 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("preprocessing",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("preprocessing",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row44.createCell(column_sections).setCellValue(value);
			row44.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("preprocessing",xml_attribute_help,doc);
		    row44.createCell(column_help).setCellValue(value);
		    row44.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    value = findNodeValue("preprocessing",doc);
		    row44.createCell(column_content).setCellValue(value);
		    row44.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row45
		    HSSFRow row45 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("goodness_of_fit",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("goodness_of_fit",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row45.createCell(column_sections).setCellValue(value);
			row45.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("goodness_of_fit",xml_attribute_help,doc);
		    row45.createCell(column_help).setCellValue(value);
		    row45.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    value = findNodeValue("goodness_of_fit",doc);
		    row45.createCell(column_content).setCellValue(value);
		    row45.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row46
		    HSSFRow row46 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("loo",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("loo",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row46.createCell(column_sections).setCellValue(value);
			row46.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("loo",xml_attribute_help,doc);
		    row46.createCell(column_help).setCellValue(value);
		    row46.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    value = findNodeValue("loo",doc);
		    row46.createCell(column_content).setCellValue(value);
		    row46.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row47
		    HSSFRow row47 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("lmo",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("lmo",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row47.createCell(column_sections).setCellValue(value);
			row47.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("lmo",xml_attribute_help,doc);
		    row47.createCell(column_help).setCellValue(value);
		    row47.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    value = findNodeValue("lmo",doc);
		    row47.createCell(column_content).setCellValue(value);
		    row47.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row48
		    HSSFRow row48 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("yscrambling",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("yscrambling",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row48.createCell(column_sections).setCellValue(value);
			row48.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("yscrambling",xml_attribute_help,doc);
		    row48.createCell(column_help).setCellValue(value);
		    row48.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    value = findNodeValue("yscrambling",doc);
		    row48.createCell(column_content).setCellValue(value);
		    row48.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row49
		    HSSFRow row49 = sheet_qmrf.createRow(row_number);
		    chapter = findAttributeValue("QSAR_Predictivity",xml_attribute_chapter, doc);
			name = findAttributeValue("QSAR_Predictivity",xml_attribute_name, doc);
			value = chapter+dot+ name;
			row49.createCell(column_chapters).setCellValue(value);
			row49.getCell(column_chapters).setCellStyle(cellStyle_chapter);

		    sheet_qmrf.addMergedRegion(new Region(row_number,column_chapters,row_number+9,column_chapters));

		    row49.createCell(column_sections);
		    row49.getCell(column_sections).setCellStyle(cellStyle_yellow_subchapter_first);

		    row49.createCell(column_help);
		    row49.getCell(column_help).setCellStyle(cellStyle_yellow_help_text_first);
		    row49.createCell(column_content);
		    row49.getCell(column_content).setCellStyle(cellStyle_dynamic_text_first);
		    row_number++;
		    
		    //row50
		    HSSFRow row50 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("validation_set_availability",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("validation_set_availability",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row50.createCell(column_sections).setCellValue(value);
			row50.getCell(column_sections).setCellStyle(cellStyle1);

		    value = findAttributeValue("validation_set_availability",xml_attribute_help,doc);
		    row50.createCell(column_help).setCellValue(value);
		    row50.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findAnswer("validation_set_availability",doc);
		    row50.createCell(column_content).setCellValue(value);
		    row50.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row51
		    HSSFRow row51 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("validation_set_data",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("validation_set_data",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row51.createCell(column_sections).setCellValue(value);
			row51.getCell(column_sections).setCellStyle(cellStyle1);

		    value = findAttributeValue("validation_set_data",xml_attribute_help,doc);
		    row51.createCell(column_help).setCellValue(value);
		    row51.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findDataAvailable("validation_set_data",doc);
		    row51.createCell(column_content).setCellValue(value);
		    row51.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row52
		    HSSFRow row52 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("validation_set_descriptors",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("validation_set_descriptors",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row52.createCell(column_sections).setCellValue(value);
			row52.getCell(column_sections).setCellStyle(cellStyle1);

		    value = findAttributeValue("validation_set_descriptors",xml_attribute_help,doc);
		    row52.createCell(column_help).setCellValue(value);
		    row52.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findAnswer("validation_set_descriptors",doc);
		    row52.createCell(column_content).setCellValue(value);
		    row52.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row53
		    HSSFRow row53 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("validation_dependent_var_availability",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("validation_dependent_var_availability",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row53.createCell(column_sections).setCellValue(value);
			row53.getCell(column_sections).setCellStyle(cellStyle1);

		    value = findAttributeValue("validation_dependent_var_availability",xml_attribute_help,doc);
		    row53.createCell(column_help).setCellValue(value);
		    row53.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findAnswer("validation_dependent_var_availability",doc);
		    row53.createCell(column_content).setCellValue(value);
		    row53.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row54
		    HSSFRow row54 = sheet_qmrf.createRow(row_number); row_number++;
		    sub_chapter_number = findAttributeValue("validation_other_info",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("validation_other_info",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row54.createCell(column_sections).setCellValue(value);
			row54.getCell(column_sections).setCellStyle(cellStyle1);

		    value = findAttributeValue("validation_other_info",xml_attribute_help,doc);
		    row54.createCell(column_help).setCellValue(value);
		    row54.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findNodeValue("validation_other_info",doc);
		    row54.createCell(column_content).setCellValue(value);
		    row54.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row55
		    HSSFRow row55 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("experimental_design",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("experimental_design",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row55.createCell(column_sections).setCellValue(value);
			row55.getCell(column_sections).setCellStyle(cellStyle1);

		    value = findAttributeValue("experimental_design",xml_attribute_help,doc);
		    row55.createCell(column_help).setCellValue(value);
		    row55.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findNodeValue("experimental_design",doc);
		    row55.createCell(column_content).setCellValue(value);
		    row55.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row56
		    HSSFRow row56 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("validation_predictivity",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("validation_predictivity",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row56.createCell(column_sections).setCellValue(value);
			row56.getCell(column_sections).setCellStyle(cellStyle1);

		    value = findAttributeValue("validation_predictivity",xml_attribute_help,doc);
		    row56.createCell(column_help).setCellValue(value);
		    row56.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findNodeValue("validation_predictivity",doc);
		    row56.createCell(column_content).setCellValue(value);
		    row56.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row57
		    HSSFRow row57 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("validation_assessment",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("validation_assessment",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row57.createCell(column_sections).setCellValue(value);
			row57.getCell(column_sections).setCellStyle(cellStyle1);

		    value = findAttributeValue("validation_assessment",xml_attribute_help,doc);
		    row57.createCell(column_help).setCellValue(value);
		    row57.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findNodeValue("validation_assessment",doc);
		    row57.createCell(column_content).setCellValue(value);
		    row57.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row58
		    HSSFRow row58 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("validation_comments",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("validation_comments",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row58.createCell(column_sections).setCellValue(value);
			row58.getCell(column_sections).setCellStyle(cellStyle1);

		    value = findAttributeValue("validation_comments",xml_attribute_help,doc);
		    row58.createCell(column_help).setCellValue(value);
		    row58.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findNodeValue("validation_comments",doc);
		    row58.createCell(column_content).setCellValue(value);
		    row58.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row59
		    HSSFRow row59 = sheet_qmrf.createRow(row_number);
		    chapter = findAttributeValue("QSAR_Interpretation",xml_attribute_chapter, doc);
			name = findAttributeValue("QSAR_Interpretation",xml_attribute_name, doc);
			value = chapter+dot+ name;


			row59.createCell(column_chapters).setCellValue(value);
			row59.getCell(column_chapters).setCellStyle(green_cellStyle_chapter);

			row59.createCell(column_sections);
			row59.getCell(column_sections).setCellStyle(green_cellStyle_subchapter_first);
			row59.createCell(column_help);
			row59.getCell(column_help).setCellStyle(cellStyle_help_text_green_first);
			row59.createCell(column_content);
			row59.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green_first);

			sheet_qmrf.addMergedRegion(new Region(row_number,(short)0,row_number+3,(short)0));
			row_number++;

			//row60
		    HSSFRow row60 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("mechanistic_basis",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("mechanistic_basis",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row60.createCell(column_sections).setCellValue(value);
			row60.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("mechanistic_basis",xml_attribute_help,doc);
		    row60.createCell(column_help).setCellValue(value);
		    row60.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    value = findNodeValue("mechanistic_basis",doc);
		    row60.createCell(column_content).setCellValue(value);
		    row60.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row61
		    HSSFRow row61 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("mechanistic_basis_comments",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("mechanistic_basis_comments",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row61.createCell(column_sections).setCellValue(value);
			row61.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("mechanistic_basis_comments",xml_attribute_help,doc);
		    row61.createCell(column_help).setCellValue(value);
		    row61.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    value = findNodeValue("mechanistic_basis_comments",doc);
		    row61.createCell(column_content).setCellValue(value);
		    row61.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row62
		    HSSFRow row62 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("mechanistic_basis_info",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("mechanistic_basis_info",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row62.createCell(column_sections).setCellValue(value);
			row62.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("mechanistic_basis_info",xml_attribute_help,doc);
		    row62.createCell(column_help).setCellValue(value);
		    row62.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    value = findNodeValue("mechanistic_basis_info",doc);
		    row62.createCell(column_content).setCellValue(value);
		    row62.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row63
		    HSSFRow row63 = sheet_qmrf.createRow(row_number);
		    chapter = findAttributeValue("QSAR_Miscelaneous",xml_attribute_chapter, doc);
			name = findAttributeValue("QSAR_Miscelaneous",xml_attribute_name, doc);
			value = chapter+dot+ name;
			row63.createCell(column_chapters).setCellValue(value);
			row63.getCell(column_chapters).setCellStyle(cellStyle_chapter);

		    sheet_qmrf.addMergedRegion(new Region(row_number,(short)0,row_number+3,(short)0));

		    row63.createCell(column_sections);
		    row63.getCell(column_sections).setCellStyle(cellStyle_yellow_subchapter_first);

		    row63.createCell(column_help);
		    row63.getCell(column_help).setCellStyle(cellStyle_yellow_help_text_first);
		    row63.createCell(column_content);
		    row63.getCell(column_content).setCellStyle(cellStyle_dynamic_text_first);
		    row_number++;
		    
		    //row64
		    HSSFRow row64 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("comments",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("comments",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row64.createCell(column_sections).setCellValue(value);
			row64.getCell(column_sections).setCellStyle(cellStyle1);

		    value = findAttributeValue("comments",xml_attribute_help,doc);
		    row64.createCell(column_help).setCellValue(value);
		    row64.getCell(column_help).setCellStyle(cellStyle_help_text);

		    value = findNodeValue("comments",doc);
		    row64.createCell(column_content).setCellValue(value);
		    row64.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row65
		    HSSFRow row65 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("bibliography",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("bibliography",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row65.createCell(column_sections).setCellValue(value);
			row65.getCell(column_sections).setCellStyle(cellStyle1);

		    value = findAttributeValue("bibliography",xml_attribute_help,doc);
		    row65.createCell(column_help).setCellValue(value);
		    row65.getCell(column_help).setCellStyle(cellStyle_help_text);

		    
		    cell = row65.createCell(column_content);
		    cell.setCellValue(listNodeAttributes(doc,"bibliography","publication_ref", "publication", att_reference,true));
		    row65.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row66
		    HSSFRow row66 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("attachments",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("attachments",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row66.createCell(column_sections).setCellValue(value);
			row66.getCell(column_sections).setCellStyle(cellStyle1);


		    value = findAttributeValue("attachments",xml_attribute_help,doc);
		    row66.createCell(column_help).setCellValue(value);
		    row66.getCell(column_help).setCellStyle(cellStyle_help_text);

		    StringBuffer b = new StringBuffer();
		    b.append("Training set\n");
		    b.append(listAttachments(doc,"attachment_training_data"));
		    b.append("Test set\n");
		    b.append(listAttachments(doc,"attachment_validation_data"));
		    b.append("Supporting information\n");
		    b.append(listAttachments(doc,"attachment_documents"));
		    row66.createCell(column_content).setCellValue(b.toString());
		    row66.getCell(column_content).setCellStyle(cellStyle_dynamic_text);

		    //row67
		    HSSFRow row67 = sheet_qmrf.createRow(row_number);
		    chapter = findAttributeValue("QMRF_Summary",xml_attribute_chapter, doc);
			name = findAttributeValue("QMRF_Summary",xml_attribute_name, doc);
			value = chapter+dot+ name;


		    row67.createCell(column_chapters).setCellValue(value);
		    row67.getCell(column_chapters).setCellStyle(green_cellStyle_chapter_last);

		    row67.createCell(column_sections);
		    row67.getCell(column_sections).setCellStyle(green_cellStyle_subchapter_first);
		    row67.createCell(column_help);
		    row67.getCell(column_help).setCellStyle(cellStyle_help_text_green_first);
		    row67.createCell(column_content);
		    row67.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green_first);


		    sheet_qmrf.addMergedRegion(new Region(row_number,column_chapters,row_number+4,column_chapters));
		    row_number++;
		    
		    //row68
		    HSSFRow row68 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue(xml_QMRF_number,xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue(xml_QMRF_number,xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row68.createCell(column_sections).setCellValue(value);
			row68.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue(xml_QMRF_number,xml_attribute_help,doc);
		    row68.createCell(column_help).setCellValue(value);
		    row68.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    value = findNodeValue(xml_QMRF_number,doc);
		    row68.createCell(column_content).setCellValue(value);
		    row68.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row69
		    HSSFRow row69 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("date_publication",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("date_publication",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row69.createCell(column_sections).setCellValue(value);
			row69.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("date_publication",xml_attribute_help,doc);
		    row69.createCell(column_help).setCellValue(value);
		    row69.getCell(column_help).setCellStyle(cellStyle_help_text_green);

		    value = findNodeValue("date_publication",doc);
		    row69.createCell(column_content).setCellValue(value);
		    row69.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row70
		    HSSFRow row70 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("keywords",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("keywords",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row70.createCell(column_sections).setCellValue(value);
			row70.getCell(column_sections).setCellStyle(green_cellStyle_subchapter);
		    value = findAttributeValue("keywords",xml_attribute_help,doc);
		    row70.createCell(column_help).setCellValue(value);
		    row70.getCell(column_help).setCellStyle(cellStyle_help_text_green);
		    value = findNodeValue("keywords",doc);
		    row70.createCell(column_content).setCellValue(value);
		    row70.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green);

		    //row71
		    HSSFRow row71 = sheet_qmrf.createRow(row_number);row_number++;
		    sub_chapter_number = findAttributeValue("summary_comments",xml_attribute_chapter, doc);
			sub_chapter_name = findAttributeValue("summary_comments",xml_attribute_name, doc);
			value = space+sub_chapter_number+dot+ sub_chapter_name+doubledot;
			row71.createCell(column_sections).setCellValue(value);
			row71.getCell(column_sections).setCellStyle(green_cellStyle_subchapter_last);
		    value = findAttributeValue("summary_comments",xml_attribute_help,doc);
		    row71.createCell(column_help).setCellValue(value);
		    row71.getCell(column_help).setCellStyle(cellStyle_help_text_green_last);

		    value = findNodeValue("summary_comments",doc);
		    row71.createCell(column_content).setCellValue(value);
		    
		    row71.getCell(column_content).setCellStyle(cellStyle_dynamic_text_green_last);

		    sheet_qmrf.autoSizeColumn(column_chapters);
		    sheet_qmrf.autoSizeColumn(column_sections);
		    sheet_qmrf.autoSizeColumn(column_help);
		    sheet_qmrf.autoSizeColumn(column_content);

		    wb.write(excel);
		    excel.close();		

/*		
			catch (SAXParseException err) {
	        System.out.println ("** Parsing error" + ", line "
	             + err.getLineNumber () + ", uri " + err.getSystemId ());
	        System.out.println(space + err.getMessage ());

	        }catch (SAXException e) {
	        Exception x = e.getException ();
	        ((x == null) ? e : x).printStackTrace ();

	        }catch (Throwable t) {
	        t.printStackTrace ();
	        }
*/	        
	
	}



	class  BoldProperty{
		 int StartValue;
		 int EndValue;
		 public BoldProperty(){}

	}


	protected HSSFCellStyle chapterStyle(HSSFWorkbook wb, HSSFFont font, boolean first, boolean last, short colorIndex) {

	    //green cells chapter
	    HSSFCellStyle green_cellStyle_chapter = wb.createCellStyle();
	    green_cellStyle_chapter.setAlignment(HSSFCellStyle.ALIGN_LEFT);
	    green_cellStyle_chapter.setFillForegroundColor(colorIndex);
	    green_cellStyle_chapter.setFillPattern((short)1);
	    if (first) green_cellStyle_chapter.setBorderTop(HSSFCellStyle.BORDER_THIN);
	    else green_cellStyle_chapter.setBorderTop(HSSFCellStyle.BORDER_DOTTED);

	    if (last)
	    	green_cellStyle_chapter.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	    else
	    	green_cellStyle_chapter.setBorderBottom(HSSFCellStyle.BORDER_DOTTED);
	    green_cellStyle_chapter.setBorderRight(HSSFCellStyle.BORDER_DOTTED);
	    
	    green_cellStyle_chapter.setWrapText( true );
	    green_cellStyle_chapter.setFont(font);
	    green_cellStyle_chapter.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP );
	    return green_cellStyle_chapter;
	}
	
	
	protected HSSFCellStyle subchapterStyle(HSSFWorkbook wb, HSSFFont font,boolean first, boolean last, short colorIndex) {
	    HSSFCellStyle green_cellStyle_subchapter = wb.createCellStyle();
	    green_cellStyle_subchapter.setAlignment(HSSFCellStyle.ALIGN_LEFT);
	    green_cellStyle_subchapter.setFillForegroundColor(colorIndex);
	    green_cellStyle_subchapter.setFillPattern((short)1);
	    if (first)
	    	green_cellStyle_subchapter.setBorderTop(HSSFCellStyle.BORDER_THIN);
	    else
	    	green_cellStyle_subchapter.setBorderTop(HSSFCellStyle.BORDER_DOTTED);
	    if (last)
	    	green_cellStyle_subchapter.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	    else
	    	green_cellStyle_subchapter.setBorderBottom(HSSFCellStyle.BORDER_DOTTED);
	    green_cellStyle_subchapter.setBorderRight(HSSFCellStyle.BORDER_DOTTED);
	    green_cellStyle_subchapter.setBorderLeft(HSSFCellStyle.BORDER_DOTTED);
	    green_cellStyle_subchapter.setWrapText( true );
	    green_cellStyle_subchapter.setFont(font);
	    green_cellStyle_subchapter.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP );
	    HSSFPalette palette = wb.getCustomPalette();
	    palette.setColorAtIndex(HSSFColor.GREEN.index,
	    		color_even_sections[0],color_even_sections[1],color_even_sections[2]
	    );
	    return green_cellStyle_subchapter;
	}	
	protected HSSFCellStyle textStyle(HSSFWorkbook wb, HSSFFont font,boolean first, boolean last, short colorIndex) {
	    //yellow cells  dynamic text
	    HSSFCellStyle cellStyle_dynamic_text = wb.createCellStyle();
	    cellStyle_dynamic_text.setAlignment(HSSFCellStyle.ALIGN_LEFT);
	    cellStyle_dynamic_text.setFillForegroundColor(colorIndex);
	    cellStyle_dynamic_text.setFillPattern((short)1);
	    if (first)
	    	cellStyle_dynamic_text.setBorderTop(HSSFCellStyle.BORDER_THIN);
	    else
	    	cellStyle_dynamic_text.setBorderTop(HSSFCellStyle.BORDER_DOTTED);
	    cellStyle_dynamic_text.setBorderRight(HSSFCellStyle.BORDER_THIN);
	    
	    
	    if (last) {
	    	cellStyle_dynamic_text.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	    	cellStyle_dynamic_text.setBorderLeft(HSSFCellStyle.BORDER_DOTTED);
	    }else
	    	cellStyle_dynamic_text.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	    cellStyle_dynamic_text.setWrapText( true );
	    cellStyle_dynamic_text.setFont(font);
	    cellStyle_dynamic_text.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP );
	    return cellStyle_dynamic_text;
	}
	protected HSSFCellStyle helpStyle(HSSFWorkbook wb, HSSFFont font,boolean first, boolean last, short colorIndex) {
	    HSSFCellStyle cellStyle_help_text = wb.createCellStyle();
	    cellStyle_help_text.setAlignment(HSSFCellStyle.ALIGN_LEFT);
	    cellStyle_help_text.setFillForegroundColor(colorIndex);
	    cellStyle_help_text.setFillPattern((short)1);
	    if (first)
	    	cellStyle_help_text.setBorderTop(HSSFCellStyle.BORDER_THIN);
	    else	
	    	cellStyle_help_text.setBorderTop(HSSFCellStyle.BORDER_DOTTED);
	    if (last)
	    	cellStyle_help_text.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	    cellStyle_help_text.setBorderRight(HSSFCellStyle.BORDER_THIN);
	    cellStyle_help_text.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	    cellStyle_help_text.setWrapText( true );
	    cellStyle_help_text.setFont(font);
	    cellStyle_help_text.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP );
	    return cellStyle_help_text;
	}
	
	protected HSSFCellStyle headerStyle(HSSFWorkbook wb, HSSFFont font,short align, boolean left, boolean top,boolean right, boolean bottom, short colorIndex) {
	    HSSFCellStyle cellStyle_header_text = wb.createCellStyle();
	    cellStyle_header_text.setAlignment(align);
	    cellStyle_header_text.setFillForegroundColor(colorIndex);
	    cellStyle_header_text.setFillPattern((short)1);
	    
	    if (top)
	    	cellStyle_header_text.setBorderTop(HSSFCellStyle.BORDER_THIN);
	    if (bottom)
	    	cellStyle_header_text.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	    if (right)
	    	cellStyle_header_text.setBorderRight(HSSFCellStyle.BORDER_THIN);
	    if (left)
	    	cellStyle_header_text.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	    cellStyle_header_text.setWrapText( true );
	    cellStyle_header_text.setFont(font);
	    cellStyle_header_text.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP );
	    
	    return cellStyle_header_text;
	}	
	protected void createReadmeSheet(HSSFWorkbook wb, HSSFSheet sheet_readme ,Document doc) {
		final String  readme_font = "Times New Roman";
	    HSSFFont top_text = wb.createFont();
	    top_text.setFontName(readme_font);
	    top_text.setFontHeightInPoints((short)18);
	    top_text.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	    
	    HSSFFont header_text = wb.createFont();
	    header_text.setFontName(readme_font);
	    header_text.setFontHeightInPoints((short)16);
	    header_text.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	    
	    HSSFFont text = wb.createFont();
	    text.setFontName(readme_font);
	    text.setFontHeightInPoints((short)12);
	    
	    HSSFPalette palette = wb.getCustomPalette();
	
	    HSSFCellStyle topCell = headerStyle(wb,top_text,HSSFCellStyle.ALIGN_CENTER,true,true,true,true,HSSFColor.WHITE.index);
	    HSSFCellStyle headerCell = headerStyle(wb,header_text,HSSFCellStyle.ALIGN_LEFT,true,true,true,false,HSSFColor.WHITE.index);
	    HSSFCellStyle textCell = headerStyle(wb,text,HSSFCellStyle.ALIGN_JUSTIFY,true,false,true,false,HSSFColor.WHITE.index);
	    HSSFCellStyle textCellLast = headerStyle(wb,text,HSSFCellStyle.ALIGN_JUSTIFY,true,false,true,true,HSSFColor.WHITE.index);
	    
	    
        //QMRF attributes
		 String[][] details = {
        		{"Name:","name"},
        		{"Version:","version"},
				 {"Release date:","date"},
				 {"Authors:","author"},
				 {"Contact Details:","contact"},
				 {"E-mail:","email"},
				 {"Website:","url"}
		 };
		 
		 int row_number = 0;
		 short column = 1;
		 HSSFRow row = sheet_readme.createRow(row_number); row_number++;
		 
		 row = sheet_readme.createRow(row_number); row_number++;
		 HSSFCell cell = row.createCell(column);
		 cell.setCellStyle(topCell);
		 cell.setCellValue("(Q)SAR MODEL REPORTING FORMAT (QMRF) v."+findAttributeValue("QMRF",details[1][1], doc));
		 
		 row = sheet_readme.createRow(row_number); row_number++;
		 
		 row = sheet_readme.createRow(row_number); row_number++;
		 cell = row.createCell(column);
		 cell.setCellStyle(headerCell);
		 cell.setCellValue("Details");
		 
		 for (int i=0; i < 7; i++) {
			 row = sheet_readme.createRow(row_number); row_number++;
			 cell = row.createCell(column);
			 if (i<6)
				 cell.setCellStyle(textCell);
			 else
				 cell.setCellStyle(textCellLast);
			 ;
			 try {
				 cell.setCellValue(details[i][0] + findAttributeValue("QMRF",details[i][1], doc));
			 } catch (Exception x) {
				 x.printStackTrace();
				 cell.setCellValue(details[i][0]);
			 }
		 }
		 
		 row = sheet_readme.createRow(row_number); row_number++;
		 
		 row = sheet_readme.createRow(row_number); row_number++;
		 cell = row.createCell(column);
		 cell.setCellStyle(headerCell);
		 cell.setCellValue("Background");		

		 row = sheet_readme.createRow(row_number); row_number++;
		 cell = row.createCell(column);
		 cell.setCellStyle(textCellLast);
		 cell.setCellValue("The set of information that you provide will be used to facilitate regulatory considerations of (Q)SARs. For this purpose, the structure of the QMRF is devised to reflect as much as possible the OECD principles for the validation, for regulatory purposes, of (Q)SAR models. \nYou are invited to consult the OECD \"Guidance Document on the Validation of (Quantitative) Structure-Activity Relationship Models\" that can aid you in filling in a number of fields of the QMRF (visit the following ECB webpage for downloading the proper documentation: http://ecb.jrc.it/qsar/background/index.php?c=OECD");
		 
		 sheet_readme.setColumnWidth(column,(short)10000);
		 sheet_readme.setDefaultRowHeight((short)50);
		 

	}
	protected void createEndpointsSheet(HSSFWorkbook wb, HSSFSheet sheet_endpoints ,Document doc) {
		int row_number = 0;
		short A = 0;
		short B = 1;
		short C = 2;
		HSSFRow row = sheet_endpoints.createRow(row_number); row_number++;
		HSSFCell cell = row.createCell(B);
		//cell.setCellStyle(headerCell);
		cell.setCellValue("Select Endpoint from list");

		
		final String[] attr = {
				"group",
				"subgroup",
				"name"
		};
		NodeList endpoints = doc.getElementsByTagName("endpoints_catalog");
		for (int i=0; i < endpoints.getLength(); i++) {
			Node endpoint = endpoints.item(i);
			if (endpoint instanceof Element) {
				row = sheet_endpoints.createRow(row_number); row_number++;
				for (short j=0; j < attr.length; j++) {
					cell = row.createCell(j);
					cell.setCellValue(((Element)endpoint).getAttribute(attr[j]));
				}	
			}
		}
		sheet_endpoints.autoSizeColumn(A);
		sheet_endpoints.autoSizeColumn(B);
		sheet_endpoints.autoSizeColumn(C);
	}

}
