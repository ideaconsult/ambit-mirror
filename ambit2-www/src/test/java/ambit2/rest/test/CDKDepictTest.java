package ambit2.rest.test;

import java.awt.Image;
import java.io.File;
import java.io.InputStream;

import javax.imageio.ImageIO;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

import ambit2.base.io.DownloadTool;

public class CDKDepictTest extends ResourceTest {
	
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/depict/cdk?search=c1ccccc1", port);
	}	
	@Test
	public void testPNG() throws Exception {
		testGet(getTestURI(),MediaType.IMAGE_PNG);
	}	
	@Override
	public boolean verifyResponsePNG(String uri, MediaType media, InputStream in)
			throws Exception {
		File file = new File("temp.png");
		file.delete();
		DownloadTool.download(in, file);
		Assert.assertTrue(file.exists());
		Image image = ImageIO.read(file);
		Assert.assertNotNull(image);
		file.delete();
		return true;
	}
	
	@Test
	public void testInvalidSmiles() throws Exception {
		Status status = testHandleError(
				String.format("http://localhost:%d/depict/cdk?search=AAAAA", port),
				MediaType.IMAGE_PNG);
		Assert.assertEquals(Status.CLIENT_ERROR_BAD_REQUEST,status);
	}	
	@Override
	public void testGetJavaObject() throws Exception {
	}
	
	//todo wrap as web service
	@Test
	public void testJMol() throws Exception {
		//TODO revive the attempt to make use of jmol for online pictures
		/*
		java.awt.Canvas display = new Canvas();
		JmolAdapter adapter = new SmarterJmolAdapter();
		org.jmol.viewer.Viewer viewer = (Viewer)
		Viewer.allocateViewer(display, adapter, null, null, null, null, null);
		try {
		viewer.setScreenDimension(new Dimension(1, 1));
		//viewer.scriptWait("load 'http://apps.ideaconsult.net:8080/ambit2/compound/100?media=chemical%2Fx-mdl-sdfile' {1 1 1};");
		String s = viewer.scriptWait("load 'http://localhost:8181/compound/7?media=chemical%2Fx-mdl-sdfile' {1 1 1};");
		System.out.println(s);
		//viewer.scriptWait("load 'crystal.cif' {1 1 1};");
		// can do more scripting here...
		viewer.setScreenDimension(new Dimension(400, 400));

		// anti-aliasing enabled
		viewer.getGraphics3D().setWindowParameters(400, 400, true);

		
		// Create image
		viewer.getImageAs("PNG", 1, 400, 400, "e:/image.png", null);

		} finally {
		// Ensure threads are stopped
		viewer.setModeMouse(JmolConstants.MOUSE_NONE);
		}
		
		*/
	}
}
