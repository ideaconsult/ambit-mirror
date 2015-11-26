package ambit2.rest.structure.diagram;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import ambit2.base.processors.batch.ListReporter;

public abstract  class DepictionReporter extends ListReporter<DepictQuery, BufferedImage> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3367225662813130216L;

	@Override
	public void header(BufferedImage output, Iterator<DepictQuery> query) {
		
	}

	@Override
	public void footer(BufferedImage output, Iterator<DepictQuery> query) {
		
	}

	protected BufferedImage createDefaultImage(int w, int h) {
		BufferedImage buffer = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = buffer.createGraphics();
		g.setColor(new Color(0x00fffffe, true));// white transparent);
		g.fillRect(0, 0, w, h);
		return buffer;
	}


}
