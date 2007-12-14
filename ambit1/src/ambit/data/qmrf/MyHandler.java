/*
Copyright (C) 2005-2007  

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

import java.io.IOException;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Stack;

import com.lowagie.text.Anchor;
import com.lowagie.text.Annotation;
import com.lowagie.text.Cell;
import com.lowagie.text.Chapter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Section;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.xml.SAXmyHandler;

public class MyHandler extends SAXmyHandler {

	public MyHandler(Document document, HashMap tagmap) throws DocumentException,IOException {
		super(document, tagmap);
	}
	
	@Override
	public void handleStartingTags(String name, Properties attributes) {
		System.out.println(name);
        if (Image.isTag(name)) {
            try {
//                Image img = Image.getInstance(attributes);
            	System.out.println("ambit/data/qmrf/logo.png");
                Image img = Image.getInstance(Qmrf_Xml_Pdf.class.getClassLoader().getResource("ambit/data/qmrf/logo.png"));
                Object current;
                try {
                    // if there is an element on the stack...
                    current = stack.pop();
                    // ...and it's a Chapter or a Section, the Image can be
                    // added directly
                    if (current instanceof Chapter
                            || current instanceof Section
                            || current instanceof Cell) {
                        ((TextElementArray) current).add(img);
                        stack.push(current);
                        return;
                    }
                    // ...if not, the Image is wrapped in a Chunk before it's
                    // added
                    else {
                        Stack newStack = new Stack();
                        try {
                            while (!(current instanceof Chapter
                                    || current instanceof Section || current instanceof Cell)) {
                                newStack.push(current);
                                if (current instanceof Anchor) {
                                    img.setAnnotation(new Annotation(0, 0, 0,
                                            0, ((Anchor) current).reference()));
                                }
                                current = stack.pop();
                            }
                            ((TextElementArray) current).add(img);
                            stack.push(current);
                        } catch (EmptyStackException ese) {
                            document.add(img);
                        }
                        while (!newStack.empty()) {
                            stack.push(newStack.pop());
                        }
                        return;
                    }
                } catch (EmptyStackException ese) {
                    // if there is no element on the stack, the Image is added
                    // to the document
                    try {
                        document.add(img);
                    } catch (DocumentException de) {
                        throw new ExceptionConverter(de);
                    }
                    return;
                }
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }

		} else super.handleStartingTags(name, attributes);
	}


	/*
	public void endElement(String uri, String lname, String name) {
		if (myTags.containsKey(name)) {
			XmlPeer peer = (XmlPeer) myTags.get(name);
			// we don't want the document to be close
			// because we are going to add a page after the xml is parsed
			if (isDocumentRoot(peer.getTag())) {
				return;
			}
			handleEndingTags(peer.getTag());
		} else {
			handleEndingTags(name);
		}
	}
	*/
}


