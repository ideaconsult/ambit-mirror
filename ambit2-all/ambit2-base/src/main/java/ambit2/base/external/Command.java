package ambit2.base.external;

public class Command {
		public Command(String exe) {
			this(exe,null);
		}
		public Command(String exe, String[] additionalFiles) {
			setExe(exe);
			setAdditionalFiles(additionalFiles);
		}
		String exe;
		public String getExe() {
			return exe;
		}
		public void setExe(String exe) {
			this.exe = exe;
		}
		public String[] getAdditionalFiles() {
			return additionalFiles;
		}
		public void setAdditionalFiles(String[] additionalFiles) {
			this.additionalFiles = additionalFiles;
		}
		String[] additionalFiles;
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(exe);
			if (additionalFiles != null)
				for (String file: additionalFiles) {
					b.append(',');
					b.append(file);	
				}
				
			
			return b.toString();
		}
	}
