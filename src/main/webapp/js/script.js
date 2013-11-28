$(check());

function check() {
	if($("#upload-file-info").html() != "" && $("#selected-framework").html() != "")
		$("#submit-button").prop('disabled', false);
	else
		$("#submit-button").prop('disabled', true);
}