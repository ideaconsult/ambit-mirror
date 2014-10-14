package ambit2.rest.structure.smirks;

import java.io.IOException;
import java.io.Writer;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.rest.AmbitResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.structure.diagram.AbstractDepict;
import dk.smartcyp.smirks.SMARTCYPReaction;

public class ReactionDepict extends AbstractDepict {
	public static final String resource = "/reaction";
	
	@Override
	protected String getTitle(Reference ref, String... smiles) throws ResourceException {
		StringBuilder b = new StringBuilder();
		b.append("<table><tr>");
		b.append("<td>");
		String visibleSmiles = smiles==null?"":(smiles.length==0?"":smiles[0]);
		b.append(AmbitResource.printWidget(
				String.format("<a href='%s/reactant?search=%s'>%s</a>",
						ref.getHierarchicalPart(),Reference.encode(visibleSmiles),"Reactant"),
						
						
				String.format("<strong>%s</strong><img id='reactant' src='%s/reactant?search=%s' alt='%s' title='%s' onError=\"hideDiv('reactant')\">",
						visibleSmiles,
						ref.getHierarchicalPart(),
						Reference.encode(visibleSmiles),
						visibleSmiles,visibleSmiles),
				"depictBox"						
					));
		b.append("</td><td>");
		b.append(AmbitResource.printWidget(
				String.format("<a href='%s/product?search=%s&smirks=%s'>%s</a>",
						ref.getHierarchicalPart(),Reference.encode(smiles[0]),Reference.encode(smirks),"Product"),
						
				String.format("<strong>Reaction SMIRKS %s</strong><img id='product' src='%s/product?search=%s&smirks=%s' alt='%s' title='%s' onError=\"hideDiv('product')\">",
						smirks==null?"":smirks,
						ref.getHierarchicalPart(),
						Reference.encode(smiles[0]),
						smirks==null?"":Reference.encode(smirks),
						smirks,smirks),
				"depictBox"		
					));
		b.append("</td>");
		b.append("</tr></table>");
		return b.toString();
	}
	
	public void writeSearchForm(Writer w,String title,Request request ,String meta,Method method,Form params) throws IOException {
		Reference baseReference = request==null?null:request.getRootRef();
		w.write("<table width='100%' bgcolor='#ffffff'>");
		w.write("<tr>");
		w.write("<td align='left' width='256px'>");
		w.write(String.format("<a href=\"http://ambit.sourceforge.net/intro.html\"><img src='%s/images/ambit-logo.png' width='256px' alt='%s' title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));
		w.write("</td>");
		w.write("<td align='center'>");
		String query_smiles = "";
		try {
			Form form = getParams(params,request);
			if ((form != null) && (form.size()>0))
				query_smiles = form.getFirstValue(QueryResource.search_param);
			else query_smiles = null;
		} catch (Exception x) {
			query_smiles = "";
		}
		
		
		w.write(String.format("<form action='' method='%s'>\n",method));
		w.write("<table width='100%'>");
		w.write("<tr>");
		w.write(String.format("<th width='20%%'><label for='%s'>%s</label>",QueryResource.search_param,"SMILES or InChI"));
		w.write(String.format("&nbsp;<input type='image' src=\"%s/images/edit.png\" title='Draw molecule' onClick='startEditor(\"%s\");'>",
				request.getRootRef(),request.getRootRef()));
		w.write("</th>");
		w.write("<td>");
		w.write(String.format("<input name='%s' size='70' value='%s'>\n",
				QueryResource.search_param,query_smiles==null?"":query_smiles));
		w.write("</td>");
		w.write("<td><input type='submit' value='Display'></td>");
		w.write("</tr>\n");

		w.write("<tr>");
		w.write(String.format("<th><label for='%s'>%s</label></th>","smirks","Reaction (SMIRKS)"));
		w.write("<td>");
			w.write(String.format("<input name='%s' size='70' value='%s' title='Applies the reaction, specified by SMIRKS'>",
					"smirks",getSmirks()==null?"":getSmirks()));
		w.write("</td>");			
		w.write("<td>&nbsp;</td></tr>\n");
		
		w.write("</tr><tr>");
		w.write(String.format("<th valign='top'>%s</th>","Example reactions"));
		w.write("<td colspan='1' bgcolor='#FDFDFD'>");
		//w.write("<select name='smirks'>");
		//w.write("<option  value=\"\" %s selected='selected'></option>");
		for (SMARTCYPReaction reaction : SMARTCYPReaction.values()) {
			boolean selected = reaction.getSMIRKS().equals(smirks);
			w.write(String.format("<i><a href='?search=%s&smirks=%s' title='%s'>%s%s%s</a></i>&nbsp;&nbsp;", 
					Reference.encode(smiles[0]),
					Reference.encode(reaction.getSMIRKS()),
					reaction.getSMIRKS(),
					selected?"[":"",
					reaction.toString(),
					selected?"]":""
					));
		}
		w.write("</font>");
		w.write("</td></tr>");
		
		w.write("</table>");
		w.write("</form>\n");
	
		w.write("</td>");
		w.write("<td align='left' valign='bottom' width='256px'>");
		w.write(AmbitResource.disclaimer);

		w.write("</td>");
		w.write("</tr></table>");		
		
		
		
		w.write("<hr>");
		
	}	
}
