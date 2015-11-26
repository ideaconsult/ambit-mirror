package ambit2.export.isa.base;

import java.util.ArrayList;
import java.util.List;

public class Investigation 
{	
	//ONTOLOGY SOURCE REFERENCE
	public InvestigationField termSourceName = null;
	public InvestigationField termSourceFile = null;
	public InvestigationField termSourceVersion = null;
	public InvestigationField termSourceDescription = null;
	
	//INVESTIGATION
	public InvestigationField investigationIdentifier = null;
	public InvestigationField investigationTitle = null;
	public InvestigationField investigationDescription = null;
	public InvestigationField investigationSubmissionDate = null;
	public InvestigationField investigationPublicReleaseDate = null;
	
	//INVESTIGATION PUBLICATIONS
	public InvestigationField investigationPubmedID = null;
	public InvestigationField investigationPublicationDOI = null;
	public InvestigationField investigationPublicationAuthorlist = null;
	public InvestigationField investigationPublicationTitle = null;
	public InvestigationField investigationPublicationStatus = null;
	public InvestigationField investigationPublicationStatusTermAccessionNumber = null;
	public InvestigationField investigationPublicationStatusTermSourceREF = null;
	
	//INVESTIGATION CONTACTS
	public InvestigationField investigationPersonLastName = null;
	public InvestigationField investigationPersonFirstName = null;
	public InvestigationField investigationPersonMidInitials = null;
	public InvestigationField investigationPersonEmail = null;
	public InvestigationField investigationPersonPhone = null;
	public InvestigationField investigationPersonFax = null;
	public InvestigationField investigationPersonAddress = null;
	public InvestigationField investigationPersonAffiliation = null;
	public InvestigationField investigationPersonRoles = null;
	public InvestigationField investigationPersonRolesTermAccessionNumber = null;
	public InvestigationField investigationPersonRolesTermSourceREF = null;
	
	
	//public InvestigationField  = null;
	
	public List<StudyInfo> studies = new ArrayList<StudyInfo>();
	
}
