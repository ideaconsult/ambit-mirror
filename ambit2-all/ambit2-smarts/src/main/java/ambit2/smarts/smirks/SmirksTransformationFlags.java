package ambit2.smarts.smirks;

public enum SmirksTransformationFlags 
{
	SSMode, 
	CheckResultStereo, 
	FilterEquivalentMappings, 
	ProcessResultStructures, 
	ClearHybridizationBeforeResultProcess,
	ClearAromaticityBeforeResultProcess,
	ClearImplicitHAtomsBeforeResultProcess,
	ClearExcplicitHAtomsBeforeResultProcess,
	AddImplicitHAtomsOnResultProcess,
	ConvertAddedImplicitHToExplicitOnResultProcess,
	CheckAromaticityOnResultProcess,
	ApplyStereoTransformation,
	HAtomsTransformation,
	HAtomsTransformationMode,
	AromaticityTransformation
}
