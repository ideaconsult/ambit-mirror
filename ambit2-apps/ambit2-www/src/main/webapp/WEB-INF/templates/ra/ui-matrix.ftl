<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

	<link rel="stylesheet" href="${ambit_root}/style/jBox.css" type="text/css">
	<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>
	<link rel="stylesheet" href="${ambit_root}/style/ketcher.css"/>
	
	<link rel="stylesheet" href="${ambit_root}/style/ra/ui-matrix.css"/>

  <script src="${ambit_root}/scripts/ra/i5enums.js"></script>

  <script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js'></script>
  <script type='text/javascript' src='${ambit_root}/scripts/colResizable-1.3.min.js'></script>

  <script type='text/javascript' src='${ambit_root}/scripts/jBox.js'></script>
  
  <script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>
  <script type='text/javascript' src='${ambit_root}/scripts/ketcher.js'></script>

    <script src="${ambit_root}/scripts/ra/ui-matrix.js"></script>
  <script src="${ambit_root}/scripts/ra/config-matrix.js"></script>
  <script src="${ambit_root}/scripts/config-study.js"></script>
  <script type='text/javascript' src='${ambit_root}/scripts/config-study.js'></script>
  
  <script type='text/javascript'>

	$(document).ready(function() {
        jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle?page=0&pagesize=100" title="Assessments">Assessments</a></li>');
        <#if bundleid??>
        jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle/${bundleid}" title="Assessment">Assessment #${bundleid}</a></li>');
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
	          <tr><th class="right size-third">Assessment<a href='#' class='chelp assessment'>?</a></th></th><th class="data-field" data-field="id"></th></tr>
	          <tr><td class="right size-third">Name <a href='#' class='chelp a_name'>?</a>:</td><td><input class="data-field first-time validate" data-field="title" name="title"></input></td></tr>
			  <tr><td class="right size-third">Source <a href='#' class='chelp a_source'>?</a>:</td><td><input class="data-field first-time validate" data-field="seeAlso" name="source"></input></td></tr>
	          <tr><td class="right size-third">Source URL <a href='#' class='chelp a_source'>?</a>:</td><td><input class="data-field first-time validate" data-field="source" name="url"></input></td></tr>
	          <tr><td class="right top size-third">Purpose <a href='#' class='chelp a_description'>?</a>:</td><td><textarea class="validate nomargin data-field" data-field="description" name="description"></textarea></td></tr>
	          <tr><td class="right size-third">Owner <a href='#' class='chelp a_maintainer'>?</a>:</td><td><input class="data-field first-time validate" data-field="maintainer" name="maintainer"></input></td></tr>
	          
	          <tr><td class="right size-third">Version <a href='#' class='chelp a_version'>?</a>:</td><td class="data-field" data-field="version" title='versions not supported yet'></td></tr>
	          	          	          
	          <tr><td class="right size-third">License of Waiver <a href='#' class='chelp a_license'>?</a>:</td><td><input class="data-field first-time validate" data-field="rights.URI" name="license"></input></td></tr>
	          <tr><td class="right size-third">Rights holder <a href='#' class='chelp a_rightsholder'>?</a>:</td><td><input class="data-field first-time validate" data-field="rightsHolder" name="rightsHolder"></input></td></tr>

	          <tr><td class="right size-third">Status <a href='#' class='chelp a_status'>?</a>:</td><td class="data-field" data-field="status"></td></tr>
	          <tr><td class="right size-third">Date started <a href='#' class='chelp a_started'>?</a>:</td><td class="data-field" data-field="created"></td></tr>
	          <tr><td class="right size-third">Date finished <a href='#' class='chelp a_started'>?</a>:</td><td class="data-field" data-field="finished"></td></tr>
	          <tr>
	          	<td class="right size-third">Flags <a href='#' class='chelp a_flags'>?</a>:</td>
	          	<td>
		          	<div class="jq-buttonset">
		          		<input type="hidden" name="flags"/>
								  <input type="checkbox" id="confidential" class="accumulate" data-accumulate="flags" value="confidential"><label for="confidential">Confidential</label></input>
								  <input type="checkbox" id="internal" class="accumulate" data-accumulate="flags" value="internal"><label for="internal">Internal</label></input>
								</div>
							</td>
						</tr>
	          <tr>
	          	<td class="right size-third">Published:</td>
	          	<td>
		          	<div class="jq-buttonset">
								  <input type="radio" id="pub-yes" name="published" value="true"><label for="pub-yes">Yes</label></input>
								  <input type="radio" id="pub-no" name="published" value="false" checked="checked"><label for="pub-no">No</label></input>
								</div>
							</td>
						</tr>
	          <tr>
	          	<td class="right size-third">Use allowed:</td>
	          	<td>
		          	<div class="jq-buttonset">
								  <input type="radio" id="use-yes" value="true" name="useAllowed"><label for="use-yes">Yes</label></input>
								  <input type="radio" id="use-no" value="false" name="useAllowed" checked="checked"><label for="use-no">No</label></input>
								</div>
							</td>
						</tr>

	          <tr><td class="right size-third">Rating <a href='#' class='chelp a_rating'>?</a>:</td><td class="data-stars-field"><input type="hidden" name="stars" value="0"></input></td></tr>
	        </thead>
	      </table>
	      
	      <div class="row actions">
		      <button name="assStart" type="button">Start</button>
		      <button name="assFinalize" type="button">Finalize</button>
		      <button name="assDuplicate" type="button">Duplicate</button>
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
        <div id="browser" class="jtox-toolkit" data-kit="compound" data-show-tabs="false" data-details-height="500px" data-hide-empty="true" data-on-details="onDetailedRow" data-show-diagrams="true" data-on-loaded="onBrowserFilled"></div>
      </div>
    </div>
    <div id="jtox-endpoints">
	  	<div class="jq-buttonset center action" data-action="onEndpoint">
			  <input type="radio" id="endsubstance" name="endaction" checked="checked"><label for="endsubstance">Search substance(s)</label></input>
			  <input type="radio" id="endpoints" name="endaction"><label for="endpoints">Selection of endpoints</label></input>
			</div>
			<div class="size-full">
  			<div class="jtox-slidable">
          <div class="jtox-inline tab-substance"></div><div class="jtox-inline tab-points"><div class="check-all"><input type="checkbox" name="endpointAll"/>Show all<span>&nbsp;</span></div></div>
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
    <div id="jtox-report" class="jtox-report"></div>
  </div>
  <div class="jtox-template">
    <div id="info-box">
      <table>
        <thead>
          <tr>
            <th rowspan="2">Endpoint</th>
            <th rowspan="2">Value</th>
            <th class="conditions center">Conditions</th>
            <th rowspan="2">Guidance</th>
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
  	    <div class="jtox-details font-heavy">Study type</div>
  	    <select class="data-field type-list" data-field="type"><option value="-1"> - Select type - </option></select>
	    </div>
	    <div class="jtox-medium-box box-field" data-name="reference">
  	    <div class="jtox-details font-heavy">Reference</div>
  	    <input type="text" class="data-field" data-field="reference" placeholder="Reference_"/>
	    </div>
	    <div class="jtox-medium-box box-field size-full" data-name="justification">
  	    <div class="jtox-details font-heavy">Justification</div>
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
