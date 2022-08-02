package ambit2.rest.config;

public class AMBITAppConfigInternal extends AMBITAppConfigProperties {
	protected static final String attachDepict = "attach.depict";
	protected static final String attachSubstance = "attach.substance";
	protected static final String attachInvestigation = "attach.investigation";
	protected static final String attachSubstanceOwner = "attach.substanceowner";
	protected static final String attachToxmatch = "attach.toxmatch";

	protected static final String adminAAEnabled = "aa.admin";
	protected static final String compoundAAEnabled = "aa.compound";
	protected static final String featureAAEnabled = "aa.feature";
	protected static final String modelAAEnabled = "aa.model"; // ignored
	
	public static final String OPENTOX_AA_ENABLED = "aa.enabled";
	public static final String LOCAL_AA_ENABLED = "aa.local.enabled";
	public static final String DB_AA_ENABLED = "aa.db.enabled";
	public static final String OIDC_AA_ENABLED = "aa.oidc.enabled";
	
	public static final String WARMUP_ENABLED = "warmup.enabled";

	/**
	 * No config overriding at all
	 */
	public AMBITAppConfigInternal() {
		super(null);
		setConfigOverrideVar(null);
	}
	

	public synchronized boolean attachDepictRouter() {
		return getBooleanPropertyWithDefault(attachDepict, ambitProperties, true);
	}

	public synchronized boolean attachSubstanceRouter() {
		return getBooleanPropertyWithDefault(attachSubstance, ambitProperties, true);
	}

	public synchronized boolean attachInvestigationRouter() {
		return getBooleanPropertyWithDefault(attachInvestigation, ambitProperties, false);
	}
	
	public synchronized boolean attachSubstanceOwnerRouter() {
		return getBooleanPropertyWithDefault(attachSubstanceOwner, ambitProperties, true);
	}

	public synchronized boolean attachToxmatchRouter() {
		return getBooleanPropertyWithDefault(attachToxmatch, ambitProperties, true);
	}

	public synchronized boolean isOpenToxAAEnabled() {
		return getBooleanPropertyWithDefault(OPENTOX_AA_ENABLED,configProperties,false);
	}


	public synchronized boolean isDBAAEnabled() {
		return getBooleanPropertyWithDefault(DB_AA_ENABLED,configProperties,false);
	}
	
	
	public synchronized boolean isSimpleSecretAAEnabled() {
		return getBooleanPropertyWithDefault(LOCAL_AA_ENABLED,configProperties,false);
	}
	
	public synchronized boolean isOIDC_AA_Enabled() {
		return getBooleanPropertyWithDefault(OIDC_AA_ENABLED,configProperties,false);
	}	

	public synchronized boolean isWarmupEnabled() {
		return getBooleanPropertyWithDefault(WARMUP_ENABLED,configProperties,false);
	}	
	public synchronized boolean protectAdminResource() {
		return getBooleanPropertyWithDefault(adminAAEnabled,ambitProperties,false);
	}

	
	public synchronized boolean protectCompoundResource() {
		return getBooleanPropertyWithDefault(compoundAAEnabled,ambitProperties,false);
	}

	public synchronized boolean protectFeatureResource() {
		return getBooleanPropertyWithDefault(featureAAEnabled,ambitProperties,false);
	}
	
	
}
