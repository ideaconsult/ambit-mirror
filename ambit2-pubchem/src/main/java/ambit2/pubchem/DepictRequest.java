package ambit2.pubchem;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.imageio.ImageIO;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import ambit2.base.exceptions.HttpException;
import ambit2.base.processors.ProcessorException;

public class DepictRequest extends DefaultAmbitProcessor<String,BufferedImage> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8933763425306077100L;
	protected String root = "http://www.daylight.com";
	protected String server = root + "/daycgi/depictform";
	protected String form = 
		"<HTML><HEAD><CENTER><TITLE>Depict</TITLE><H1>Depict</H1>Interactive depiction of SMILES<BR></CENTER><P>"+
		"<A HREF=\"/dayhtml/help/depict-help.html\">Help</A>for this page is available.</P></HEAD>"+
		"<BODY><hr><FORM METHOD=\"POST\" ACTION=\"/daycgi/depictform\"><P>SMILES:"+
		"<INPUT NAME=\"smiles\" value=\"%s\" SIZE=50><INPUT TYPE=\"submit\" VALUE=\"Submit\">"+
		"</P></FORM>"+
		"</BODY></HTML>";	
	protected String imagesrc="<IMG SRC=\"/daycgi/smi2gif?";
	
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	protected BufferedImage parseInput(InputStream in)
			throws ProcessorException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		try {
			while ((line =reader.readLine())!= null) {
				int index = line.indexOf(imagesrc);
				if (index >= 0) {
					line = line.substring(index+10);
					index = line.indexOf(">");
					if (index >0) 
						return retrieveImage(new URL(root+line.substring(0,index)));
				}
			}
		} catch (ProcessorException x) {
			throw x;
		} catch (Exception x) {
			throw new ProcessorException(this,x);
		}
		return null;
	}

	public BufferedImage retrieveImage(URL url) throws Exception {

		InputStream in = null;
		HttpURLConnection uc = null;
		try {
			uc =(HttpURLConnection) url.openConnection();
			uc.setDoOutput(true);
			uc.setRequestMethod("GET");
			
			int code = uc.getResponseCode();
			
			if (code==200) {
				in= uc.getInputStream();
				return ImageIO.read(uc.getInputStream());
			} else throw new HttpException(url.toString(),code,uc.getResponseMessage());

		} catch (Exception x) {
			throw x;
		} finally {
			try {
				if (in != null) in.close();
			} catch (Exception x) {} 
			try { uc.disconnect();} catch (Exception x) {}
		}
	}
	
	public BufferedImage process(String target) throws AmbitException {
		BufferedWriter bw = null;
		try {
			if (target==null) return null;
			HttpURLConnection uc =(HttpURLConnection) new  URL(getServer()).openConnection();
			uc.setDoOutput(true);
			uc.setRequestMethod("POST");
			bw = new BufferedWriter(new OutputStreamWriter(uc.getOutputStream()));
			bw.write(URLEncoder.encode("smiles"));
			bw.write("="); 
	        bw.write(URLEncoder.encode(target));
	        bw.flush(); bw.close();	        

			int code = uc.getResponseCode();
			
			if (code==200) {
				return parseInput(uc.getInputStream());
			} else throw new HttpException(getServer(),code,uc.getResponseMessage());	
			
		} catch (HttpException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		} finally {
			try {bw.flush(); bw.close();} catch (Exception x) {}
		}
	}

}
