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

package ambit.data.qmrf.test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;

import junit.framework.TestCase;
import ambit.data.qmrf.JUnicodePanel;
import ambit.data.qmrf.QMRFAttachment;
import ambit.data.qmrf.QMRFAttachments;
import ambit.data.qmrf.QMRFObject;
import ambit.data.qmrf.QMRFPanel;

public class QMRFPanelTest extends TestCase {
	public void test() {
		QMRFObject qmrf = new QMRFObject();
		QMRFPanel qmrfPanel = new QMRFPanel(qmrf);
		JOptionPane.showMessageDialog(null,qmrfPanel);
	}
    
    public void testAttachment() {
        QMRFAttachments attachments  = new QMRFAttachments("attachments");
        QMRFAttachment a = new QMRFAttachment("data","file","pdf","");
        attachments.getAttachments()[2].add(a);
        attachments.getAttachments()[0].add( new QMRFAttachment("data","url","sdf","")); 
        JOptionPane.showMessageDialog(null,attachments.editor(attachments.isEditable()).getJComponent());
    }  
    public void test1() {
    	 Map map = Charset.availableCharsets();
    	    Iterator it = map.keySet().iterator();
    	    while (it.hasNext()) {
    	        // Get charset name
    	    	String name = it.next().toString();
    	    	//System.out.println(name);
    	        String charsetName = name;
    	    
    	        // Get charset
    	        Charset charset = Charset.forName(charsetName);
    	        //System.out.println(charset.aliases());
    	        
    	    }
/*
 * ISO-8859-1
    ISO-8859-15
    US-ASCII
    UTF-16
    UTF-16BE
    UTF-16LE
    UTF-8
    windows-1252

 */
    }
    public void test2() {
    	 // Create the encoder and decoder for ISO-8859-1
        Charset charset = Charset.forName("UTF-8");
        String s = "string \u0395\u03B8     \u03C0";
        JOptionPane.showMessageDialog(null,s);
        CharsetDecoder decoder = charset.newDecoder();
        CharsetEncoder encoder = charset.newEncoder();
        
        try {
            // Convert a string to ISO-LATIN-1 bytes in a ByteBuffer
            // The new ByteBuffer is ready to be read.
            ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(s));
            
            
            // Convert ISO-LATIN-1 bytes in a ByteBuffer to a character ByteBuffer and then to a string.
            // The new ByteBuffer is ready to be read.
            CharBuffer cbuf = decoder.decode(bbuf);
            s = cbuf.toString();
            
            System.out.println(s);
        } catch (CharacterCodingException e) {
        	e.printStackTrace();
        	fail();
        }

    }
    
    public void test3() {
    	/*
    	   BreakIterator iterator = BreakIterator.getCharacterInstance(Locale.CANADA);
    	    iterator.setText("aString");
    	    for (int index=iterator.first(); index != BreakIterator.DONE; index=iterator.next()) {
    	        System.out.println(index);
    	    }
    	    */
    	//Charset charset = Charset.forName("UTF-8");
    	String s = "\u0391";
    	System.out.print(s);
    	JOptionPane.showMessageDialog(null,s);
    	StringBuffer b = new StringBuffer();
    	for (int i=Character.MIN_CODE_POINT; i < Character.MAX_CODE_POINT; i++) {
    		System.out.print(i+1);
    		System.out.print('.');
    		System.out.println(Character.toChars(i)[0]);
    	}	
    }
    
    public void test5() {
    	try {
    		int page = 0;
    		JUnicodePanel p = new JUnicodePanel();
    		 //p.setBase((char)(page * 0));
    		JOptionPane.showMessageDialog(null,p);
    		
    	} catch (Exception x) {
    		fail();
    		x.printStackTrace();
    	}
    	
    }
    
}

