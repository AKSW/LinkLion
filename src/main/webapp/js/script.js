/* 
 * TODO 
 * check if everything is okay (framework, algorithm, new stuff, sources)
 */
function check() {
	if($("#upload-file-info").html() != "" && $("#selected-framework").html() != "")
		$("#submit-button").prop('disabled', false);
	else
		$("#submit-button").prop('disabled', true);
}

function new_framework(nf) {
	if(nf) {
		$('#new-framework').show();
	} else {
		$('#new-framework').hide();
	}
}

function new_algorithm(nf) {
	if(nf) {
		$('#new-algorithm').show();
	} else {
		$('#new-algorithm').hide();
	}
}

function frameworkTrigger(name, uri) {
	// frontend
	if(name == "") {
		$("#selected-framework").html("Add new framework");
		new_framework(true);
	} else {
		$("#selected-framework").html(name);
		new_framework(false);
	}
	check();
	// backend
	$("#existing-framework-uri").val(uri);
}


function algorithmTrigger(name, uri) {
	// frontend
	if(name == "") {
		$("#selected-algorithm").html("Add new algorithm");
		new_algorithm(true);
	} else {
		$("#selected-algorithm").html(name);
		new_algorithm(false);
	}
	check();
	// backend
	$("#existing-algorithm-uri").val(uri);
}

$(document).ready(function () {
	$(check());
	$(new_framework(false));
	$(new_algorithm(false));
});