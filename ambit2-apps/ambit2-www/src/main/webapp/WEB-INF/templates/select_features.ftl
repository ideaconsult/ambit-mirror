<ul id="dselectable">
<li class="ui-selectee" onClick="toggleSelectionDiv('#select_datasets','#ctrl_dataset');" title='Click to show/hide the datasets available'>
<span class="ui-icon ui-icon-folder-open" style="float: left; margin-right: .3em;" id='ctrl_dataset'></span>
Datasets 
<a href='${ambit_root}/dataset?max=25' title='Available datasets'>
<span class="ui-icon ui-icon-link" style="float:right;margin-right: .3em;"></span></a>
</li>
</ul>
<div style='background: #F2F0E6;margin: 3px; padding: 0em; font-size: 0.85em; ' id='select_datasets' class='row remove-bottom'>
        <table id='datasets' style="margin:0;padding:0px;">
        <thead style="display:none;">
        <tr>
        <th></th>
        <th>Dataset</th>
        </tr>
        </thead>
        <tbody>
        </tbody>
        </table>
</div>

<ul id="mselectable">
<li class="ui-selectee" onClick="toggleSelectionDiv('#select_models','#ctrl_model');" title='Click to show/hide the models available'>
<span class="ui-icon ui-icon-folder-open" style="float: left; margin-right: .3em;" id='ctrl_model'></span>
Predictions
<a href='${ambit_root}/model?max=25' target='model' title='Available models'>
<span class="ui-icon ui-icon-link" style="float: right;  margin-right: .3em;"></span></a>
 <a href='${ambit_root}/algorithm?max=25' target='algorithm' title='Available algorithms'>
 <span class="ui-icon ui-icon-link" style="float: right;  margin-right: .3em;"></span>
 </a>
</li>
</ul>
<div style='background: #F2F0E6;margin: 3px; padding: 0em; font-size: 0.85em; ' id='select_models'>
        <table id='models' style="margin:0;padding:0px;">
        <thead style="display:none;">
        <tr>
        <th></th>
        <th>Model</th>
        </tr>
        </thead>
        <tbody>
        </tbody>
        </table>
</div>
