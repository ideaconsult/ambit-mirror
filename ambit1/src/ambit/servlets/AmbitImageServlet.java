package ambit.servlets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ambit.database.AmbitID;
import ambit.database.data.DbCompoundImageTools;
import ambit.ui.data.CompoundImageTools;

/**
 * Generates png image given a SMILES string. Parameters:
 * smiles (mandatory)<br>
 * width (optional, default 100)<br>
 * height (optional, default 100)<br>
 * background (optional, default #FFFFFF)<br>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public class AmbitImageServlet extends AmbitServlet {
    //protected DbCompoundImageTools tools = new DbCompoundImageTools();
	public AmbitImageServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("image/png");
		OutputStream os = response.getOutputStream();
		String par = request.getParameter("width");
		int width = 200;
		try {
			width = Integer.parseInt(par);
		} catch (Exception x) {
		}

		int height = 200;
		par = request.getParameter("height");
		try {
			height = Integer.parseInt(par);
		} catch (Exception x) {
			
		}

		Color background = Color.WHITE;
		//par = request.getParameter("height");
		try {
			background = new Color(Integer.parseInt(request
					.getParameter("background"), 16));
		} catch (Exception x) {
			background = Color.white;
		}

		String smiles = request.getParameter("smiles");
        
		BufferedImage buffer = null;
		DbCompoundImageTools tools = new DbCompoundImageTools(new Dimension(width,height));
        tools.setBackground(background);
		if (smiles != null)  {
            buffer = tools.getImage(smiles);
            try {
            	ImageIO.write(buffer, "png", os);
            } catch (Exception x) {
                ImageIO.write(CompoundImageTools.emptyImage(width, height, background, x.getMessage()), "png", os);
            }
        } else {
            try {
            	AmbitID id = new AmbitID(request.getParameter("id"),request.getParameter("idstructure"));
            	Connection c = pool.getConnection();
            	buffer = tools.getImage(c, id);
            	pool.returnConnection(c);
            	if (buffer == null) 
            			buffer = CompoundImageTools.emptyImage(width, height, background, "");
            	ImageIO.write(buffer, "png", os);
            } catch (Exception x) {
            	x.printStackTrace();
                ImageIO.write(CompoundImageTools.emptyImage(width, height, background, x.getMessage()), "png", os);
            }

        
        }
		tools = null;
		
		os.close();
		//System.out.print(this.getClass().getName());
		//System.out.println("\tSTOP");
	}

	protected BufferedImage generateTestImage(int width, int height,
			Color background) {
		String[] slices = new String[] { "60", "300" };
		Color[] colors = new Color[] { Color.red, Color.blue };
		int[] sizes = new int[slices.length];

		for (int i = 0; i < slices.length; i++) {
			sizes[i] = Integer.parseInt(slices[i]);
		}

		BufferedImage buffer = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = buffer.createGraphics();
		g.setColor(background);
		g.fillRect(0, 0, width, height);
		int arc = 0;
		for (int i = 0; i < sizes.length; i++) {
			g.setColor(colors[i]);
			g.fillArc(0, 0, width, height, arc, sizes[i]);
			arc += sizes[i];
		}
		return buffer;
	}

}
