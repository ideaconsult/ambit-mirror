<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>

<script type='text/javascript'>
	function downloadTemplate(root, endpoint, assay) {
		url = root + "/datatemplate?endpoint=" + encodeURIComponent(endpoint) + "&assay=" + encodeURIComponent(assay) + "&media=application%2Fvnd.openxmlformats-officedocument.spreadsheetml.sheet";
		window.open(url);
	}
</script>
<script type='text/javascript'>
$(document).ready(function() {
    $('#templates').DataTable(
					{
						"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
    					"bSearchable" : true,
						"sSearch" : "Filter:",
						"bPaginate" : true,
						"sPaginationType" : "full_numbers",
						"sPaginate" : ".dataTables_paginate _paging",
						"oLanguage" : {
							"sLoadingRecords" : "No templates found.",
							"sZeroRecords" : "No templates found.",
							"sInfo" : "Showing _TOTAL_ templates (_START_ to _END_)",
							"sLengthMenu" : 'Display <select>'
									+ '<option value="20">20</option>'
									+ '<option value="50">50</option>'
									+ '<option value="100">100</option>'
									+ '<option value="-1">all</option>'
									+ '</select> templates.'
						}
						});
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/datatemplate" title="Templates">Data entry templates</a></li>');
	jQuery("#breadCrumb").jBreadCrumb();
	downloadForm("${ambit_request}");
	loadHelp("${ambit_root}","datatemplate");
});
</script>
</head>
<body>

<div class="container columns" style="margin:0;padding:0;">
<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="one column remove-bottom" style="padding:0;" >
&nbsp;
</div>
<div class="thirteen columns remove-bottom" style="padding:0;" >
		
<table  id='templates'   class='jtoxkit' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
<thead>
<tr><th>Type</th><th>Endpoint</th><th>Assay</th><th>Download XLSX template</th></tr>
</thead>
<tbody>
<tr><td>PCHEM</td><td>BATCHDISPERSION</td><td>batch dispersion</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_BATCHDISPERSION.xlsx","batch dispersion")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>COMPOSITIONCHEMICAL</td><td>composition_EDX</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_COMPOSITIONCHEMICAL.xlsx","composition_EDX")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>COMPOSITIONCHEMICAL</td><td>composizion_ICP-OES</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_COMPOSITIONCHEMICAL.xlsx","composizion_ICP-OES")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>COMPOSITIONCHEMICAL</td><td>elemental composition_ICP-MS</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_COMPOSITIONCHEMICAL.xlsx","elemental composition_ICP-MS")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>COMPOSITIONCHEMICAL</td><td>elemental compostion_CHN analys</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_COMPOSITIONCHEMICAL.xlsx","elemental compostion_CHN analys")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>CRYSTALLINEPHASE</td><td>WAXD</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_CRYSTALLINEPHASE.xlsx","WAXD")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>CRYSTALLINEPHASE</td><td>XRD</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_CRYSTALLINEPHASE.xlsx","XRD")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>DENSITY</td><td>density</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_DENSITY.xlsx","density")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>DENSITY</td><td>effective density</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_DENSITY.xlsx","effective density")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>DUSTINESS</td><td>Dustiness with large drum</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_DUSTINESS.xlsx","Dustiness with large drum")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>DUSTINESS</td><td>Dustiness with other set-up</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_DUSTINESS.xlsx","Dustiness with other set-up")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>DUSTINESS</td><td>Dustiness with small drum</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_DUSTINESS.xlsx","Dustiness with small drum")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>DUSTINESS</td><td>Dustiness with Vortex Shaker</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_DUSTINESS.xlsx","Dustiness with Vortex Shaker")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>IEP</td><td>isoElectric point</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_IEP.xlsx","isoElectric point")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>POTENTIOMETRY</td><td>Potentiometry_H2O sosp</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_POTENTIOMETRY.xlsx","Potentiometry_H2O sosp")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>SIZE</td><td>size PTA</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_SIZE.xlsx","size PTA")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>SIZE</td><td>size SAXS</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_SIZE.xlsx","size SAXS")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>SIZE</td><td>size TEM</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_SIZE.xlsx","size TEM")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>SIZE</td><td>size&PSD_sp-ICP-MS</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_SIZE.xlsx","size&PSD_sp-ICP-MS")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>SIZE</td><td>size_DLS</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_SIZE.xlsx","size_DLS")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>SIZE</td><td>size_WAXD</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_SIZE.xlsx","size_WAXD")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>SIZE</td><td>size_XRD</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_SIZE.xlsx","size_XRD")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>SOLUBILITY</td><td>colorimetry</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_SOLUBILITY.xlsx","colorimetry")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>SOLUBILITY</td><td>filtration_ICP-MS</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_SOLUBILITY.xlsx","filtration_ICP-MS")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>SOLUBILITY</td><td>filtration_WDXRF</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_SOLUBILITY.xlsx","filtration_WDXRF")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>SURFACECHEMISTRY</td><td>coating_MALDI-TOF</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_SURFACECHEMISTRY.xlsx","coating_MALDI-TOF")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>SURFACECHEMISTRY</td><td>coating_STEM-EDS</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_SURFACECHEMISTRY.xlsx","coating_STEM-EDS")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>SURFACECHEMISTRY</td><td>functional groups_ATR-FTIR</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_SURFACECHEMISTRY.xlsx","functional groups_ATR-FTIR")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>SURFACECHEMISTRY</td><td>functional groups_TGA</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_SURFACECHEMISTRY.xlsx","functional groups_TGA")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>VSSA</td><td>VSSA</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_VSSA.xlsx","VSSA")'>Download</a></td></tr>
<tr><td>PCHEM</td><td>ZETAPOTENTIAL</td><td>z-pot</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","PCHEM_ZETAPOTENTIAL.xlsx","z-pot")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>ALI</td><td>biochem analysis inhalation</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_ALI.xlsx","biochem analysis inhalation")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>ALI</td><td>cell counts</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_ALI.xlsx","cell counts")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>ALI</td><td>Cellularity</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_ALI.xlsx","Cellularity")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>ALI</td><td>mass concentration</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_ALI.xlsx","mass concentration")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>ALI</td><td>size distribution</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_ALI.xlsx","size distribution")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>BIODISTRIBUTION</td><td>Biodistribution</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_BIODISTRIBUTION.xlsx","Biodistribution")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>ECOTOX</td><td>soil - C. Elegans</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_ECOTOX.xlsx","soil - C. Elegans")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>ECOTOX</td><td>water - algae - P.subcap</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_ECOTOX.xlsx","water - algae - P.subcap")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>ECOTOX</td><td>water - fish - D Rerio</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_ECOTOX.xlsx","water - fish - D Rerio")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>ECOTOX</td><td>water - microalgae - D. Magna</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_ECOTOX.xlsx","water - microalgae - D. Magna")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>GENOTOX</td><td>chromosomal damage in vitro</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_GENOTOX.xlsx","chromosomal damage in vitro")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>GENOTOX</td><td>COMET</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_GENOTOX.xlsx","COMET")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>GENOTOX</td><td>Micronucleus</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_GENOTOX.xlsx","Micronucleus")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>GENOTOX</td><td>TEM observation</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_GENOTOX.xlsx","TEM observation")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>IMMUNOTOX</td><td>Blood counts</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_IMMUNOTOX.xlsx","Blood counts")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>IMMUNOTOX</td><td>Cytokine secretion MPh</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_IMMUNOTOX.xlsx","Cytokine secretion MPh")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>IMMUNOTOX</td><td>FACS analysis</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_IMMUNOTOX.xlsx","FACS analysis")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>IMMUNOTOX</td><td>NO production</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_IMMUNOTOX.xlsx","NO production")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>IMMUNOTOX</td><td>Polyclonal proliferation</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_IMMUNOTOX.xlsx","Polyclonal proliferation")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>IMMUNOTOX</td><td>Serum cytokine</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_IMMUNOTOX.xlsx","Serum cytokine")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>IMMUNOTOX</td><td>Serum total antibodies</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_IMMUNOTOX.xlsx","Serum total antibodies")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>INHALATION</td><td>cytokines levels</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_INHALATION.xlsx","cytokines levels")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>INHALATION</td><td>DNA-damage lung</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_INHALATION.xlsx","DNA-damage lung")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>INHALATION</td><td>hystopathology</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_INHALATION.xlsx","hystopathology")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>INHALATION</td><td>mRNA expression in vivo</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_INHALATION.xlsx","mRNA expression in vivo")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>INHALATION</td><td>MW digestion + ICP-MS</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_INHALATION.xlsx","MW digestion + ICP-MS")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>PATTERNDISTRIBUTION</td><td>CRM</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_PATTERNDISTRIBUTION.xlsx","CRM")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>PATTERNDISTRIBUTION</td><td>IBM</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_PATTERNDISTRIBUTION.xlsx","IBM")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>PATTERNDISTRIBUTION</td><td>PBPK</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_PATTERNDISTRIBUTION.xlsx","PBPK")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>PATTERNDISTRIBUTION</td><td>ToF-SIMS</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_PATTERNDISTRIBUTION.xlsx","ToF-SIMS")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>PULMONARY</td><td>biochemical analysis</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_PULMONARY.xlsx","biochemical analysis")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>PULMONARY</td><td>cytokines levels</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_PULMONARY.xlsx","cytokines levels")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>PULMONARY</td><td>DNA-damage lung</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_PULMONARY.xlsx","DNA-damage lung")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>PULMONARY</td><td>histopathology</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_PULMONARY.xlsx","histopathology")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>PULMONARY</td><td>mRNA expression in vivo</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_PULMONARY.xlsx","mRNA expression in vivo")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>TOXLONGTERM</td><td>COMET - sistemic genotox </td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_TOXLONGTERM.xlsx","COMET - sistemic genotox ")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>TOXLONGTERM</td><td>lung genotoxicity</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_TOXLONGTERM.xlsx","lung genotoxicity")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>TOXLONGTERM</td><td>Micronuclei - sistemic genotox</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_TOXLONGTERM.xlsx","Micronuclei - sistemic genotox")'>Download</a></td></tr>
<tr><td>INVIVO</td><td>TOXLONGTERM</td><td>PIG-A sistemic genotox</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVIVO_TOXLONGTERM.xlsx","PIG-A sistemic genotox")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>BARRIERCROSSING</td><td>barrier integrity - LY</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_BARRIERCROSSING.xlsx","barrier integrity - LY")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>BARRIERCROSSING</td><td>NP crossing ICP-OES</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_BARRIERCROSSING.xlsx","NP crossing ICP-OES")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>BARRIERCROSSING</td><td>NP crossing ToF-SIMS</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_BARRIERCROSSING.xlsx","NP crossing ToF-SIMS")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>BARRIERCROSSING</td><td>NP intracell pen(confocal micro</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_BARRIERCROSSING.xlsx","NP intracell pen(confocal micro")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>BARRIERCROSSING</td><td>NPs crossing TEM-SEM</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_BARRIERCROSSING.xlsx","NPs crossing TEM-SEM")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>BARRIERCROSSING</td><td>oral mucosa 3Dmodel penetration</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_BARRIERCROSSING.xlsx","oral mucosa 3Dmodel penetration")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>BARRIERCROSSING</td><td>TEER</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_BARRIERCROSSING.xlsx","TEER")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>BARRIERCROSSING</td><td>TEM observation</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_BARRIERCROSSING.xlsx","TEM observation")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>BARRIERCROSSING</td><td>TEM-SEM - intracell penetration</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_BARRIERCROSSING.xlsx","TEM-SEM - intracell penetration")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>CTA</td><td>hCTA</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_CTA.xlsx","hCTA")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>CYTOTOXICITY</td><td>LDH</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_CYTOTOXICITY.xlsx","LDH")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>CYTOTOXICITY</td><td>MTS</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_CYTOTOXICITY.xlsx","MTS")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>CYTOTOXICITY</td><td>Resazurin</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_CYTOTOXICITY.xlsx","Resazurin")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>CYTOTOXICITY</td><td>Trypan blue</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_CYTOTOXICITY.xlsx","Trypan blue")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>GENOTOXICITY</td><td>chromosomal damage in vitro</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_GENOTOXICITY.xlsx","chromosomal damage in vitro")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>GENOTOXICITY</td><td>COMET</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_GENOTOXICITY.xlsx","COMET")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>GENOTOXICITY</td><td>Micronucleus</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_GENOTOXICITY.xlsx","Micronucleus")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>GENOTOXICITY</td><td>TEM observation</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_GENOTOXICITY.xlsx","TEM observation")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>IMMUNOTOX</td><td>Aerosol Characterisation</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_IMMUNOTOX.xlsx","Aerosol Characterisation")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>IMMUNOTOX</td><td>Apoptosis</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_IMMUNOTOX.xlsx","Apoptosis")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>IMMUNOTOX</td><td>Aspect of deposit</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_IMMUNOTOX.xlsx","Aspect of deposit")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>IMMUNOTOX</td><td>cytokine secretion</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_IMMUNOTOX.xlsx","cytokine secretion")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>IMMUNOTOX</td><td>NO production</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_IMMUNOTOX.xlsx","NO production")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>IMMUNOTOX</td><td>TEM observation</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_IMMUNOTOX.xlsx","TEM observation")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>OCCULARIRRITATON</td><td>hemolysis</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_OCCULARIRRITATON.xlsx","hemolysis")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>VIABILITY</td><td>Alamar blue</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_VIABILITY.xlsx","Alamar blue")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>VIABILITY</td><td>CFE</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_VIABILITY.xlsx","CFE")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>VIABILITY</td><td>Impedance_flow cytom</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_VIABILITY.xlsx","Impedence_flow cytom")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>VIABILITY</td><td>LDH</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_VIABILITY.xlsx","LDH")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>VIABILITY</td><td>MTS</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_VIABILITY.xlsx","MTS")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>VIABILITY</td><td>Neutral red</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_VIABILITY.xlsx","Neutral red")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>VIABILITY</td><td>Resazurin</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_VIABILITY.xlsx","Resazurin")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>VIABILITY</td><td>RT- Impedence_adherence cell</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_VIABILITY.xlsx","RT- Impedence_adherence cell")'>Download</a></td></tr>
<tr><td>INVITRO</td><td>VIABILITY</td><td>Trypan blue</td><td><a href='#' onClick='downloadTemplate("${ambit_root}","INVITRO_VIABILITY.xlsx","Trypan blue")'>Download</a></td></tr>

</tbody>
</table> 		
			
		</div> 
		
		<div class="two columns remove-bottom" style="padding:0;" >

		<!-- help-->		
		<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
		<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'>		
		</div>
		<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'>		
		</div>		
		
		</div>
		
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>