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

package ambit2.taglibs;

import java.io.IOException;
import java.io.StringReader;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.xml.sax.InputSource;

import ambit2.data.qmrf.Qmrf_Xml_Excel;
import ambit2.data.qmrf.Qmrf_Xml_Pdf;

public class QMRFExportTag extends SimpleTagSupport {
	protected String xml;
	protected String type = "pdf";
	protected String fontURL = "times.ttf";
	
	@Override
	public void doTag() throws JspException, IOException {
		try {
			PageContext pageContext = (PageContext) getJspContext();
			if ("xls".equals(getType())) {
				
				Qmrf_Xml_Excel x = new Qmrf_Xml_Excel();
	   			pageContext.getResponse().setContentType("application/vnd.ms-excel");
	   			x.xml2excel(new InputSource(new StringReader(xml)),pageContext.getResponse().getOutputStream());
        	} else {
			
        	Qmrf_Xml_Pdf x = new Qmrf_Xml_Pdf(fontURL);
   			pageContext.getResponse().setContentType("application/pdf");
   			x.xml2pdf(xml,pageContext.getResponse().getOutputStream());
        	}
        }
        catch (Exception x)
        {
            throw new JspException(x.toString());
        }


	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getFontURL() {
		return fontURL;
	}

	public void setFontURL(String fontURL) {
		this.fontURL = fontURL;
	}
}


