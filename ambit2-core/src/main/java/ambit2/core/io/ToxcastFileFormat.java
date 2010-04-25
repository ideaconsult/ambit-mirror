package ambit2.core.io;

/**
 * Toxcast Assay files are tab delimited files
 * @author nina
 *
 */
public class ToxcastFileFormat extends DelimitedFileFormat {
	public ToxcastFileFormat() {
		super("\t",'"');
	}
}
