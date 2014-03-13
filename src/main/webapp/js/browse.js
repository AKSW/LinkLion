
function check() {
	if($("#selected-mapping").html() != "")
		$("#submit-button").prop('disabled', false);
	else
		$("#submit-button").prop('disabled', true);
}

function mappingTrigger(name, uri) {
	// frontend
	$("#selected-mapping").html(name);
	check();
	// backend
	$("#mapping-uri").val(uri);
}

$(document).ready(function () {
	$(check());
});