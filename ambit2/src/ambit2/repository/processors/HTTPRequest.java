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

package ambit2.repository.processors;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import ambit2.repository.IProcessor;
import ambit2.repository.ProcessorException;

public abstract class HTTPRequest<Target, Result> implements IProcessor<Target, Result> {
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
	public Result process(Target target) throws ProcessorException {
		int retry = 0;
		while (!isCancelled()) {
	       try {
	            URL url = new URL(getUrl());
	            URLConnection connection= url.openConnection();
	            if (connection instanceof HttpURLConnection) {
	                HttpURLConnection hc = ((HttpURLConnection)connection);
	                //System.out.println(hc.getConnectTimeout());
	                //System.out.println(hc.getReadTimeout());
	                hc.setRequestMethod(httpMethod);
	                hc.setDoOutput(true);
	                prepareOutput(target, hc.getOutputStream());

	                return parseInput(target, hc.getInputStream());

	            } else
	            	return null;
	       	} catch (SocketTimeoutException x) {
	    	   retry++;
	    	   setCancelled(retry >= maxretry);
	        } catch (Exception x) {
	            throw new ProcessorException(x);
	        }
		}    
		throw new ProcessorException("Maximum retry count reached "+maxretry);
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


