<#include "/html.ftl">
<head>
  <#include "/header_updated.ftl">
  

<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>
<link rel="stylesheet" href="${ambit_root}/style/ketcher.css"/>

<script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/colResizable-1.3.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/ketcher.js'></script>
  
<script type='text/javascript'>  
$(document)
		.ready(
				function() {
					    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/_search" title="AMBIT Structure search">Structure search</a></li>');
    					jQuery("#breadCrumb").jBreadCrumb();
    					jQuery("#welcome").html("&nbsp;");
    					loadHelp("${ambit_root}","search");
				});
</script>  
  <script language="JavaScript">
    function onSideLoaded(result) {
    	var tEl = $('.title', $(this.rootElement).parents('.jtox-foldable')[0])[0];
    	var set = result.dataset || result.model;
    	tEl.innerHTML = tEl.innerHTML.replace(/(.+)\((\d+)\/(\d+)(.*)?/, '$1($2/' + set.length + '$4');;
    }
    
    function onSelectedUpdate(el) {
      var par = $(el).parents('.jtox-foldable')[0];
    	var tEl = $('.title', par)[0];
    	var v = $('input[type="checkbox"]:checked', par).length;
    	tEl.innerHTML = tEl.innerHTML.replace(/(.+)\((\d+)\/(\d+)(.*)?/, '$1(' + v + '/$3$4');;
    }
    
    function onDetailedRow(row, data, index) {
      var el = $('.jtox-details-composition', row);
      var uri = $(el).data('uri');
      uri = this.settings.baseUrl + '/substance?type=related&compound_uri=' + encodeURIComponent(uri);
      el = $(el).parents('table')[0];
      el = el.parentNode;
      $(el).empty();
      var div = document.createElement('div');
      el.appendChild(div);
      var ds = new jToxSubstance(div, {
      			crossDomain: true, 
      			substanceUri: uri, 
      			showControls: false,
      			showDiagrams: true , 
      			onDetails: function (root, data, event) {
        			var comp = new jToxStudy(root);
        			comp.querySubstance(data);
      			} 
      		} );
    }
    
    $(document).ready(function(){
      var toggleBar = function () {
        $(this).parents('#sidebar').toggleClass('hidden');
      };
      $('#sidebar span.ui-icon').on('click', toggleBar);
      $('#sidebar div.side-title').on('click', toggleBar);
      
      $('#sidebar a.select-all').on('click', function () {
        $('input[type="checkbox"]', this.parentNode).each(function () { this.checked = true;});
        onSelectedUpdate(this);
      });
      $('#sidebar a.unselect-all').on('click', function () {
        $('input[type="checkbox"]', this.parentNode).each(function () { this.checked = false;});
        onSelectedUpdate(this);
      });
    });
  </script>
  
  <script type='text/javascript' src='${ambit_root}/scripts/config-dataset.js'></script>
  
<style>
	#sidebar {
	  position: fixed;
  	background-color: #fafafa;
  	border: 1px solid #ccc;
	  width: 350px;
	  top: 50px;
	  left: 0px;
  	bottom: 0px;
  	z-index: 100;
  	padding: 0px 25px 0px 8px;
  	transition: left 0.5s ease;
  	-moz-transition: left 0.5s ease;
  	-webkit-transition: left 0.5s ease;
  	-o-transition: left 0.5s ease;
  	border-radius: 7px;
  	box-shadow: 3px 3px 7px #999;
	}
	
	#sidebar.hidden {
  	left: -360px;
	}
	
	#sidebar span.side-open-close {
	  position: relative;
  	left: 355px;
	}
	
	#sidebar .jtox-foldable {
  	padding: 5px 5px 10px 5px;
	}
	
	#sidebar .content {
  	font-size: 80%;
  	overflow: scroll;
  	height: 95%;
	}
	
	#sidebar .side-title {
	  position: absolute;
	  right: -32px;
	  bottom: 50%;
	  text-align: center;
  	transform: rotate(270deg);
    -webkit-transform: rotate(270deg);
    -moz-transform: rotate(270deg);   
    -o-transform: rotate(270deg);  	
    -ms-transform: rotate(270deg);  	
	}
		
  #browser, #searchbar .ketcher {
    margin-left: 30px;
  }
  
  #browser {
    position: relative;
    top: 0px;
  	transition: top 0.3s ease 0.2s;
  	-moz-transition: top 0.3s ease 0.2s;
  	-webkit-transition: top 0.3s ease 0.2s;
  	-o-transition: top 0.3s ease 0.2s;
  }
	</style>
</head>
<body>

<div class="container" style="margin:0;padding:0;">


<#include "/banner_crumbs.ftl">
	
		<!-- Page Content
		================================================== -->
	<div class="sixteen columns remove-bottom" style="padding:0;" >

  <div class="jtox-toolkit" data-kit="query" data-initial-query="yes" data-cross-domain="true" data-configuration="config_dataset" 
  		data-base-url="${ambit_root}">
    <div id="searchbar" class="jtox-toolkit jtox-widget" data-kit="search"></div>
    <div id="sidebar" class="hidden">
      <div>
        <span class="side-open-close ui-icon ui-icon-carat-2-e-w"></span>
        <div class="side-title">Data sources</div>
      </div>
      <div class="content">
        <div class="jtox-foldable folded">
          <div class="title">
            <h5>Datasets (0/0)</h5>
          </div>
          <div class="content">
            <a href="#" class="select-all">select all</a>&nbsp;<a href="#" class="unselect-all">unselect all</a>
            <div class="jtox-toolkit jtox-widget" data-kit="dataset" data-short-stars="true" data-s-dom="rt" data-selectable="true" data-selection-handler="checked" data-on-loaded="onSideLoaded" data-load-on-init="true"></div>
          </div>
        </div>
        <div class="jtox-foldable folded">
          <div class="title">
            <h5>Models (0/0)</h5>
          </div>
          <div class="content">
            <a href="#" class="select-all">select all</a>&nbsp;<a href="#" class="unselect-all">unselect all</a>
            <div class="jtox-toolkit jtox-widget" data-kit="model" data-short-stars="true" data-s-dom="rt" data-selectable="true" data-selection-handler="checked" data-on-loaded="onSideLoaded" data-load-on-init="true" ></div>
          </div>
        </div>
      </div>
    </div>
	  <div id="browser" class="jtox-toolkit" data-kit="compound" data-remember-checks="true" data-on-details="onDetailedRow" data-details-height="500px" data-tabs-folded="true"></div>
  </div>

   </div>

		
<div class='row add-bottom'>&nbsp;</div>
<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>