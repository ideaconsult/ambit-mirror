/* AmbitRegistrationServlet.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-12 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ambit2.data.AmbitUser;

/**
 * User registration. Not completed and not used at this moment.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-12
 */
public class AmbitRegistrationServlet extends AmbitServlet {
    protected static Object[][] fields = {
        {"title",new Boolean(false)},
        {"firstname",new Boolean(true)},
        {"lastname",new Boolean(true)},
        {"webpage",new Boolean(false)},
        {"affiliation",new Boolean(true)},
        {"address",new Boolean(false)},
        {"city",new Boolean(true)},
        {"country",new Boolean(true)},
        {"email",new Boolean(true)},
        {"password",new Boolean(true)},               
    };
    /**
     * 
     */
    public AmbitRegistrationServlet() {
        super();
    }
    /* (non-Javadoc)
     * @see ambit2.servlets.AmbitServlet#process(java.sql.Connection, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void process(Connection connection, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        super.process(connection, request, response);
        Object parameter = null;
        StringBuffer b = new StringBuffer();
        b.append("/Registration");
        char d = '?';
        boolean redirect = false;
        AmbitUser user = new AmbitUser();
        for (int i=0;i< fields.length;i++) {
            parameter = request.getParameter(fields[i][0].toString());
            if ( ((parameter == null) || parameter.equals("")) &&
                    ((Boolean)fields[i][1]).booleanValue()) {
                //required value missing
                redirect = true;
            } else {
                b.append(d);
                b.append(fields[i][0].toString());
                b.append("=");
                if (parameter == null) parameter = "";
                b.append(parameter.toString());
                switch (i) {
                case 0: { user.setTitle(parameter.toString()); break; }
                case 1: { user.setFirstName(parameter.toString()); break; }
                case 2: { user.setLastName(parameter.toString()); break; }
                case 3: { user.setWww(parameter.toString()); break; }
                case 4: { user.setAffiliation(parameter.toString()); break; }
                case 5: { user.setAddress(parameter.toString()); break; }
                case 6: { user.setCity(parameter.toString()); break; }
                case 7: { user.setCountry(parameter.toString()); break; }
                case 8: { user.setEmail(parameter.toString()); break; }
                case 9: { user.setPassword(parameter.toString()); break; }
                }
            }
        }
        if (redirect) {
            response.sendRedirect(b.toString());
        }    
        //TODO  send confirmation e-mail; write to database
        //sendMail(user);
    }
}
