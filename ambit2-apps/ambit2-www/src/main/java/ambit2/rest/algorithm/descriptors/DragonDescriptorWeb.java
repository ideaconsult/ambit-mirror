package ambit2.rest.algorithm.descriptors;

import ambit2.base.config.AMBITConfigProperties;
import ambit2.base.external.ShellException;
import ambit2.dragon.DescriptorDragonShell;
import ambit2.dragon.DragonShell;

public class DragonDescriptorWeb extends DescriptorDragonShell {

	public DragonDescriptorWeb() throws ShellException {
		super();
	}

	@Override
	protected DragonShell createDragonShell() throws ShellException {
		return new DragonShell() {
			/**
			* 
			*/
			private static final long serialVersionUID = 9099725609913728923L;

			@Override
			protected String getHome() throws ShellException {
				return getHomeFromConfig(AMBITConfigProperties.ambitProperties, DragonShell.DRAGON_HOME);
			}
		};
	}

}
