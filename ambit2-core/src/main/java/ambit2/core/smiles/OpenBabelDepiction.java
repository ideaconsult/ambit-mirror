package ambit2.core.smiles;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.external.ShellException;

public class OpenBabelDepiction extends OpenBabelAbstractShell<String> {
	protected String outputFileName=null;
	protected int size=210;

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1989646229616982430L;
	public OpenBabelDepiction() throws ShellException {
		super();
	}

	@Override
	protected String getOutputOption() {
		return String.format("-xp %s",size);
	}

	@Override
	protected synchronized String getOutputFileName() {
		if (outputFileName==null)
			outputFileName = 
			    new StringBuilder().append("obabel_")
			        //.append(.getTime())
			        .append(UUID.randomUUID())
			        .append(".").append("png").toString();
		
		return outputFileName;
	}
	
	@Override
	protected synchronized IAtomContainer parseOutput(String path, String mol)
			throws ShellException {
		return null;
	}
	
	public BufferedImage getImage() throws IOException {
		BufferedImage image = null;
    	String homeDir = getHomeDir(null); // getPath(new File(exe));
    	File dir = new File(homeDir);
    	if (!dir.exists()) dir.mkdirs();
    	
    	String outfile = String.format("%s%s%s",homeDir,File.separator,getOutputFile());
    	File file = new File(outfile);
    	if (file.exists()) try {
    		image = ImageIO.read(file);
    		file.delete();
    	} catch (Exception x) {}
    	return image;
	}
	
}
