/*
Copyright (C) 2005-2006  

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

package ambit.data.qmrf;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class QMRFConverter {
	public enum Mode {chapter,title, text, reference, answer, dataset, attachments};
    public enum ScriptMode {normal, subscript, superscript};
	protected final String xml_attribute_chapter = "chapter";
	protected final String xml_attribute_name = "name";
	protected final String xml_attribute_help = "help";
	
	protected final String xml_QSAR_title = "QSAR_title";
	protected final String xml_QSAR_models = "QSAR_models";
	protected final String xml_QMRF_number = "QMRF_number";
	
    final String[] att_author = {"name","affiliation","contact","email","url"};
    final String[] att_software = {"name","description","contact","url"};
    final String[] att_reference   = {"title","url"};
    final String[] att_descriptor = {"name","units","description"};
    final String[] att_endpoint = {"group","subgroup","name"};
    final String[] att_algorithm = {"definition","description"};
	protected final String dot = ". ";
	protected final String space = " ";
	protected final String doubledot = ":";
	

	
	Object[][] subchapters = {
			{"QSAR_identifier","QSAR_identifier",Mode.chapter},
			{"QSAR_title","QSAR_title",Mode.title},
			{"QSAR_title","QSAR_title_text",Mode.text},

			{"QSAR_models","QSAR_models",Mode.title},
			{"QSAR_models","QSAR_models_text",Mode.text},
			
			{"QSAR_software","QSAR_software",Mode.title},
			{"QSAR_software","QSAR_software_text",Mode.reference,"software_ref","software",att_software, new Boolean(true)},

			
			{"QSAR_General_information","QSAR_General_information",Mode.chapter},
			{"qmrf_date","qmrf_date",Mode.title},
			{"qmrf_date","qmrf_date_text",Mode.text},	
			
			{"qmrf_authors","qmrf_authors",Mode.title},
			{"qmrf_authors","qmrf_authors_ref",Mode.reference,"author_ref","author",att_author, new Boolean(true)},	

			{"qmrf_date_revision","qmrf_date_revision",Mode.title},
			{"qmrf_date_revision","qmrf_date_revision_text",Mode.text},	

			{"qmrf_revision","qmrf_revision",Mode.title},
			{"qmrf_revision","qmrf_revision_text",Mode.text},	

			{"model_authors","model_authors",Mode.title},
			{"model_authors","model_authors_ref",Mode.reference,"author_ref","author",att_author, new Boolean(true)},	
			
			{"model_date","model_date",Mode.title},
			{"model_date","model_date_text",Mode.text},	
			
			{"references","references",Mode.title},
			{"references","qmrf_date_text",Mode.reference,"publication_ref","publication",att_reference, new Boolean(true)},	

			{"info_availability","info_availability",Mode.title},
			{"info_availability","info_availability_text",Mode.text},							
			
			{"related_models","related_models",Mode.title},
			{"related_models","related_models_text",Mode.text},	
			
			
			{"QSAR_Endpoint","QSAR_Endpoint",Mode.chapter},
			
			{"model_species","model_species",Mode.title},
			{"model_species","model_species_text",Mode.text},	

			{"model_endpoint","model_endpoint",Mode.title},
			{"model_endpoint","model_endpoint_text",Mode.reference,"endpoint_ref","endpoint",att_endpoint, new Boolean(true)},						
			
			{"endpoint_comments","endpoint_comments",Mode.title},
			{"endpoint_comments","endpoint_comments_text",Mode.text},	
			
			{"endpoint_units","endpoint_units",Mode.title},
			{"endpoint_units","endpoint_units_text",Mode.text},	

			{"endpoint_variable","endpoint_variable",Mode.title},
			{"endpoint_variable","endpoint_variable_text",Mode.text},							
			
			{"endpoint_protocol","endpoint_protocol",Mode.title},
			{"endpoint_protocol","endpoint_protocol_text",Mode.text},							

			{"endpoint_data_quality","endpoint_data_quality",Mode.title},
			{"endpoint_data_quality","endpoint_data_quality_text",Mode.text},							
			
			{"QSAR_Algorithm","QSAR_Algorithm",Mode.chapter},
			
			{"algorithm_type","algorithm_type",Mode.title},
			{"algorithm_type","algorithm_type_text",Mode.text},							
			
			{"algorithm_explicit","algorithm_explicit",Mode.title},
			{"algorithm_explicit","algorithm_explicit_text",Mode.reference,"algorithm_ref","algorithm",att_algorithm, new Boolean(false)},

			{"equation","equation",Mode.text},
			
			{"algorithm_comments","algorithm_comments",Mode.title},
			{"algorithm_comments","algorithm_comments_text",Mode.text},							

			{"algorithms_descriptors","algorithms_descriptors",Mode.title},
			{"algorithms_descriptors","algorithms_descriptors_text",Mode.reference,"descriptor_ref","descriptor",att_descriptor, new Boolean(true)},							
			
			{"descriptors_selection","descriptors_selection",Mode.title},
			{"descriptors_selection","descriptors_selection_text",Mode.text},							

			{"descriptors_generation","descriptors_generation",Mode.title},
			{"descriptors_generation","descriptors_generation_text",Mode.text},							

			{"descriptors_generation_software","descriptors_generation_software",Mode.title},
			{"descriptors_generation_software","descriptors_generation_software_text",Mode.reference,"software_ref","software",att_software, new Boolean(false)},							

			{"descriptors_chemicals_ratio","descriptors_chemicals_ratio",Mode.title},
			{"descriptors_chemicals_ratio","descriptors_chemicals_ratio_text",Mode.text},
			
			{"QSAR_Applicability_domain","QSAR_Applicability_domain",Mode.chapter},
			
			{"app_domain_description","app_domain_description",Mode.title},
			{"app_domain_description","app_domain_description_text",Mode.text},							
			
			{"app_domain_method","app_domain_method",Mode.title},
			{"app_domain_method","app_domain_method_text",Mode.text},							

			{"app_domain_software","app_domain_software",Mode.title},
			{"app_domain_software","app_domain_software_text",Mode.reference,"software_ref","software",att_software, new Boolean(false)},	

			{"applicability_limits","applicability_limits",Mode.title},
			{"applicability_limits","applicability_limits_text",Mode.text},							

			{"QSAR_Robustness","QSAR_Robustness",Mode.chapter},
			
			{"training_set_availability","training_set_availability",Mode.title},
			{"training_set_availability","training_set_availability_text",Mode.answer},							
			
			{"training_set_data","training_set_data",Mode.title},
			{"training_set_data","training_set_data_text",Mode.text},
			{"training_set_data","training_set_data_dataset",Mode.dataset},

			{"training_set_descriptors","training_set_descriptors",Mode.title},
			{"training_set_descriptors","training_set_descriptors_text",Mode.answer},
			
			{"dependent_var_availability","dependent_var_availability",Mode.title},
			{"dependent_var_availability","dependent_var_availability_text",Mode.answer},							

			{"other_info","other_info",Mode.title},
			{"other_info","other_info_text",Mode.text},
			
			{"preprocessing","preprocessing",Mode.title},
			{"preprocessing","preprocessing_text",Mode.text},							

			{"goodness_of_fit","goodness_of_fit",Mode.title},
			{"goodness_of_fit","goodness_of_fit_text",Mode.text},
			
			{"loo","loo",Mode.title},
			{"loo","loo_text",Mode.text},							

			{"lmo","lmo",Mode.title},
			{"lmo","lmo_text",Mode.text},
			
			{"yscrambling","yscrambling",Mode.title},
			{"yscrambling","yscrambling_text",Mode.text},
			
			{"bootstrap","bootstrap",Mode.title},
			{"bootstrap","bootstrap_text",Mode.text},
			
			{"other_statistics","other_statistics",Mode.title},
			{"other_statistics","other_statistics_text",Mode.text},
			
			{"QSAR_Predictivity","QSAR_Predictivity",Mode.chapter},
			
			{"validation_set_availability","validation_set_availability",Mode.title},
			{"validation_set_availability","validation_set_availability_text",Mode.answer},							
			
			{"validation_set_data","validation_set_data",Mode.title},
			{"validation_set_data","validation_set_data_text",Mode.text},
			{"validation_set_data","validation_set_data_dataset",Mode.dataset},							

			{"validation_set_descriptors","validation_set_descriptors",Mode.title},
			{"validation_set_descriptors","validation_set_descriptors_text",Mode.answer},
			
			{"validation_dependent_var_availability","validation_dependent_var_availability",Mode.title},
			{"validation_dependent_var_availability","validation_dependent_var_availability_text",Mode.answer},	
			
			{"validation_other_info","validation_other_info",Mode.title},
			{"validation_other_info","validation_other_info_text",Mode.text},
			
			{"experimental_design","experimental_design",Mode.title},
			{"experimental_design","experimental_design_text",Mode.text},		
			
			{"validation_predictivity","validation_predictivity",Mode.title},
			{"validation_predictivity","validation_predictivity_text",Mode.text},	
			
			{"validation_assessment","validation_assessment",Mode.title},
			{"validation_assessment","validation_assessment_text",Mode.text},	
			
			{"validation_comments","validation_comments",Mode.title},
			{"validation_comments","validation_comments_text",Mode.text},	
			
			{"QSAR_Interpretation","QSAR_Interpretation",Mode.chapter},
			
			{"mechanistic_basis","mechanistic_basis",Mode.title},
			{"mechanistic_basis","mechanistic_basis_text",Mode.text},	
			
			{"mechanistic_basis_comments","mechanistic_basis_comments",Mode.title},
			{"mechanistic_basis_comments","mechanistic_basis_comments_text",Mode.text},	
			
			{"mechanistic_basis_info","mechanistic_basis_info",Mode.title},
			{"mechanistic_basis_info","mechanistic_basis_info_text",Mode.text},	
			
			{"QSAR_Miscelaneous","QSAR_Miscelaneous",Mode.chapter},
			
			{"comments","comments",Mode.title},
			{"comments","comments_text",Mode.text},	
			
			{"bibliography","bibliography",Mode.title},
			{"bibliography","bibliography_text",Mode.reference,"publication_ref", "publication", att_reference, new Boolean(true)},	
			
			{"attachments","attachments",Mode.title},
			{"attachments","attachments_text",Mode.attachments},	
			
			{"QMRF_Summary","QMRF_Summary",Mode.chapter},			
			
			{xml_QMRF_number,xml_QMRF_number,Mode.title},
			{xml_QMRF_number,xml_QMRF_number+"_text",Mode.text},	
			
			{"date_publication","date_publication",Mode.title},
			{"date_publication","date_publication_text",Mode.text},	
			
			{"keywords","keywords",Mode.title},
			{"keywords","keywords_text",Mode.text},	
			
			{"summary_comments","summary_comments",Mode.title},
			{"summary_comments","summary_comments_text",Mode.text},							
	};
	
	
	protected static Pattern HTML_tags = Pattern.compile("(<html>|</html>|<head>|</head>|<title>|</title>|<body>|</body>|<b>|</b>|<i>|</i>|\t)");
	protected static Pattern ptag = Pattern.compile("(<p>|<p style=\"margin-top: 0\">|</p>)");
	protected static Pattern CRLF = Pattern.compile("(\n|\r)");

	public static String replaceNewLine(String text) {
		if (text == null) return text;
		String  newText = text;
		Matcher m = CRLF.matcher(newText);
		if (m.find()) {
			newText = m.replaceAll("");
		}
		return newText;

	}

	
	public static String replaceTags(String text) {
		if (text == null) return text;

		Matcher m = HTML_tags.matcher(text);
		String  newText = "";
		if (m.find()) {
			newText = m.replaceAll("");
		}
		else{
			newText = text;
		}
		m = CRLF.matcher(newText);
		if (m.find()) {
			newText = m.replaceAll("");
		}
		m = ptag.matcher(newText);
		if (m.find()) {
			newText = m.replaceAll("\n");
		}

		return newText.trim();


	}
	/*
	 * !ATTLIST attachments  chapter CDATA  #FIXED "9.3" name CDATA #FIXED "Supporting information" help CDATA "">
<!ELEMENT attachment_training_data (molecules*) >
<!ELEMENT attachment_validation_data (molecules*) >
<!ELEMENT attachment_documents (document*)>
	 */
	protected String listAttachments(Document doc, String attachments) {
		StringBuffer b = new StringBuffer();
		NodeList nodes = doc.getElementsByTagName(attachments);
		if (nodes.getLength() ==  0) b.append("N/A");
		else
			for (int i =0; i < nodes.getLength(); i++)
				if (nodes.item(i) instanceof Element) {
					NodeList attachment = nodes.item(i).getChildNodes();
					if (attachment.getLength() ==  0) b.append("N/A\n");
					else
						for (int j =0; j < attachment.getLength(); j++) 
							if (attachment.item(j) instanceof Element) {
								Element e = ((Element)attachment.item(j));
								
								b.append(org.apache.commons.lang.StringEscapeUtils.unescapeXml(e.getAttribute("description")));
								b.append('\t');
/*								
								b.append(e.getAttribute("filetype"));
								b.append(' ');
*/								
								b.append(org.apache.commons.lang.StringEscapeUtils.unescapeXml(e.getAttribute("url")));
								b.append('\n');
							}
				}
			return b.toString();
	}
	public String listNodeAttributes(Document doc,String name_node,String ref_name, String catalog_name, String[] attributes, boolean line) throws Exception {
	    ArrayList node_list = findNodeIDRef(name_node,ref_name, catalog_name, doc);

	    int r = 1;
	    boolean printnumbers = (node_list.size()>1) && line;
	    StringBuffer b = new StringBuffer();
	    boolean printnewline = false;
	    for (Iterator it = node_list.iterator(); it.hasNext(); ) {
	    	   if (printnewline) {
			   		b.append('\n');
			   		if (!line) b.append('\n');	    		   
	    	   }
	    	   Node node = (Node)it.next();
	    	   if (printnumbers) {
	    		   b.append('[');
	    		   b.append(r);
	    		   b.append(']');
	    	   }
	    	   printnewline = false;
		   		for (int i=0; i < attributes.length;i++) 
                    try {
    					String value = findAttributeValue(attributes[i],node).trim();
    					if (value.length()>0) {
    						b.append(value);
    						if (line) b.append(' ');
    						else
    							b.append('\n');
    						printnewline = true;
    					}
                    } catch (Exception x) {
                        System.out.println(attributes[i] + x.getMessage());
                        //x.printStackTrace();
                    }
		   		r++;
	    }
		return b.toString();
	}
	protected String findAttributeValue(String name_node,String name_attribute, org.w3c.dom.Document source) {

		source.getDocumentElement();
		NodeList objCatNodes = source.getElementsByTagName(name_node);
		Node objNode=objCatNodes.item(0);
		if (objNode == null) return null;
		NamedNodeMap objAttributes =  objNode.getAttributes();
		Node attribute = objAttributes.getNamedItem(name_attribute);
		if (attribute != null)
			return org.apache.commons.lang.StringEscapeUtils.unescapeXml(replaceTags(attribute.getNodeValue()).trim());
		else return null;
	}
	protected String findAttributeValue(String name_attribute, Node source) throws Exception {

		NamedNodeMap objAttributes =  source.getAttributes();
		Node attribute = objAttributes.getNamedItem(name_attribute);
		return org.apache.commons.lang.StringEscapeUtils.unescapeXml(replaceTags(attribute.getNodeValue()).trim());
	}
	
	public static ArrayList findNodeIDRef(String name_node,String ref_name, String catalog_name, org.w3c.dom.Document source) throws Exception {

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
						//Node name = objNodesAuthor.item(j).getAttributes().getNamedItem(xml_attribute_name);
						return_list.add(objNodesAuthor.item(j));
					}


				}

			}

		}
		return return_list;
	}
	protected String findAnswer(String name_node, org.w3c.dom.Document source) {
		StringBuffer b = new StringBuffer();
	    	b.append(findAttributeValue(name_node,"answer",source));
	    	b.append('\n');
	    	findNodeValue(name_node,source);
	    return b.toString();
	}
	
	protected String findDataAvailable(String name_node, org.w3c.dom.Document source) {
		final String[][] attr = 
		{{"cas","CAS RN"},
		 {"chemname","Chemical Name"},
		 {"smiles","Smiles"},
		 {"formula","Formula"},
		 {"inchi","INChI"},
		 {"mol","MOL file"}}; 
	    StringBuffer b = new StringBuffer();
	    for (int i=0; i < attr.length;i++) {
	    	b.append(attr[i][1]);
	    	b.append(":\t");
	    	b.append(findAttributeValue(name_node,attr[i][0],source));
	    	b.append('\n');
	    }	
	    return b.toString();
	}
	protected String findNodeValue(String name_node, org.w3c.dom.Document source) {
        NodeList n = source.getElementsByTagName(name_node);
        if (n.getLength() ==0) return "";
        String v = n.item(0).getTextContent();
        return org.apache.commons.lang.StringEscapeUtils.unescapeXml(replaceTags(v));
                //replaceTags(v));        
        /*
		NodeList objCatNodes = source.getElementsByTagName(name_node);
		if(objCatNodes.getLength() == 0) return "";
		Node objNode=objCatNodes.item(0);
		NodeList objNodes = objNode.getChildNodes();
		if(objNodes.getLength() == 0) return "";

		return org.apache.commons.lang.StringEscapeUtils.unescapeXml(replaceTags(objNodes.item(0).getNodeValue()).trim());
        */
	}
    public static Color Hex2Color(String clr) throws Exception {
        return  new Color(
                Integer.valueOf(clr.substring(0,2), 16),
                Integer.valueOf(clr.substring(2,4), 16),
                Integer.valueOf(clr.substring(4,6), 16));
    }	
}


