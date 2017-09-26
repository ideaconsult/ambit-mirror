<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<link rel="stylesheet" href="${ambit_root}/style/jBox.css" type="text/css">
<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>
<link rel="stylesheet" href="${ambit_root}/style/ra/ui-matrix.css"/>

<script src="${ambit_root}/scripts/ra/i5enums.js"></script>
<link rel="stylesheet" href="${ambit_root}/style/jquery.tokenize.css"/>

<script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/colResizable-1.6.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/es6-shim.min.js'></script>
<script type='text/javascript'  src="${ambit_root}/jquery/jquery.tokenize.js"></script>
<script type='text/javascript' src='${ambit_root}/scripts/jBox.js'></script>

<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>

<script src="${ambit_root}/scripts/ra/ui-matrix.js"></script>
<script src="${ambit_root}/scripts/ra/config-matrix.js"></script>

<link rel="stylesheet" href="${ambit_root}/style/ketcher.css"/>
<script type='text/javascript' src='${ambit_root}/scripts/ketcher.js'></script>


<script type='text/javascript' src='${ambit_root}/scripts/config/i5.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config/ce.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config/npo.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config/bao.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config/toxcast.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config-study.js'></script>

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
				 	    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle/${bundleid}/version" title="Assessment versions">Versions</a></li>');        	
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
						
							
						//var sb = new jToxSearch($("#searchbar")[0], {crossDomain: false, baseUrl : "${ambit_root}/" } );
						//sb.makeQuery("50-00-0");
						
											 	
				});
</script>  
</head>
<body>
<div class="container" style="margin:0;padding:0;">
  <div id="logger" class="jtox-toolkit jtox-widget hidden" data-kit="log" data-resend="false" data-right-side="true" data-base-url="${ambit_root}"></div>

  <#include "/banner_crumbs.ftl">
  
	<!-- Page Content
	================================================== -->
	
  <div class="sixteen columns remove-bottom" style="padding:0;" >

  <div id="jtox-bundle" class="jtox-toolkit" data-kit="bundle" data-base-url="${ambit_root}"
	  <#if bundleid??>
				data-bundle-uri="${ambit_root}/bundle/${bundleid}"
			</#if>	
	  >
	  
    <ul>
      <li><a href="#jtox-identifiers">Assessment identifier</a></li>
      <li><a href="#jtox-structures">Collect structures</a></li>
      <li><a href="#jtox-endpoints">Endpoint data used</a></li>
      <li><a href="#jtox-matrix">Assessment details</a></li>
      <li><a href="#jtox-report">Report</a></li>
    </ul>
    <div id="jtox-identifiers" data-action="onIdentifiers">
	  	<form name="raform"  >
	  	<div class="thirteen columns remove-bottom" style="padding:0;" >
	      <table class="dataTable">
	        <thead>
	          <tr><th class="right size-third"><label for="title" id="l_title">Title</label>*<a href='#' class='chelp a_name'>?</a>:</th><td><input class="data-field first-time validate" data-field="title" name="title" id="title" required /></td></tr>
            <tr><th class="right size-third"><label for="maintainer" id="l_maintainer">Maintainer</label>*<a href='#' class='chelp a_maintainer'>?</a>:</th><td><input class="data-field first-time validate" data-field="maintainer" name="maintainer" id="maintainer" required /></td></tr>

            <tr><th class="right top size-third"><label for="description">Purpose</label>*<a href='#' class='chelp a_description'>?</a>:</th><td><textarea class="validate nomargin data-field" data-field="description" name="description" id="description" required ></textarea></td></tr>
            <tr><th class="right size-third">Version <a href='#' class='chelp a_version'>?</a>:</th><td class="data-field" data-field="version">?.?</td></tr>
            <tr><th class="right size-third">Version start date <a href='#' class='chelp a_version_date'>?</a>:</th><td class="data-field" data-field="created" data-format="formatDate"></td></tr>
            <tr><th class="right size-third">Version last modified on <a href='#' class='chelp a_version_date'>?</a>:</th><td class="data-field" data-field="updated" data-format="formatDate"></td></tr>
            <tr>
              <th class="right size-third">Status<a href='#' class='chelp a_published'>?</a>:</th>
              <td class="data-field" data-field="status" data-format="formatStatus"></td>
            </tr>
            <tr class="lri_hide"><th class="right size-third"><label for="license">License</label>*:</th><td><input class="data-field first-time validate" data-field="rights.URI" name="license" id="license" /></td></tr>
            <tr class="lri_hide"><th class="right size-third"><label for="rightsHolder">Rights holder</label>*<a href='#' class='chelp a_rightsholder'>?</a>:</th><td><input class="data-field first-time validate" data-field="rightsHolder" name="rightsHolder" id="rightsHolder" /></td></tr>
	          <tr ><th class="right size-third"><label for="seeAlso" id="l_seeAlso">See also</label>*<a href='#' class='chelp a_code'>?</a>:</th><td><input class="data-field first-time validate" data-field="seeAlso" name="seeAlso" id="seeAlso" required /></td></tr>
	          <tr >
              <th class="right size-third"><label for="source" id="l_source">Source URL</label>*<a href='#' class='chelp a_doclink'>?</a>:</th>
              <td>
                <input class="data-field first-time validate" data-field="source" name="source" id="source" required />
                <a href="" id="source-link" target="_blank" class="ui-icon ui-icon-extlink">open link</a>
              </td>
            </tr>
            <tr><th class="right size-third"><label for="number" id="l_number">Identifier</label><a href='#' class='chelp assessment'>?</a>:</th><td class="data-field" data-field="number"></td></tr>
	          <tr class="lri_hide"><th class="right size-third">Rating <a href='#' class='chelp a_rating'>?</a>:</th><td class="data-stars-field"><input type="hidden" name="stars" value="0"/></td></tr>
	         <#if menu_profile??> 
	       	<#switch menu_profile>
	       	<#case 'lri'>	          
            <tr class="aadb">
              <th class="right size-third top"><label for="users-write">Users with write access</label><a href='#' class='chelp bundle_rw'>?</a>:</th>
              <td class="jtox-user-rights">
                <select name="users-write" id="users-write" multiple class="jtox-users-select">
                </select>
                <button type="button" class="jtox-users-submit">Save</button>
              </td>
            </tr>
            <tr class="aadb">
              <th class="right size-third top"><label for="users-read">Users with read access</label><a href='#' class='chelp bundle_rw'>?</a>:</th>
              <td class="jtox-user-rights">
                <select name="users-read" id="users-read" multiple class="jtox-users-select">
                </select>
                <button type="button" class="jtox-users-submit">Save</button>
              </td>
            </tr>
            <#break>
	       	<#default>
	       	</#switch>
	       	</#if>
	        </thead>
	      </table>
	      <div class="actions default_hide">
		      <button name="assStart" type="submit" class="default_hide">Start</button>
		      <button name="assFinalize" type="button" class="default_hide">Finalize Assessment</button>
		      <button name="assNewVersion" type="button" class="default_hide">Generate new version</button>
	      </div>
	      </div>
	      <div class="three columns remove-bottom">
				<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
				<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'></div>
				<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'></div>
				      
	      </div>	      
	  	</form>
	  </div>
    <div id="jtox-structures">
	  	<div class="jq-buttonset center action" data-action="onStructures">
			  <input type="radio" id="structcollect" name="structaction" checked="checked"><label for="structcollect">Collect structures</label></input>
			  <input type="radio" id="structlist" name="structaction"><label for="structlist">List collected</label></input>
			</div>
      <div id="jtox-query" class="jtox-toolkit" data-kit="query" data-configuration="jTConfigurator" data-initial-query="false" data-base-url="${ambit_root}">
        <div class="jtox-foldable folded">
          <div class="title"><p class="data-field" data-field="title">Search</p></div>
          <div class="content">
            <div id="searchbar" class="jtox-toolkit jtox-widget" data-kit="search" data-hide-options="url,context" data-base-url="${ambit_root}"></div>
          </div>
        </div>
        <div id="browser" class="jtox-toolkit" data-kit="compound" data-show-tabs="false" data-hide-empty="true" data-on-details="onDetailedRow" data-details-height="500px" data-show-diagrams="true" data-on-loaded="onBrowserFilled" data-base-url="${ambit_root}"></div>
      </div>
    </div>
    <div id="jtox-endpoints">
	  	<div class="jq-buttonset center action" data-action="onEndpoint">
			  <input type="radio" id="endsubstance" name="endaction" checked="checked"><label for="endsubstance">Search substance(s)</label></input>
			  <input type="radio" id="endpoints" name="endaction"><label for="endpoints">Selection of endpoints</label></input>
			</div>
			<div class="size-full">
  			<div class="jtox-slidable">

          <div class="jtox-inline tab-substance">
            <div class="float-right">
              <button type="button" id="structures-expand-all">Expand all</button><button type="button" id="structures-collapse-all">Collapse all</button>
            </div>
            <div id="jtox-substance-query" class="jtox-toolkit" data-kit="query" data-configuration="jTConfigurator" data-initial-query="false" data-base-url="${ambit_root}">
              <div id="substance-browser" class="jtox-toolkit" data-kit="compound" data-show-tabs="false" data-hide-empty="true" data-pre-details="preDetailedRow" data-show-diagrams="true" data-base-url="${ambit_root}"></div>
            </div>
          </div>

          <div class="jtox-inline tab-points">
            <div class="check-all">
              <label for="endpointAll"><input type="checkbox" name="endpointAll" id="endpointAll"/> Show all endpoints</label>
            </div>
          </div>

  			</div>
  		</div>
		</div>
    <div id="jtox-matrix">
	  	<div class="jq-buttonset center action" data-action="onMatrix">
			  <input type="radio" id="xinitial" name="xaction" checked="checked"><label for="xinitial">Initial matrix</label></input>
			  <input type="radio" id="xworking" name="xaction"><label for="xworking">Working matrix</label></input>
			  <input type="radio" id="xfinal" name="xaction"><label for="xfinal">Final matrix</label></input>
			</div>
			<button class="save-button jt-disabled">Saved</button>
			<button class="create-button">Create working copy</button>
			<div class="jtox-toolkit" data-kit="compound" data-manual-init="true" data-base-url="${ambit_root}"></div>
    </div>
    <div id="jtox-report" class="jtox-report">
    <#if bundleid??>
      <p>
        <a href="${ambit_root}/ui/assessment_report?bundle_uri=${ambit_root}/bundle/${bundleid}" id="open-report">Create assessment report</a>
      </p>
      <p>
        <a href="${ambit_root}/bundle/${bundleid}/substance?media=application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" id="export-substance">Create Excel file with all used experimental data</a>
      </p>
      <p>
        <a href="${ambit_root}/bundle/${bundleid}/dataset?media=application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" id="export-initial-matrix">Create Excel file with the initial matrix</a>
      </p>
      <p>
        <a href="${ambit_root}/bundle/${bundleid}/matrix?media=application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" id="export-working-matrix">Create Excel file with the working matrix</a>
      </p>

      </#if>
    </div>
  </div>
  <div class="jtox-template">
    <div id="info-box">
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
      <table class="delete-box">
	      <tr>
		      <td><textarea placeholder="Reason for deleting_"></textarea></td>
		      <td><button class="jt-alert">Delete</button></td>
	      </tr>
      </table>
    </div>
    <div id="edit-box">
	    <div class="jtox-medium-box box-field" data-name="type">
  	    <div class="jtox-details font-heavy jtox-required">Study type</div>
  	    <select class="data-field type-list" data-field="type"><option value="-1"> - Select type - </option></select>
	    </div>
	    <div class="jtox-medium-box box-field" data-name="reference">
  	    <div class="jtox-details font-heavy jtox-required">Reference</div>
  	    <input type="text" class="data-field" data-field="reference" placeholder="Reference_"/>
	    </div>
	    <div class="jtox-medium-box box-field size-full" data-name="justification">
  	    <div class="jtox-details font-heavy jtox-required">Guideline or Justification</div>
  	    <textarea class="data-field" data-field="justification" placeholder="Justification_"></textarea>
	    </div>
	    <div class="jtox-medium-box box-field size-full" data-name="remarks">
  	    <div class="jtox-details font-heavy">Remarks</div>
  	    <textarea class="data-field" data-field="remarks" placeholder="Remarks_"></textarea>
	    </div>
      <div class="size-full the-send">
        <span class="data-field the-endpoint" data-field="endpoint"></span>
        <input value="Apply" type="button"/>
      </div>
    </div>
  </div>
  <!-- sixteen columns -->
  </div>
  
  
  <div class='row add-bottom'>&nbsp;</div>

  <div class="sixteen  columns add-bottom">&nbsp;</div>


<#if menu_profile??>
	<#include "/menu/profile/${menu_profile}/footer.ftl" >
</#if>

		
<#if username??>
	    <form id='logoutForm' name='logoutForm'  action='${ambit_root}/provider/signout?targetUri=${ambit_root}&method=DELETE' method='POST'><input type='hidden' value="${username}"></input></form>
</#if>

	

<!-- container -->  
</div>  
</body>
</html>
