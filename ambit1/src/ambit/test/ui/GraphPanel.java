/*
 *  Copyright (C) 2003  Jens Kanschik,
 * 	mail : jensKanschik@users.sourceforge.net
 *
 *  Part of <hypergraph>, an open source project at sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package ambit.test.ui;

import hypergraph.graphApi.AttributeManager;
import hypergraph.graphApi.Edge;
import hypergraph.graphApi.Element;
import hypergraph.graphApi.Graph;
import hypergraph.graphApi.Node;

import java.awt.Image;
import java.awt.event.MouseEvent;


/**
 * @author Jens Kanschik
 */
public class GraphPanel extends hypergraph.visualnet.GraphPanel {

	private static String	homepageUrl = "http://hypergraph.sourceforge.net";


	public GraphPanel(Graph graph) {
		super(graph);
		getEdgeRenderer().setLabelVisible(true);
	}

	public void setHoverElement(Element element, boolean repaint) {
		if (getHoverElement() != element) {
			if (element == null) {
				setToolTipText(null);
			}
			// show a tooltip (xlink:href) if the element is a node
			if (element != null && element.getElementType() == Element.NODE_ELEMENT) {

				AttributeManager attrMgr = getGraph().getAttributeManager();
				String href = (String) attrMgr.getAttribute("xlink:href", element);
				if (href == null)
					href = "No reference";

				setToolTipText(href);
			}
			// do something different if the element is an edge
			if (element != null && element.getElementType() == Element.EDGE_ELEMENT) {
				setToolTipText(((Edge) element).getLabel());
			}
		}
		super.setHoverElement(element, repaint);
		//if (getHoverElement() == null)
			//explorer.getAppletContext().showStatus("");
	}
	/** Called when a user clicks on the logo. Loads the homepage of hypergraph.
	 * @param e The event of the mouse click.
	 */
	protected void logoClicked(MouseEvent e) {
		super.logoClicked(e);
	    //try {
			//explorer.getAppletContext().showDocument(
	    	//	new URL(homepageUrl));
	    //} catch (MalformedURLException mue) {}
	}

	/** Called when the mouse is moved. Delegates the main work to the superclass,
	 * if the mouse is over the logo, the homepage url is shown in the browser's
	 * status line.
	 * @param e The event of the mouse movement.
	 */
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		//if (isOnLogo(e.getPoint()))
			//explorer.getAppletContext().showStatus(homepageUrl);
	}
	/** Called if the node is clicked.
	 * Depending on the attribute "xlink:href", "xlink:show" and
	 * the property "hypergraph.applications.hexplorer.GraphPanel.target"
	 * either another node is centered or another page is shown by the browser.
	 * <br />
	 * If "xlink:href" starts with '#',
	 * the text after '#' is interpreted as the id of a node.
	 * This node is then centered.
	 * <br />
	 * Otherwise "xlink:href" denotes another document that is then shown by the browser. The "xlink:show" attribute and the "hypergraph.applications.hexplorer.GraphPanel.target" are used to determine the browser's target:
	 * <table>
	 * <tr><td>xlink:show</td><td>property "...target"</td><td>target of the browser</td></tr>
	 * <tr><td>"replace"</td><td>any</td><td>_self, the target property is ignored</td></tr>
	 * <tr><td>"new"</td><td>any</td><td>_blank, the target property is ignored</td></tr>
	 * <tr><td>any other</td><td>any</td><td>as specified in the target property</td></tr>
	 * <tr><td>any other</td><td>not specified</td><td>_self</td></tr>
	 * </table>
	 *
	 * @param clickCount The number of clicks on the node.
	 * @param node The node that has been clicked on.
	 */
	public void nodeClicked(int clickCount, Node node) {
		AttributeManager attrMgr = getGraph().getAttributeManager();
		String href = (String) attrMgr.getAttribute("xlink:href", node);
		if (href != null && href.charAt(0) == '#') {
			Element element = getGraph().getElement(href.substring(1));
			if (element.getElementType() == Element.NODE_ELEMENT)
				centerNode((Node) element);
		}
		if (href != null && href.charAt(0) != '#') {
			// the attribute xlink:show from xml file (if available).
			String xmlShow = (String) attrMgr.getAttribute("xlink:show", node);
			// the target property from property file (if available).
			String propTarget = getPropertyManager().getString("hypergraph.applications.hexplorer.GraphPanel.target");
			// the target as used in showDocument.
			String target = null;
			if (xmlShow != null && xmlShow.equals("replace"))
				target = "_self";
			if (xmlShow != null && xmlShow.equals("new"))
				target = "_blank";
			if (target == null && propTarget != null)
				target = propTarget;
			if (target == null)
				target = "_self";
			try {
				//URL url = new URL(explorer.getDocumentBase(), href);
				//AppletContext context = explorer.getAppletContext();
				//context.showDocument(url, target);
			} catch (Exception ex) {
				System.out.println(ex);
			}
		}
		if (href == null)
			super.nodeClicked(clickCount, node);
	}
	@Override
	public void setLogo(Image arg0) {

		super.setLogo(null);
	}
	@Override
	public void setSmallLogo(Image l) {
		super.setSmallLogo(null);
	}
}
