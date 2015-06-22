<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

	<link rel="stylesheet" href="${ambit_root}/style/jBox.css" type="text/css">
	<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>
	<link rel="stylesheet" href="${ambit_root}/style/ketcher.css"/>
	<link rel="stylesheet" href="${ambit_root}/style/ra/ui-matrix.css"/>
	
  <script src="${ambit_root}/scripts/ra/i5enums.js"></script>
  <link rel="stylesheet" href="${ambit_root}/style/jquery.tokenize.css"/>

  <script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js'></script>
  <script type='text/javascript' src='${ambit_root}/scripts/colResizable-1.3.min.js'></script>
  <script type='text/javascript'  src="${ambit_root}/jquery/jquery.tokenize.js"></script>
  <script type='text/javascript' src='${ambit_root}/scripts/jBox.js'></script>
  
  <script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>
  <script type='text/javascript' src='${ambit_root}/scripts/ketcher.js'></script>
  
    <script src="${ambit_root}/scripts/ra/ui-matrix.js"></script>
  <script src="${ambit_root}/scripts/ra/config-matrix.js"></script>
  
 <script type='text/javascript' src='${ambit_root}/scripts/config/ce.js'></script>
 <script type='text/javascript' src='${ambit_root}/scripts/config/toxcast.js'></script>
  <script type='text/javascript' src='${ambit_root}/scripts/config-study.js'></script>

  
  <script type='text/javascript'>

	$(document).ready(function() {
        jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle?page=0&pagesize=100" title="Assessments">All assessments</a></li>');
        <#if bundleid??>
        jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle/${bundleid}" title="Assessment">This assessment</a></li>');
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle/${bundleid}/version" title="Assessment versions">Versions</a></li>');        
        </#if>
        loadHelp("${ambit_root}","ra");
	});
	
	</script>
  
</head>
<body>

<div class="container" style="margin:0;padding:0;">
<div id="logger" class="jtox-toolkit jtox-widget hidden" data-kit="log" data-resend="false" rightSide="true"></div>

<#include "/banner_crumbs.ftl">
	
		<!-- Page Content
		================================================== -->
	<div class="sixteen columns remove-bottom" style="padding:0;" >

  <div id="jtox-bundle" class="jtox-toolkit" data-kit="bundle" 
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
	  	<form>
	  	  <div class="thirteen columns remove-bottom">
	      <table class="dataTable">

	        <thead>
	       <#if menu_profile??> 
	       <#switch menu_profile>
	       	<#case 'lri'>
	          <tr><th class="right size-third">Assessment title <a href='#' class='chelp a_name'>?</a>:</th><td><input class="data-field first-time validate" data-field="title" name="title"/></td></tr>
	          <tr><th class="right size-third">Owner<a href='#' class='chelp a_maintainer'>?</a>:</th><td><input class="data-field first-time validate" data-field="owner" name="maintainer"/></td></tr>
	
	          <tr><th class="right top size-third">Purpose<a href='#' class='chelp a_description'>?</a>:</th><td><textarea class="validate nomargin data-field" data-field="description" name="description"></textarea></td></tr>
	          <tr><th class="right size-third">Version<a href='#' class='chelp a_version'>?</a>:</th><td class="data-field" data-field="version">?.?</td></tr>

	          <tr><th class="right size-third">Version start date <a href='#' class='chelp a_version_date'>?</a>:</th><td class="data-field" data-field="created" data-format="formatDate"></td></tr>
	          <tr><th class="right size-third">Version last modified on<a href='#' class='chelp a_version_date'>?</a>:</th><td class="data-field" data-field="updated" data-format="formatDate"></td></tr>
	          <tr>
	            <th class="right size-third">Published <a href='#' class='chelp a_published'>?</a>:</th>
	            <td>
	              <select name="status" class="data-field" data-field="status">
	                <option value="draft">No</option>
	                <option value="published">Yes</option>
	              </select>
	            </td>
	            </tr>
          
	            <tr><th class="right size-third">Assessment code <a href='#' class='chelp a_code'>?</a>:</th><td><input class="data-field first-time validate" data-field="seeAlso" name="source"/></td></tr>
	            <tr><th class="right size-third">Assessment Doclink(s) <a href='#' class='chelp a_doclink'>?</a>:</th><td><input class="data-field first-time validate" data-field="source" name="url" /></td></tr>
	            
	       		<span style="display:none;">
       			<input class="data-field first-time validate" data-field="rights.URI" name="license" value="${ambit_root}"/>
       			<#if username??>
       				<input class="data-field first-time validate" data-field="rightsHolder" name="rightsHolder" value="${username}"/>
       			<#else>
       				<input class="data-field first-time validate" data-field="rightsHolder" name="rightsHolder" value="Default"/>
       			</#if>
       			<span class="data-stars-field"><input type="hidden" name="stars" value="5" /></span>
       			</span>
                <tr><th class="right size-third">Assessment ID<a href='#' class='chelp assessment'>?</a>:</th><td class="data-field" data-field="number"></td></tr>
                
                <tr>
                <th class="right size-third top">Users with write access <a href='#' class='chelp bundle_rw'>?</a>:</th>
                <td class="jtox-user-rights">
                  <select name="users-write" id="users-write" multiple class="jtox-users-select">
                  </select>
                </td>
              </tr>
              <tr>
                <th class="right size-third top">Users with read access <a href='#' class='chelp bundle_rw'>?</a>:</th>
                <td class="jtox-user-rights">
                  <select name="users-read" id="users-read" multiple class="jtox-users-select">
                  </select>
                </td>
              </tr>
              
	       	<#break>
	       	<#default>
	          <tr><th class="right size-third">Title <a href='#' class='chelp a_name'>?</a>:</th><td><input class="data-field first-time validate" data-field="title" name="title"/></td></tr>
	          <tr><th class="right size-third">Maintainer<a href='#' class='chelp a_maintainer'>?</a>:</th><td><input class="data-field first-time validate" data-field="maintainer" name="maintainer"/></td></tr>
	
	          <tr><th class="right top size-third">Purpose<a href='#' class='chelp a_description'>?</a>:</th><td><textarea class="validate nomargin data-field" data-field="description" name="description"></textarea></td></tr>
	          <tr><th class="right size-third">Version<a href='#' class='chelp a_version'>?</a>:</th><td class="data-field" data-field="version">?.?</td></tr>

	          <tr><th class="right size-third">Version start date<a href='#' class='chelp a_version_date'>?</a>:</th><td class="data-field" data-field="created" data-format="formatDate"></td></tr>
	          <tr><th class="right size-third">Version last modified on<a href='#' class='chelp a_version_date'>?</a>:</th><td class="data-field" data-field="updated" data-format="formatDate"></td></tr>
	          <tr><th class="right size-third">Published status<a href='#' class='chelp a_published_status'>?</a>:</th><td class="data-field" data-field="status"></td></tr>	          
          
	            <tr><th class="right size-third">Source<a href='#' class='chelp a_source'>?</a>:</th><td><input class="data-field first-time validate" data-field="seeAlso" name="source"/></td></tr>
	            <tr><th class="right size-third">Source URL<a href='#' class='chelp a_source'>?</a>:</th><td><input class="data-field first-time validate" data-field="source" name="url"/></td></tr>	       	
	       		<tr><th class="right size-third">License or Waiver<a href='#' class='chelp a_license'>?</a>:</th><td><input class="data-field first-time validate" data-field="rights.URI" name="license"/></td></tr>
	       		<tr><th class="right size-third">Rights holder<a href='#' class='chelp a_rightsholder'>?</a>:</th><td><input class="data-field first-time validate" data-field="rightsHolder" name="rightsHolder"/></td></tr>
	       		<tr><th class="right size-third">Rating <a href='#' class='chelp a_rating'>?</a>:</th><td class="data-stars-field"><input type="hidden" name="stars" value="0"/></td></tr>
	            <tr><th class="right size-third">Identifier <a href='#' class='chelp assessment'>?</a>:</th><td class="data-field" data-field="number"></td></tr>
	       	</#switch>
		   </#if>       

            
          
	        </thead>
	      </table>
	      <div class="actions">
		      <button name="assStart" type="button">Start</button>
		      <button name="assFinalize" type="button">Finalize</button>
		      <button name="assNewVersion" type="button">Generate new version</button>
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
      <div id="jtox-query" class="jtox-toolkit" data-kit="query" data-cross-domain="true" data-configuration="jTConfigurator" data-initial-query="false">
        <div class="jtox-foldable folded">
          <div class="title"><p class="data-field" data-field="title">Search</p></div>
          <div class="content">
            <div id="searchbar" class="jtox-toolkit jtox-widget" data-kit="search" data-hide-options="url,context"></div>
          </div>
        </div>
        <div id="browser" class="jtox-toolkit" data-kit="compound" data-show-tabs="false" data-hide-empty="true" data-on-details="onDetailedRow" data-details-height="500px" data-show-diagrams="true" data-on-loaded="onBrowserFilled"></div>
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
            <div id="jtox-substance-query" class="jtox-toolkit" data-kit="query" data-cross-domain="true" data-configuration="jTConfigurator" data-initial-query="false">
              <div id="substance-browser" class="jtox-toolkit" data-kit="compound" data-show-tabs="false" data-hide-empty="true" data-pre-details="preDetailedRow" data-show-diagrams="true"></div>
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
			<button class="create-final-button">Create final matrix</button>
			<div class="jtox-toolkit" data-kit="compound" data-manual-init="true"></div>
    </div>
    <div id="jtox-report" class="jtox-report" data-action="onReport">

      <div id="jtox-report-cover">
        <h1>Ambit Assessment Report</h1>
        <h2 class="data-field" data-field="title">Category For Glymes</h2>
        <dl>
          <dt>Author:</dt><dd class="data-field" data-field="maintainer"></dd>
          <dt>Company:</dt><dd class="data-field" data-field="rightsHolder"></dd>
          <dt>Date:</dt><dd class="data-field" data-field="created" data-format="formatDate"></dd>
          <dt>Assessment code:</dt><dd class="data-field" data-field="number"></dd>
          <dt>Purpose:</dt><dd class="data-field" data-field="description"></dd>
        </dl>
      </div>

      <section id="jtox-report-identifiers">
        <h2>Assessment Identifiers</h2>

        <table class="dataTable">
          <thead>
            <tr><th class="right size-third">Title:</th><td class="data-field" data-field="title"></td></tr>
            <tr><th class="right size-third">Maintainer:</th><td class="data-field" data-field="maintainer"></td></tr>

            <tr><th class="right top size-third">Purpose:</th><td class="data-field" data-field="description"></td></tr>
            <tr><th class="right size-third">Version:</th><td class="data-field" data-field="version">?.?</td></tr>
            <tr><th class="right size-third">Status:</th><td class="data-field" data-field="status"></td></tr>
            <tr><th class="right size-third">Version start date:</th><td class="data-field" data-field="created" data-format="formatDate"></td></tr>
            <tr><th class="right size-third">Version last modified on:</th><td class="data-field" data-field="updated" data-format="formatDate"></td></tr>
            <tr><th class="right size-third">Published:</th><td class="data-field" data-field="status"></td></tr>
            <tr><th class="right size-third">License:</th><td class="data-field" data-field="rights.URI"></td></tr>
            <tr><th class="right size-third">Rights holder:</th><td class="data-field" data-field="rightsHolder"></td></tr>
            <tr><th class="right size-third">Source:</th><td class="data-field" data-field="seeAlso"></td></tr>
            <tr><th class="right size-third">Source URL:</th><td class="data-field" data-field="source"></td></tr>
            <tr><th class="right size-third">Assessment:</th><td class="data-field" data-field="number"></td></tr>
          </thead>
        </table>

        <p>The original assessment in Ambit can be found via “Assessment ID”.</p>

      </section>

      <section id="jtox-report-structlist">
        <h2>List of structures for assessment</h2>
        <p>In the assessment, similar structures were selected from exact structure, substructure and/or similarity searches, or were added manually. The rationale for the selection is given in the table.</p>
        <div id="jtox-report-query" class="jtox-toolkit" data-kit="query" data-cross-domain="true" data-configuration="jTConfigurator" data-initial-query="false">
          <div id="browser" class="jtox-toolkit" data-kit="compound" data-show-tabs="false" data-hide-empty="true" data-on-details="onDetailedRow" data-details-height="500px" data-show-diagrams="true" data-on-loaded="onBrowserFilled"></div>
        </div>
      </section>

      <section id="jtox-report-substances">
        <h2>List of substances related to the structures</h2>
        <p>In the following, for each structure listed in chapter 2, substances were selected and the rationale is given.</p>
        <div id="jtox-report-substance-query" class="jtox-toolkit" data-kit="query" data-cross-domain="true" data-configuration="jTConfigurator" data-initial-query="false">
          <div id="report-substance-browser" class="jtox-toolkit" data-kit="compound" data-show-tabs="false" data-hide-empty="true" data-pre-details="preDetailedRow" data-show-diagrams="true" data-on-loaded="onReportSubstancesLoaded"></div>
        </div>
      </section>

      <section id="jtox-report-matrix">
        <h2>Substance composition matrix</h2>
        <p>In the following, for each substance, the associated structure(s) and the composition are given.</p>
        <div class="jtox-toolkit" data-kit="compound" data-manual-init="true"></div>
      </section>

      <section id="jtox-report-final">
        <h2>Assessment data matrix</h2>
        <p>In the following, for each substance, the associated endpoint data are given, either experimental data, waiving or read-across.</p>
        <p>For detailed data or rationale for waiving and read-across, click hyperlinks in the table. These data or rationales can also be found in the annex of the report.</p>
        <div class="jtox-toolkit" data-manual-init="true"></div>
      </section>


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
            <th rowspan="2">Guideline</th>
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
  	    <div class="jtox-details font-heavy jtox-required">Justification</div>
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
  
  </div> <!-- sixteen columns -->

  
  <div class='row add-bottom'>&nbsp;</div>

  <div class="sixteen  columns add-bottom">&nbsp;</div>


<#if menu_profile??>
	<#include "/menu/profile/${menu_profile}/footer.ftl" >
</#if>

		
<#if username??>
	    <form id='logoutForm' name='logoutForm'  action='${ambit_root}/provider/signout?targetUri=${ambit_root}&method=DELETE' method='POST'><input type='hidden' value="${username}"></input></form>
</#if>

	
	

  </div> <!-- container -->
  
</body>
</html>
