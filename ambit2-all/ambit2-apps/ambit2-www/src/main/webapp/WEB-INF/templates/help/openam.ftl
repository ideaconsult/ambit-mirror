<div class='helptitle' style='font-weight:bold;'>Help</div>
<div class='helpcontent'>
<ul>
<li>Browsing publicly available resources does not require login.</li>
<li>
To browse protected resources or upload data, please log in (use the default guest account or an OpenTox account.</li>  
<li>To obtain OpenTox account, please register (free) at <a title='Register at OpenTox site' class='qxternal' target="opentox" href='http://opentox.org/join_form' class='qxternal' >OpenTox site</a></li>
</ul>
User management and access control is handled by OpenTox Authentication and Authorization 
<a href='http://opentox.org/dev/apis/api-1.2/AA' class='qxternal' target=_blank>API</a>
which relies on a customized OpenAM <a href='#' class='chelp openam'>?</a> service. 

Each resource can be assigned a policy <a href='#' class='chelp policy'>?</a>, specifying read / write access for selected groups and users. 
<br/>

</div>

<div id="keys" style="display:none;">
  <ul>
    <li><a href="#openam">OpenSSO / OpenAM</a></li>
    <li><a href="#policy">OpenAM policies</a></li>
    <li><a href='#aa' title='Authentication and authorization'>AA</a></li>
  </ul>
  	
	<div id='aa'>
		Access to AMBIT web services can be controlled via OpenTox Authentication and Authorization infrastructure. 
		Security tokens are obtained after log in into a preconfigured OpenAM service.
		<a href='http://forgerock.com/what-we-offer/open-identity-stack/openam/' class='qxternal' target='_blank'>OpenAM</a> |
		<a href='http://opentox.org/dev/apis/api-1.2/AA' class='qxternal' target='_blank'>API</a> |
		<a href='${ambit_root}/admin/policy' target='_blank'>Manage policies</a> 
   </div>

	<div id='openam'>
		<a href='http://en.wikipedia.org/wiki/OpenAM' target=_blank class='qxternal'>OpenAM</a> is an open source access management, entitlements and federation server platform.  
		OpenAM is the software that allows OpenTox services running at each partner facility to use one and the same user credentials (user name and password) – this is known as <a href='https://en.wikipedia.org/wiki/Single_sign-on' class='qxternal' target=_blank>Single Sign On</a> capability.
	</div>
	
	<div id='policy'>
	The policies use eXtensible Access Control Markup Language <a href='https://en.wikipedia.org/wiki/XACML' targe=_blank class='qxternal'>(XACML)</a> syntax.
	The policies can be created and modified via OpenTox AA <a href="http://opentox.org/dev/apis/api-1.2/AA#section-2">API</a>.
	</div>

</div>      

