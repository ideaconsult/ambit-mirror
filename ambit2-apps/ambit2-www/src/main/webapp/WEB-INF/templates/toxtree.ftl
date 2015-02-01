<#include "/html.ftl" >
<head>
<#include "/header_updated_noambitcss.ftl" >
  

<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>
<link rel="stylesheet" href="${ambit_root}/style/ketcher.css"/>
<link rel="stylesheet" href='${ambit_root}/scripts/toxtree/ui-toxtree.css'/>

<script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/oecdcategories.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/ketcher.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/toxtree/ui-toxtree.js'></script>
  
</head>
<body>
  <div id="logger" class="jtox-toolkit jtox-widget hidden" data-kit="log" data-resend-events="false" data-right-side="true"></div>
	
<div class="remove-bottom h5"  style="padding-top:0px">
    <#if menu_profile??>
			<a href="${ambit_root}/ui"><img class='scale-with-grid' border='0' src='${ambit_root}/images/profile/${menu_profile}/logo.png' title='Home' alt='AMBIT logo'></a>
	<#else>
			<a href="${ambit_root}/ui"><img class='scale-with-grid' border='0' src='${ambit_root}/images/profile/default/logo.png' title='Home' alt='AMBIT logo'></a>
	</#if>
</div>

  <div class="jtox-toolkit" 
	data-kit="query" 
	data-cross-domain="false" 
	data-configuration="config_toxtree" 
	data-base-url="${ambit_root}">

    <div id="tt-searchbar" class="jtox-toolkit jtox-widget cc-fixed" data-kit="search"></div>
    <div id="tt-bigpane" class="cc-flex">
      <div>
        <div id="tt-browser-panel">
    			<div id="tt-features" class="cc-flex">
      			<div class="cc-fixed"><span class="counter-field">Available structure attributes </span></div>
      			<div class="tt-controls"><a class="paginate_disabled_previous prev-field" tabindex="0" role="button">Previous</a><a class="paginate_disabled_next next-field" tabindex="0" role="button">Next</a></div>
      			<div class="list jtox-panel cc-flex"></div>
      		</div>
    			<div id="tt-diagram" class="jtox-foldable cc-fixed">
    			  <div class="title"><span class="counter-field">Structure diagram</span></div>
    			  <div class="content">
              <img class="toxtree-diagram" alt="Compoung diagram"/>
    			  </div>
          </div>
        </div>
        <div id="tt-models-panel">
          <div class="cc-fixed">
            <div class="title jtox-inline"><span class="counter-field">Toxicity prediction modules (0/0)</span></div>
            <div class="jtox-inline selections"><a href="#" class="select-unselect" title="(Un)select all algorithms" data-other="unselect">select</a>&nbsp;<a href="#" class="expand-collapse" title="Expand/collapse all algorithm panes" data-other="collapse">expand</a>&nbsp;<a href="#" class="run-selected" title="Run predictions for all selected algorithms">run</a>&nbsp;<a href="#" class="show-hide" data-other="show" title="Hide/show unselected algorithms">hide</a></div>
          </div>
          <div class="jtox-toolkit jtox-widget cc-flex size-full" data-kit="model" data-algorithms="true" data-no-interface="true" data-on-loaded="onAlgoLoaded" data-algorithm-needle="ToxTree"></div>
        </div>
        <div id="tt-table" class="jtox-toolkit jtox-panel" data-kit="compound" data-configuration="config_toxtree" data-manual-init="true" data-pre-details="onTableDetails" data-show-tabs="false" data-on-loaded="onDataLoaded" data-selection-handler="checked"></div>
      </div>
    </div>
    <div id="sidebar">
      <div class="side-title">
        <div data-mode="table">Table view mode</div>
        <div data-mode="single">Single view mode</div>
      </div>
    </div>
  </div>
  
  <div class="jtox-template">
    <div id="tt-feature" class="tt-feature">
      <div class="data-field tt-name" data-field="title"></div><div class="data-field tt-value" data-field="value"></div>
    </div>
    <div id="tt-class" class="tt-class">
      <span class="data-field" data-field="title"></span><span class="ui-icon ui-icon-check"></span>
    </div>
    <div id="tt-algorithm" class="tt-algorithm jtox-foldable folded">
      <div class="title" >
        <a target="_blank" class="data-field attribute" data-field="uri" data-attribute="href"><span class="ui-icon ui-icon-link jtox-inline"></span></a>
        <span class="data-field" data-field="name" data-format="formatAlgoName">?</span>
        <div class="jtox-inline command-buttons">
          <button class="jt-toggle jtox-handler predict" data-handler="runPredict" title="Run prediction with the algorithm on current compound">▶︎</button>
          <button class="jt-toggle jtox-handler model" data-handler="makeModel" title="Prepare the model for this algorithm">M</button>
          <button class="jt-toggle jtox-handler auto" data-handler="markAuto" title="Run automatically on new queries">A</button>
        </div>
        <div class="tt-classification">
        </div>
      </div>
      <div class="content">
        <div class="tt-explanation"></div>
      </div>
    </div>
  </div>


<!-- toxtree ends -->

<!-- uncomment when layout breakup is solved ..
</div>
<#include "/footer.ftl" >
</div>
-->

</body>
</html>
