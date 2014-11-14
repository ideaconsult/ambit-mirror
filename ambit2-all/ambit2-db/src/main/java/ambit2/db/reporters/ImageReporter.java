package ambit2.db.reporters;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
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
			if (STRUC_TYPE.NANO.equals(item.getType())) {
				return new JustTheImage(depict.createDefaultImage("NanoMaterial"),null);
			} else if (item instanceof SubstanceRecord) {
				return new JustTheImage(depict.createDefaultImage(((SubstanceRecord)item).getSubstancetype()),null);
			} else {
				imageWrapper.setImage(null);
				imageWrapper.setProperty(null);
				//Object path = item.getProperty(img);
				//path=null;//test
				String tmpDir = System.getProperty("java.io.tmpdir");
				String dimensions = getQueryName();
				File path = getFilePath(tmpDir, getConnection().getCatalog(), dimensions, item, subType);
				File jsonpath = getFilePath(tmpDir, getConnection().getCatalog(), dimensions, item, "json");
				//check if both png & json exists, otherwise generate
				if ((path != null) && path.exists() && (jsonpath!=null) && (jsonpath.exists())) {
					imageWrapper.setImage(ImageIO.read(path));
					return imageWrapper;
				} 
				return imageWrapper;
			}
		} catch (Exception x) {
			logger.log(java.util.logging.Level.WARNING,x.getMessage(),x);
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


