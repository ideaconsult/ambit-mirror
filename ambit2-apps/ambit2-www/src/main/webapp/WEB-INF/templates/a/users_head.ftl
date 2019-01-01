<script type='text/javascript' src='${ambit_root}/scripts/a/myprofile.js'></script>
<script type='text/javascript' src='${ambit_root}/jquery/jquery.validate.min.js'></script>

<script type='text/javascript'>
$(document).ready(function() {
	getMyAccount('${ambit_root}','${ambit_request_json}',true,'${username}');
	
		// validate the comment form when it is submitted
	$("#form_myaccount").validate({
		rules : {
			'firstname': {
				required : true
			},
			'lastname': {
				required : true
			},
			'email': {
				required : true,
				email: true
			},
			'homepage': {
				url: true
			}			
			
		},
		messages : {
			'firstname' : "Please provide your first name",
			'lastname'   : {
				required: "Please provide your last name"
			},
			'email'   : {
				required: "Please provide e-mail",
				email: "Please provide valid e-mail"
			},
			'homepage'   : {
				url: "Please provide valid web address"
			}
		}
	});
});
</script>