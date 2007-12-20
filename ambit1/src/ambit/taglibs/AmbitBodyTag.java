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

package ambit.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class AmbitBodyTag extends BodyTagSupport {

	@Override
	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}
	@Override
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}
	@Override
	public void doInitBody() throws JspException {

		super.doInitBody();
	}
	@Override
	public int doAfterBody() throws JspException {
			//		 Get the current body content
        BodyContent body = getBodyContent();

        try
        {
      		// Ask the body content to write itself out to the response.
            body.writeOut(body.getEnclosingWriter());
        }
        catch (IOException exc)
        {
            throw new JspException(exc.toString());
        }
        	// Tell the JSP engine that the body content has been evaluated.
        return SKIP_BODY;

	}
}


