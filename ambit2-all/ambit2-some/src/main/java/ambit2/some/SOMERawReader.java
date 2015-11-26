package ambit2.some;

import java.awt.Color;
import java.io.Reader;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;

import ambit2.core.io.RawIteratingReader;
import ambit2.rendering.IAtomContainerHighlights;

/**
 * Reads SOME records
 * 
 * @author nina
 * 
 */
public class SOMERawReader extends RawIteratingReader<String> {

	public enum someindex {
		atomIndex, atomType, aliphaticHydroxylation {
			@Override
			public String toString() {
				return "Aliphatic Hydroxylation";
			}

			@Override
			public Color getColor(double arg0) {
				return new Color(0xeb, 0xbe, 0x9f, (int) (arg0 * 255));
			}
		},
		aromaticHydroxylation {
			@Override
			public String toString() {
				return "Aromatic Hydroxylation";
			}

			@Override
			public Color getColor(double arg0) {
				return new Color(0x3e, 0xa6, 0x3b, (int) (arg0 * 255));
			}
		},
		NDealkylation {
			@Override
			public String toString() {
				return "N-Dealkylation";
			}

			@Override
			public Color getColor(double arg0) {
				return new Color(0xd1, 0x2c, 0x8b, (int) (arg0 * 255));
			}
		},
		NOxidation {
			@Override
			public String toString() {
				return "N-Oxidation";
			}

			@Override
			public Color getColor(double arg0) {
				return new Color(0x90, 0x62, 0x0c, (int) (arg0 * 255));
			}
		},
		ODealkylation {
			@Override
			public String toString() {
				return "O-Dealkylation";
			}

			@Override
			public Color getColor(double arg0) {
				return new Color(0xff, 0x6d, 0x06, (int) (arg0 * 255));
			}
		},
		SOxidation {
			@Override
			public String toString() {
				return "S-Oxidation";
			}

			@Override
			public Color getColor(double arg0) {
				return new Color(0x0a, 0x55, 0xa3, (int) (arg0 * 255));
			}
		};
		public Color getColor(double value) {
			return Color.getHSBColor((ordinal() - 1.0f) / 6.0f, 0.8f,
					(float) value);

		}
	}

	protected String firstLine = null;

	public SOMERawReader(Reader in) throws CDKException {
		super(in);

	}

	@Override
	public IResourceFormat getFormat() {
		return null;
	}

	@Override
	public boolean isEndOfRecord(String line) {
		return line.startsWith("@SOME results:");
	}

	@Override
	public String nextRecord() {
		return recordBuffer == null ? null : recordBuffer.toString();
	}

	public boolean hasNext() {
		recordBuffer = null;
		try {
			while (input.ready()) {
				String line = input.readLine();

				if (isEndOfRecord(line) && (recordBuffer != null)) {
					firstLine = line;
					return true;
				}

				if (recordBuffer == null) {
					recordBuffer = new StringBuilder();
					if (firstLine != null)
						recordBuffer.append(firstLine);
				}
				recordBuffer.append(line);
				recordBuffer.append('\n');

			}
			// return recordBuffer!=null;
			return acceptLastRecord();
		} catch (Exception x) {
			logger.log(Level.SEVERE, x.getMessage(), x);
		}
		return false;
	}

	@Override
	protected boolean acceptLastRecord() {
		return recordBuffer != null;
	}

	public static IAtomContainerHighlights getAtomSelector(
			IAtomContainer structure, someindex index) throws Exception {
		return new IAtomContainerHighlights() {
			@Override
			public IChemObjectSelection process(IAtomContainer target)
					throws AmbitException {
				return null;
			}

			public boolean isEnabled() {
				return true;
			}

			public long getID() {
				return 0;
			}

			public void setEnabled(boolean arg0) {
			}

			@Override
			public void open() throws Exception {
			}

			@Override
			public void close() throws Exception {
			}
		};
	}

}
