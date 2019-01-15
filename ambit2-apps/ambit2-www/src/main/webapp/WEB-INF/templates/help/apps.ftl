<div class='helptitle' style='font-weight:bold;'>My account</div>
<div class='helpcontent'>
API access tokens
<ul> 
<li>New token<a href='#' class='chelp hnewtoken'>?</a> </li>
</ul>

</div>

<div id="keys" style="display:none;">
  <ul>
    <li><a href="#happs">Access tokens</a></li>
    <li><a href="#hnewtoken">New token</a></li>
  </ul>
  <div id="myprofile">
    <p></p>
  </div>

  <div id="happs">
    <p>
    Access tokens
    
    </p>
  </div>
  <div id="hnewtoken">
				<input type='hidden' size='40' id='username' name='username' value='${username}'/>
				<button onclick="generateToken('${ambit_root}')">Generate</button>
    </p>
  </div>      
</div>