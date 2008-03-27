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

package ambit2.data.qmrf;

import java.io.InputStream;
import java.net.URL;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import ambit2.log.AmbitLogger;

public class QMRFSchemaResolver implements EntityResolver {
	protected AmbitLogger logger;
    protected String location;
    protected boolean ignoreSystemID = false;
    public QMRFSchemaResolver(String location, AmbitLogger logger) {
        super();
        this.location = location;
        this.logger = logger;
    }
    
    // This method is called whenever an external entity is accessed
    // for the first time.
    public InputSource resolveEntity(String publicId, String systemId)
    {
        try
        {
            
        	InputStream in = null;
            try {
            	if (ignoreSystemID) throw new Exception("Ignore systemID="+systemId);
            	if (logger != null)
                logger.info("DTDSchema: Trying systemID "+ systemId);
                
                in = new URL(systemId).openStream();
                if (in == null) throw new Exception("SystemID not found");                
            } catch (Exception x) {
                try {
                	if (logger != null)
                		logger.info("DTDSchema: Trying publicID "+ publicId);
                    in = new URL(publicId).openStream();
                    if (in == null) throw new Exception("PublicID not found");
            
                } catch (Exception e) {
                    try {
                    	if (logger != null)
                    		logger.info("DTDSchema: Trying predefined location "+ location);
                        in = new URL(location).openStream();
                        if (in == null) throw new Exception("location not found");
                    } catch (Exception xx) {
                    	if (logger != null)
                    		logger.info("DTDSchema: Trying internal "+ "ambit/data/qmrf/qmrf.dtd");
                        String filename = "ambit/data/qmrf/qmrf.dtd";
                        in = this.getClass().getClassLoader().getResourceAsStream(filename);
                        /*
                        return new InputSource(new URL(
                            "file:///D:/src/ambit/qmrf.dtd"
                            ).openStream());
                            */
                    }
                }
            }
            if (in != null)
            	return new InputSource(in);
            else return null;
        }
        catch (Exception e) { 
        	if (logger != null)
        		logger.error(e);
            e.printStackTrace();
        }
        
        // Returning null causes the caller to try accessing the systemid
        return null;
    }

	public boolean isIgnoreSystemID() {
		return ignoreSystemID;
	}

	public void setIgnoreSystemID(boolean ignoreSystemID) {
		this.ignoreSystemID = ignoreSystemID;
	}
    


}


