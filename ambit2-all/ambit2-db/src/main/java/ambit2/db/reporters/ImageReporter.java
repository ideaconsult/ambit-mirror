package ambit2.db.reporters;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rendering.CachedImage;

/**
SELECT idchemical,idstructure,text,title FROM structure
left join query_results using(idchemical,idstructure)
left join `query` using(idquery)
left join sessions using(idsessions)
where
sessions.title="SMARTS" and
idchemical=460 and idstructure=100664
union
SELECT idchemical,idstructure,null,"SMARTS" FROM structure
where
idchemical=460 and idstructure=100664
 * @author nina
 *
 * @param <Q>
 */
public class ImageReporter<Q extends IQueryRetrieval<IStructureRecord>> extends AbstractImageReporter<Q,BufferedImage > {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;
	
	public ImageReporter(String mainType,String subType) {
		this(mainType,subType,new Dimension(250,250));
	}
	public ImageReporter(String mainType,String subType,Dimension dimension) {
		super(mainType,subType,dimension);

	}
	@Override
	protected CachedImage<BufferedImage> createImageWrapper(BufferedImage image) {
		return new JustTheImage(image, null);
	}

	@Override
	protected CachedImage<BufferedImage> getCached(IStructureRecord item) {
		try {
			imageWrapper.setImage(null);
			imageWrapper.setProperty(null);
			Object path = item.getProperty(img);
			if (path != null) {
				File file = new File(path.toString());
				if (file.exists()) {
					imageWrapper.setImage(ImageIO.read(file));
					return imageWrapper;
				}
			} 
			return imageWrapper;
		} catch (Exception x) {
			logger.warn(x);
			return null;
		}
	}
	/*
	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {
		try {
			BufferedImage image = getCached(item);
			if (image == null) {
				image = createImage(item);
				cache(item,image);
			}
			if (image==null) 
				image = (BufferedImage) depict.getDefaultImage();
			setOutput(image);
			return image;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	
	}

	*/
	public void open() throws DbAmbitException {
	}
	@Override
	public void footer(BufferedImage output, Q query) {
		
	}
	@Override
	public void header(BufferedImage output, Q query) {
	}

}

class JustTheImage extends CachedImage<BufferedImage> {
	public JustTheImage(BufferedImage image, String path) {
		super(image,path);
	}
	@Override
	public BufferedImage getProperty() {
		return image;
	}
}


