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
}
