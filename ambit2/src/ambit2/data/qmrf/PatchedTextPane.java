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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JTextPane;

public class PatchedTextPane extends JTextPane
{
	/**
	 * This one can paste also from Word 2000 etc. 
	 * Strange tags and comments are removed.
	 */
	public void paste()
	{
		Clipboard clipboard = getToolkit().getSystemClipboard();
		final Transferable content = clipboard.getContents(this);
		/*
		 * create a new transferable to filter the original one
		 */
		Transferable newContent = new Transferable()
		{
			public DataFlavor[] getTransferDataFlavors()
			{
				DataFlavor[] flavors = content.getTransferDataFlavors();
				ArrayList myFlavorList = new ArrayList(flavors.length);
				for (int i = 0; i < flavors.length; i++)
				{
					DataFlavor flavor = flavors[i];
					String s = "text/plain; class=java.io.InputStream; charset=UTF-8";
					//String s = "text/plain; class=String; charset=UTF-8";
					try {
					if ( flavors[i].equals(new DataFlavor(s))) 
						myFlavorList.add(flavor);
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				DataFlavor[] myFlavors = new DataFlavor[myFlavorList.size()];
				for (int i = 0; i < myFlavorList.size(); i++)
				{
					DataFlavor flavor = (DataFlavor) myFlavorList.get(i);
					myFlavors[i] = flavor;
				}
				return myFlavors;
				
			}
			
		
			/*
			 * only return DataFlavors which mimeTypes are String

			public DataFlavor[] getTransferDataFlavors()
			{
				DataFlavor[] flavors = content.getTransferDataFlavors();
				ArrayList myFlavorList = new ArrayList(flavors.length);
				for (int i = 0; i < flavors.length; i++)
				{
					DataFlavor flavor = flavors[i];
					String mimeType = flavor.getMimeType();
					if (mimeType.indexOf("String") >= 0)
					{
						myFlavorList.add(flavor);
					}
				}
				DataFlavor[] myFlavors = new DataFlavor[myFlavorList.size()];
				for (int i = 0; i < myFlavorList.size(); i++)
				{
					DataFlavor flavor = (DataFlavor) myFlavorList.get(i);
					myFlavors[i] = flavor;
				}
				return myFlavors;
				
			}
			*/
			/*
			 * unchanged
			 */
			public boolean isDataFlavorSupported(DataFlavor flavor)
			{
				return content.isDataFlavorSupported(flavor);
			}
			/*
			 * transforms Strings that are of type HTML
			 */
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
			{
				String mimeType = flavor.getMimeType();
				if (mimeType.indexOf("String") < 0 || mimeType.indexOf("html") < 0)
				{
					return content.getTransferData(flavor);
				}
				else
				{
					String data = (String) content.getTransferData(flavor);
					/*
					 * extract body
					 */
					data = data.substring(data.indexOf("<body>"), data.indexOf("</body>") + 7);
					/*
					 * remove comments
					 */
					// to do: remove html comments
					/*
					 * remove strange tags
					 */
					// to do: remove  etc.
					/*
					 * that's it :)
					 */
					return data;
				}
			}
		};
		/*
		 * set the new transferable to the clipboard
		 */
		clipboard.setContents(newContent, null);
		super.paste();
	}
	@Override
	public boolean isManagingFocus() {
		return isEditable()	;
	}
}
