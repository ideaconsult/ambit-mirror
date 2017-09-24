<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >


<link rel="stylesheet" href="${ambit_root}/style/jBox.css" type="text/css">
<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>
	
<link rel="stylesheet" href="${ambit_root}/style/jquery.tokenize.css"/>
<link rel="stylesheet" href="${ambit_root}/style/ra/ui-matrix.css"/>
	
  <script src="${ambit_root}/scripts/ra/i5enums.js"></script>

  <script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js'></script>
  <script type='text/javascript' src='${ambit_root}/scripts/colResizable-1.6.min.js'></script>
  <script type='text/javascript' src='${ambit_root}/scripts/es6-shim.min.js'></script>
  <script type='text/javascript'  src="${ambit_root}/jquery/jquery.tokenize.js"></script>
  <script type='text/javascript' src='${ambit_root}/scripts/jBox.js'></script>

  <script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>

  <script src="${ambit_root}/scripts/ra/ui-matrix.js"></script>
  <script src="${ambit_root}/scripts/ra/config-matrix.js"></script>

  <script type='text/javascript' src='${ambit_root}/scripts/config/i5.js'></script>
  <script type='text/javascript' src='${ambit_root}/scripts/config/ce.js'></script>
  <script type='text/javascript' src='${ambit_root}/scripts/config/npo.js'></script>
  <script type='text/javascript' src='${ambit_root}/scripts/config/bao.js'></script>
  <script type='text/javascript' src='${ambit_root}/scripts/config/toxcast.js'></script>
  <script type='text/javascript' src='${ambit_root}/scripts/config-study.js'></script>
  
   <script src="${ambit_root}/scripts/ie-shims.js"></script>
  <script src="${ambit_root}/scripts/ra/docxgen.js"></script>
  <script src="${ambit_root}/scripts/ra/docxgen-image.js"></script>

  <script src="${ambit_root}/scripts/ra/FileSaver.min.js"></script>
  <script src="${ambit_root}/scripts/ra/jszip-utils.js"></script>

  <script src="${ambit_root}/scripts/es6-promise.min.js"></script>
	

  <script>
    jToxBundle.settings.onLoaded = function(){
      this.onReport();
    }
  </script>

  <script type='text/javascript'>
  
  
var customize = {
    "lri": {
        "title": {
            "l_title": "Assessment title",
            "l_maintainer": "Owner",
            "l_number": "Assessment ID",
            "l_seeAlso": "Assessment code",
            "l_source": "Assessment Doclink(s)"
        },
        "hide": "lri_hide"
    },
    "default" : {
    	"title" : {},	
    	"hide": "default_hide"
    }
};

  $(document)
  		.ready(
  				function() {
  				        jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle?page=0&pagesize=100" title="Assessments">All assessments</a></li>');
  				        <#if bundleid??>
  				    	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/assessment?bundle_uri=${ambit_root}/bundle/${bundleid}" title="Assessment">This assessment</a></li>');        
				        jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/assessment_report?bundle_uri=${ambit_root}/bundle/${bundleid}" title="Report">Report</a></li>');
  				        </#if>
  				        
  				        loadHelp("${ambit_root}","ra");
  				        
  						var profile = "default";
  						<#if menu_profile??>
  							profile = "${menu_profile}";
  						</#if>
  						var c_profile = customize[profile];
  						if (c_profile == undefined) { profile ="default" ; c_profile = customize[profile]};
  							
  						$.each(customize[profile]["title"],function(key,value) {
  								try {$("#"+key).html(value);} catch (err) {};
  							});
  						try {$("." + customize[profile]["hide"]).hide();	} catch (err) {};				        
  				});
  </script>  
</head>
<body class="jtox-report">
<div class="container" style="margin: 0 0 10 10 ;padding:0;">
  <div id="logger" class="jtox-toolkit jtox-widget hidden" data-kit="log" data-resend="false" data-right-side="true"></div>

  <#include "/banner_crumbs.ftl">
  
	<!-- Page Content
	================================================== -->
	 <div class="sixteen columns remove-bottom" style="padding:0;" >
	
  <div id="jtox-bundle" class="jtox-toolkit" data-kit="bundle" data-base-url="${ambit_root}">

    <div id="jtox-report" class="jtox-report">

      <button type="button" id="generate-doc">Create Word file</button>

      <div id="jtox-report-cover">
        <h1>Ambit Assessment Report</h1>
        <h2 class="data-field" data-field="title"></h2>
        <dl>
          <dt>Author:</dt><dd class="data-field" data-field="maintainer"></dd>
          <dt>Date:</dt><dd class="data-field" data-field="created" data-format="formatDate"></dd>
          <dt>Assessment code:</dt><dd class="data-field" data-field="number"></dd>
          <dt>Purpose:</dt><dd class="data-field" data-field="description"></dd>
        </dl>
      </div>

      <section id="jtox-report-identifiers">
        <h2>Assessment Identifiers</h2>

        <table class="dataTable">
          <thead>
            <tr><th class="right size-third">Assessment title:</th><td class="data-field" data-field="title"></td></tr>
            <tr><th class="right size-third">Owner:</th><td class="data-field" data-field="maintainer"></td></tr>

            <tr><th class="right top size-third">Purpose:</th><td class="data-field" data-field="description"></td></tr>
            <tr><th class="right size-third">Version:</th><td class="data-field" data-field="version">?.?</td></tr>
            <tr><th class="right size-third">Status:</th><td class="data-field" data-field="status"></td></tr>
            <tr><th class="right size-third">Version start date:</th><td class="data-field" data-field="created" data-format="formatDate"></td></tr>
            <tr><th class="right size-third">Version last modified on:</th><td class="data-field" data-field="updated" data-format="formatDate"></td></tr>
            <tr><th class="right size-third">Published:</th><td class="data-field" data-field="status"></td></tr>
            <tr><th class="right size-third">Assessment code:</th><td class="data-field" data-field="seeAlso"></td></tr>
            <tr><th class="right size-third">Assessment DocLink:</th><td class="data-field" data-field="source" data-format="formatLink"></td></tr>
            <tr><th class="right size-third">Assessment ID:</th><td class="data-field" data-field="number"></td></tr>
          </thead>
        </table>

        <p>The original assessment in Ambit can be found via <a href="${ambit_root}/ui/assessment?bundle_uri=${ambit_root}/bundle/${bundleid}">Assessment ID</a> </p>

      </section>

      <section id="jtox-report-structlist">
        <h2>List of structures for assessment</h2>
        <p>In the assessment, similar structures were selected from exact structure, substructure and/or similarity searches, or were added manually. The rationale for the selection is given in the table.</p>
        <div id="jtox-report-query" class="jtox-toolkit" data-kit="query" data-configuration="jTConfigurator" data-initial-query="false" data-base-url="${ambit_root}">
          <div id="browser" class="jtox-toolkit" data-kit="compound" data-show-tabs="false" data-hide-empty="true" data-on-details="onDetailedRow" data-details-height="500px" data-show-diagrams="true" data-on-loaded="onBrowserFilled"></div>
        </div>
      </section>

      <section id="jtox-report-substances">
        <h2>List of substances related to the structures</h2>
        <p>In the following, for each structure listed in chapter 2, substances were selected and the rationale is given.</p>
        <div id="jtox-report-substance-query" class="jtox-toolkit" data-kit="query" data-configuration="jTConfigurator" data-initial-query="false" data-base-url="${ambit_root}">
          <div id="report-substance-browser" class="jtox-toolkit" data-kit="compound" data-show-tabs="false" data-hide-empty="true" data-pre-details="preDetailedRow" data-show-diagrams="true" data-on-loaded="onReportSubstancesLoaded" data-base-url="${ambit_root}"></div>
        </div>
      </section>

      <section id="jtox-report-matrix">
        <h2>Substance composition matrix</h2>
        <p>In the following, for each substance, the associated structure(s) and the composition are given.</p>
        <div class="jtox-toolkit" data-kit="compound" data-manual-init="true" data-base-url="${ambit_root}"></div>
      </section>

      <section id="jtox-report-final">
        <h2>Assessment data matrix</h2>
        <p>In the following, for each substance, the associated endpoint data are given, either experimental data, waiving or read-across.</p>
        <p>For detailed data or rationale for waiving and read-across, click hyperlinks in the table. These data or rationales can also be found in the annex of the report.</p>
        <div class="jtox-toolkit" data-manual-init="true" data-base-url="${ambit_root}"></div>
      </section>

      <section id="jtox-report-justification">
        <h2>Justification for read-across / category</h2>
        <div>
          <textarea id="report-justification" name="report-justification"></textarea>
        </div>
      </section>

      <section class="annex" id="jtox-report-experimental-data">
        <h2>Experimental data</h2>

        <p>
          <a href="${ambit_root}/bundle/${bundleid}/substance?media=application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" id="export-substance">Create Excel file with all used experimental data</a>
        </p>

      </section>

      <section class="annex" id="jtox-report-gap-filling">
        <h2>Rationale for gap filling</h2>
      </section>

      <section class="annex" id="jtox-report-deleting-data">
        <h2>Rationale for deleting experimental data</h2>
      </section>

      <section class="annex" id="jtox-report-initial-matrix">
        <h2>Initial matrix</h2>

        <p>
          <a href="${ambit_root}/bundle/${bundleid}/dataset?media=application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" id="export-initial-matrix">Create Excel file with the initial matrix</a>
        </p>

      </section>

      <section class="annex" id="jtox-report-working-matrix">
        <h2>Working matrix</h2>

        <p>
          <a href="${ambit_root}/bundle/${bundleid}/matrix?media=application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" id="export-working-matrix">Create Excel file with the working matrix</a>
        </p>

      </section>

    </div>
  </div>
  <div class="jtox-template">
    <section id="jtox-report-substance">
      <h3>Substance <span class="data-field" data-field="number"></span>: <span class="data-field" data-field="name"></span></h3>
    </section>
    <div id="jtox-report-feature">
      <h4 class="data-field" data-field="title"></h4>
    </div>
    <div id="info-box" class="popup-box">
      <table>
        <thead>
          <tr>
            <th rowspan="2">Endpoint</th>
            <th rowspan="2">Value</th>
            <th class="conditions center">Conditions</th>
            <th rowspan="2">Guideline or Justification</th>
          </tr>
          <tr class="conditions">
          </tr>
        </thead>
        <tbody>
          <tr>
            <td class="data-field the-endpoint" data-field="endpoint"></td>
            <td class="data-field the-value non-breakable" data-field="value"></td>
            <td class="data-field postconditions" data-field="guidance"></td>
          </tr>
        </tbody>
      </table>
      <h5>Rationale for <span class="data-field" data-field="studyType"></span></h5>
      <p class="justification data-field" data-field="remarks">
      </p>
    </div>
  </div>
<!-- sixteen column -->  
</div>
<!-- container -->  
</div>  
</body>
</html>
