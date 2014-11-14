/*
Copyright (C) 2005-2008  

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

package ambit2.pubchem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import ambit2.base.processors.ProcessorException;

public abstract class HTTPRequest<Target, Result> extends DefaultAmbitProcessor<Target, Result> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3444405772335211466L;
	protected int timeout = 60000;
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	protected int maxretry = 6;
	protected String url;
	protected String httpMethod = "POST";
	protected boolean cancelled;
	public boolean isCancelled() {
		return cancelled;
	}
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Result process(Target target) throws AmbitException {
		int retry = 0;
		int status = 200;
		setCancelled(false);
		while (!isCancelled()) {
	       try {
	            URL url = new URL(getUrl());
	            URLConnection connection= url.openConnection();
	            if (connection instanceof HttpURLConnection) {
	                HttpURLConnection hc = ((HttpURLConnection)connection);
	                hc.setReadTimeout(timeout);
	                hc.setConnectTimeout(timeout);
	                hc.setRequestMethod(httpMethod);
	                hc.setDoOutput(true);
	                
	                prepareOutput(target, hc.getOutputStream());
	                InputStream in = hc.getInputStream();
	                Result result =  parseInput(target, hc.getInputStream());
	                in.close();
	                status = hc.getResponseCode();
	                switch (status) {
	                case 200: return result;
	                case 404: return null;
	                case 500:  {
	                	retry++;
	     	    	    setCancelled(retry >= 10);
	                }
	                default: {
	                	throw new ProcessorException(this,String.format("STATUS: %d", status));
	                }
	                }

	            } else
	            	return null;
	       	} catch (SocketTimeoutException x) {
	    	   retry++;
	    	   setCancelled(retry >= maxretry);
	    	   if (isCancelled())
	    		   if (maxretry >1) 
	    			   throw new ProcessorException(this,"Maximum retry count reached "+maxretry);
	    		   else
	    			   throw new ProcessorException(this,x);
	       	} catch (FileNotFoundException x) {
	       		throw new ambit2.pubchem.FileNotFoundException(this,x);
	       	} catch (IOException x) {
	       		
	       		if (x.getMessage().indexOf("Server returned HTTP response code: 4")>=0) 
	       			setCancelled(true);
	       		throw new ProcessorException(this,x);
	        } catch (Exception x) {
	            throw new ProcessorException(this,x);
	        } finally {

	        }
		}    
		if (isCancelled())
			throw new ProcessorException(this,"Maximum retry count reached "+maxretry);
		else
			throw new ProcessorException(this,"STATUS: "+status);

	}
	protected abstract void prepareOutput(Target target, OutputStream out) throws ProcessorException;
	protected abstract Result parseInput(Target target, InputStream in)  throws ProcessorException;
	
	public String getHttpMethod() {
		return httpMethod;
	}
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	
}


