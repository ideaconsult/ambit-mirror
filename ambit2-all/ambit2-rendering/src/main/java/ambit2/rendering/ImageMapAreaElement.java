package ambit2.rendering;

import java.awt.Color;

import org.openscience.cdk.renderer.elements.TextElement;

public class ImageMapAreaElement extends TextElement {

	public ImageMapAreaElement(double xCoord, double yCoord, String text,Color color) {
		super(xCoord, yCoord, text, color);
	}

}
