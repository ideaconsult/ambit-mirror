package ambit2.export.isa.base;

public class ISAConst {
	public static enum DataFileFormat {
		TEXT_TAB, TEXT_CSV
	}

	public static enum ISAVersion {
		Ver1_0, Ver2_0
	}

	public static enum ISAFormat {
		TAB {
			@Override
			public String getMediaType() {
				return "application/x-isatab";
			}
			@Override
			public String getExtension() {
				return "txt";
			}
		},
		JSON {
			@Override
			public String getMediaType() {
				return "application/isa+json";
			}
			@Override
			public String getExtension() {
				return "json";
			}
		};
		/**
		 * MIME type
		 * See https://github.com/ISA-tools/isa-api/issues/96
		 * @return
		 */
		public abstract String getMediaType();
		public abstract String getExtension();
		@Override
		public String toString() {
			return String.format("ISA %s", name());
		}
	}

	public static final String investigationFilePrefix = "i_";
	public static final String studyFilePrefix = "s_";
	public static final String assayFilePrefix = "a_";

	public static final String addSeparator = " ";

}
