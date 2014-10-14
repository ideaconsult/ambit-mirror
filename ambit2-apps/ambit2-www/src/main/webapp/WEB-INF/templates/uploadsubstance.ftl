<#include "/html.ftl" >

<head>
<#include "/header_updated.ftl" >


<noscript><link rel="stylesheet" href="${ambit_root}/jquery/fileupload/style.css"></noscript>
<link rel="stylesheet" href="${ambit_root}/jquery/fileupload/jquery.fileupload.css">
<link rel="stylesheet" href="${ambit_root}/jquery/fileupload/jquery.fileupload-ui.css">
<!-- CSS adjustments for browsers with JavaScript disabled -->
<noscript><link rel="stylesheet" href="${ambit_root}/jquery/fileupload/css/jquery.fileupload-noscript.css"></noscript>
<noscript><link rel="stylesheet" href="${ambit_root}/jquery/fileupload/css/jquery.fileupload-ui-noscript.css"></noscript>

<script type='text/javascript' src='${ambit_root}/scripts/i5criteria.js'></script>

<script type='text/javascript'>

$(document)
		.ready(function() {
			loadHelp("${ambit_root}","substance");
			jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance" title="Substance">Substances</a></li>');
		    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/uploadsubstance1" title="File upload">Import</a></li>');
		    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/uploadsubstance" title="Upload substances as defined in multiple IUCLID 5 .i5d or .i5z files">Multiple .i5z files upload</a></li>');
		    jQuery("#breadCrumb").jBreadCrumb();
			jQuery("#welcome").text("Substance Import");
			var form = $('#fileupload'); 
			 form.fileupload({
			        // Uncomment the following to send cross-domain cookies:
			        //xhrFields: {withCredentials: true},
			        url: '${ambit_root}/ui/uploadsubstance'
		    });
			 form.bind('fileuploadalways', function (e, data) {
				console.log(e);
			 });
			 form.bind('chunkfail', function (e, data) {
					console.log(e);
		     });
			 form.bind('fileuploadsubmit', function (e, data) {
					console.log(e);
		     });
			 
			 
		    _i5.getQAOptions(_i5.qaSettings);
});
</script>

</head>
<body>

<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/searchmenu/menu_substance.ftl">

</div>


		<!-- Page Content
		================================================== -->
<div class="eleven columns" style="padding:0;" >


<div class='ui-widget-header ui-corner-top'>&nbsp;Import new substance(s)</div>

<!-- Multi file upload (JS needed) -->

<div class='ui-widget-content ui-corner-bottom' id='multiUpload' >	

	<div style="margin:5px;padding:5px;">	
	<form action="${ambit_root}/upload" id="fileupload"  method="POST"   ENCTYPE="multipart/form-data">
	<!-- The fileupload-buttonbar contains buttons to add/delete files and start/cancel the upload -->
	

	<div class="fileupload-buttonbar row remove-bottom">
	    <div class="fileupload-buttons sixteen columns">
	        <!-- The fileinput-button span is used to style the file input field as button -->
	        <span class="fileinput-button">
	            <span>Add files...</span>
	            <input type="file" name="files[]" multiple>
	        </span>
	        <button type="submit" class="start">Start upload</button>
	        <button type="reset" class="cancel">Cancel upload</button>
	        <!--
	        <button type="button" class="delete">Delete</button>
	        <input type="checkbox" class="toggle">
	        -->
	        <!-- The global file processing state -->
	        <span class="fileupload-process remove-bottom"></span>
	    </div>
	    
		<div class='row remove-bottom'>
			<label class='four columns alpha' title='Uncheck to add or replace study records, retaining existing where relevant'>Clear existing study records</label>
		   	<input class='one column omega half-bottom' type="checkbox" id="clearMeasurements" name="clearMeasurements" class="toggle" checked>
		   	<label class='four columns omega' title='Uncheck to add or replace compositions'>Clear existing composition records</label>
		   	<input class='one column omega half-bottom' type="checkbox" id="clearComposition" name="clearComposition" class="toggle" checked>
		   	<div class='six columns omega'></div>
		</div>	  	    

		<div class='row remove-bottom'>
			<label class='six columns alpha' title='Use predefined filtering criteria'>Import only high quality study records</label>
	       	<input class='one column alpha half-bottom' type="checkbox" id="qaenabled" name="qaenabled" class="toggle" checked>
	       	<span class='five columns alpha half-bottom' >(uncheck to import all records)</span>
	       	<div class='four columns omega'></div>
		</div>	    
	    <!-- The global progress state -->
	    <div class="fileupload-progress fade remove-bottom" style="display:none">
	        <!-- The global progress bar -->
	        <div class="progress" role="progressbar" aria-valuemin="0" aria-valuemax="100"></div>
	        <!-- The extended global progress state -->
	        <div class="progress-extended">&nbsp;</div>
	    </div>
	</div>
	
	    <div class='row remove-bottom'>
			<div class='six columns alpha remove-bottom'>
				<label style="float: left; margin-left: .1em;" for="purposeflag">Purpose flag</label>
				<a href="javascript:void(0)" onclick="_i5.selections.purposeflag.highQuality()" ><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-flag"></span></a>
				<a href="javascript:void(0)" onclick="_i5.selections.purposeflag.selectall(true);"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-plus"></span></a> 
				<a href="javascript:void(0)" onclick="_i5.selections.purposeflag.selectall(false)"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-minus"></span></a> 
				<br/>	
				<select multiple id="purposeflag" name="purposeflag" size='5'></select>
	    	</div>
			<div class='six columns omega remove-bottom'>
				<label style="float: left; margin-left: .1em;" for="studyresulttype">Study result type</label>
				<a href="javascript:void(0)" onclick="_i5.selections.studyresulttype.highQuality()" ><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-flag"></span></a>
				<a href="javascript:void(0)" onclick="_i5.selections.studyresulttype.selectall(true);"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-plus"></span></a> 
				<a href="javascript:void(0)" onclick="_i5.selections.studyresulttype.selectall(false)"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-minus"></span></a>				
				<br/>	
				<select multiple id="studyresulttype" name="studyresulttype" size='5'></select>
	    	</div>
			<div class='four columns omega remove-bottom'>
				<label style="float: left; margin-left: .1em;" for="testmaterial">Test material</label>
				<a href="javascript:void(0)" onclick="_i5.selections.testmaterial.highQuality()" ><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-flag"></span></a>
				<a href="javascript:void(0)" onclick="_i5.selections.testmaterial.selectall(true);"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-plus"></span></a> 
				<a href="javascript:void(0)" onclick="_i5.selections.testmaterial.selectall(false)"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-minus"></span></a>				
				<br/>	
				<select multiple id="testmaterial" name="testmaterial" size='3'></select>
	    	</div>

	    </div>
		<div class='row remove-bottom'>
			<div class='six columns alpha remove-bottom'>
				<label style="float: left; margin-left: .1em;" for="reliability">Reliability</label>
				<a href="javascript:void(0)" onclick="_i5.selections.reliability.highQuality()" ><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-flag"></span></a>
				<a href="javascript:void(0)" onclick="_i5.selections.reliability.selectall(true);"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-plus"></span></a> 
				<a href="javascript:void(0)" onclick="_i5.selections.reliability.selectall(false)"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-minus"></span></a>				
				<br/>	
				<select multiple id="reliability" name="reliability" size='5'></select>
	    	</div>
			<div class='six columns omega remove-bottom'>
				<label span style="float: left; margin-left: .1em;" for="referencetype" >Reference type</label>
				<a href="javascript:void(0)" onclick="_i5.selections.referencetype.highQuality()" ><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-flag"></span></a>
				<a href="javascript:void(0)" onclick="_i5.selections.referencetype.selectall(true);"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-plus"></span></a> 
				<a href="javascript:void(0)" onclick="_i5.selections.referencetype.selectall(false)"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-minus"></span></a>			
				<br/>	
				<select multiple id="referencetype" name="referencetype"  size='5'></select>
	    	</div>
			<div class='four columns omega remove-bottom'>
				<a href="javascript:void(0)" onclick="_i5.selections.highQuality()" title='Click to select a predefined set of high quality study criteria'>
				<span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-flag"></span>Select high quality study criteria</a> 
				<br/>
				<a href="javascript:void(0)" onclick="_i5.selections.selectall(true);">
				<span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-plus"></span>Select All</a> 
				<br/>
				<a href="javascript:void(0)" onclick="_i5.selections.selectall(false)">
				<span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-minus"></span>Unselect all</a>
	    	</div>
	    	
		</div>	
		<div class='row half-bottom' >
		<hr class='remove-bottom' />
		</div>	

	<!-- The table listing the files available for upload/download -->
	<div role="presentation" border="1" ><div class="files"></div></div>
	
	
	
	
	</form>
	</div>	
</div>


<div class='row' >
		&nbsp;
	</div>
			
	<div class='row half-bottom'>
		<div class='four columns alpha'>&nbsp;</div>
		<div class='twelve columns omega half-bottom'>
		    Substance import options: 
			<a href="${ambit_root}/ui/uploadsubstance" title="Multiple .i5z files upload">Multiple .i5z files upload</a>
			 | 
			<a href="${ambit_root}/ui/uploadsubstance1" title="Single .i5z file upload">Single .i5z file upload</a>
			 | 
			<a href="${ambit_root}/ui/updatesubstancei5" title="Retrieve substance(s) from IUCLID5 server">Retrieve substance(s) from IUCLID5 server</a>
		</div>	
	</div>

<div class='row add-bottom' style="height:140px;">&nbsp;</div>
<!--"eleven columns"-->
</div>

<#include "/chelp.ftl" >


<#include "/footer.ftl" >
</div> <!-- container -->


<!-- The template to display files available for upload -->
<script id="template-upload" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
    <div class="template-upload fade row remove-bottom">
        <span class="preview one column">&nbsp;</span>
        <span class="name six columns">{%=file.name%}</span>
        <strong class="error two columns"></strong>
        <span class="size two columns">Processing...</span>
       	<div class="progress two columns"></div>
        <div>
            {% if (!i && !o.options.autoUpload) { %}
                <button class="start" disabled>Start</button>
            {% } %}
            {% if (!i) { %}
                <button class="cancel">Cancel</button>
            {% } %}
        </div>
    </div>
{% } %}
</script>

<!-- The template to display files available for download -->
<script id="template-download" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
	<div class="template-upload fade row remove-bottom">
	 	<span class="preview one column remove-bottom">
                {% if (file.thumbnailUrl) { %}
                    <a href="{%=file.url%}" title="{%=file.name%}" data-gallery><img src="{%=file.thumbnailUrl%}"></a>
                {% } %}
        &nbsp;</span>
        <span class="name six columns remove-bottom">
                <a href="{%=file.url%}" title="{%=file.name%}" target='_substance'>{%=file.name%}</a>
        </span>
        <span class="size two columns remove-bottom">{%=o.formatFileSize(file.size)%}</span>
        {% if (file.error) { %}
        <div class="four columns remove-bottom"><span class="error">Error</span> {%=file.error%}</div>
        {% } %}        
    </div>
{% } %}
</script>


 
<!-- The Templates plugin is included to render the upload/download listings -->
<script type='text/javascript' src="${ambit_root}/jquery/fileupload/tmpl.min.js"></script>
<!-- The Load Image plugin is included for the preview images and image resizing functionality--> 
<script type='text/javascript' src="${ambit_root}/jquery/fileupload/load-image.min.js"></script>
<!-- The basic File Upload plugin -->
<script type='text/javascript' src="${ambit_root}/jquery/fileupload/jquery.fileupload.js"></script>
<!-- The File Upload processing plugin -->
<script type='text/javascript' src="${ambit_root}/jquery/fileupload/jquery.fileupload-process.js"></script>
<!-- The File Upload image preview & resize plugin -->
<script type='text/javascript'  src="${ambit_root}/jquery/fileupload/jquery.fileupload-image.js"></script>
<!-- The File Upload validation plugin -->
<script type='text/javascript' src="${ambit_root}/jquery/fileupload/jquery.fileupload-validate.js"></script>
<!-- The File Upload user interface plugin -->
<script type='text/javascript' src="${ambit_root}/jquery/fileupload/jquery.fileupload-ui.js"></script>
<!-- The File Upload jQuery UI plugin -->
<script type='text/javascript' src="${ambit_root}/jquery/fileupload/jquery.fileupload-jquery-ui.js"></script>

</body>
</html>
