<div class='helptitle' style='font-weight:bold;'>User management</div>
<div class='helpcontent'>
<ul>
<li>Users <a href='#' class='chelp husers'></a>
The list of registered users can be viewed by the users, assigned an admin<a href='#' class='chelp hadmin'></a> role.</li>
<li>Roles. AMBIT user access is role-based. There are three roles: regular user, data manager <a href='#' class='chelp hcurator'></a> and admin <a href='#' class='chelp hadmin'></a>.
</li>
<li>Access rights <a href='#' class='chelp hpolicy'></a>  
Each role<a href='#' class='chelp hroles'></a> is granted access rights to set of resource URI<a href='#' class='chelp hresource'></a></li>
</ul>
</div>

<div id="keys" style="display:none;">
  <ul>
  	<li><a href="#hcurator">Data manager</a></li>
    <li><a href="#hadmin">Admin</a></li>
    <li><a href="#roles">Roles</a></li>
    <li><a href="#husers">Users</a></li>
    <li><a href="#hresource">Resource</a></li>
    <li><a href="#hpolicy">Access rights</a></li>
  </ul>
  <div id="husers">
    <p>AMBIT users</p>
     While in the <a href='${ambit_root}/user'>Users</a> page, double click in the last two columns to change  
 	 admin <a href='#' class='chelp hadmin'></a> and data manager<a href='#' class='chelp hcurator'></a> roles<a href='#' class='chelp roles'></a>.
  </div>
  
  <div id="hresource">
    <p>
   A resource is any AMBIT web page. A resource is identified with its URI - e.g. <a href='${ambit_root}/dataset'>${ambit_root}/dataset</a> 
    </p>
  </div>  
  <div id="hcurator">
    <p>
   Data manager : Can <a href='${ambit_root}/ui/uploadstruc'>import</a>, update and delete datasets.
    </p>
  </div>  
  <div id="roles">
    <p>
   AMBIT user access is role-based. There are three roles: regular user, data manager <a href='#' class='chelp hcurator'></a> 
   and admin <a href='#' class='chelp hadmin'></a>.
    </p>
  </div>    
  <div id="hadmin">
    <p>
    The admin could assign and revoke data manager and admin roles as well; 
    as well as <a href='${ambit_root}/admin/restpolicy'>edit access policies</a>.
    </p>
  </div>    
    <div id="hpolicy">
    <p>
    The <a href='${ambit_root}/admin/restpolicy'>access rights</a> specify if the resource content can be read ( GET ), deleted (DELETE) , updated (PUT), or new subresources be created (POST)
    by the specified role holder.
    </p>
  </div>  
</div>